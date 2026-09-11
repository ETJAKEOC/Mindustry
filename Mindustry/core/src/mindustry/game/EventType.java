package mindustry.game;

import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.core.GameState.State;
import mindustry.core.NetServer;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.*;
import mindustry.graphics.MultiPacker;
import mindustry.mod.data.DataAsset;
import mindustry.net.Host;
import mindustry.net.NetConnection;
import mindustry.net.Packets.AdminAction;
import mindustry.net.Packets.ConnectPacket;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Sector;
import mindustry.ui.builder.MenuResult;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;

public class EventType {

	//events that occur very often
	public enum Trigger {
		shock,
		cannotUpgrade,
		fireCreate,
		openConsole,
		blastFreeze,
		impactPower,
		blastGenerator,
		shockwaveTowerUse,
		forceProjectorBreak,
		thoriumReactorOverheat,
		neoplasmReact,
		fireExtinguish,
		acceleratorUse,
		newGame,
		tutorialComplete,
		flameAmmo,
		resupplyTurret,
		turretCool,
		enablePixelation,
		exclusionDeath,
		suicideBomb,
		openWiki,
		teamCoreDamage,
		socketConfigChanged,
		update,
		beforeGameUpdate,
		afterGameUpdate,
		unitCommandChange,
		unitCommandPosition,
		unitCommandAttack,
		unitCommandBoost,
		importMod,
		draw,
		drawOver,
		preDraw,
		postDraw,
		uiDrawBegin,
		uiDrawEnd,
		//before/after bloom used, skybox or planets drawn
		universeDrawBegin,
		//skybox drawn and bloom is enabled - use Vars.renderer.planets
		universeDraw,
		//planets drawn and bloom disabled
		universeDrawEnd
	}

	public static class WinEvent {
	}

	public static class LoseEvent {
	}

	public static class ResizeEvent {
	}

	public static class MapMakeEvent {
	}

	public static class MapPublishEvent {
	}

	public static class SaveWriteEvent {
	}

	public static class ClientCreateEvent {
	}

	public static class ServerLoadEvent {
	}

	public static class DisposeEvent {
	}

	public static class PlayEvent {
	}

	public static class ResetEvent {
	}

	public static class HostEvent {
	}

	public static class WaveEvent {
	}

	public static class TurnEvent {
	}

	/**
	 * Called when the player places a line, mobile or desktop.
	 */
	public static class LineConfirmEvent {
	}

	/**
	 * Called when the player opens info for a specific block.
	 */
	public static class BlockInfoEvent {
	}

	/**
	 * Called *after* all content has been initialized.
	 */
	public static class ContentInitEvent {
	}

	/**
	 * Called *after* all content has been added to the atlas, but before its pixmaps are disposed.
	 */
		public record AtlasPackEvent(MultiPacker multiPacker) {
	}

	/**
	 * Called *after* all mod content has been loaded, but before it has been initialized.
	 */
	public static class ModContentLoadEvent {
	}

	/**
	 * Called when the client game is first loaded.
	 */
	public static class ClientLoadEvent {
	}

	/**
	 * Called after SoundControl registers its music.
	 */
	public static class MusicRegisterEvent {
	}

	/**
	 * Called *after* all the modded files have been added into Vars.tree
	 */
	public static class FileTreeInitEvent {
	}

	/**
	 * Called when a game begins and the world tiles are loaded, just set `generating = false`. Entities are not yet loaded at this stage.
	 */
	public static class WorldLoadEvent {
	}

	/**
	 * Called when the world begin to load, just set `generating = true`.
	 */
	public static class WorldLoadBeginEvent {
	}

	/**
	 * Called when a game begins and the world tiles are initiated. About to updates tile proximity and sets up physics for the world(Before WorldLoadEvent)
	 */
	public static class WorldLoadEndEvent {
	}

	/**
	 * Called when a save loads custom data patches. {@link #assets} can be modified in the event handler. The array may be empty.
	 */
		public record DataPatchLoadEvent(Seq<DataAsset> assets) {
	}

