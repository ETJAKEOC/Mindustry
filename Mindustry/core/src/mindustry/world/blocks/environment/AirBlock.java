package mindustry.world.blocks.environment;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import mindustry.annotations.Annotations.OverrideCallSuper;
import mindustry.world.Tile;

public class AirBlock extends Floor {

	public AirBlock(String name) {
		super(name);
		alwaysReplace = true;
		hasShadow = false;
		useColor = false;
		wall = this;
		generateIcons = false;
		needsSurface = false;
		canShadow = false;
		drawCached = false;
		drawDynamic = false;
	}

	@Override
	public void drawBase(Tile tile) {
	}

	@OverrideCallSuper
	@Override
	public void load() {
	}

	@OverrideCallSuper
	@Override
	public void init() {
		decoration = this;
	}

	@Override
	public boolean isHidden() {
		return true;
	}

	@Override
	public TextureRegion[] variantRegions() {
		if (variantRegions == null) {
			variantRegions = new TextureRegion[]{Core.atlas.find("clear")};
		}
		return variantRegions;
	}
}
