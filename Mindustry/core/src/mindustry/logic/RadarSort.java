package mindustry.logic;

import arc.math.geom.Position;
import mindustry.gen.*;

public enum RadarSort {
	distance((pos, other) -> -pos.dst2(other)),
	health((pos, other) -> other.health()),
	shield((pos, other) -> other.shield()),
	armor((pos, other) -> other.armor()),
	maxHealth((pos, other) -> other.maxHealth());

	public static final RadarSort[] all = values();
	public final RadarSortFunc func;

	RadarSort(RadarSortFunc func) {
		this.func = func;
	}

	public interface RadarSortFunc {
		float get(Position pos, Unit other);
	}
}
