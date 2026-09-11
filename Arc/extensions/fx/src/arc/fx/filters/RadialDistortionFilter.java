package arc.fx.filters;

import arc.Core;
import arc.fx.FxFilter;

public final class RadialDistortionFilter extends FxFilter{
    public float zoom = 1f;
    public float distortion = 0.3f;

    public RadialDistortionFilter(){
        super(compileShader(
        Core.files.classpath("vfxshaders/screenspace.vert"),
        Core.files.classpath("vfxshaders/radial-distortion.frag")));
        rebind();
    }

    @Override
    public void setParams(){
        shader.setUniformi("u_texture0", u_texture0);
        shader.setUniformf("distortion", distortion);
        shader.setUniformf("zoom", zoom);
    }
}
