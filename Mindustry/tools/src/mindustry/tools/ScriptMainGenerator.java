package mindustry.tools;

import com.google.common.reflect.ClassPath;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Modifier;

import arc.Core;
import arc.Graphics.Cursor.SystemCursor;
import arc.files.Fi;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.TextureData;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.Shader;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.I18NBundle;
import arc.util.Interval;
import arc.util.Log;
import arc.util.Structs;
import arc.util.Time;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.io.SaveIO;
import mindustry.net.NetConnection;
import mindustry.world.Block;

public class ScriptMainGenerator{

    public static void main(String[] args) throws Exception{
        String base = "mindustry";
        Seq<String> blacklist = Seq.with("tools", "arc.flabel.effects");
        Seq<String> nameBlacklist = Seq.with();
        Seq<Class<?>> whitelist = Seq.with(Draw.class, Fill.class, Lines.class, Core.class, TextureAtlas.class, TextureRegion.class, Time.class, System.class, PrintStream.class,
        AtlasRegion.class, String.class, Mathf.class, Angles.class, Color.class, Runnable.class, Object.class, Icon.class, Tex.class, Shader.class,
        Sounds.class, Musics.class, Call.class, Texture.class, TextureData.class, Pixmap.class, I18NBundle.class, Interval.class, DataInput.class, DataOutput.class,
        DataInputStream.class, DataOutputStream.class, Integer.class, Float.class, Double.class, Long.class, Boolean.class, Short.class, Byte.class, Character.class);
        Seq<String> nopackage = Seq.with("java.lang", "java");

        Seq<Class<?>> classes = Seq.withArrays(
            getClasses("mindustry"),
            getClasses("arc"),
            getClasses("arc.func"),
            getClasses("arc.struct"),
            getClasses("arc.scene"),
            getClasses("arc.math"),
            getClasses("arc.audio"),
            getClasses("arc.input"),
            getClasses("arc.util"),
            getClasses("arc.files"),
            getClasses("arc.flabel"),
            getClasses("arc.struct")
        );
        classes.addAll(whitelist);
        classes.sort(Structs.comparing(Class::getName));

        classes.removeAll(type -> type.isSynthetic() || type.isAnonymousClass() || type.getCanonicalName() == null || type.getSimpleName().contains("Legacy") || Modifier.isPrivate(type.getModifiers())
        || blacklist.contains(s -> type.getName().startsWith(base + "." + s + ".")) || nameBlacklist.contains(type.getSimpleName()) || blacklist.contains(type.getPackage().getName()));
        classes.add(NetConnection.class, SaveIO.class, SystemCursor.class);

        classes.distinct();
        classes.sortComparing(Class::getName);
        ObjectSet<String> used = ObjectSet.with();

        StringBuilder result = new StringBuilder("//Generated class. Do not modify.\n");
        result.append("\n").append(new Fi("scripts/base.js").readString()).append("\n");
        for(Class type : classes){
            if(used.contains(type.getPackage().getName()) || nopackage.contains(s -> type.getName().startsWith(s))) continue;
            result.append("importPackage(Packages.").append(type.getPackage().getName()).append(")\n");
            used.add(type.getPackage().getName());
        }

        Log.info("Imported @ packages.", used.size);

        for(Class type : EventType.class.getClasses()){
            result.append("const ").append(type.getSimpleName()).append(" = ").append("Packages.").append(type.getName().replace('$', '.')).append("\n");
        }

        new Fi("scripts/global.js").writeString(result.toString());

        //map simple name to type
        Seq<String> packages = Seq.with(
        "mindustry.entities.effect",
        "mindustry.entities.bullet",
        "mindustry.entities.abilities",
        "mindustry.ai.types",
        "mindustry.type.weather",
        "mindustry.type.weapons",
        "mindustry.type.ammo",
        "mindustry.game.Objectives",
        "mindustry.world.blocks",
        "mindustry.world.consumers",
        "mindustry.world.draw",
        "mindustry.type",
        "mindustry.entities.pattern",
        "mindustry.entities.part"
        );

        String classTemplate = "package mindustry.mod;\n" +
        "\n" +
        "import arc.struct.*;\n" +
        "/** Generated class. Maps simple class names to concrete classes. For use in JSON mods. */\n" +
        "@SuppressWarnings(\"deprecation\")\n" +
        "public class ClassMap{\n" +
        "    public static final ObjectMap<String, Class<?>> classes = new ObjectMap<>();\n" +
        "    \n" +
        "    static{\n$CLASSES$" +
        "    }\n" +
        "}\n";

        StringBuilder cdef = new StringBuilder();

        Seq<Class<?>> mapped = classes.select(c -> Modifier.isPublic(c.getModifiers()) && packages.contains(c.getCanonicalName()::startsWith))
        .add(Block.class); //special case

        for(Class<?> c : mapped){
            cdef.append("        classes.put(\"").append(c.getSimpleName()).append("\", ").append(c.getCanonicalName()).append(".class);\n");
            SchemaGenerator.writeSchema(c);
        }

        SchemaGenerator.finalizeSchemas(mapped);

        new Fi("../../core/src/mindustry/mod/ClassMap.java").writeString(classTemplate.replace("$CLASSES$", cdef.toString()));
        Log.info("Generated @ class mappings.", mapped.size);

        SchemaGenerator.writeDefaultContent();
    }
    public static Seq<Class> getClasses(String packageName) throws Exception{
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();

        var result = new Seq<Class>();

        for(ClassPath.ClassInfo info : ClassPath.from(loader).getAllClasses()){
            if(info.getName().startsWith(packageName + ".")){
                result.add(info.load());
            }
        }
        return result;
    }
}
