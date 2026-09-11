package mindustry.maps.generators;

import mindustry.world.Tiles;
import mindustry.world.WorldParams;

public interface WorldGenerator {
	void generate(Tiles tiles, WorldParams params);

	/**
	 * Do not modify tiles here. This is only for specialized configuration.
	 */
	default void postGenerate(Tiles tiles) {
	}
}
