package VanillaExpansion.ui;

import arc.Core;
import arc.files.Fi;
import arc.freetype.FreeTypeFontGenerator;
import arc.graphics.Color;
import arc.graphics.g2d.Font;
import arc.util.Log;
import mindustry.Vars;

public class VEFonts {
    public static Font novo;

    public static void loadFonts(){
        Fi fi = Vars.tree != null ? Vars.tree.get("fonts/novo.ttf") : Core.files.internal("fonts/novo.ttf");
        if(fi != null && fi.exists()) {
            gen(fi);
            Log.info("Font loaded from " + fi.path());
        } else {
            Log.info("Font novo.ttf not found");
        }
    }

    public static void gen(Fi fi){
        FreeTypeFontGenerator.FreeTypeFontParameter param = fontParameter();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fi);
        novo = generator.generateFont(param);
    }

    static FreeTypeFontGenerator.FreeTypeFontParameter fontParameter(){
        return new FreeTypeFontGenerator.FreeTypeFontParameter(){{
            size = 32;
            shadowColor = Color.white;
            shadowOffsetY = 0;
            incremental = true;
        }};
    }
}
