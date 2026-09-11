package mindustry.io.versions;

import static mindustry.Vars.content;

import java.io.DataInputStream;
import java.io.IOException;

import arc.util.io.CounterInputStream;
import mindustry.io.SaveReadState;

/** This version does not read custom chunk data (<= 6). */
public class LegacyRegionSaveVersion extends ShortChunkSaveVersion{

    public LegacyRegionSaveVersion(int version){
        super(version);
    }

    @Override
    public void read(DataInputStream stream, CounterInputStream counter, SaveReadState saveState) throws IOException{
        readRegion("meta", stream, counter, in -> readMeta(in, saveState));
        readRegion("content", stream, counter, this::readContentHeader);

        try{
            readRegion("map", stream, counter, in -> readMap(in, saveState));
            readRegion("entities", stream, counter, in -> readEntities(in, saveState));
        }finally{
            content.setTemporaryMapper(null);

        }
    }
}
