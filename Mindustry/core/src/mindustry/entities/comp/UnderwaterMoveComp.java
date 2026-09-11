package mindustry.entities.comp;

import mindustry.annotations.Annotations.Component;
import mindustry.annotations.Annotations.Import;
import mindustry.annotations.Annotations.MethodPriority;
import mindustry.annotations.Annotations.Replace;
import mindustry.async.PhysicsProcess;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.type.UnitType;

@Component
abstract class UnderwaterMoveComp implements WaterMovec{
    @Import UnitType type;

    @MethodPriority(10f)
    @Replace
    public void draw(){
        //TODO draw status effects?

        Drawf.underwater(() -> {
            type.draw(self());
        });
    }

    @Override
    public int collisionLayer(){
        return PhysicsProcess.layerUnderwater;
    }

    @Override
    public boolean hittable(){
        return false && type.hittable(self());
    }

    @Override
    public boolean targetable(Team targeter){
        return false && type.targetable(self(), targeter);
    }
}

