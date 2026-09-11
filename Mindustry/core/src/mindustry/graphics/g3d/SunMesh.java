package mindustry.graphics.g3d;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.noise.Simplex;
import mindustry.graphics.Shaders;
import mindustry.type.Planet;

public class SunMesh extends HexMesh {

	public SunMesh(Planet planet, int divisions, double octaves, double persistence, double scl, double pow, double mag, float colorScale, Color... colors) {
		super(planet, new HexMesher() {

			@Override
			public float getHeight(Vec3 position) {
				return 0;
			}

			@Override
			public void getColor(Vec3 position, Color out) {
				double height = Math.pow(Simplex.noise3d(0, octaves, persistence, scl, position.x, position.y, position.z), pow) * mag;
				out.set(colors[Mathf.clamp((int) (height * colors.length), 0, colors.length - 1)]).mul(colorScale);
			}
		}, divisions, Shaders.unlit);
	}
}
