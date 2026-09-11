package mindustry.entities.comp;

import arc.math.Scaled;
import arc.util.Time;
import mindustry.annotations.Annotations.Component;
import mindustry.annotations.Annotations.MethodPriority;
import mindustry.gen.*;

@Component
abstract class TimedComp implements Entityc, Scaled {
	float time, lifetime;

	//called last so pooling and removal happens then.
	@MethodPriority(100)
	@Override
	public void update() {
		time = Math.min(time + Time.delta, lifetime);

		if (time >= lifetime) {
			remove();
		}
	}

	@Override
	public float fin() {
		return time / lifetime;
	}
}
