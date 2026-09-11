package newhorizon;

import arc.Core;
import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType;
import newhorizon.expand.game.NHWorldData;
import newhorizon.expand.logic.components.CutsceneControl;
import newhorizon.expand.logic.components.CutsceneUI;
import newhorizon.util.game.UpdateProxy;
import newhorizon.util.graphic.ScreenShaderDrawer;
import newhorizon.util.ui.TableFunc;

public class NHVars {
    public static NHModCore core;
    public static NHWorldData worldData;
    public static NHRenderer renderer;

    public static CutsceneControl cutscene;
    public static CutsceneUI cutsceneUI;

    public static void init() {
        if (core != null) return;

        worldData = new NHWorldData();

        UpdateProxy.init();

        cutscene = new CutsceneControl();
        cutsceneUI = new CutsceneUI();

        core = new NHModCore();
        if (Core.app != null) Core.app.addListener(core);

        if (Vars.headless) return;
        initHeadless();
    }

    public static void initHeadless() {
        if (Core.scene == null) {
            Events.on(EventType.ClientLoadEvent.class, e -> initHeadless());
            return;
        }
        if (renderer != null) return;
        renderer = new NHRenderer();
        ScreenShaderDrawer.init();

        NHSetting.loadUI();
        if (NHSetting.getBool(NHSetting.DEBUG_PANEL)) TableFunc.tableMain();
    }
}
