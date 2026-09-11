package mindustry.net;

import java.io.DataInputStream;

import arc.util.io.Reads;
import arc.util.io.ReusableByteInStream;
import arc.util.io.Writes;

public abstract class Packet {
	/**
	 * Does not get handled unless client is connected.
	 */
	public static final int priorityLow = 0;
	/**
	 * Gets put in a queue and processed if not connected.
	 */
	public static final int priorityNormal = 1;
	/**
	 * Gets handled immediately, regardless of connection status.
	 */
	public static final int priorityHigh = 2;

	//these are constants because I don't want to bother making an enum to mirror the annotation enum
	//internally used by generated code
	protected static final byte[] NODATA = {};
	protected static final ReusableByteInStream BAIS = new ReusableByteInStream();
	protected static final Reads READ = new Reads(new DataInputStream(BAIS));

	public void read(Reads read) {
	}

	public void write(Writes write) {
	}

	public boolean allow(boolean server) {
		return true;
	}

	public void read(Reads read, int length) {
		read(read);
	}

	public void handled() {
	}

	public int getPriority() {
		return priorityNormal;
	}

	public void handleClient() {
	}

	public void handleServer(NetConnection con) {
	}
}
