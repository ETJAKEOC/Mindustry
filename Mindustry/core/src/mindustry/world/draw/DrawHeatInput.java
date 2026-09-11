package mindustry.world.draw;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatConsumer;

public class DrawHeatInput extends DrawBlock {
	public String suffix = "-heat";
	public Color heatColor = new Color(1f, 0.22f, 0.22f, 0.8f);
	public float heatPulse = 0.3f, heatPulseScl = 10f;

	public TextureRegion heat;

	public DrawHeatInput(String suffix) {
		this.suffix = suffix;
	}

	public DrawHeatInput() {
	}

	@Override
	public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
	}

	@Override
	public void draw(Building build) {

		Draw.z(Layer.blockAdditive);
		if (build instanceof HeatConsumer hc) {
			float[] side = hc.sideHeat();
			for (int i = 0; i < 4; i++) {
				if (side[i] > 0) {
					Draw.blend(Blending.additive);
					Draw.color(heatColor, side[i] / hc.heatRequirement() * (heatColor.a * (1f - heatPulse + Mathf.absin(heatPulseScl, heatPulse))));
					Draw.rect(heat, build.x, build.y, i * 90f);
					Draw.blend();
					Draw.color();
				}
			}
		}
		Draw.z(Layer.block);
	}

	@Override
	public void load(Block block) {
		heat = Core.atlas.find(block.name + suffix);
	}
}
