package mindustry.entities.comp;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.annotations.Annotations.Component;
import mindustry.annotations.Annotations.EntityDef;
import mindustry.annotations.Annotations.Import;
import mindustry.annotations.Annotations.Replace;
import mindustry.gen.*;
import mindustry.graphics.Layer;

@EntityDef(value = {Decalc.class}, pooled = true, serialize = false)
@Component(base = true)
abstract class DecalComp implements Drawc, Timedc, Rotc, Posc {
	@Import
	float x, y, rotation;

	Color color = new Color(1, 1, 1, 1);
	TextureRegion region;

	@Override
	public void draw() {
		Draw.z(Layer.scorch);

		Draw.mixcol(color, color.a);
		Draw.alpha(1f - Mathf.curve(fin(), 0.98f));
		Draw.rect(region, x, y, rotation);
		Draw.reset();
	}

	@Replace
	public float clipSize() {
		return region.width * 2;
	}

}
