package mindustry.logic;

import arc.Core;
import arc.graphics.Color;
import arc.scene.style.Drawable;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.gen.*;
import mindustry.graphics.Pal;

public record LCategory(String name, int id, Color color,
                        @Nullable Drawable icon) implements Comparable<LCategory> {
	public static final Seq<LCategory> all = new Seq<>();

	public static final LCategory

			unknown = new LCategory("unknown", Pal.darkishGray);
	public static final LCategory io = new LCategory("io", Pal.logicIo, Icon.logicSmall);
	public static final LCategory block = new LCategory("block", Pal.logicBlocks, Icon.effectSmall);
	public static final LCategory operation = new LCategory("operation", Pal.logicOperations, Icon.settingsSmall);
	public static final LCategory control = new LCategory("control", Pal.logicControl, Icon.rotateSmall);
	public static final LCategory unit = new LCategory("unit", Pal.logicUnits, Icon.unitsSmall);
	public static final LCategory world = new LCategory("world", Pal.logicWorld, Icon.terrainSmall);

	public LCategory(String name, Color color) {
		this(name, color, null);
	}

	public LCategory(String name, Color color, Drawable icon) {
		this(name, all.size, color, icon);
		all.add(this);
	}

	public String localized() {
		return Core.bundle.get("lcategory." + name);
	}

	public String description() {
		return Core.bundle.get("lcategory." + name + ".description");
	}

	@Override
	public int compareTo(LCategory o) {
		return id - o.id;
	}
}
