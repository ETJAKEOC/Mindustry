package mindustry.world.blocks.logic;

import static mindustry.Vars.state;

import arc.audio.Sound;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.annotations.Annotations.Load;
import mindustry.gen.*;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

public class SwitchBlock extends Block {
	public Sound clickSound = Sounds.click;

	public @Load("@-on") TextureRegion onRegion;

	public SwitchBlock(String name) {
		super(name);
		configurable = true;
		update = true;
		drawDisabled = false;
		autoResetEnabled = false;
		configureSound = Sounds.none;
		group = BlockGroup.logic;
		envEnabled = Env.any;

		config(Boolean.class, (SwitchBuild entity, Boolean b) -> entity.enabled = b);
	}

	public boolean accessible() {
		return !privileged || state.rules.editor || state.rules.allowEditWorldProcessors;
	}

	@Override
	public boolean canBreak(Tile tile) {
		return accessible();
	}

	public class SwitchBuild extends Building {
		@Override
		public void damage(float damage) {
			if (privileged) return;
			super.damage(damage);
		}

		@Override
		public boolean canPickup() {
			return !privileged;
		}

		@Override
		public boolean collide(Bullet other) {
			return !privileged;
		}

		@Override
		public boolean configTapped() {
			configure(!enabled);
			clickSound.at(this);
			return false;
		}

		@Override
		public void draw() {
			super.draw();

			if (enabled) {
				Draw.rect(onRegion, x, y);
			}
		}

		@Override
		public Boolean config() {
			return enabled;
		}

		@Override
		public byte version() {
			return 1;
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			if (revision == 1) {
				enabled = read.bool();
			}
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.bool(enabled);
		}
	}
}
