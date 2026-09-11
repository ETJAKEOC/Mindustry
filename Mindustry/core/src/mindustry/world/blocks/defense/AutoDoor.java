package mindustry.world.blocks.defense;

import static mindustry.Vars.net;
import static mindustry.Vars.pathfinder;
import static mindustry.Vars.tilesize;

import arc.audio.Sound;
import arc.func.Boolf;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.annotations.Annotations.Load;
import mindustry.annotations.Annotations.Loc;
import mindustry.annotations.Annotations.Remote;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.*;
import mindustry.logic.LAccess;
import mindustry.world.Tile;

public class AutoDoor extends Wall {
	protected final static Rect rect = new Rect();
	protected final static Seq<Unit> units = new Seq<>();
	protected final static Boolf<Unit> groundCheck = u -> u.isGrounded() && !u.type.allowLegStep;

	public final int timerToggle = timers++;

	public float checkInterval = 20f;
	public Effect openfx = Fx.dooropen;
	public Effect closefx = Fx.doorclose;
	public Sound doorSound = Sounds.door;
	public @Load("@-open") TextureRegion openRegion;
	public float triggerMargin = 12f;

	public AutoDoor(String name) {
		super(name);
		solid = false;
		solidifes = true;
		update = true;
		teamPassable = true;

		noUpdateDisabled = true;
		drawDisabled = true;
	}

	@Remote(called = Loc.server)
	public static void autoDoorToggle(Tile tile, boolean open) {
		if (tile == null || !(tile.build instanceof AutoDoorBuild build)) return;
		build.setOpen(open);
	}

	public class AutoDoorBuild extends Building {
		public boolean open = false;

		public AutoDoorBuild() {
			//make sure it is staggered
			timer.reset(timerToggle, Mathf.random(checkInterval));
		}

		@Override
		public void updateTile() {
			if (timer(timerToggle, checkInterval) && !net.client()) {
				units.clear();
				team.data().tree().intersect(rect.setSize(size * tilesize + triggerMargin * 2f).setCenter(x, y), units);
				boolean shouldOpen = units.contains(groundCheck);

				if (open != shouldOpen) {
					Call.autoDoorToggle(tile, shouldOpen);
				}
			}
		}

		@Override
		public double sense(LAccess sensor) {
			if (sensor == LAccess.enabled) return open ? 1 : 0;
			return super.sense(sensor);
		}

		public void setOpen(boolean open) {
			this.open = open;
			pathfinder.updateTile(tile);
			if (wasVisible) {
				(!open ? closefx : openfx).at(this, size);
				doorSound.at(this);
			}
			recache();
		}

		@Override
		public void draw() {
			Draw.rect(open ? openRegion : region, x, y);
		}

		@Override
		public boolean checkSolid() {
			return !open;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.bool(open);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			open = read.bool();
		}
	}

}
