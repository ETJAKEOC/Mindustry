package mindustry.desktop;

import arc.*;
import arc.Files.*;
import arc.backend.sdl.*;
import arc.backend.sdl.jni.*;
import arc.filedialogs.*;
import arc.files.*;
import arc.math.*;
import arc.profiling.*;
import arc.struct.*;
import arc.util.*;
import arc.util.Log.*;
import arc.util.serialization.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.mod.Mods.*;
import mindustry.net.*;
import mindustry.net.Net.*;
import mindustry.service.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.FileChooser.*;
import mindustry.ui.dialogs.*;

import java.io.*;

import static mindustry.Vars.*;

public class DesktopLauncher extends ClientLauncher{
    public final static long discordID = 610508934456934412L;
    public final String[] args;

    boolean useDiscord = !OS.hasProp("nodiscord"), loadError = false;
    Throwable steamError;

    public static void main(String[] arg){
        try{
            Version.init();
            Vars.loadLogger();
            Vars.loadFileLogger(new Fi(Version.isSteam ? "saves" : OS.getAppDataDirectoryString(appName)).child("last_log.txt"));

            check32Bit();
            checkJavaVersion();

            new SdlApplication(new DesktopLauncher(arg), new SdlConfig(){{
                title = "Mindustry";
                maximized = true;
                coreProfile = true;
                width = 1920;
                height = 1080;

                //on Windows, Intel drivers might be buggy with OpenGL 3.x, so only use 2.x. See https://github.com/Anuken/Mindustry/issues/11041
                if(IntelGpuCheck.wasIntel()){
                    allowGl30 = false;
                    coreProfile = false;
                    glVersions = new int[][]{{2, 1}, {2, 0}};
                }else if(OS.isMac){
                    //MacOS supports 4.1 at most
                    glVersions = new int[][]{{4, 1}, {3, 2}, {2, 1}, {2, 0}};
                }else{
                    //try essentially every OpenGL version
                    glVersions = new int[][]{{4, 6}, {4, 5}, {4, 4}, {4, 1}, {3, 3}, {3, 2}, {3, 1}, {2, 1}, {2, 0}};
                }

                for(int i = 0; i < arg.length; i++){
                    if(arg[i].charAt(0) == '-'){
                        String name = arg[i].substring(1);
                        switch(name){
                            case "width" -> width = Strings.parseInt(arg[i + 1], width);
                            case "height" -> height = Strings.parseInt(arg[i + 1], height);
                            case "gl" -> {
                                String str = arg[i + 1];
                                if(str.contains(".")){
                                    String[] split = str.split("\\.");
                                    if(split.length == 2 && Strings.canParsePositiveInt(split[0]) && Strings.canParsePositiveInt(split[1])){
                                        glVersions = new int[][]{{Strings.parseInt(split[0]), Strings.parseInt(split[1])}};
                                        allowGl30 = true; //when a version is explicitly specified always allow GL 3
                                        break;
                                    }
                                }
                                Log.err("Invalid GL version format string: '@'. GL version must be of the form <major>.<minor>", str);
                            }
                            case "coreGl" -> coreProfile = true;
                            case "compatibilityGl" -> coreProfile = false;
                            case "antialias" -> samples = 16;
                            case "debug" -> Log.level = LogLevel.debug;
                            case "maximized" -> maximized = Boolean.parseBoolean(arg[i + 1]);
                            case "testMobile" -> testMobile = true;
                            case "gltrace" -> {
                                Events.on(ClientCreateEvent.class, e -> {
                                    var profiler = new GLProfiler(Core.graphics);
                                    profiler.enable();
                                    Core.app.addListener(new ApplicationListener(){
                                        @Override
                                        public void update(){
                                            profiler.reset();
                                        }
                                    });
                                });
                            }
                        }
                    }
                }
                setWindowIcon(FileType.internal, "icons/icon_64.png");
            }});
        }catch(Throwable e){
            handleCrash(e);
        }
    }

    static void checkJavaVersion(){
        if(OS.javaVersionNumber < 17){
            //this is technically a lie: Java 25 isn't actually required (17 is), but I want people to get the highest available version they can.
            //Java 25 *might* be required in the future for FFM bindings.
            ErrorDialog.show("Java 25 is required to run Mindustry. Your version: " + OS.javaVersionNumber + "\n" +
            "\n" +
            "Please uninstall your current Java version, and download Java 25.\n" +
            "\n" +
            "It is recommended to download Java from adoptium.net.\n" +
            "Do not download from java.com, as that will give you Java 8 by default.");
        }
    }