	/**
	 * Called when a new texture is received from the server via {@link NetServer#sendTexture}.
	 */
		public record TextureStreamEvent(String name) {
	}

	public record SaveLoadEvent(boolean isMap) {
	}

	/**
	 * Called when a sector is destroyed by waves when you're not there.
	 */
		public record SectorLoseEvent(Sector sector) {
	}

	/**
	 * Called when a sector is destroyed by waves when you're not there.
	 */
		public record SectorInvasionEvent(Sector sector) {
	}

	public record LaunchItemEvent(ItemStack stack) {
	}

	public record SectorLaunchEvent(Sector sector) {
	}

	public record SectorLaunchLoadoutEvent(Sector sector, Sector from, Schematic loadout) {
	}

	public record SchematicCreateEvent(Schematic schematic) {
	}

	public record ClientPreConnectEvent(Host host) {
	}

	public record ClientServerConnectEvent(String ip, int port) {
	}

	/**
	 * Consider using Menus.registerMenu instead.
	 */
		public record MenuOptionChooseEvent(Player player, int menuId, int option) {
	}

	/**
	 * Consider using Menus.registerMenu instead.
	 */
		public record MenuBuilderOptionChooseEvent(Player player, int menuId, MenuResult result) {
	}

	/**
	 * Consider using Menus.registerTextInput instead.
	 */
		public record TextInputEvent(Player player, int textInputId, @Nullable String text) {
			public TextInputEvent(Player player, int textInputId, String text) {
				this.player = player;
				this.textInputId = textInputId;
				this.text = text;
			}
		}

	public record PlayerChatEvent(Player player, String message) {
	}

	/**
	 * Called when the client sends a chat message. This only fires clientside!
	 */
		public record ClientChatEvent(String message) {
	}

	/**
	 * Called when a sector is conquered, e.g. a boss or base is defeated.
	 */
		public record SectorCaptureEvent(Sector sector, boolean initialCapture) {
	}

	/**
	 * Called when the player withdraws items from a block.
	 */
		public record WithdrawEvent(Building tile, Player player, Item item, int amount) {
	}

	/**
	 * Called when a player deposits items to a block.
	 */
		public record DepositEvent(Building tile, Player player, Item item, int amount) {
	}

	/**
	 * Called when a specific building has its configuration changed.
	 */
		public record ConfigEvent(Building tile, @Nullable Player player, Object value) {
	}

	/**
	 * Called when a player taps any tile.
	 */
		public record TapEvent(Player player, Tile tile) {
	}

	public record PickupEvent(Unit carrier, @Nullable Unit unit, @Nullable Building build) {
			public PickupEvent(Unit carrier, Unit unit) {
				this(carrier, unit, null);
			}

			public PickupEvent(Unit carrier, Building build) {
				this(carrier, null, build);
			}
		}

	public record PayloadDropEvent(Unit carrier, @Nullable Unit unit, @Nullable Building build) {
			public PayloadDropEvent(Unit carrier, Unit unit) {
				this(carrier, unit, null);
			}

			public PayloadDropEvent(Unit carrier, Building build) {
				this(carrier, null, build);
			}
		}

	public record UnitControlEvent(Player player, @Nullable Unit unit) {
	}

	public record BuildingCommandEvent(Player player, Building building, Vec2 position) {
	}

	public record GameOverEvent(Team winner) {
	}

	/**
	 * Called when a bullet damages a building. May not be called for all damage events!
	 * This event is re-used! Never do anything to re-raise this event in the listener.
	 *
	 */
	public static class BuildDamageEvent {
		public Building build;
		public Bullet source;

		public BuildDamageEvent set(Building build, Bullet source) {
			this.build = build;
			this.source = source;
			return this;
		}
	}

	/**
	 * Called when a bullet has been created.
	 * WARNING! This event is special: its instance is reused! Do not cache or use with a timer.
	 * Do not modify any tiles inside listeners that use this tile.
	 *
	 */
	public static class BulletCreateEvent {
		public Bullet bullet;

