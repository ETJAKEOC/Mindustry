package mindustry.world.consumers;

import arc.func.Boolf;
import arc.struct.ObjectFloatMap;
import arc.util.Nullable;
import mindustry.type.Item;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import mindustry.world.meta.Stats;

public class ConsumeItemEfficiency extends ConsumeItemFilter {
	/**
	 * This has no effect on the consumer itself, but is used for stat display.
	 */
	public @Nullable ObjectFloatMap<Item> itemDurationMultipliers;

	public ConsumeItemEfficiency(Boolf<Item> item) {
		super(item);
	}

	public ConsumeItemEfficiency() {
	}

	@Override
	public void display(Stats stats) {
		stats.add(booster ? Stat.booster : Stat.input, StatValues.itemEffMultiplier(this::itemEfficiencyMultiplier, stats.timePeriod, filter, itemDurationMultipliers));
	}
}
