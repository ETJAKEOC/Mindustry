package mindustry.entities.comp;

import mindustry.annotations.Annotations.Component;
import mindustry.annotations.Annotations.Import;
import mindustry.annotations.Annotations.Replace;
import mindustry.entities.EntityCollisions;
import mindustry.entities.EntityCollisions.SolidPred;
import mindustry.gen.*;

@Component
abstract class ElevationMoveComp implements Velc, Posc, Hitboxc, Unitc {
	@Import
	float x, y;

	@Replace
	@Override
	public SolidPred solidity() {
		return isFlying() || ignoreSolids() ? null : EntityCollisions::solid;
	}

}
