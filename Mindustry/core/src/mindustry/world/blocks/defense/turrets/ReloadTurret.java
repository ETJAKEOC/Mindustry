package mindustry.world.blocks.defense.turrets;

import static mindustry.Vars.tilesize;

import arc.math.Mathf;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

public class ReloadTurret extends BaseTurret {
	public float reload = 10f;

	public ReloadTurret(String name) {
		super(name);
	}

	@Override
	public void setStats() {
		super.setStats();

		if (coolant != null) {
			stats.replace(Stat.booster, StatValues.boosters(reload, coolant.amount, coolantMultiplier, true, coolant::consumes));
		}
	}

	public class ReloadTurretBuild extends BaseTurretBuild {
		public float reloadCounter;

		protected void updateCooling() {
			if (coolant != null && coolant.efficiency(this) > 0 && efficiency > 0) {
				float capacity = coolant instanceof ConsumeLiquidFilter filter ? filter.getConsumed(this).heatCapacity : (coolant.consumes(liquids.current()) ? liquids.current().heatCapacity : 0.4f);
				float amount = coolant.amount * coolant.efficiency(this);
				coolant.update(this);
				reloadCounter += amount * edelta() * capacity * coolantMultiplier * ammoReloadMultiplier();

				if (Mathf.chance(0.06 * amount)) {
					coolEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
				}
			}
		}

		protected float ammoReloadMultiplier() {
			return 1f;
		}

		protected float baseReloadSpeed() {
			return efficiency;
		}
	}
}
