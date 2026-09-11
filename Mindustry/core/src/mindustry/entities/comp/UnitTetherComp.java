package mindustry.entities.comp;

import arc.util.Nullable;
import mindustry.annotations.Annotations.Component;
import mindustry.annotations.Annotations.Import;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.UnitType;

/**
 * A unit that depends on a units's existence; if that unit is removed, it despawns.
 */
@Component
abstract class UnitTetherComp implements Unitc {
	//spawner unit cannot be read directly for technical reasons.
	public transient @Nullable Unit spawner;
	public int spawnerUnitId = -1;
	@Import
	UnitType type;
	@Import
	Team team;

	@Override
	public void afterRead() {
		if (spawnerUnitId != -1) spawner = Groups.unit.getByID(spawnerUnitId);
		spawnerUnitId = -1;
	}

	@Override
	public void afterSync() {
		if (spawnerUnitId != -1) spawner = Groups.unit.getByID(spawnerUnitId);
		spawnerUnitId = -1;
	}

	@Override
	public void update() {
		if (spawner == null || !spawner.isValid() || spawner.team != team) {
			Call.unitDespawn(self());
		} else {
			spawnerUnitId = spawner.id;
		}
	}
}