		public BulletCreateEvent set(Bullet bullet) {
			this.bullet = bullet;
			return this;
		}
	}

	/**
	 * Called *before* a tile has changed.
	 * WARNING! This event is special: its instance is reused! Do not cache or use with a timer.
	 * Do not modify any tiles inside listeners that use this tile.
	 *
	 */
	public static class TilePreChangeEvent {
		public Tile tile;

		public TilePreChangeEvent set(Tile tile) {
			this.tile = tile;
			return this;
		}
	}

	/**
	 * Called *after* a tile has changed.
	 * WARNING! This event is special: its instance is reused! Do not cache or use with a timer.
	 * Do not modify any tiles inside listener code.
	 *
	 */
	public static class TileChangeEvent {
		public Tile tile;

		public TileChangeEvent set(Tile tile) {
			this.tile = tile;
			return this;
		}
	}

	/**
	 * Called when a tile changes its floor. Do not cache or use with a timer.
	 * Do not modify any tiles inside listener code.
	 *
	 */
	public static class TileFloorChangeEvent {
		public Tile tile;
		public Floor previous, floor;

		public TileFloorChangeEvent set(Tile tile, Floor previous, Floor floor) {
			this.tile = tile;
			this.previous = previous;
			this.floor = floor;
			return this;
		}
	}

	/**
	 * Called when a tile changes its overlay. Do not cache or use with a timer.
	 * Do not modify any tiles inside listener code.
	 *
	 */
	public static class TileOverlayChangeEvent {
		public Tile tile;
		public Floor previous, overlay;

		public TileOverlayChangeEvent set(Tile tile, Floor previous, Floor overlay) {
			this.tile = tile;
			this.previous = previous;
			this.overlay = overlay;
			return this;
		}
	}

	/**
	 * Called after a building's team changes.
	 * Event object is reused, do not nest!
	 *
	 */
	public static class BuildTeamChangeEvent {
		public Team previous;
		public Building build;

		public BuildTeamChangeEvent set(Team previous, Building build) {
			this.build = build;
			this.previous = previous;
			return this;
		}
	}

	/**
	 * Called when a core block is placed/removed or its team is changed.
	 */
	public static class CoreChangeEvent {
		public CoreBuild core;

		public CoreChangeEvent(CoreBuild core) {
			this.core = core;
		}
	}

	public record StateChangeEvent(State from, State to) {
	}

	public record UnlockEvent(UnlockableContent content) {
	}

	public record ResearchEvent(UnlockableContent content) {
	}

	/**
	 * Called when all rules of the current map are loaded.
	 */
		public record RulesLoadEvent(Rules rules, boolean fromSave) {
			public RulesLoadEvent(Rules rules) {
				this(rules, false);
			}

	}

	/**
	 * Called when block building begins by placing down the ConstructBlock.
	 * The tile's block will nearly always be a ConstructBlock.
	 */
		public record BlockBuildBeginEvent(Tile tile, Team team, @Nullable Unit unit,
		                                   boolean breaking) {
			public BlockBuildBeginEvent(Tile tile, Team team, Unit unit, boolean breaking) {
				this.tile = tile;
				this.team = team;
				this.unit = unit;
				this.breaking = breaking;
			}
		}

	public record BlockBuildEndEvent(Tile tile, Team team, @Nullable Unit unit, boolean breaking,
	                                 @Nullable Object config) {
			public BlockBuildEndEvent(Tile tile, @Nullable Unit unit, Team team, boolean breaking, @Nullable Object config) {
				this(tile, team, unit, breaking, config);
			}
		}

	public record BuildRotateEvent(Building build, @Nullable Unit unit, int previous) {
	}

	/**
	 * Called when a player or drone begins building something.
	 * This does not necessarily happen when a new ConstructBlock is created.
	 */
		public record BuildSelectEvent(Tile tile, Team team, Unit builder, boolean breaking) {
	}

	/**
	 * Called right before a block is destroyed.
	 * The building of the tile in this event cannot be null when this happens.
	 */
		public record BlockDestroyEvent(Tile tile) {
	}

