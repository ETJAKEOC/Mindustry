package mindustry.world.draw;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.world.Block;

public class DrawLiquidRegion extends DrawBlock {
	public Liquid drawLiquid;
	public TextureRegion liquid;
	public String suffix = "-liquid";
	public float alpha = 1f;

	public DrawLiquidRegion(Liquid drawLiquid) {
		this.drawLiquid = drawLiquid;
	}

	public DrawLiquidRegion() {
	}

	@Override
	public void draw(Building build) {
		Liquid drawn = drawLiquid != null ? drawLiquid : build.liquids.current();
		Drawf.liquid(liquid, build.x, build.y,
				build.liquids.get(drawn) / build.block.liquidCapacity * alpha,
				drawn.color
		);
	}

	@Override
	public void load(Block block) {
		if (!block.hasLiquids) {
			throw new RuntimeException("Block '" + block + "' has a DrawLiquidRegion, but hasLiquids is false! Make sure it is true.");
		}

		liquid = Core.atlas.find(block.name + suffix);
	}
}
