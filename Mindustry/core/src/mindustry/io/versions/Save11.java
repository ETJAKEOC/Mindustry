package mindustry.io.versions;

import static mindustry.Vars.content;
import static mindustry.Vars.state;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

import arc.Events;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.io.CounterInputStream;
import mindustry.game.EventType.DataPatchLoadEvent;
import mindustry.io.SaveReadState;
import mindustry.io.SaveVersion;
import mindustry.mod.data.DataAsset;
import mindustry.mod.data.PatchAsset;

/**
 * Adds patches in content header. Unlike >= 12, this version has patches before the content header.
 *
 */
public class Save11 extends SaveVersion {

	public Save11() {
		super(11);
	}

	@Override
	public void read(DataInputStream stream, CounterInputStream counter, SaveReadState saveState) throws IOException {
		readRegion("meta", stream, counter, in -> readMeta(in, saveState));
		readRegion("content", stream, counter, this::readContentHeader);

		try {
			readRegion("patches", stream, counter, in -> readDataPatches(in, saveState));
			readRegion("map", stream, counter, in -> readMap(in, saveState));
			readRegion("entities", stream, counter, in -> readEntities(in, saveState));
			readRegion("markers", stream, counter, this::readMarkers);
			readRegion("custom", stream, counter, this::readCustomChunks);
		} finally {
			content.setTemporaryMapper(null);
		}
	}

	//old, simplified string-only data patches
	@Override
	public void readDataPatches(DataInput stream, SaveReadState saveState) throws IOException {
		Seq<DataAsset> assets = new Seq<>();

		int amount = stream.readUnsignedByte();
		for (int i = 0; i < amount; i++) {
			int len = stream.readInt();
			byte[] bytes = new byte[len];
			stream.readFully(bytes);
			assets.add(new PatchAsset(new String(bytes, Strings.utf8)));
		}

		Events.fire(new DataPatchLoadEvent(assets));

		state.data.load(assets);
	}
}
