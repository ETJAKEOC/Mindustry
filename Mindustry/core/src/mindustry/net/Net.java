package mindustry.net;

import static arc.util.Log.debug;
import static mindustry.Vars.headless;
import static mindustry.Vars.netClient;
import static mindustry.Vars.platform;
import static mindustry.Vars.ui;

import net.jpountz.lz4.LZ4Exception;

import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.ExecutorService;

import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.func.Cons2;
import arc.func.Prov;
import arc.net.ArcNetException;
import arc.net.Server;
import arc.net.Server.ServerConnectFilter;
import arc.struct.IntMap;
import arc.struct.ObjectIntMap;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.ArcRuntimeException;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.OS;
import arc.util.Strings;
import arc.util.Threads;
import arc.util.Time;
import mindustry.game.EventType.ClientServerConnectEvent;
import mindustry.gen.*;
import mindustry.net.Packets.AssetRequirementStream;
import mindustry.net.Packets.AssetStream;
import mindustry.net.Packets.ConnectPacket;
import mindustry.net.Packets.KickReason;
import mindustry.net.Packets.StreamBegin;
import mindustry.net.Packets.StreamChunk;
import mindustry.net.Packets.TextureStream;
import mindustry.net.Packets.WorldStream;
import mindustry.net.Streamable.StreamBuilder;

@SuppressWarnings("unchecked")
public class Net {
	public static final int packetIdAssetStream, packetIdWorldStream, packetIdTextureStream;

	private static final Seq<Prov<? extends Packet>> packetProvs = new Seq<>();
	private static final Seq<Class<? extends Packet>> packetClasses = new Seq<>();
	private static final ObjectIntMap<Class<?>> packetToId = new ObjectIntMap<>();

	static {
		registerPacket(StreamBegin::new);
		registerPacket(StreamChunk::new);
		packetIdWorldStream = registerPacket(WorldStream::new);
		registerPacket(ConnectPacket::new);
		registerPacket(AssetRequirementStream::new);
		packetIdAssetStream = registerPacket(AssetStream::new);
		packetIdTextureStream = registerPacket(TextureStream::new);

		//register generated packet classes
		Call.registerPackets();
	}

	private final Seq<Packet> packetQueue = new Seq<>();
	private final ObjectMap<Class<?>, Cons> clientListeners = new ObjectMap<>();
	private final ObjectMap<Class<?>, Cons2<NetConnection, Object>> serverListeners = new ObjectMap<>();
	private final IntMap<StreamBuilder> streams = new IntMap<>();
	private final ExecutorService pingExecutor =
			OS.isIos ? Threads.boundedExecutor("Ping Servers", 32) : //on IOS, 256 threads can crash, so limit the amount
					Threads.unboundedExecutor();
	private final NetProvider provider;
	private boolean server;
	private boolean active;
	private boolean clientLoaded;
	private @Nullable StreamBuilder currentStream;

	public Net(NetProvider provider) {
		this.provider = provider;
	}

	/**
	 * Registers a new packet type for serialization.
	 */
	public static <T extends Packet> int registerPacket(Prov<T> cons) {
		int id = packetProvs.size;
		packetProvs.add(cons);
		var t = cons.get();
		packetClasses.add(t.getClass());
		packetToId.put(t.getClass(), id);
		return id;
	}

	public static byte getPacketClassId(Class<?> c) {
		return (byte) packetToId.get(c, -1);
	}

	public static byte getPacketId(Packet packet) {
		int id = packetToId.get(packet.getClass(), -1);
		if (id == -1) throw new ArcRuntimeException("Unknown packet type: " + packet.getClass());
		return (byte) id;
	}

	public static <T extends Packet> T newPacket(byte id) {
		return ((Prov<T>) packetProvs.get(id & 0xff)).get();
	}

	public void handleException(Throwable e) {
		if (e instanceof ArcNetException) {
			Core.app.post(() -> showError(new IOException("mismatch", e)));
		} else if (e instanceof ClosedChannelException) {
			Core.app.post(() -> showError(new IOException("alreadyconnected", e)));
		} else {
			Core.app.post(() -> showError(e));
		}
	}

	/**
	 * Display a network error. Call on the graphics thread.
	 */
	public void showError(Throwable e) {

		if (!headless) {

			Throwable t = e;
			while (t.getCause() != null) {
				t = t.getCause();
			}

			String baseError = Strings.getFinalMessage(e);

			String error = baseError == null ? "" : baseError.toLowerCase();
			String type = t.getClass().toString().toLowerCase();
			boolean isError = false;

			if (e instanceof BufferUnderflowException || e instanceof BufferOverflowException || e.getCause() instanceof EOFException) {
				error = Core.bundle.get("error.io");
			} else if (error.equals("mismatch") || e instanceof LZ4Exception || (e instanceof IndexOutOfBoundsException && e.getStackTrace().length > 0 && e.getStackTrace()[0].getClassName().contains("java.nio"))) {
				error = Core.bundle.get("error.mismatch");
			} else if (error.contains("port out of range") || error.contains("invalid argument") || (error.contains("invalid") && error.contains("address")) || Strings.neatError(e).contains("address associated")) {
				error = Core.bundle.get("error.invalidaddress");
			} else if (error.contains("connection refused") || error.contains("route to host") || type.contains("unknownhost")) {
				error = Core.bundle.get("error.unreachable");
			} else if (type.contains("timeout")) {
				error = Core.bundle.get("error.timedout");
			} else if (error.equals("alreadyconnected") || error.contains("connection is closed")) {
				error = Core.bundle.get("error.alreadyconnected");
			} else if (!error.isEmpty()) {
				error = Core.bundle.get("error.any");
				isError = true;
			}

			if (isError) {
				ui.showException("@error.any", e);
			} else {
				ui.showText("", Core.bundle.format("connectfail", error));
			}
			ui.loadfrag.hide();

			if (client()) {
				netClient.disconnectQuietly();
			}
		}

		Log.err(e);
	}

