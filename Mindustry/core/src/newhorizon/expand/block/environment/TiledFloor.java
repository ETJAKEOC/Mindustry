package newhorizon.expand.block.environment;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Log;
import mindustry.Vars;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import newhorizon.NewHorizon;
import newhorizon.util.graphic.SpriteUtil;

public class TiledFloor extends Floor {
	public TextureRegion[][] spilt;

	public String tileName = "plating-floor";

	public int splitTileSize = 4;
	public int splitVariants = 12;

	public boolean useTiles = true;

	boolean splitLoaded = false;

	public TiledFloor(String name) {
		super(name);
	}

	public TiledFloor(String name, int sSize, int sVar) {
		super(name);
		splitTileSize = sSize;
		splitVariants = sVar;
	}

	public static TextureRegion findRegion(String name) {
		if (name == null || name.isEmpty()) return Core.atlas.find("error");

		String strippedName = name.startsWith("new-horizon-") ? name.substring("new-horizon-".length()) : name;

		TextureRegion region = Core.atlas.find(name);
		if (region != null && region.found()) return region;

		region = Core.atlas.find(NewHorizon.name(strippedName));
		if (region != null && region.found()) return region;

		region = Core.atlas.find(strippedName);
		if (region != null && region.found()) return region;

		String[] candidatePaths = {
				"sprites/blocks/environment/metal/" + strippedName + ".png",
				"sprites/blocks/environment/metal/autotile/" + strippedName + ".png",
				"sprites/blocks/environment/" + strippedName + ".png",
				"sprites/blocks/environment/autotile/" + strippedName + ".png",
				"sprites/" + strippedName + ".png"
		};

		for (String path : candidatePaths) {
			Fi file = Vars.tree != null ? Vars.tree.get(path) : Core.files.internal(path);
			if (file != null && file.exists()) {
				try {
					Texture texture = new Texture(file);
					texture.setFilter(Texture.TextureFilter.nearest);
					TextureRegion newRegion = new TextureRegion(texture);
					Core.atlas.addRegion(name, newRegion);
					Core.atlas.addRegion(strippedName, newRegion);
					Core.atlas.addRegion(NewHorizon.name(strippedName), newRegion);
					return newRegion;
				} catch (Throwable t) {
					Log.err("Failed to load loose texture " + path, t);
				}
			}
		}

		return Core.atlas.find(name);
	}

	@Override
	public void load() {
		super.load();

		var full = findRegion(tileName);

		if (autotile) {
			var tiled = findRegion(name + "-tiled");
			if (tiled.height == 128) autotileRegions = SpriteUtil.splitRegionArray(tiled, 32, 32);
			if (tiled.height == 136)
				autotileRegions = SpriteUtil.splitRegionArray(tiled, 32, 32, 1);

			if (autotileVariants > 1) {
				autotileVariantRegions = new TextureRegion[autotileVariants][];
				for (int i = 0; i < autotileVariants; i++) {
					String vName = name + "-tiled-" + (i + 1);
					var tiledVariant = findRegion(vName);
					if (tiledVariant.height == 128)
						autotileVariantRegions[i] = SpriteUtil.splitRegionArray(tiledVariant, 32, 32);
					else if (tiledVariant.height == 136)
						autotileVariantRegions[i] = SpriteUtil.splitRegionArray(tiledVariant, 32, 32, 1);
					else
						Log.err("Failed to load tile " + vName + ": " + tiledVariant.width + "x" + tiledVariant.height);
				}
			}
		}

		if (!useTiles) return;

		int pw = splitTileSize * splitVariants * 32;
		int ph = splitTileSize * 32;
		if (full.width == pw && full.height == ph) {
			spilt = new TextureRegion[splitTileSize * splitVariants][splitTileSize];
			for (int i = 0; i < splitVariants; i++) {
				spilt = full.split(32, 32);
			}
			splitLoaded = true;
		} else {
			Log.err("Failed to load tile " + tileName + " with size " + pw + "x" + ph, ". tiled disable.");
		}
	}

	private void drawTile(Tile tile) {
		int tx = tile.x / splitTileSize * splitTileSize;
		int ty = tile.y / splitTileSize * splitTileSize;

		int index = Mathf.randomSeed(Point2.pack(tx, ty), 0, splitVariants - 1);
		int ix = index * splitTileSize + tile.x - tx;
		int iy = splitTileSize - (tile.y - ty) - 1;
		Draw.rect(spilt[ix][iy], tile.worldx(), tile.worldy());
	}

	@Override
	public void drawBase(Tile tile) {
		if (useTiles && splitLoaded) drawTile(tile);
		super.drawBase(tile);
	}
}