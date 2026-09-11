package mindustry.entities.comp;

import static mindustry.Vars.world;

import arc.math.geom.Position;
import arc.util.Nullable;
import mindustry.annotations.Annotations.Component;
import mindustry.annotations.Annotations.SyncField;
import mindustry.annotations.Annotations.SyncLocal;
import mindustry.content.Blocks;
import mindustry.core.World;
import mindustry.gen.*;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

@Component
abstract class PosComp implements Position{
    @SyncField(true) @SyncLocal float x, y;

    void set(float x, float y){
        this.x = x;
        this.y = y;
    }

    void set(Position pos){
        set(pos.getX(), pos.getY());
    }

    void trns(float x, float y){
        set(this.x + x, this.y + y);
    }

    void trns(Position pos){
        trns(pos.getX(), pos.getY());
    }

    int tileX(){
        return World.toTile(x);
    }

    int tileY(){
        return World.toTile(y);
    }

    /** Returns air if this unit is on a non-air top block. */
    Floor floorOn(){
        Tile tile = tileOn();
        return tile == null || tile.block() != Blocks.air ? (Floor)Blocks.air : tile.floor();
    }

    Block blockOn(){
        Tile tile = tileOn();
        return tile == null ? Blocks.air : tile.block();
    }

    @Nullable
    Building buildOn(){
        return world.buildWorld(x, y);
    }

    @Nullable
    Tile tileOn(){
        return world.tileWorld(x, y);
    }

    boolean onSolid(){
        Tile tile = tileOn();
        return tile == null || tile.solid();
    }

    @Override
    public float getX(){
        return x;
    }

    @Override
    public float getY(){
        return y;
    }
}