	/**
	 * Sets the client loaded status, or whether it will receive normal packets from the server.
	 */
	public void setClientLoaded(boolean loaded) {
		clientLoaded = loaded;

		if (loaded) {
			//handle all packets that were skipped while loading
			for (int i = 0; i < packetQueue.size; i++) {
				handleClientReceived(packetQueue.get(i));
			}
		}
		//clear inbound packet queue
		packetQueue.clear();
	}

	public void setClientConnected() {
		active = true;
		server = false;
	}

	/**
	 * Connect to an address.
	 */
	public void connect(String ip, int port, Runnable success) {
		streams.clear();
		currentStream = null;

		try {
			if (!active) {
				Events.fire(new ClientServerConnectEvent(ip, port));
				provider.connectClient(ip, port, success);
				active = true;
				server = false;
			} else {
				throw new IOException("alreadyconnected");
			}
		} catch (IOException e) {
			showError(e);
		}
	}

	/**
	 * Host a server at an address.
	 */
	public void host(int port) throws IOException {
		provider.hostServer(port);
		active = true;
		server = true;

		Time.runTask(60f, platform::updateRPC);
	}

	/**
	 * Closes the server.
	 */
	public void closeServer() {
		for (NetConnection con : getConnections()) {
			Call.kick(con, KickReason.serverClose);
		}

		provider.closeServer();
		server = false;
		active = false;
	}

	public void reset() {
		closeServer();
		netClient.disconnectNoReset();
	}

	public void disconnect() {
		if (active && !server) {
			Log.info("Disconnecting.");
		}
		provider.disconnectClient();
		server = false;
		active = false;
		for (var stream : streams) {
			stream.value.close();
		}
		currentStream = null;
	}

	/**
	 * Starts discovering servers on a different thread.
	 * Callback is run on the main Arc thread.
	 */
	public void discoverServers(Cons<Host> cons, Runnable done) {
		provider.discoverServers(cons, done);
	}

	/**
	 * Returns a list of all connections IDs.
	 */
	public Iterable<NetConnection> getConnections() {
		return (Iterable<NetConnection>) provider.getConnections();
	}

	/**
	 * Send an object to all connected clients, or to the server if this is a client.
	 */
	public void send(Object object, boolean reliable) {
		if (server) {
			provider.sendAllServer(object, reliable);
		} else {
			provider.sendClient(object, reliable);
		}
	}

	/**
	 * Server bulk-send to several clients.
	 */
	public void send(Object object, Iterable<NetConnection> connections, boolean reliable) {
		provider.sendAllServer(object, connections, reliable);
	}

	/**
	 * Send an object to everyone EXCEPT a certain client. Server-side only.
	 */
	public void sendExcept(NetConnection except, Object object, boolean reliable) {
		provider.sendExceptServer(except, object, reliable);
	}

	public @Nullable StreamBuilder getCurrentStream() {
		return currentStream;
	}

	/**
	 * Registers a client listener for when an object is received.
	 */
	public <T> void handleClient(Class<T> type, Cons<T> listener) {
		clientListeners.put(type, listener);
	}

	/**
	 * Registers a server listener for when an object is received.
	 */
	public <T> void handleServer(Class<T> type, Cons2<NetConnection, T> listener) {
		serverListeners.put(type, (Cons2<NetConnection, Object>) listener);
	}

	/**
	 * Call to handle a packet being received for the client.
	 */
	public void handleClientReceived(Packet object) {
		if (!object.allow(false)) {
			return;
		}

		object.handled();

		if (object instanceof StreamBegin b) {
			streams.put(b.id, currentStream = new StreamBuilder(b, ((Streamable) Net.newPacket(b.type)).incremental()));
			b.incrementalStream = currentStream.incrementalStream;

			var listeners = clientListeners.get(StreamBegin.class);
			if (listeners != null) listeners.get(object);

		} else if (object instanceof StreamChunk c) {
			StreamBuilder builder = streams.get(c.id);
			if (builder == null) {
				throw new RuntimeException("Received stream chunk without a StreamBegin beforehand!");
			}
			builder.add(c.data);

			if (ui.loadfrag.showingProgress()) {
				ui.loadfrag.setProgress(builder.progress());
				ui.loadfrag.snapProgress();
			}

			netClient.resetTimeout();

			if (builder.isDone()) {
				streams.remove(builder.id);
				//incremental streams don't send an event as the data gets handled as it comes in
				if (!builder.incremental) {
					handleClientReceived(builder.build());
					currentStream = null;
				} else {
					builder.incrementalStream.finish();
				}
			}
		} else {
			int p = object.getPriority();

			if (clientLoaded || p == Packet.priorityHigh) {
				if (clientListeners.get(object.getClass()) != null) {
					clientListeners.get(object.getClass()).get(object);
				} else {
					object.handleClient();
				}
			} else if (p != Packet.priorityLow) {
				packetQueue.add(object);
			}
		}
	}

