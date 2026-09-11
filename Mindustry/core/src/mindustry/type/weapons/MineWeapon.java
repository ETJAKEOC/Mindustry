package mindustry.type.weapons;

import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.type.Weapon;

public class MineWeapon extends Weapon {
	{
		rotate = true;
		noAttack = true;
		predictTarget = false;
		display = false;
		bullet = new BulletType();
		useAttackRange = false;
	}

	public MineWeapon() {
		super();
	}

	public MineWeapon(String name) {
		super(name);
	}

	@Override
	public void update(Unit unit, WeaponMount mount) {
		mount.shoot = false;
		mount.rotate = true;

		//always aim at build plan
		if (unit.mining()) {
			mount.aimX = unit.mineTile().drawx();
			mount.aimY = unit.mineTile().drawy();
		} else {
			//aim for front
			float weaponRotation = unit.rotation - 90 + baseRotation;
			mount.aimX = unit.x + Angles.trnsx(unit.rotation - 90, x, y) + Angles.trnsx(weaponRotation, this.shootX, this.shootY);
			mount.aimY = unit.y + Angles.trnsy(unit.rotation - 90, x, y) + Angles.trnsy(weaponRotation, this.shootX, this.shootY);
		}

		super.update(unit, mount);
	}

	@Override
	public void draw(Unit unit, WeaponMount mount) {
		super.draw(unit, mount);

		if (unit.mining()) {
			float
					z = Draw.z(),
					rotation = unit.rotation - 90,
					weaponRotation = rotation + (rotate ? mount.rotation : 0),
					wx = unit.x + Angles.trnsx(rotation, x, y) + Angles.trnsx(weaponRotation, 0, -mount.recoil),
					wy = unit.y + Angles.trnsy(rotation, x, y) + Angles.trnsy(weaponRotation, 0, -mount.recoil),
					sY = shootY + Mathf.absin(Time.time, 1.1f, 0.5f),
					px = wx + Angles.trnsx(weaponRotation, shootX, sY),
					py = wy + Angles.trnsy(weaponRotation, shootX, sY);

			unit.type.drawMiningBeam(unit, px, py);
			Draw.z(z);
		}
	}
}
