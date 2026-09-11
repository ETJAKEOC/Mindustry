package arc.fx.filters;

import arc.Core;
import arc.fx.FxFilter;
import arc.math.geom.Vec2;

public class OldTvFilter extends FxFilter {
	private final Vec2 resolution = new Vec2();

	public OldTvFilter() {
		super(compileShader(
				Core.files.classpath("vfxshaders/screenspace.vert"),
				Core.files.classpath("vfxshaders/old-tv.frag")));
		rebind();
	}

	@Override
	public void resize(int width, int height) {
		this.resolution.set(width, height);
		rebind();
	}

	@Override
	public void setParams() {
		shader.setUniformi("u_texture0", u_texture0);
		shader.setUniformf("u_resolution", resolution);
		shader.setUniformf("u_time", time);
	}
}
