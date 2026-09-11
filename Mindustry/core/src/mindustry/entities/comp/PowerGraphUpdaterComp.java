package mindustry.entities.comp;

import mindustry.annotations.Annotations.Component;
import mindustry.annotations.Annotations.EntityDef;
import mindustry.gen.*;
import mindustry.world.blocks.power.PowerGraph;

@EntityDef(value = PowerGraphUpdaterc.class, serialize = false, genio = false)
@Component
abstract class PowerGraphUpdaterComp implements Entityc {
	public transient PowerGraph graph;

	@Override
	public void update() {
		graph.update();
	}
}
