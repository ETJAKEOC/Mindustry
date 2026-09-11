package mindustry.tools;

import static mindustry.Vars.content;
import static mindustry.Vars.editor;
import static mindustry.Vars.headless;
import static mindustry.Vars.logic;
import static mindustry.Vars.mods;
import static mindustry.Vars.net;
import static mindustry.Vars.netServer;
import static mindustry.Vars.tree;
import static mindustry.Vars.world;

import java.util.Locale;

import arc.ApplicationCore;
import arc.ApplicationListener;
import arc.Core;
import arc.backend.headless.HeadlessApplication;
import arc.files.Fi;
import arc.util.I18NBundle;
import mindustry.Vars;
import mindustry.core.FileTree;
import mindustry.core.Logic;
import mindustry.core.NetServer;
import mindustry.core.World;
import mindustry.editor.MapEditor;
import mindustry.net.Net;

public class HeadlessSetup{

    public static void setup(){
        ApplicationCore core = new ApplicationCore(){
            @Override
            public void setup(){
                Core.settings.setDataDirectory(new Fi("../../tools/build/test_data"));
                Core.bundle = I18NBundle.createBundle(Core.files.internal("bundles/bundle"), Locale.ENGLISH);
                headless = true;
                net = new Net(null);
                tree = new FileTree();
                Vars.init();
                world = new World();
                content.createBaseContent();
                mods.loadScripts();
                content.createModContent();

                add(logic = new Logic());
                add(netServer = new NetServer());

                content.init();
                editor = new MapEditor();
            }
        };

        new HeadlessApplication(core){
            @Override
            protected void initialize(){
                //don't create a thread, just init on the main thread
                for(ApplicationListener listener : listeners){
                    listener.init();
                }
            }
        };
    }
}