    static void check32Bit(){
        if(OS.isWindows && !OS.is64Bit){
            String versionWarning = "";

            if(Version.isSteam){
                versionWarning = "\n\nIf you are unable to upgrade, consider switching to the legacy v7 branch on Steam, which is the last release that supported 32-bit windows:\n(properties -> betas -> select version-7.0 in the drop-down box).";
            }else if(OS.javaVersion.equals("1.8.0_151-1-ojdkbuild")){ //version string of JVM packaged with the 32-bit version of the game on itch/steam
                versionWarning = "\n\nMake sure you have downloaded the 64-bit version of the game, not the 32-bit one.";
            }else if(OS.javaVersionNumber < 25){
                //technically, java 25 isn't required yet, but it might be in the future, so tell users to get that one
                versionWarning = "\n\nYour current Java version is: " + OS.javaVersionNumber + ". To run the game, upgrade to Java 25 on a 64-bit machine.";
            }

            ErrorDialog.show("You are running a 32-bit installation of Windows and/or a 32-bit JVM. 32-bit windows is no longer supported." + versionWarning);
        }
    }

    public DesktopLauncher(String[] args){
        this.args = args;
    }

    static void handleCrash(Throwable e){
        boolean badGPU = false;
        String finalMessage = Strings.getFinalMessage(e);
        String total = Strings.getCauses(e).toString();

        if(total.contains("Couldn't create window") || total.contains("OpenGL 2.0 or higher") || total.toLowerCase().contains("pixel format") || total.contains("GLEW")|| total.contains("unsupported combination of formats")){

            message(
                total.contains("Couldn't create window") ? "A graphics initialization error has occured! Try to update your graphics drivers:\n" + finalMessage :
                            "Your graphics card does not support the right OpenGL features.\n" +
                                    "Try to update your graphics drivers. If this doesn't work, your computer may not support Mindustry.\n\n" +
                                    "Full message: " + finalMessage);
            badGPU = true;
        }

        boolean fbgp = badGPU;

        LoadedMod cause = CrashHandler.getModCause(e);
        String causeString = cause == null ? (Structs.contains(e.getStackTrace(), st -> st.getClassName().contains("rhino.gen.")) ? "A mod or script has caused Mindustry to crash.\nConsider disabling your mods if the issue persists.\n" : "Mindustry has crashed.") :
            "'" + cause.meta.displayName + "' (" + cause.name + ") has caused Mindustry to crash.\nConsider disabling this mod if issues persist.\n";

        CrashHandler.handle(e, file -> {
            Throwable fc = Strings.getFinalCause(e);
            if(!fbgp){
                message(causeString + "\nThe logs have been saved in:\n" + file.getAbsolutePath() + "\n" + fc.getClass().getSimpleName().replace("Exception", "") + (fc.getMessage() == null ? "" : ":\n" + fc.getMessage()));
            }
        });
    }

    @Override
    public void showFileChooser(FileChooserParams params){
        Threads.daemon(() -> {
            try{
                FileDialogs.loadNatives();
                var ext = params.extensions;

                String result;
                String[] patterns = new String[ext.length];
                for(int i = 0; i < ext.length; i++){
                    patterns[i] = "*." + ext[i];
                }

                //on MacOS, .msav is not properly recognized until I put garbage into the array?
                if(patterns.length == 1 && OS.isMac && params.open){
                    patterns = new String[]{"", "*." + ext[0]};
                }

                if(params.open){
                    result = FileDialogs.openFileDialog(params.title, FileChooserDialog.getLastDirectory().absolutePath() + "/", patterns, "." + ext[0] + " files", params.allowMultiple);
                }else{
                    result = FileDialogs.saveFileDialog(params.title, FileChooserDialog.getLastDirectory().child(params.fileName).absolutePath(), patterns, "." + ext[0] + " files");
                }

                if(result == null) return;

                if(result.length() > 1 && result.contains("\n")){
                    result = result.split("\n")[0];
                }

                //cancelled selection, ignore result
                if(result.isEmpty() || result.equals("\n")) return;
                if(result.endsWith("\n")) result = result.substring(0, result.length() - 1);

                Fi[] resultFiles = Seq.with(result.split("\\|")).map(Core.files::absolute).toArray(Fi.class);

                if(result.isEmpty()) return;

                Core.app.post(() -> {
                    FileChooserDialog.setLastDirectory(resultFiles[0].isDirectory() ? resultFiles[0] : resultFiles[0].parent());

                    if(!params.open){
                        Fi single = resultFiles[0];
                        //fix extension to match filters
                        if(!Structs.contains(params.extensions, single::extEquals)){
                            single = single.parent().child(single.nameWithoutExtension() + "." + ext[0]);
                        }
                        params.handleChooseResult(single);
                    }else{
                        params.handleChooseResult(resultFiles);
                    }
                });
            }catch(Throwable error){
                Log.err("Failed to execute native file chooser", error);
                Core.app.post(() -> FileChooser.showFallbackFileChooser(params));
            }
        });
    }

    private static void message(String message){
        SDL.SDL_ShowSimpleMessageBox(SDL.SDL_MESSAGEBOX_ERROR, "oh no", message);
    }
}
