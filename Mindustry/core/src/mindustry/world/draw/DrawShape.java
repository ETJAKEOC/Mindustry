package mindustry.world.draw;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import mindustry.gen.*;
import mindustry.graphics.Pal;

public class DrawShape extends DrawBlock {
	public Color color = Pal.accent.cpy();
	public int sides = 4;
	public float radius = 2f, timeScl = 1f, layer = -1f, x, y;
	public boolean useWarmupRadius = false;

	@Override
	public void draw(Building build) {
		float pz = Draw.z();
		if (layer > 0) Draw.z(layer);

		Draw.color(color);
		Fill.poly(build.x + x, build.y + y, sides, useWarmupRadius ? radius * build.warmup() : radius, build.totalProgress() * timeScl);

		Draw.reset();
		Draw.z(pz);
	}
}