	/**
	 * Call to handle a packet being received for the server.
	 */
	public void handleServerReceived(NetConnection connection, Packet object) {
		if (!object.allow(true)) {
			return;
		}

		try {
			if (connection.hasConnected || object.getPriority() == Packet.priorityHigh) {
				object.handled();

				//handle object normally
				if (serverListeners.get(object.getClass()) != null) {
					serverListeners.get(object.getClass()).get(connection, object);
				} else {
					object.handleServer(connection);
				}
			}
		} catch (ValidateException e) {
			//ignore invalid actions
			debug("Validation failed for '@': @", e.player, e.getMessage());
		} catch (RuntimeException e) {
			//ignore indirect ValidateException-s
			if (e.getCause() instanceof ValidateException v) {
				debug("Validation failed for '@': @", v.player, v.getMessage());
			} else {
				//rethrow if not ValidateException
				throw e;
			}
		}
	}

	public @Nullable ServerConnectFilter getConnectFilter() {
		return provider.getConnectFilter();
	}

	/**
	 * Sets a connection filter by IP address. If the filter returns {@code false}, the connection will be closed. Server only.
	 */
	public void setConnectFilter(@Nullable ServerConnectFilter filter) {
		provider.setConnectFilter(filter);
	}

	/**
	 * Pings a host in a pooled thread. If an error occurred, failed() should be called with the exception.
	 * If the port is the default mindustry port, SRV records are checked too.
	 */
	public void pingHost(String address, int port, Cons<Host> valid, Cons<Exception> failed) {
		pingExecutor.submit(() -> provider.pingHost(address, port, valid, failed));
	}

	/**
	 * Whether the net is active, e.g. whether this is a multiplayer game.
	 */
	public boolean active() {
		return active;
	}

	/**
	 * Whether this is a server or not.
	 */
	public boolean server() {
		return server && active;
	}

	/**
	 * Whether this is a client or not.
	 */
	public boolean client() {
		return !server && active;
	}

	public void dispose() {
		provider.dispose();
		server = false;
		active = false;
	}

	/**
	 * Networking implementation.
	 */
	public interface NetProvider {

		/**
		 * Sends a packet to a specific list of clients.
		 */
		default void sendAllServer(Object object, Iterable<NetConnection> connections, boolean reliable) {
			for (NetConnection con : connections) {
				con.send(object, reliable);
			}
		}

		/**
		 * Sends a packet to all connected clients.
		 */
		default void sendAllServer(Object object, boolean reliable) {
			for (NetConnection con : getConnections()) {
				con.send(object, reliable);
			}
		}

		/**
		 * Sends a packet to all connected clients, except the specified one.
		 */
		default void sendExceptServer(NetConnection except, Object object, boolean reliable) {
			for (NetConnection con : getConnections()) {
				if (con != except) {
					con.send(object, reliable);
				}
			}
		}

		/**
		 * Connect to a server.
		 */
		void connectClient(String ip, int port, Runnable success) throws IOException;

		/**
		 * Send an object to the server.
		 */
		void sendClient(Object object, boolean reliable);

		/**
		 * Disconnect from the server.
		 */
		void disconnectClient();

		/**
		 * Discover servers. This should run the callback regardless of whether any servers are found. Should not block.
		 * Callback should be run on the main thread.
		 *
		 * @param done is the callback that should run after discovery.
		 */
		void discoverServers(Cons<Host> callback, Runnable done);

		/**
		 * Ping a host. If an error occurred, failed() should be called with the exception. This method should block.
		 * If the port is the default mindustry port (6567), SRV records are checked too.
		 */
		void pingHost(String address, int port, Cons<Host> valid, Cons<Exception> failed);

		/**
		 * Host a server at specified port.
		 */
		void hostServer(int port) throws IOException;

		/**
		 * Return all connected users.
		 */
		Iterable<? extends NetConnection> getConnections();

		/**
		 * Close the server connection.
		 */
		void closeServer();

		/**
		 * Close all connections.
		 */
		default void dispose() {
			disconnectClient();
			closeServer();
		}

		default @Nullable ServerConnectFilter getConnectFilter() {
			return null;
		}

		/**
		 * Sets a connection filter by IP address. If the filter returns {@code false}, the connection will be closed.
		 */
		default void setConnectFilter(Server.ServerConnectFilter connectFilter) {
		}
	}
}
