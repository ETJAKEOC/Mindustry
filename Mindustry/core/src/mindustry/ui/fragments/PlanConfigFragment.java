package mindustry.ui.fragments;

import static mindustry.Vars.control;
import static mindustry.Vars.player;
import static mindustry.Vars.tilesize;

import arc.Core;
import arc.Events;
import arc.math.Interp;
import arc.math.geom.Vec2;
import arc.scene.Group;
import arc.scene.actions.Actions;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Align;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;

/**
 * Displays the configuration UI for build plans before they have been placed.
 * Shamelessly stolen from Foo's Client.
 */
public class PlanConfigFragment {
	Table table = new Table();
	BuildPlan selected;

	public void build(Group parent) {
		table.visible = false;
		parent.addChild(table);

		Events.on(EventType.ResetEvent.class, e -> forceHide());
	}

	public void showConfig(BuildPlan plan) {
		if (this.selected == plan || plan.block == null) {
			hide();
			return;
		}
		Block block = plan.block;
		if (!block.configurable) return;
		selected = plan;
		table.clear();

		var options = new Seq<UnlockableContent>();
		block.getPlanConfigs(options);

		if (options.isEmpty()) return;

		ItemSelection.buildTable(
				table, options,
				() -> selected != null ? (selected.config instanceof UnlockableContent c ? c : null) : null,
				content -> {
					selected.config = content;
					hide();
				},
				block.selectionRows, block.selectionColumns
		);
		table.pack();
		table.setTransform(true);
		table.visible = true;
		table.actions(Actions.scaleTo(0f, 1f), Actions.visible(true),
				Actions.scaleTo(1f, 1f, 0.07f, Interp.pow3Out));
		table.update(() -> {
			table.setOrigin(Align.center);
			if (plan.isDone() || !(control.input.selectPlans.contains(plan) || player.dead() || player.unit().plans.contains(plan))) {
				this.hide();
				return;
			}
			Vec2 pos = Core.input.mouseScreen(plan.drawx(), plan.drawy() - block.size * tilesize / 2.0F - 1);
			table.setPosition(pos.x, pos.y, Align.top);
		});
	}

	public void forceHide() {
		table.visible = false;
		selected = null;
	}

	public void hide() {
		selected = null;
		table.actions(Actions.scaleTo(0f, 1f, 0.06f, Interp.pow3Out), Actions.visible(false));
	}
}