package mindustry.world.blocks.production;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.annotations.Annotations.Load;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.meta.BlockStatus;

/**
 * Incinerator that accepts only items and optionally requires a liquid, e.g. slag.
 */
public class ItemIncinerator extends Block {
	public Effect effect = Fx.incinerateSlag;
	public float effectChance = 0.2f;

	public @Load("@-liquid") TextureRegion liquidRegion;
	public @Load("@-top") TextureRegion topRegion;

	public ItemIncinerator(String name) {
		super(name);
		update = true;
		solid = true;
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region, topRegion};
	}

	public class ItemIncineratorBuild extends Building {

		@Override
		public void updateTile() {
		}

		@Override
		public BlockStatus status() {
			return !enabled ? BlockStatus.logicDisable : efficiency > 0 ? BlockStatus.active : BlockStatus.noInput;
		}

		@Override
		public void draw() {
			super.draw();

			if (liquidRegion.found()) {
				Drawf.liquid(liquidRegion, x, y, liquids.currentAmount() / liquidCapacity, liquids.current().color);
			}
			if (topRegion.found()) {
				Draw.rect(topRegion, x, y);
			}
		}

		@Override
		public void handleItem(Building source, Item item) {
			if (Mathf.chance(effectChance)) {
				effect.at(x, y);
			}
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			return efficiency > 0;
		}
	}
}
