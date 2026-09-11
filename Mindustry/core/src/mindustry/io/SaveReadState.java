package mindustry.io;

import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.gen.*;
import mindustry.world.WorldContext;

public class SaveReadState{
    public final WorldContext context;
    /** true when generating a map preview */
    public boolean preview;
    public @Nullable String ruleString;
    public Seq<Building> allBuildings = new Seq<>();

    public SaveReadState(WorldContext context){
        this.context = context;
    }
}
