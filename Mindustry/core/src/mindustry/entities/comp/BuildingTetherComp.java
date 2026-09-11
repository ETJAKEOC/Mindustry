package mindustry.entities.comp;

import arc.util.Nullable;
import mindustry.annotations.Annotations.Component;
import mindustry.annotations.Annotations.Import;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.UnitType;

/** A unit that depends on a building's existence; if that building is removed, it despawns. */
@Component
abstract class BuildingTetherComp implements Unitc{
    @Import UnitType type;
    @Import Team team;

    public @Nullable Building building;

    @Override
    public void update(){
        if(building == null || !building.isValid() || building.team != team){
            Call.unitDespawn(self());
        }
    }
}
