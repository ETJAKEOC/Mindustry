package mindustry.entities.comp;

import static mindustry.Vars.fogControl;
import static mindustry.Vars.state;

import arc.util.Nullable;
import mindustry.annotations.Annotations.Component;
import mindustry.annotations.Annotations.Import;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;

@Component
abstract class TeamComp implements Posc{
    @Import float x, y;

    Team team = Team.derelict;

    public boolean cheating(){
        return team.rules().cheat;
    }

    /** @return whether the center of this entity is visible to the viewing team. */
    boolean inFogTo(Team viewer){
        return this.team != viewer && !fogControl.isVisible(viewer, x, y);
    }

    @Nullable
    public CoreBuild core(){
        return team.core();
    }

    @Nullable
    public CoreBuild closestCore(){
        return state.teams.closestCore(x, y, team);
    }

    @Nullable
    public CoreBuild closestEnemyCore(){
        return state.teams.closestEnemyCore(x, y, team);
    }
}
