package mindustry.graphics.g3d;

import arc.math.geom.Mat3D;
import arc.util.Disposable;

public interface GenericMesh extends Disposable {
	void render(PlanetParams params, Mat3D projection, Mat3D transform);
}
