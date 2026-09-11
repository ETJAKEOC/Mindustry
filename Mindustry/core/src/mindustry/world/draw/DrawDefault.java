package mindustry.world.draw;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.world.Block;

public class DrawDefault extends DrawBlock {

	@Override
	public void draw(Building build) {
		Draw.rect(build.block.region, build.x, build.y, build.drawrot());
	}

	@Override
	public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
		block.drawDefaultPlanRegion(plan, list);
	}

	@Override
	public TextureRegion[] icons(Block block) {
		return new TextureRegion[]{block.region};
	}
}
