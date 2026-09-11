package mindustry.entities.comp;

import java.nio.FloatBuffer;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.annotations.Annotations.Component;
import mindustry.game.Team;
import mindustry.gen.*;

@Component
abstract class SyncComp implements Entityc {
	transient long lastUpdated, updateSpacing;

	//all these method bodies are internally generated
	void snapSync() {
	}

	void snapInterpolation() {
	}

	void readSync(Reads read) {
	}

	void writeSync(Writes write) {
	}

	void readSyncManual(FloatBuffer buffer) {
	}

	void writeSyncManual(FloatBuffer buffer) {
	}

	void afterSync() {
	}

	void interpolate() {
	}

	boolean isSyncHidden(Team team) {
		return false;
	}

	void handleSyncHidden() {

	}

	@Override
	public void update() {
		//interpolate the player if:
		//- this is a client and the entity is everything except the local player
		//- this is a server and the entity is a remote player
		if ((Vars.net.client() && !isLocal()) || isRemote()) {
			interpolate();
		}
	}

	@Override
	public void remove() {
		//notify client of removal
		if (Vars.net.client()) {
			Vars.netClient.addRemovedEntity(id());
		}
	}
}
