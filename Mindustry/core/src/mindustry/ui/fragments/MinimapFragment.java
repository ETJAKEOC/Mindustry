package mindustry.ui.fragments;

import static arc.Core.input;
import static arc.Core.scene;
import static mindustry.Vars.control;
import static mindustry.Vars.mobile;
import static mindustry.Vars.player;
import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.ui;
import static mindustry.Vars.world;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.event.ElementGestureListener;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Scl;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.gen.*;
import mindustry.input.Binding;
import mindustry.ui.Styles;

public class MinimapFragment {
	public Element elem;
	float panx, pany, zoom = 1f, lastZoom = -1;
	private boolean shown;
	private float baseSize = Scl.scl(5f);

	protected Rect getRectBounds() {
		float
				w = Core.graphics.getWidth(),
				h = Core.graphics.getHeight(),
				ratio = renderer.minimap.getTexture() == null ? 1f : (float) renderer.minimap.getTexture().height / renderer.minimap.getTexture().width,
				size = baseSize * zoom * world.width();

		return Tmp.r1.set(w / 2f + panx * zoom - size / 2f, h / 2f + pany * zoom - size / 2f * ratio, size, size * ratio);
	}

	public void build(Group parent) {
		elem = parent.fill((x, y, w, h) -> {
			w = Core.graphics.getWidth();
			h = Core.graphics.getHeight();
			float size = baseSize * zoom * world.width();

			Draw.color(Color.black);
			Fill.crect(0, 0, w, h);

			if (renderer.minimap.getTexture() != null) {
				Draw.color();
				float ratio = (float) renderer.minimap.getTexture().height / renderer.minimap.getTexture().width;
				TextureRegion reg = Draw.wrap(renderer.minimap.getTexture());
				Draw.rect(reg, w / 2f + panx * zoom, h / 2f + pany * zoom, size, size * ratio);

				Rect bounds = getRectBounds();
				renderer.minimap.drawEntities(bounds.x, bounds.y, bounds.width, bounds.height, true);
			}

			Draw.reset();
		});

		elem.visible(() -> shown);
		elem.update(() -> {
			if (!ui.chatfrag.shown() && !(scene.getKeyboardFocus() instanceof TextField)) {
				elem.requestKeyboard();
				elem.requestScroll();
			}
			elem.setFillParent(true);
			elem.setBounds(0, 0, Core.graphics.getWidth(), Core.graphics.getHeight());

			if (Core.input.keyTap(Binding.menu)) {
				shown = false;
			}
		});
		elem.touchable = Touchable.enabled;

		elem.addListener(new ElementGestureListener() {

			@Override
			public void zoom(InputEvent event, float initialDistance, float distance) {
				if (lastZoom < 0) {
					lastZoom = zoom;
				}

				zoom = Mathf.clamp(distance / initialDistance * lastZoom, 0.25f, 10f);
			}

			@Override
			public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
				if (event.keyCode != KeyCode.mouseRight) {
					panx += deltaX / zoom;
					pany += deltaY / zoom;
				} else {
					panTo(x, y);
				}
			}

			@Override
			public void touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
				super.touchDown(event, x, y, pointer, button);
				if (button == KeyCode.mouseRight) {
					panTo(x, y);
				}
			}

			@Override
			public void tap(InputEvent event, float x, float y, int count, KeyCode button) {
				super.tap(event, x, y, count, button);
				if (mobile && count == 2) {
					Vec2 pos = convert(x, y);
					Call.pingLocation(Vars.player, pos.x, pos.y, null);
				}
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) {
				lastZoom = zoom;
			}
		});

		elem.addListener(new InputListener() {

			@Override
			public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
				zoom = Mathf.clamp(zoom - amountY / 10f * zoom, 0.25f, 10f);
				return true;
			}

			@Override
			public boolean keyDown(InputEvent event, KeyCode keycode) {
				if (keycode == Binding.ping.value.key) {
					Vec2 pos = convert(event.stageX, event.stageY).cpy();
					if (input.ctrl()) {
						ui.showTextInput("", "@ping.text", Vars.maxPingTextLength, "", result -> Call.pingLocation(Vars.player, pos.x, pos.y, UI.formatIcons(result)));
					} else {
						Call.pingLocation(Vars.player, pos.x, pos.y, null);
					}
				}
				return super.keyDown(event, keycode);
			}
		});

		parent.fill(t -> {
			t.setFillParent(true);
			t.visible(() -> shown);
			t.update(() -> t.setBounds(0, 0, Core.graphics.getWidth(), Core.graphics.getHeight()));

			t.add("@minimap").style(Styles.outlineLabel).pad(10f);
			t.row();
			t.add().growY();
			t.row();
			t.button("@back", Icon.leftOpen, () -> shown = false).size(220f, 60f).pad(10f);
		});
	}

	public void panTo(float relativeX, float relativeY) {
		control.input.panCamera(convert(relativeX, relativeY).clamp(-tilesize / 2f, -tilesize / 2f, world.unitWidth() + tilesize / 2f, world.unitHeight() + tilesize / 2f));
	}

	public Vec2 convert(float relativeX, float relativeY) {
		Rect r = getRectBounds();
		return Tmp.v1.set(relativeX, relativeY).sub(r.x - scene.marginLeft, r.y - scene.marginBottom).scl(1f / r.width, 1f / r.height).scl(world.unitWidth(), world.unitHeight()).sub(tilesize / 2f, tilesize / 2f);
	}

	public boolean shown() {
		return shown;
	}

	public void hide() {
		shown = false;
	}

	public void toggle() {
		if (renderer.minimap.getTexture() != null) {
			float size = baseSize * zoom * world.width();
			float ratio = (float) renderer.minimap.getTexture().height / renderer.minimap.getTexture().width;
			float px = player.dead() ? Core.camera.position.x : player.x, py = player.dead() ? Core.camera.position.y : player.y;
			panx = (size / 2f - px / (world.width() * tilesize) * size) / zoom;
			pany = (size * ratio / 2f - py / (world.height() * tilesize) * size * ratio) / zoom;
		}

		shown = !shown;
	}
}
