package mindustry.world.draw;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.blocks.production.HeatCrafter.HeatCrafterBuild;

public class DrawHeatRegion extends DrawBlock {
	public Color color = new Color(1f, 0.22f, 0.22f, 0.8f);
	public float pulse = 0.3f, pulseScl = 10f;
	public float layer = Layer.blockAdditive;

	public TextureRegion heat;
	public String suffix = "-glow";

	public DrawHeatRegion(float layer) {
		this.layer = layer;
	}

	public DrawHeatRegion(String suffix) {
		this.suffix = suffix;
	}

	public DrawHeatRegion() {
	}

	@Override
	public void draw(Building build) {
		Draw.z(Layer.blockAdditive);
		if (build instanceof HeatCrafterBuild hc && hc.heat > 0) {

			float z = Draw.z();
			if (layer > 0) Draw.z(layer);
			Draw.blend(Blending.additive);
			Draw.color(color, Mathf.clamp(hc.heat / hc.heatRequirement()) * (color.a * (1f - pulse + Mathf.absin(pulseScl, pulse))));
			Draw.rect(heat, build.x, build.y);
			Draw.blend();
			Draw.color();
			Draw.z(z);
		}
	}

	@Override
	public void load(Block block) {
		heat = Core.atlas.find(block.name + suffix);
	}
}
