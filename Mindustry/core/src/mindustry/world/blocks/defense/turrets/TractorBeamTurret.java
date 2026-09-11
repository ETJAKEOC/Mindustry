package mindustry.world.blocks.defense.turrets;

import static mindustry.Vars.control;
import static mindustry.Vars.headless;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.annotations.Annotations.Load;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Liquid;
import mindustry.type.StatusEffect;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

public class TractorBeamTurret extends BaseTurret {
	public final int timerTarget = timers++;
	public float retargetTime = 5f;

	public float shootCone = 6f;
	public float shootLength = 5f;
	public float laserWidth = 0.6f;
	public float force = 0.3f;
	public float scaledForce = 0f;
	public float damage = 0f;
	public boolean targetAir = true, targetGround = false;
	public Color laserColor = Color.white;
	public StatusEffect status = StatusEffects.none;
	public float statusDuration = 300;
	public float statusChance = 1f;

	public Sound shootSound = Sounds.beamParallax;
	public float shootSoundVolume = 0.9f;

	public @Load(value = "@-base", fallback = "block-@size") TextureRegion baseRegion;
	public @Load("@-laser") TextureRegion laser;
	public @Load(value = "@-laser-start", fallback = "@-laser-end") TextureRegion laserStart;
	public @Load("@-laser-end") TextureRegion laserEnd;

	public TractorBeamTurret(String name) {
		super(name);

		rotateSpeed = 10f;
		coolantMultiplier = 1f;
		envEnabled |= Env.space;
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{baseRegion, region};
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.add(Stat.targetsAir, targetAir);
		stats.add(Stat.targetsGround, targetGround);
		if (damage > 0) stats.add(Stat.damage, damage * 60f, StatUnit.perSecond);
		if (status != StatusEffects.none && statusChance > 0f) {
			stats.add(Stat.status, StatValues.statusText(status, statusDuration, statusChance));
		}
	}

	@Override
	public void init() {
		super.init();

		updateClipRadius(range + tilesize);
	}

	public class TractorBeamBuild extends BaseTurretBuild {
		public @Nullable Unit target;
		public float lastX, lastY, strength;
		public boolean any;
		public float coolantMultiplier = 1f;

		@Override
		public void updateTile() {
			if (activationTimer > 0) {
				activationTimer -= Time.delta;
				return;
			}

			float eff = efficiency * coolantMultiplier, edelta = eff * delta();

			//retarget
			if (timer(timerTarget, retargetTime)) {
				target = Units.closestEnemy(team, x, y, range, u -> u.checkTarget(targetAir, targetGround));
			}

			//consume coolant
			if (target != null && coolant != null) {
				float maxUsed = coolant.amount;

				Liquid liquid = liquids.current();

				float used = Math.min(Math.min(liquids.get(liquid), maxUsed * Time.delta), Math.max(0, (1f / coolantMultiplier) / liquid.heatCapacity));

				liquids.remove(liquid, used);

				if (Mathf.chance(0.06 * used)) {
					coolEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
				}

				coolantMultiplier = 1f + (used * liquid.heatCapacity * coolantMultiplier);
			}

			any = false;

			//look at target
			if (target != null && target.within(this, range + target.hitSize / 2f) && target.team() != team && target.checkTarget(targetAir, targetGround) && efficiency > 0.02f) {
				if (!headless) {
					control.sound.loop(shootSound, this, shootSoundVolume);
				}

				float dest = angleTo(target);
				rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta);
				lastX = target.x;
				lastY = target.y;
				strength = Mathf.lerpDelta(strength, 1f, 0.1f);

				//shoot when possible
				if (Angles.within(rotation, dest, shootCone)) {
					if (damage > 0) {
						target.damageContinuousPierce(damage * eff * timeScale * state.rules.blockDamage(team));
					}

					if (status != StatusEffects.none && Mathf.chance(statusChance)) {
						target.apply(status, statusDuration);
					}

					any = true;
					target.impulseNet(Tmp.v1.set(this).sub(target).limit((force + (1f - target.dst(this) / range) * scaledForce) * edelta));
				}
			} else {
				strength = Mathf.lerpDelta(strength, 0, 0.1f);
			}
		}

		@Override
		public boolean shouldConsume() {
			return super.shouldConsume() && target != null;
		}

		@Override
		public float estimateDps() {
			if (!any || damage <= 0) return 0f;
			return damage * 60f * efficiency * coolantMultiplier;
		}

		@Override
		public void draw() {
			Draw.rect(baseRegion, x, y);
			Drawf.shadow(region, x - (size / 2f), y - (size / 2f), rotation - 90);
			Draw.rect(region, x, y, rotation - 90);

			//draw laser if applicable
			if (any && !isPayload()) {
				Draw.z(Layer.bullet);
				float ang = angleTo(lastX, lastY);

				Draw.mixcol(laserColor, Mathf.absin(4f, 0.6f));

				Drawf.laser(laser, laserStart, laserEnd,
						x + Angles.trnsx(ang, shootLength), y + Angles.trnsy(ang, shootLength),
						lastX, lastY, strength * efficiency * laserWidth);

				Draw.mixcol();
			}
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.f(rotation);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			rotation = read.f();
		}
	}
}
