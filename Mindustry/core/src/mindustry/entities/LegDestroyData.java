package mindustry.entities;

import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Vec2;

public class LegDestroyData {
	public Vec2 a, b;
	public TextureRegion region;

	public LegDestroyData(Vec2 a, Vec2 b, TextureRegion region) {
		this.a = a;
		this.b = b;
		this.region = region;
	}
}
