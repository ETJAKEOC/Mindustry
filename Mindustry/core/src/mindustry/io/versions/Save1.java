package mindustry.io.versions;

import java.io.DataInput;
import java.io.IOException;

import mindustry.io.SaveReadState;

public class Save1 extends LegacySaveVersion{

    public Save1(){
        super(1);
    }

    @Override
    public void readEntities(DataInput stream, SaveReadState state) throws IOException{
        readLegacyEntities(stream);
    }
}
