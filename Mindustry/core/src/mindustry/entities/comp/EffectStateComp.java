package mindustry.entities.comp;

import arc.graphics.Color;
import mindustry.annotations.Annotations.Component;
import mindustry.annotations.Annotations.EntityDef;
import mindustry.annotations.Annotations.Import;
import mindustry.annotations.Annotations.Replace;
import mindustry.entities.Effect;
import mindustry.gen.*;

@EntityDef(value = {EffectStatec.class, Childc.class}, pooled = true, serialize = false)
@Component(base = true)
abstract class EffectStateComp implements Posc, Drawc, Timedc, Rotc, Childc {
	@Import
	float time, lifetime, rotation, x, y;
	@Import
	int id;

	Color color = new Color(Color.white);
	Effect effect;
	Object data;

	@Override
	public void draw() {
		lifetime = effect.render(id, color, time, lifetime, rotation, x, y, data);
	}

	@Replace
	public float clipSize() {
		return effect.clip;
	}
}
