package mindustry.world.blocks.units;

import static mindustry.Vars.content;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.annotations.Annotations.Load;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.meta.BlockFlag;

public class UnitCargoUnloadPoint extends Block {
	/**
	 * If a block is full for this amount of time, it will not be flown to anymore.
	 */
	public float staleTimeDuration = 60f * 6f;

	public @Load("@-top") TextureRegion topRegion;

	public UnitCargoUnloadPoint(String name) {
		super(name);
		update = solid = true;
		hasItems = true;
		configurable = true;
		saveConfig = true;
		clearOnDoubleTap = true;
		flags = EnumSet.of(BlockFlag.unitCargoUnloadPoint);

		config(Item.class, (UnitCargoUnloadPointBuild build, Item item) -> build.item = item);
		configClear((UnitCargoUnloadPointBuild build) -> build.item = null);
	}

	public class UnitCargoUnloadPointBuild extends Building {
		public Item item;
		public float staleTimer;
		public boolean stale;

		@Override
		public void draw() {
			super.draw();

			if (item != null) {
				Draw.color(item.color);
				Draw.rect(topRegion, x, y);
				Draw.color();
			}
		}

		@Override
		public void drawSelect() {
			super.drawSelect();
			drawItemSelection(item);
		}

		@Override
		public void updateTile() {
			super.updateTile();

			if (items.total() < itemCapacity) {
				staleTimer = 0f;
				stale = false;
			}

			if (dumpAccumulate()) {
				staleTimer = 0f;
				stale = false;
			} else if (items.total() >= itemCapacity && (staleTimer += Time.delta) >= staleTimeDuration) {
				stale = true;
			}
		}

		@Override
		public int acceptStack(Item item, int amount, Teamc source) {
			return Math.min(itemCapacity - items.total(), amount);
		}

		@Override
		public void buildConfiguration(Table table) {
			ItemSelection.buildTable(UnitCargoUnloadPoint.this, table, content.items(), () -> item, this::configure);
		}

		@Override
		public Object config() {
			return item;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.s(item == null ? -1 : item.id);
			write.bool(stale);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			item = Vars.content.item(read.s());
			stale = read.bool();
		}
	}
}
