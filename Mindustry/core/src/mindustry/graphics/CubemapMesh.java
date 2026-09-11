package mindustry.graphics;

import arc.Core;
import arc.graphics.Cubemap;
import arc.graphics.Gl;
import arc.graphics.Mesh;
import arc.graphics.Texture.TextureFilter;
import arc.graphics.VertexAttribute;
import arc.graphics.gl.Shader;
import arc.math.geom.Mat3D;
import arc.util.Disposable;

public class CubemapMesh implements Disposable{
    private static final float[] vertices = {
    -1.0f,  1.0f, -1.0f,
    -1.0f, -1.0f, -1.0f,
    1.0f, -1.0f, -1.0f,
    1.0f, -1.0f, -1.0f,
    1.0f,  1.0f, -1.0f,
    -1.0f,  1.0f, -1.0f,

    -1.0f, -1.0f,  1.0f,
    -1.0f, -1.0f, -1.0f,
    -1.0f,  1.0f, -1.0f,
    -1.0f,  1.0f, -1.0f,
    -1.0f,  1.0f,  1.0f,
    -1.0f, -1.0f,  1.0f,

    1.0f, -1.0f, -1.0f,
    1.0f, -1.0f,  1.0f,
    1.0f,  1.0f,  1.0f,
    1.0f,  1.0f,  1.0f,
    1.0f,  1.0f, -1.0f,
    1.0f, -1.0f, -1.0f,

    -1.0f, -1.0f,  1.0f,
    -1.0f,  1.0f,  1.0f,
    1.0f,  1.0f,  1.0f,
    1.0f,  1.0f,  1.0f,
    1.0f, -1.0f,  1.0f,
    -1.0f, -1.0f,  1.0f,

    -1.0f,  1.0f, -1.0f,
    1.0f,  1.0f, -1.0f,
    1.0f,  1.0f,  1.0f,
    1.0f,  1.0f,  1.0f,
    -1.0f,  1.0f,  1.0f,
    -1.0f,  1.0f, -1.0f,

    -1.0f, -1.0f, -1.0f,
    -1.0f, -1.0f,  1.0f,
    1.0f, -1.0f, -1.0f,
    1.0f, -1.0f, -1.0f,
    -1.0f, -1.0f,  1.0f,
    1.0f, -1.0f,  1.0f
    };

    private final Mesh mesh;
    private final Shader shader;
    private Cubemap map;

    public CubemapMesh(Cubemap map){
        this.map = map;
        this.map.setFilter(TextureFilter.linear);
        this.mesh = new Mesh(true, vertices.length, 0, VertexAttribute.position3);
        mesh.getVerticesBuffer().limit(vertices.length);
        mesh.getVerticesBuffer().put(vertices, 0, vertices.length);

        shader = new Shader(Core.files.internal("shaders/cubemap.vert"), Core.files.internal("shaders/cubemap.frag"));
    }

    public void setCubemap(Cubemap map){
        this.map = map;
    }

    public void render(Mat3D projection){
        map.bind();
        shader.bind();
        shader.setUniformi("u_cubemap", 0);
        shader.setUniformMatrix4("u_proj", projection.val);
        mesh.render(shader, Gl.triangles);
    }

    @Override
    public void dispose(){
        mesh.dispose();
        map.dispose();
    }
}
