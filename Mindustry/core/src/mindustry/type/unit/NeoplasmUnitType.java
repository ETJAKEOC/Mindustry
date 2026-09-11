package mindustry.type.unit;

import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.entities.abilities.LiquidExplodeAbility;
import mindustry.entities.abilities.LiquidRegenAbility;
import mindustry.entities.abilities.RegenAbility;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.world.meta.Env;

/**
 * This is just a preset. Contains no new behavior.
 */
public class NeoplasmUnitType extends UnitType {

	public NeoplasmUnitType(String name) {
		super(name);

		outlineColor = Pal.neoplasmOutline;
		immunities.addAll(StatusEffects.burning, StatusEffects.melting);
		envDisabled = Env.none;
		drawCell = false;

		abilities.add(new RegenAbility() {{
			//fully regen in 70 seconds
			percentAmount = 1f / (70f * 60f) * 100f;
		}});

		abilities.add(new LiquidExplodeAbility() {{
			liquid = Liquids.neoplasm;
		}});

		abilities.add(new LiquidRegenAbility() {{
			liquid = Liquids.neoplasm;
			slurpEffect = Fx.neoplasmHeal;
		}});

		//green flashing is unnecessary since they always regen
		healFlash = true;

		healColor = Pal.neoplasm1;

		//TODO
		//- liquid regen ability
		//- new explode effect
	}
}
