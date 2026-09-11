package mindustry;

import static arc.Core.app;
import static arc.Core.assets;
import static arc.Core.atlas;
import static arc.Core.batch;
import static arc.Core.files;
import static arc.Core.gl30;
import static arc.Core.graphics;
import static mindustry.Vars.asyncCore;
import static mindustry.Vars.bases;
import static mindustry.Vars.checkLaunch;
import static mindustry.Vars.clientLoaded;
import static mindustry.Vars.content;
import static mindustry.Vars.control;
import static mindustry.Vars.finishLaunch;
import static mindustry.Vars.ios;
import static mindustry.Vars.loadFileLogger;
import static mindustry.Vars.loadLogger;
import static mindustry.Vars.logic;
import static mindustry.Vars.mainExecutor;
import static mindustry.Vars.mapExtension;
import static mindustry.Vars.maps;
import static mindustry.Vars.maxDeltaClient;
import static mindustry.Vars.maxTextureSize;
import static mindustry.Vars.mobile;
import static mindustry.Vars.mods;
import static mindustry.Vars.netClient;
import static mindustry.Vars.netServer;
import static mindustry.Vars.platform;
import static mindustry.Vars.renderer;
import static mindustry.Vars.saveExtension;
import static mindustry.Vars.schematicExtension;
import static mindustry.Vars.schematics;
import static mindustry.Vars.tree;
import static mindustry.Vars.ui;

import arc.ApplicationCore;
import arc.ApplicationListener;
import arc.Core;
import arc.Events;
import arc.assets.AssetDescriptor;
import arc.assets.AssetManager;
import arc.assets.Loadable;
import arc.assets.loaders.MusicLoader;
import arc.assets.loaders.SoundLoader;
import arc.audio.Music;
import arc.audio.Sound;
import arc.files.Fi;
import arc.graphics.Gl;
import arc.graphics.Texture;
import arc.graphics.Texture.TextureWrap;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.PixmapPacker;
import arc.graphics.g2d.SpriteBatch;
import arc.graphics.g2d.TextureAtlas;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.OS;
import arc.util.Strings;
import arc.util.Threads;
import arc.util.Time;
import mindustry.ai.BaseRegistry;
import mindustry.audio.SoundPriority;
import mindustry.core.ContentLoader;
import mindustry.core.Control;
import mindustry.core.FileTree;
import mindustry.core.Logic;
import mindustry.core.NetClient;
import mindustry.core.NetServer;
import mindustry.core.PerfCounter;
import mindustry.core.Platform;
import mindustry.core.Renderer;
import mindustry.core.UI;
import mindustry.ctype.Content;
import mindustry.game.EventType.ClientCreateEvent;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.Saves.SaveSlot;
import mindustry.game.Schematics;
import mindustry.gen.*;
import mindustry.graphics.IntelGpuCheck;
import mindustry.graphics.LoadRenderer;
import mindustry.graphics.NvGpuInfo;
import mindustry.io.SaveIO;
import mindustry.io.SaveMeta;
import mindustry.maps.Map;
import mindustry.maps.MapPreviewLoader;
import mindustry.mod.Mod;
import mindustry.mod.Mods;
import mindustry.net.Net;
import mindustry.ui.Fonts;

public abstract class ClientLauncher extends ApplicationCore implements Platform{
    private static final int loadingFPS = 20;

    private long nextFrame;
    private long beginTime;
    private long lastTargetFps = -1;
    private boolean finished = false;
    private LoadRenderer loader;

