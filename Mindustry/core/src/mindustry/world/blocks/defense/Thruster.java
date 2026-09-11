package mindustry.world.blocks.defense;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.annotations.Annotations.Load;
import mindustry.entities.units.BuildPlan;

public class Thruster extends Wall {
	public @Load("@-top") TextureRegion topRegion;

	public Thruster(String name) {
		super(name);
		rotate = true;
		quickRotate = false;
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		Draw.rect(region, plan.drawx(), plan.drawy());
		Draw.rect(topRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region, topRegion};
	}

	public class ThrusterBuild extends WallBuild {

		@Override
		public void drawCached() {
			Draw.rect(block.region, x, y);
			Draw.rect(topRegion, x, y, rotdeg());
		}
	}
}
