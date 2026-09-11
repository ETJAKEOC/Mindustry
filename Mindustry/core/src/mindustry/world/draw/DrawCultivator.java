package mindustry.world.draw;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.world.Block;

public class DrawCultivator extends DrawBlock {
	public Color plantColor = Color.valueOf("5541b1");
	public Color plantColorLight = Color.valueOf("7457ce");
	public Color bottomColor = Color.valueOf("474747");

	public int bubbles = 12, sides = 8;
	public float strokeMin = 0.2f, spread = 3f, timeScl = 70f;
	public float recurrence = 6f, radius = 3f, x, y;

	public TextureRegion middle;

	@Override
	public void draw(Building build) {
		Drawf.liquid(middle, build.x, build.y, build.warmup(), plantColor);

		Draw.color(bottomColor, plantColorLight, build.warmup());

		rand.setSeed(build.pos());
		for (int i = 0; i < bubbles; i++) {
			float rx = rand.range(spread), ry = rand.range(spread);
			float life = 1f - ((Time.time / timeScl + rand.random(recurrence)) % recurrence);

			if (life > 0) {
				Lines.stroke(build.warmup() * (life + strokeMin));
				Lines.poly(build.x + rx + x, build.y + ry + y, sides, (1f - life) * radius);
			}
		}

		Draw.color();
	}

	@Override
	public void load(Block block) {
		middle = Core.atlas.find(block.name + "-middle");
	}
}