    @Override
    public void setup(){
        String dataDir = System.getProperty("mindustry.data.dir", OS.env("MINDUSTRY_DATA_DIR"));
        if(dataDir != null){
            Core.settings.setDataDirectory(files.absolute(dataDir));
        }

        checkLaunch();
        loadLogger();

        loader = new LoadRenderer();
        Events.fire(new ClientCreateEvent());

        loadFileLogger();
        platform = this;
        maxTextureSize = Gl.getInt(Gl.maxTextureSize);
        beginTime = Time.millis();

        //debug GL information
        Log.info("[GL] Version: @", graphics.getGLVersion());
        Log.info("[GL] Max texture size: @", maxTextureSize);
        Log.info("[GL] Using @ API.", gl30 != null ? "OpenGL 3" : "OpenGL 2");

        IntelGpuCheck.init(graphics.getGLVersion().vendorString);

        boolean isIntel = IntelGpuCheck.wasIntel();

        if(isIntel && !graphics.isGL30Available()) Log.warn("[GL] Intel GPU detected on previous launch. Due to memory corruption issues, OpenGL 3 support has been disabled for Intel GPUs. See issue #11041.");

        if(gl30 == null && !isIntel) Log.warn("[GL] Your device or video drivers do not support OpenGL 3. This will cause performance issues.");

        if(NvGpuInfo.hasMemoryInfo()) Log.info("[GL] Total available VRAM: @", Strings.formatByteCount(NvGpuInfo.getMaxMemoryKB() * 1000L));

        if(maxTextureSize < 4096) Log.warn("[GL] Your maximum texture size is below the recommended minimum of 4096. This will cause severe performance issues.");

        Log.info("[JAVA] Version: @", OS.javaVersion);

        if(Core.app.isAndroid()) Log.info("[ANDROID] API level: @", Core.app.getVersion());
        if(Core.app.isIOS()) Log.info("[iOS] OS version: @", Core.app.getVersion());

        long ram = Runtime.getRuntime().maxMemory();

        if(!OS.isIos) Log.info("[RAM] Available: @", Strings.formatByteCount(ram));

        Time.setDeltaProvider(() -> {
            float result = Core.graphics.getDeltaTime() * 60f;
            return (Float.isNaN(result) || Float.isInfinite(result)) ? 1f : Mathf.clamp(result, 0.0001f, maxDeltaClient);
        });

        UI.loadColors();
        batch = new SpriteBatch();
        assets = new AssetManager();
        assets.setLoader(Texture.class, "." + mapExtension, new MapPreviewLoader());

        tree = new FileTree();
        assets.setLoader(Sound.class, new SoundLoader(tree){
            @Override
            public void loadAsync(AssetManager manager, String fileName, Fi file, SoundParameter parameter){

            }

            @Override
            public Sound loadSync(AssetManager manager, String fileName, Fi file, SoundParameter parameter){
                if(parameter != null && parameter.sound != null){
                    parameter.sound.loadLazy(file);

                    return parameter.sound;
                }else{
                    Sound sound = new Sound();
                    sound.loadLazy(file);
                    return sound;
                }
            }
        });
        assets.setLoader(Music.class, new MusicLoader(tree){
            @Override
            public void loadAsync(AssetManager manager, String fileName, Fi file, MusicParameter parameter){}

            @Override
            public Music loadSync(AssetManager manager, String fileName, Fi file, MusicParameter parameter){
                if(parameter != null && parameter.music != null){
                    mainExecutor.submit(() -> {
                        try{
                            parameter.music.load(file);
                        }catch(Throwable t){
                            Log.err("Error loading music: " + file, t);
                        }
                    });

                    return parameter.music;
                }else{
                    Music music = new Music();

                    mainExecutor.submit(() -> {
                        try{
                            music.load(file);
                        }catch(Throwable t){
                            Log.err("Error loading music: " + file, t);
                        }
                    });

                    return music;
                }
            }
        });

        assets.load("sprites/error.png", Texture.class);
        atlas = TextureAtlas.blankAtlas();
        Vars.net = new Net(platform.getNet());
        MapPreviewLoader.setupLoaders();
        mods = new Mods();
        schematics = new Schematics();

        Fonts.loadSystemCursors();

        assets.load(new Vars());

        Fonts.loadDefaultFont();

        //load fallback atlas if max texture size is below 4096
        assets.load(new AssetDescriptor<>(maxTextureSize >= 4096 ? "sprites/sprites.aatls" : "sprites/fallback/sprites.aatls", TextureAtlas.class)).loaded = t -> atlas = t;
        assets.loadRun("maps", Map.class, () -> maps.loadPreviews());

        Musics.load();
        Sounds.load();

        assets.loadRun("contentcreate", Content.class, () -> {
            content.createBaseContent();
            content.loadColors();
        }, () -> {
            mods.loadScripts();
            content.createModContent();
        });

        assets.load(mods);
        assets.loadRun("mergeUI", PixmapPacker.class, () -> {}, () -> Fonts.mergeFontAtlas(atlas));

        add(logic = new Logic());
        add(control = new Control());
        add(renderer = new Renderer());
        add(ui = new UI());
        add(netServer = new NetServer());
        add(netClient = new NetClient());

        assets.load(schematics);

        assets.loadRun("contentinit", ContentLoader.class, () -> content.init(), () -> content.load());
        assets.loadRun("baseparts", BaseRegistry.class, () -> {}, () -> bases.load());

        Core.assets.load("sprites/schematic-background.png", Texture.class).loaded = t -> t.setWrap(TextureWrap.repeat);
    }

