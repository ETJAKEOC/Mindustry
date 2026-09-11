package mindustry.world.blocks.sandbox;

import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;

public class PowerVoid extends PowerBlock {

	public PowerVoid(String name) {
		super(name);
		consumePower(Float.MAX_VALUE);
		envEnabled = Env.any;
		enableDrawStatus = false;
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.remove(Stat.powerUse);
	}
}
