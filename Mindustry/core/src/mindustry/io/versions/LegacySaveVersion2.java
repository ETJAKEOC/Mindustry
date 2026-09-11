package mindustry.io.versions;

import java.io.DataInput;
import java.io.IOException;

import arc.func.Prov;
import mindustry.gen.*;
import mindustry.io.SaveReadState;

/**
 * This version did not read/write entity IDs to the save.
 */
public class LegacySaveVersion2 extends LegacyRegionSaveVersion {

	public LegacySaveVersion2(int version) {
		super(version);
	}

	@Override
	public void readWorldEntities(DataInput stream, Prov[] mapping, SaveReadState state) throws IOException {

		int amount = stream.readInt();
		for (int j = 0; j < amount; j++) {
			readLegacyShortChunk(stream, (in, len) -> {
				int typeid = in.ub();
				if (mapping[typeid] == null) {
					in.skip(len - 1);
					return;
				}

				Entityc entity = (Entityc) mapping[typeid].get();
				entity.read(in);
				entity.add();
			});
		}
	}
}
