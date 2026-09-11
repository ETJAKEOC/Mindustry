package mindustry.mod;

import java.net.URLClassLoader;

public class ClassLoaderCloser{

    /** Workaround for the close() method not being available on Android. */
    public static void close(ClassLoader loader) throws Exception{
        if(loader instanceof URLClassLoader u){
            u.close();
        }
    }
}
