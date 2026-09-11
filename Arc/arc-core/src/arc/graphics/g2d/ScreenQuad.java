package arc.graphics.g2d;

import arc.graphics.Gl;
import arc.graphics.Mesh;
import arc.graphics.VertexAttribute;
import arc.graphics.gl.Shader;
import arc.util.Disposable;

public class ScreenQuad implements Disposable {
	public final Mesh mesh;

	public ScreenQuad() {
		mesh = new Mesh(true, 4, 0, VertexAttribute.position, VertexAttribute.texCoords);
		mesh.setVertices(new float[]{-1f, -1f, 0f, 0f, 1f, -1f, 1f, 0f, 1f, 1f, 1f, 1f, -1f, 1f, 0f, 1f});
	}

	public void render(Shader shader) {
		mesh.render(shader, Gl.triangleFan, 0, 4);
	}

	@Override
	public void dispose() {
		mesh.dispose();
	}
}
