package mindustry.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.*;
import mindustry.world.Block;

public class DrawFade extends DrawBlock {
	public String suffix = "-top";
	public float alpha = 0.6f, scale = 3f, x, y;
	public TextureRegion region;

	@Override
	public void draw(Building build) {
		Draw.alpha(Mathf.absin(build.totalProgress(), scale, alpha) * build.warmup());
		Draw.rect(region, build.x + x, build.y + y);
		Draw.reset();
	}

	@Override
	public void load(Block block) {
		region = Core.atlas.find(block.name + suffix);
	}
}
