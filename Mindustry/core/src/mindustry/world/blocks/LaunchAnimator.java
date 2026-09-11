package mindustry.world.blocks;

import arc.audio.Music;
import arc.util.Nullable;
import mindustry.gen.*;

public interface LaunchAnimator {

	void drawLaunch();

	default void drawLaunchGlobalZ() {
	}

	void beginLaunch(boolean launching);

	void endLaunch();

	void updateLaunch();

	float launchDuration();

	default @Nullable Music landMusic() {
		return Musics.land;
	}

	float zoomLaunch();
}