    @Override
    public void add(ApplicationListener module){
        super.add(module);

        //autoload modules when necessary
        if(module instanceof Loadable l){
            assets.load(l);
        }
    }

    @Override
    public void resize(int width, int height){
        if(assets == null) return;

        if(!finished){
            Draw.proj().setOrtho(0, 0, width, height);
        }else{
            super.resize(width, height);
        }
    }

    @Override
    public void update(){
        PerfCounter.update.begin();

        int targetfps = ios ? 0 : Core.settings.getInt("fpscap", 120);
        boolean changed = lastTargetFps != targetfps && lastTargetFps != -1;
        boolean limitFps = targetfps > 0 && targetfps <= 240;

        lastTargetFps = targetfps;

        if(limitFps && !changed){
            nextFrame += (1000 * 1000000) / targetfps;
        }else{
            nextFrame = Time.nanos();
        }

        if(!finished){
            if(loader != null){
                loader.draw();
            }
            if(assets.update(1000 / loadingFPS)){
                loader.dispose();
                loader = null;
                SoundPriority.init();
                for(ApplicationListener listener : modules){
                    listener.init();
                }
                mods.eachClass(Mod::init);
                finished = true;
                Events.fire(new ClientLoadEvent());
                Log.info("Total time to load: @ms", Time.timeSinceMillis(beginTime));
                clientLoaded = true;
                super.resize(graphics.getWidth(), graphics.getHeight());
                app.post(() -> app.post(() -> app.post(() -> app.post(() -> {
                    super.resize(graphics.getWidth(), graphics.getHeight());

                    //mark initialization as complete
                    finishLaunch();
                }))));
            }
        }else{
            asyncCore.begin();

            super.update();

            asyncCore.end();
        }

        if(limitFps){
            long current = Time.nanos();
            if(nextFrame > current){
                long toSleep = nextFrame - current;
                Threads.sleep(toSleep / 1000000, (int)(toSleep % 1000000));
            }
        }

        PerfCounter.update.end();

        long rawUpdate = PerfCounter.update.latestValueNs();
        for(var other : PerfCounter.displayedCounters){
            if(other != PerfCounter.other) rawUpdate -= other.latestValueNs();
        }
        PerfCounter.other.add(rawUpdate);

        for(var counter : PerfCounter.all){
            counter.checkUpdate();
        }
    }

    @Override
    public void exit(){
        //on graceful exit, finish the launch normally.
        Vars.finishLaunch();
    }

    @Override
    public void init(){
        nextFrame = Time.nanos();
        setup();
    }

    @Override
    public void resume(){
        if(finished){
            super.resume();
        }
    }

    @Override
    public void pause(){
        //when the user tabs out on mobile, the exit() event doesn't fire reliably - in that case, just assume they're about to kill the app
        //this isn't 100% reliable but it should work for most cases
        if(mobile){
            Vars.finishLaunch();
        }
        if(finished){
            super.pause();
        }
    }

    @Override
    public void fileDropped(Fi file){
        if(OS.isIos || OS.isAndroid) return;

        if(file.extEquals(saveExtension) || file.extEquals(schematicExtension)){
            handleFileImport(file);
        }
    }

    public static void runOnClientLoad(Runnable run){
        if(clientLoaded){
            run.run();
        }else{
            Events.on(ClientLoadEvent.class, e -> run.run());
        }
    }

    /** Can be called from any thread. The file must exist and not have any permission nonsense guarding it. */
    public static void handleFileImport(Fi file){
        Core.app.post(() -> {
            try{
                if(Schematics.isSchematic(file)){
                    ui.schematics.show();
                    ui.schematics.importAndShow(file);
                }else{
                    SaveMeta meta = SaveIO.getMeta(file);
                    if(!meta.isMap()){ //open save
                        if(meta.rules.sector == null){
                            //not sure if this is a great idea, but leaving the editor open would lead to catastrophic bugs
                            if(ui.editor.isShown()) ui.editor.hide();
                            if(ui.maps.isShown()) ui.maps.hide();

                            SaveSlot slot = control.saves.importSave(file);
                            ui.load.runLoadSave(slot);
                        }else{
                            ui.showErrorMessage("@save.nocampaign");
                        }
                    }else{ //open map
                        if(!ui.maps.isShown()) ui.maps.show();
                        ui.maps.tryImportMap(file, result -> ui.maps.showMap(result));
                    }
                }
            }catch(Throwable e){
                Log.err("Failed to import file", e);
                ui.showException("@save.import.invalid", e);
            }
        });
    }
}
