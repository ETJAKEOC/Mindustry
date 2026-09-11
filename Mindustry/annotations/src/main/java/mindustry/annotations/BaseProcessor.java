package mindustry.annotations;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.sun.source.util.Trees;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Log.LogLevel;
import arc.util.OS;
import arc.util.Structs;
import mindustry.annotations.util.Selement;
import mindustry.annotations.util.Smethod;
import mindustry.annotations.util.Stype;
import mindustry.annotations.util.Svar;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public abstract class BaseProcessor extends AbstractProcessor{
    /** Name of the base package to put all the generated classes. */
    public static final String packageName = "mindustry.gen";

    public static Types typeu;
    public static Elements elementu;
    public static Filer filer;
    public static Messager messager;
    public static Trees trees;

    protected int round;
    protected int rounds = 1;
    protected RoundEnvironment env;
    protected Fi rootDirectory;

    public static String getMethodName(Element element){
        return ((TypeElement)element.getEnclosingElement()).getQualifiedName().toString() + "." + element.getSimpleName();
    }

    public static boolean isPrimitive(String type){
        return type.equals("boolean") || type.equals("byte") || type.equals("short") || type.equals("int")
        || type.equals("long") || type.equals("float") || type.equals("double") || type.equals("char");
    }

    public static boolean instanceOf(String type, String other){
        TypeElement a = elementu.getTypeElement(type);
        TypeElement b = elementu.getTypeElement(other);
        return a != null && b != null && typeu.isSubtype(a.asType(), b.asType());
    }

    public static String getDefault(String value){
        switch(value){
            case "float":
            case "double":
            case "int":
            case "long":
            case "short":
            case "char":
            case "byte":
                return "0";
            case "boolean":
                return "false";
            default:
                return "null";
        }
    }

    //in bytes
    public static int typeSize(String kind){
        switch(kind){
            case "boolean":
            case "byte":
                return 1;
            case "short":
                return 2;
            case "float":
            case "char":
            case "int":
                return 4;
            case "long":
                return 8;
            default:
                throw new IllegalArgumentException("Invalid primitive type: " + kind + "");
        }
    }

    public static String simpleName(String str){
        return str.contains(".") ? str.substring(str.lastIndexOf('.') + 1) : str;
    }

    public static TypeName tname(String pack, String simple){
        return ClassName.get(pack, simple);
    }

    public static TypeName tname(String name){
        if(!name.contains(".")) return ClassName.get(packageName, name);

        String pack = name.substring(0, name.lastIndexOf("."));
        String simple = name.substring(name.lastIndexOf(".") + 1);
        return ClassName.get(pack, simple);
    }

    public static TypeName tname(Class<?> c){
        return ClassName.get(c).box();
    }

    public static TypeVariableName getTVN(TypeParameterElement element){
        String name = element.getSimpleName().toString();
        List<? extends TypeMirror> boundsMirrors = element.getBounds();

        List<TypeName> boundsTypeNames = new ArrayList<>();
        for(TypeMirror typeMirror : boundsMirrors){
            boundsTypeNames.add(TypeName.get(typeMirror));
        }

        return TypeVariableName.get(name, boundsTypeNames.toArray(new TypeName[0]));
    }

    public static void write(TypeSpec.Builder builder) throws Exception{
        write(builder, null);
    }

    public static void write(TypeSpec.Builder builder, Seq<String> imports) throws Exception{
        builder.superinterfaces.sort(Structs.comparing(t -> t.toString()));
        builder.methodSpecs.sort(Structs.comparing(m -> m.toString()));
        builder.fieldSpecs.sort(Structs.comparing(f -> f.name));

        JavaFile file = JavaFile.builder(packageName, builder.build()).skipJavaLangImports(true).build();
        String writeString;

        if(imports != null){
            imports = imports.map(m -> Seq.with(m.split("\n")).sort().toString("\n"));
            imports.sort();
            String rawSource = file.toString();
            Seq<String> result = new Seq<>();
            for(String s : rawSource.split("\n", -1)){
                result.add(s);
                if(s.startsWith("package ")){
                    result.add("");
                    for (String i : imports){
                        result.add(i);
                    }
                }
            }

            writeString = result.toString("\n");
        }else{
            writeString = file.toString();
        }

        JavaFileObject object = filer.createSourceFile(file.packageName + "." + file.typeSpec.name, file.typeSpec.originatingElements.toArray(new Element[0]));
        Writer stream = object.openWriter();
        stream.write(writeString);
        stream.close();
    }

    public Seq<Selement> elements(Class<? extends Annotation> type){
        return Seq.with(env.getElementsAnnotatedWith(type)).map(Selement::new);
    }

    public Seq<Stype> types(Class<? extends Annotation> type){
        return Seq.with(env.getElementsAnnotatedWith(type)).select(e -> e instanceof TypeElement)
            .map(e -> new Stype((TypeElement)e));
    }

    public Seq<Svar> fields(Class<? extends Annotation> type){
        return Seq.with(env.getElementsAnnotatedWith(type)).select(e -> e instanceof VariableElement)
        .map(e -> new Svar((VariableElement)e));
    }

    public Seq<Smethod> methods(Class<? extends Annotation> type){
        return Seq.with(env.getElementsAnnotatedWith(type)).select(e -> e instanceof ExecutableElement)
        .map(e -> new Smethod((ExecutableElement)e));
    }

    public static void err(String message){
        messager.printMessage(Kind.ERROR, message);
        Log.err("[CODEGEN ERROR] " +message);
    }

    public static void err(String message, Element elem){
        messager.printMessage(Kind.ERROR, message, elem);
        Log.err("[CODEGEN ERROR] " + message + ": " + elem);
    }

    public static void err(String message, Selement elem){
        err(message, elem.e);
    }

    @Override
    public synchronized void init(ProcessingEnvironment env){
        super.init(env);

        trees = Trees.instance(env);
        typeu = env.getTypeUtils();
        elementu = env.getElementUtils();
        filer = env.getFiler();
        messager = env.getMessager();

        Log.level = LogLevel.info;

        if(System.getProperty("debug") != null){
            Log.level = LogLevel.debug;
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv){
        if(round++ >= rounds) return false; //only process 1 round
        if(rootDirectory == null){
            try{
                String rawPath = filer.getResource(StandardLocation.CLASS_OUTPUT, "no", "no")
                .toUri().toURL().toString().substring(OS.isWindows ? 6 : "file:".length()).replace("%20", " ");
                Fi dir = Fi.get(rawPath);
                while(dir != null && !dir.child("core").exists() && !dir.child("settings.gradle").exists()){
                    dir = dir.parent();
                }
                rootDirectory = dir != null ? dir : Fi.get(rawPath).parent();
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        }

        this.env = roundEnv;
        try{
            process(roundEnv);
        }catch(Throwable e){
            Log.err(e);
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion(){
        return SourceVersion.RELEASE_17;
    }

    public void process(RoundEnvironment env) throws Exception{

    }
}
