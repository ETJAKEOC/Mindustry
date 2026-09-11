package mindustry.ui.dialogs;

import static arc.scene.actions.Actions.fadeIn;
import static arc.scene.actions.Actions.parallel;
import static arc.scene.actions.Actions.translateBy;

import arc.Core;
import arc.math.Interp;
import arc.scene.actions.Actions;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.gen.*;
import mindustry.type.Planet;

public class CampaignCompleteDialog extends BaseDialog {

	public CampaignCompleteDialog() {
		super("");

		addCloseListener();
		shouldPause = true;

		buttons.defaults().size(210f, 64f);
		buttons.button("@menu", Icon.left, () -> {
			hide();
			Vars.ui.paused.runExitSave();
		});

		buttons.button("@continue", Icon.ok, this::hide);
	}

	public void show(Planet planet) {
		cont.clear();

		cont.add(Core.bundle.format("campaign.complete", "[#" + planet.iconColor + "]" + planet.localizedName + "[]")).row();

		float playtime = planet.sectors.sumf(s -> s.hasSave() ? s.save.meta.timePlayed : 0) / 1000f;

		//TODO needs more info?
		cont.add(Core.bundle.format("campaign.playtime", UI.formatTime(playtime))).left().row();

		setTranslation(0f, -Core.graphics.getHeight());
		color.a = 0f;

		show(Core.scene, Actions.sequence(parallel(fadeIn(1.1f, Interp.fade), translateBy(0f, Core.graphics.getHeight(), 6f, Interp.pow5Out))));
	}
}
