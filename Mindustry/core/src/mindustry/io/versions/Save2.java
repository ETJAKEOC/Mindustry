package mindustry.io.versions;

import java.io.DataInput;
import java.io.IOException;

import mindustry.io.SaveReadState;

public class Save2 extends LegacySaveVersion {

	public Save2() {
		super(2);
	}

	@Override
	public void readEntities(DataInput stream, SaveReadState state) throws IOException {
		readLegacyEntities(stream);
	}
}
