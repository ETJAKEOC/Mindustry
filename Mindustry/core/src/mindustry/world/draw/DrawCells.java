package mindustry.world.draw;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.world.Block;

public class DrawCells extends DrawBlock {
	public TextureRegion middle;

	public Color color = Color.white.cpy(), particleColorFrom = Color.black.cpy(), particleColorTo = Color.black.cpy();
	public int particles = 12;
	public float range = 4f, recurrence = 2f, radius = 1.8f, lifetime = 60f * 3f;

	@Override
	public void draw(Building build) {
		Drawf.liquid(middle, build.x, build.y, build.warmup(), color);

		if (build.warmup() > 0.001f) {
			rand.setSeed(build.id);
			for (int i = 0; i < particles; i++) {
				float offset = rand.nextFloat() * 999999f;
				float x = rand.range(range), y = rand.range(range);
				float fin = 1f - (((Time.time + offset) / lifetime) % recurrence);
				float ca = rand.random(0.1f, 1f);
				float fslope = Mathf.slope(fin);

				if (fin > 0) {
					Draw.color(particleColorFrom, particleColorTo, ca);
					Draw.alpha(build.warmup());

					Fill.circle(build.x + x, build.y + y, fslope * radius);
				}
			}
		}

		Draw.color();
	}

	@Override
	public void load(Block block) {
		middle = Core.atlas.find(block.name + "-middle");
	}
}
