package mindustry.audio;

import arc.audio.Sound;
import arc.math.geom.Position;

public interface AmbientSource extends Position {
	boolean isValid();

	boolean shouldAmbientSound();

	float getAmbientVolume();

	Sound getAmbientSound();
}
