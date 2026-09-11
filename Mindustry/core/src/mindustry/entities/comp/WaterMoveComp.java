package mindustry.entities.comp;

import static mindustry.Vars.world;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.annotations.Annotations.Component;
import mindustry.annotations.Annotations.Import;
import mindustry.annotations.Annotations.Replace;
import mindustry.content.Blocks;
import mindustry.entities.EntityCollisions;
import mindustry.entities.EntityCollisions.SolidPred;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Trail;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

@Component
abstract class WaterMoveComp implements Posc, Velc, Hitboxc, Unitc {
	@Import
	float x, y, rotation, speedMultiplier;
	@Import
	UnitType type;

	private final transient Trail tleft = new Trail(1);
	private final transient Trail tright = new Trail(1);
	private final transient Color trailColor = Blocks.water.mapColor.cpy().mul(1.5f);

	@Override
	public void update() {
		boolean flying = isFlying();
		for (int i = 0; i < 2; i++) {
			Trail t = i == 0 ? tleft : tright;
			t.length = type.trailLength;

			int sign = i == 0 ? -1 : 1;
			float cx = Angles.trnsx(rotation - 90, type.waveTrailX * sign, type.waveTrailY) + x, cy = Angles.trnsy(rotation - 90, type.waveTrailX * sign, type.waveTrailY) + y;
			t.update(cx, cy, world.floorWorld(cx, cy).isLiquid && !flying ? 1 : 0);
		}
	}

	@Override
	public void add() {
		tleft.clear();
		tright.clear();
	}

	@Override
	public void draw() {
		//TODO: move to UnitType
		float z = Draw.z();

		Draw.z(Layer.debris);

		Floor floor = tileOn() == null ? Blocks.air.asFloor() : tileOn().floor();
		Color color = Tmp.c1.set(floor.mapColor.equals(Color.black) ? Blocks.water.mapColor : floor.mapColor).mul(1.5f);
		trailColor.lerp(color, Mathf.clamp(Time.delta * 0.04f));

		tleft.draw(trailColor, type.trailScl);
		tright.draw(trailColor, type.trailScl);

		Draw.z(z);
	}

	@Replace
	@Override
	public SolidPred solidity() {
		return isFlying() || ignoreSolids() ? null : EntityCollisions::waterSolid;
	}

	@Replace
	@Override
	public boolean onSolid() {
		return EntityCollisions.waterSolid(tileX(), tileY());
	}

	@Replace
	public float floorSpeedMultiplier() {
		Floor on = isFlying() ? Blocks.air.asFloor() : floorOn();
		return (on.shallow ? 1f : 1.3f) * speedMultiplier;
	}

	public boolean onLiquid() {
		Tile tile = tileOn();
		return tile != null && tile.floor().isLiquid;
	}
}

