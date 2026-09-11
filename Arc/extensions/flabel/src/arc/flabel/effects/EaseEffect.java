package arc.flabel.effects;

import arc.flabel.FEffect;
import arc.flabel.FGlyph;
import arc.flabel.FLabel;
import arc.math.Interp;
import arc.struct.IntFloatMap;
import arc.util.Strings;

/**
 * Moves the text vertically easing it into the final position. Doesn't repeat itself.
 */
public class EaseEffect extends FEffect {
	private static final float defaultDistance = 0.15f;
	private static final float defaultIntensity = 0.075f;
	private final IntFloatMap timePassedByGlyphIndex = new IntFloatMap();
	public float distance = 1; // How much of their height they should move
	public float intensity = 1; // How fast the glyphs should move
	public boolean elastic = false; // Whether or not the glyphs have an elastic movement

	@Override
	public void applyParams(String[] params) {
		if (params.length > 0) distance = Strings.parseFloat(params[0], 1f);
		if (params.length > 1) intensity = Strings.parseFloat(params[1], 1f);
		if (params.length > 2) elastic = Boolean.parseBoolean(params[2]);
	}

	@Override
	protected void onApply(FLabel label, FGlyph glyph, int localIndex, float delta) {
		// Calculate real intensity
		float realIntensity = intensity * (elastic ? 3f : 1f) * defaultIntensity;

		// Calculate progress
		float timePassed = timePassedByGlyphIndex.increment(localIndex, 0, delta);
		float progress = timePassed / realIntensity;
		if (progress < 0 || progress > 1) {
			return;
		}

		// Calculate offset
		Interp interpolation = elastic ? Interp.swingOut : Interp.sine;
		float interpolatedValue = interpolation.apply(1, 0, progress);
		float y = getLineHeight(label) * distance * interpolatedValue * defaultDistance;

		// Apply changes
		glyph.yoffset += y;
	}

}
