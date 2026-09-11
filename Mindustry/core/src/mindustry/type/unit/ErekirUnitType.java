package mindustry.type.unit;

import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.world.meta.Env;

/**
 * Config class for special Erekir unit properties.
 */
public class ErekirUnitType extends UnitType {

	public ErekirUnitType(String name) {
		super(name);
		outlineColor = Pal.darkOutline;
		envDisabled = Env.space;
		researchCostMultiplier = 10f;
	}
}
