package mindustry.ctype;

import arc.util.Nullable;
import mindustry.ai.UnitCommand;
import mindustry.ai.UnitStance;
import mindustry.entities.bullet.BulletType;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.type.Planet;
import mindustry.type.SectorPreset;
import mindustry.type.StatusEffect;
import mindustry.type.TeamEntry;
import mindustry.type.UnitType;
import mindustry.type.Weather;
import mindustry.world.Block;

/**
 * Do not rearrange, ever!
 */
public enum ContentType {
	item("items", Item.class),
	block("blocks", Block.class),
	mech_UNUSED,
	bullet("bullets", BulletType.class),
	liquid("liquids", Liquid.class),
	status("statuses", StatusEffect.class),
	unit("units", UnitType.class),
	weather("weather", Weather.class),
	effect_UNUSED,
	sector("sectors", SectorPreset.class),
	loadout_UNUSED,
	typeid_UNUSED,
	error,
	planet("planets", Planet.class),
	ammo_UNUSED(),
	team("teams", TeamEntry.class),
	unitCommand("unitCommands", UnitCommand.class),
	unitStance("unitStances", UnitStance.class);

	public static final ContentType[] all = values();

	public final @Nullable Class<? extends Content> contentClass;
	public final String folderName;

	ContentType() {
		this.contentClass = null;
		this.folderName = "unused";
	}

	ContentType(String folderName, Class<? extends Content> contentClass) {
		this.contentClass = contentClass;
		this.folderName = folderName;
	}
}
