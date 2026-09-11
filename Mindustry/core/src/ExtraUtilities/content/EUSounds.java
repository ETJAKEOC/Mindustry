package ExtraUtilities.content;

import arc.audio.Sound;
import arc.files.Fi;
import mindustry.Vars;

public class EUSounds {
    public static Sound
            prismLoop = new Sound(),
            ciallo = new Sound();

    public static void load(){
        prismLoop = loadSound("prism-beam.ogg");
        ciallo = loadSound("ciallo.mp3");
    }

    public static Sound loadSound(String name){
        if (Vars.tree == null) return new Sound();
        Fi file = Vars.tree.get("sounds/" + name);
        return file.exists() ? new Sound(file) : new Sound();
    }
}
