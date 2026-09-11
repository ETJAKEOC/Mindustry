package mindustry.world.blocks.environment;

import arc.graphics.g2d.Draw;
import arc.util.Eachable;
import arc.util.Nullable;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.world.Block;
import mindustry.world.Tile;

public class RemoveOre extends OverlayFloor {

	public RemoveOre(String name) {
		super(name);

		allowRectanglePlacement = true;
		placeEffect = Fx.rotateBlock;
		instantBuild = true;
		ignoreBuildDarkness = true;
		placeableLiquid = true;
		inEditor = false;
		variants = 0;
	}

	@Override
	public void drawPlan(BuildPlan plan, Eachable<BuildPlan> list, boolean valid, float alpha) {
		Draw.reset();
		Draw.alpha(alpha * (valid ? 1f : 0.2f));
		float prevScale = Draw.scl;
		Draw.scl *= plan.animScale;
		drawPlanRegion(plan, list);
		Draw.scl = prevScale;
		Draw.reset();
	}

	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		return tile.overlay() != Blocks.air;
	}

	@Override
	public boolean canReplace(Block other) {
		return true;
	}

	@Override
	public void placeEnded(Tile tile, @Nullable Unit builder, int rotation, Object config) {
		tile.setOverlay(Blocks.air);
	}

}
