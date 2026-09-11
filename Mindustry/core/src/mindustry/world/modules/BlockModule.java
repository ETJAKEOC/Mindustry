package mindustry.world.modules;

import arc.util.io.Reads;
import arc.util.io.Writes;

/**
 * A class that represents compartmentalized tile entity state.
 */
public abstract class BlockModule {
	public abstract void write(Writes write);

	public void read(Reads read, boolean legacy) {
		read(read);
	}

	public void read(Reads read) {
		read(read, false);
	}
}
