package mindustry.world.meta;

import java.util.Locale;

import arc.Core;
import arc.struct.Seq;

/**
 * Describes one type of stat for content.
 */
public record Stat(StatCat category, String name, int id) implements Comparable<Stat> {
	public static final Seq<Stat> all = new Seq<>();

	public static final Stat

			health = new Stat("health");
	public static final Stat armor = new Stat("armor");
	public static final Stat size = new Stat("size");
	public static final Stat displaySize = new Stat("displaySize");
	public static final Stat buildTime = new Stat("buildTime");
	public static final Stat buildCost = new Stat("buildCost");
	public static final Stat memoryCapacity = new Stat("memoryCapacity");
	public static final Stat explosiveness = new Stat("explosiveness");
	public static final Stat flammability = new Stat("flammability");
	public static final Stat radioactivity = new Stat("radioactivity");
	public static final Stat charge = new Stat("charge");
	public static final Stat heatCapacity = new Stat("heatCapacity");
	public static final Stat viscosity = new Stat("viscosity");
	public static final Stat temperature = new Stat("temperature");
	public static final Stat flying = new Stat("flying");
	public static final Stat speed = new Stat("speed");
	public static final Stat buildSpeed = new Stat("buildSpeed");
	public static final Stat mineSpeed = new Stat("mineSpeed");
	public static final Stat mineTier = new Stat("mineTier");
	public static final Stat payloadCapacity = new Stat("payloadCapacity");
	public static final Stat baseDeflectChance = new Stat("baseDeflectChance");
	public static final Stat lightningChance = new Stat("lightningChance");
	public static final Stat lightningDamage = new Stat("lightningDamage");
	public static final Stat abilities = new Stat("abilities");
	public static final Stat canBoost = new Stat("canBoost");
	public static final Stat boostingSpeed = new Stat("boostingspeed");
	public static final Stat maxUnits = new Stat("maxUnits");

	public static final Stat damageMultiplier = new Stat("damageMultiplier");
	public static final Stat healthMultiplier = new Stat("healthMultiplier");
	public static final Stat speedMultiplier = new Stat("speedMultiplier");
	public static final Stat reloadMultiplier = new Stat("reloadMultiplier");
	public static final Stat buildSpeedMultiplier = new Stat("buildSpeedMultiplier");
	public static final Stat reactive = new Stat("reactive");
	public static final Stat healing = new Stat("healing");
	public static final Stat immunities = new Stat("immunities");

	public static final Stat itemCapacity = new Stat("itemCapacity", StatCat.items);
	public static final Stat itemsMoved = new Stat("itemsMoved", StatCat.items);
	public static final Stat launchTime = new Stat("launchTime", StatCat.items);
	public static final Stat maxConsecutive = new Stat("maxConsecutive", StatCat.items);

	public static final Stat liquidCapacity = new Stat("liquidCapacity", StatCat.liquids);

	public static final Stat powerCapacity = new Stat("powerCapacity", StatCat.power);
	public static final Stat powerUse = new Stat("powerUse", StatCat.power);
	public static final Stat powerDamage = new Stat("powerDamage", StatCat.power);
	public static final Stat powerRange = new Stat("powerRange", StatCat.power);
	public static final Stat powerConnections = new Stat("powerConnections", StatCat.power);
	public static final Stat basePowerGeneration = new Stat("basePowerGeneration", StatCat.power);
	public static final Stat meltdownTime = new Stat("meltdownTime", StatCat.power);
	public static final Stat warmupTime = new Stat("warmupTime", StatCat.power);

	public static final Stat tiles = new Stat("tiles", StatCat.crafting);
	public static final Stat input = new Stat("input", StatCat.crafting);
	public static final Stat output = new Stat("output", StatCat.crafting);
	public static final Stat productionTime = new Stat("productionTime", StatCat.crafting);
	public static final Stat maxEfficiency = new Stat("maxEfficiency", StatCat.crafting);
	public static final Stat drillTier = new Stat("drillTier", StatCat.crafting);
	public static final Stat drillSpeed = new Stat("drillSpeed", StatCat.crafting);
	public static final Stat linkRange = new Stat("linkRange", StatCat.crafting);
	public static final Stat instructions = new Stat("instructions", StatCat.crafting);

	public static final Stat weapons = new Stat("weapons", StatCat.function);
	public static final Stat bullet = new Stat("bullet", StatCat.function);

	public static final Stat speedIncrease = new Stat("speedIncrease", StatCat.function);
	public static final Stat repairTime = new Stat("repairTime", StatCat.function);
	public static final Stat repairSpeed = new Stat("repairSpeed", StatCat.function);
	public static final Stat range = new Stat("range", StatCat.function);
	public static final Stat shootRange = new Stat("shootRange", StatCat.function);
	public static final Stat inaccuracy = new Stat("inaccuracy", StatCat.function);
	public static final Stat shots = new Stat("shots", StatCat.function);
	public static final Stat reload = new Stat("reload", StatCat.function);
	public static final Stat crushDamage = new Stat("crushDamage", StatCat.function);
	public static final Stat legSplashDamage = new Stat("legSplashDamage", StatCat.function);
	public static final Stat targetsAir = new Stat("targetsAir", StatCat.function);
	public static final Stat targetsGround = new Stat("targetsGround", StatCat.function);
	public static final Stat damage = new Stat("damage", StatCat.function);
	public static final Stat status = new Stat("status", StatCat.function);
	public static final Stat frequency = new Stat("frequency", StatCat.function);
	public static final Stat ammo = new Stat("ammo", StatCat.function);
	public static final Stat ammoCapacity = new Stat("ammoCapacity", StatCat.function);
	public static final Stat ammoUse = new Stat("ammoUse", StatCat.function);
	public static final Stat shieldHealth = new Stat("shieldHealth", StatCat.function);
	public static final Stat cooldownTime = new Stat("cooldownTime", StatCat.function);
	public static final Stat regenerationRate = new Stat("regenerationRate", StatCat.function);
	public static final Stat activationTime = new Stat("activationTime", StatCat.function);
	public static final Stat moduleTier = new Stat("moduletier", StatCat.function);
	public static final Stat unitType = new Stat("unittype", StatCat.function);
	public static final Stat receiveRate = new Stat("receiveRate", StatCat.function);

	public static final Stat booster = new Stat("booster", StatCat.optional);
	public static final Stat boostEffect = new Stat("boostEffect", StatCat.optional);
	public static final Stat affinities = new Stat("affinities", StatCat.optional);
	public static final Stat opposites = new Stat("opposites", StatCat.optional);

	public Stat(String name, StatCat category) {
		this(category, name, all.size);
		all.add(this);
	}

	public Stat(String name) {
		this(name, StatCat.general);
	}

	public String localized() {
		return Core.bundle.get("stat." + name.toLowerCase(Locale.ROOT));
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(Stat o) {
		return id - o.id;
	}
}
