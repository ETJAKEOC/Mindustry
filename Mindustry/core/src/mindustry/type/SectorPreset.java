package mindustry.type;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Strings;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Rules;
import mindustry.gen.*;
import mindustry.graphics.MultiPacker;
import mindustry.graphics.MultiPacker.PageType;
import mindustry.graphics.Pal;
import mindustry.maps.generators.FileMapGenerator;
import mindustry.mod.Mods.LoadedMod;

public class SectorPreset extends UnlockableContent {
	public FileMapGenerator generator;
	public Planet planet;
	public Sector sector;

	public int captureWave = 0;
	public Cons<Rules> rules = rules -> rules.winWave = captureWave;
	/**
	 * Difficulty, 0-10.
	 */
	public float difficulty;
	public float startWaveTimeMultiplier = 2f;
	public boolean addStartingItems = false;
	public boolean noLighting = false;
	/**
	 * If true, this is the last sector in its planetary campaign.
	 */
	public boolean isLastSector;
	/**
	 * If true, this sector must be unlocked before landing is permitted.
	 */
	public boolean requireUnlock = true;
	/**
	 * If true, the icon and name is shown, even when it's a 'hidden' always-unlocked sector. TODO: this field may be changed, not sure how it should work
	 */
	public boolean showHidden = false;
	public boolean showSectorLandInfo = true;
	/**
	 * If true, uses this sector's launch fields instead
	 */
	public boolean overrideLaunchDefaults = false;
	/**
	 * Whether to allow users to specify a custom launch schematic for this map.
	 */
	public boolean allowLaunchSchematics = false;
	/**
	 * Whether to allow users to specify the resources they take to this map.
	 */
	public boolean allowLaunchLoadout = false;
	/**
	 * If true, switches to attack mode after waves end.
	 */
	public boolean attackAfterWaves = false;
	/**
	 * The original position of this sector; used for migration. Internal use for vanilla campaign only!
	 */
	public int originalPosition;
	/**
	 * Sectors that prevent this sector from being landed on until they are completed.
	 */
	public Seq<Sector> shieldSectors = new Seq<>();
	/**
	 * Set to false to disable outline generation.
	 */
	public boolean outline = true;
	public int outlineRadius = 5;
	public Color outlineColor = Pal.gray;

	private final @Nullable String fileName;

	public SectorPreset(String name, Planet planet, int sector) {
		this(name, null, planet, sector);
	}

	public SectorPreset(String name, String fileName, Planet planet, int sector) {
		this(name, fileName, null);
		initialize(planet, sector);
	}

	/**
	 * Internal use only!
	 */
	public SectorPreset(String name, LoadedMod mod) {
		this(name, null, mod);
	}

	/**
	 * Internal use only!
	 */
	public SectorPreset(String name, @Nullable String fileName, LoadedMod mod) {
		super(name);
		if (mod != null) {
			this.minfo.mod = mod;
		}
		this.fileName = fileName;
	}

	public void initialize(Planet planet, int sector) {
		initialize(planet, sector, false);
	}

	public void initialize(Planet planet, int sector, boolean override) {
		this.planet = planet;
		if (generator == null) {
			this.generator = new FileMapGenerator(fileName == null ? this.name : fileName, this);
		}
		this.originalPosition = sector;
		if (!override) {
			//auto remap based on data
			var data = planet.getData();
			if (data != null) {
				sector = data.presets.get(name, sector);
			}
		}
		sector %= planet.sectors.size;
		this.sector = planet.sectors.get(sector == -1 ? 0 : sector);

		if (sector != -1) {
			planet.preset(sector, this);
		} else {
			Log.warn("Preset '@' doesn't have a sector assigned.", name);
		}
	}

	@Override
	public void removeContent() {
		super.removeContent();
		if (sector != null && sector.preset == this) {
			sector.preset = null;
		}
	}

	@Override
	public void init() {
		super.init();

		//note that sectors can only have one visual shield target
		for (var other : shieldSectors) {
			other.shieldTarget = sector;
		}
	}

	@Override
	public void createIcons(MultiPacker packer) {
		super.createIcons(packer);

		if (outline && Core.atlas.has("sector-" + name)) {
			makeOutline(PageType.ui, packer, Core.atlas.find("sector-" + name), false, outlineColor, outlineRadius, outlineRadius);
		}
	}

	@Override
	public void loadIcon() {
		if (Icon.terrain != null) {
			uiIcon = fullIcon = Core.atlas.find("sector-" + name, Icon.terrain.getRegion());
		}
		if (this.localizedName == null || this.localizedName.equals(this.name) || this.localizedName.isEmpty()) {
			String key = "sector." + name + ".name";
			if (Core.bundle != null && Core.bundle.has(key)) {
				this.localizedName = Core.bundle.get(key);
			} else {
				String shortName = name.contains("-") ? name.substring(name.lastIndexOf('-') + 1) : name;
				String altKey = "sector." + shortName + ".name";
				if (Core.bundle != null && Core.bundle.has(altKey)) {
					this.localizedName = Core.bundle.get(altKey);
				} else {
					this.localizedName = Strings.capitalize(shortName);
				}
			}
		}
	}

	public String localizedName() {
		if (localizedName != null && !localizedName.equals(name) && !localizedName.isEmpty()) {
			return localizedName;
		}
		String key = "sector." + name + ".name";
		if (Core.bundle != null && Core.bundle.has(key)) {
			return Core.bundle.get(key);
		}
		String shortName = name.contains("-") ? name.substring(name.lastIndexOf('-') + 1) : name;
		String altKey = "sector." + shortName + ".name";
		if (Core.bundle != null && Core.bundle.has(altKey)) {
			return Core.bundle.get(altKey);
		}
		return Strings.capitalize(shortName);
	}

	@Override
	public boolean isHidden() {
		return description == null;
	}

	@Override
	public ContentType getContentType() {
		return ContentType.sector;
	}

}