	/**
	 * Called when a neoplasia (or other pressure-based block, from mods) reactor explodes due to pressure.
	 */
		public record GeneratorPressureExplodeEvent(Building build) {
	}

	/**
	 * Called when a building is directly killed by a bullet. May not fire in all circumstances.
	 */
	public static class BuildingBulletDestroyEvent {
		public Building build;
		public Bullet bullet;

		public BuildingBulletDestroyEvent(Building build, Bullet bullet) {
			this.build = build;
			this.bullet = bullet;
		}

		public BuildingBulletDestroyEvent() {
		}
	}

	public record UnitDestroyEvent(Unit unit) {
	}

	/**
	 * Called when a unit is directly killed by a bullet. May not fire in all circumstances.
	 */
	public static class UnitBulletDestroyEvent {
		public Unit unit;
		public Bullet bullet;

		public UnitBulletDestroyEvent(Unit unit, Bullet bullet) {
			this.unit = unit;
			this.bullet = bullet;
		}

		public UnitBulletDestroyEvent() {
		}
	}

	/**
	 * Called when a unit is hit by a bullet.
	 * This event is REUSED, do not nest invocations of it (e.g. damage units in its event handler)
	 *
	 */
	public static class UnitDamageEvent {
		public Unit unit;
		public Bullet bullet;

		public UnitDamageEvent set(Unit unit, Bullet bullet) {
			this.unit = unit;
			this.bullet = bullet;
			return this;
		}
	}

	public record UnitDrownEvent(Unit unit) {
	}

	/**
	 * Called when a unit is created in a reconstructor, factory or other unit.
	 */
		public record UnitCreateEvent(Unit unit, @Nullable Building spawner,
		                              @Nullable Unit spawnerUnit) {
			public UnitCreateEvent(Unit unit, Building spawner, Unit spawnerUnit) {
				this.unit = unit;
				this.spawner = spawner;
				this.spawnerUnit = spawnerUnit;
			}

			public UnitCreateEvent(Unit unit, Building spawner) {
				this(unit, spawner, null);
			}
		}

	/**
	 * Called when a unit is spawned by wave.
	 */
		public record UnitSpawnEvent(Unit unit) {
	}

	/**
	 * Called when a unit is dumped from any payload block.
	 */
		public record UnitUnloadEvent(Unit unit) {
	}

	public record UnitChangeEvent(Player player, Unit unit) {
	}

	/**
	 * Called when a connection is established to a client.
	 */
		public record ConnectionEvent(NetConnection connection) {
	}

	/**
	 * Called when a player sends a connection packet.
	 */
		public record ConnectPacketEvent(NetConnection connection, ConnectPacket packet) {
	}

	/**
	 * Called after player confirmed it has received world data and is ready to play.
	 * Note that if this is the first world receival, then player.con.hasConnected is false.
	 */
		public record PlayerConnectionConfirmed(Player player) {
	}

	/**
	 * Called after connecting; when a player receives world data and is ready to play. Fired only once, after initial connection.
	 */
		public record PlayerJoin(Player player) {
	}

	/**
	 * Called when a player connects, but has not joined the game yet.
	 */
		public record PlayerConnect(Player player) {
	}

	/**
	 * Called before a player leaves the game.
	 */
		public record PlayerLeave(Player player) {
	}

	public record PlayerBanEvent(@Nullable Player player, String uuid) {
			public PlayerBanEvent(Player player, String uuid) {
				this.player = player;
				this.uuid = uuid;
			}
		}

	public record PlayerUnbanEvent(@Nullable Player player, String uuid) {
			public PlayerUnbanEvent(Player player, String uuid) {
				this.player = player;
				this.uuid = uuid;
			}
		}

	public record PlayerIpBanEvent(String ip) {
	}

	public record PlayerIpUnbanEvent(String ip) {
	}

	public record AdminRequestEvent(Player player, @Nullable Player other, AdminAction action) {
			public AdminRequestEvent(Player player, Player other, AdminAction action) {
				this.player = player;
				this.other = other;
				this.action = action;
			}
		}
}
