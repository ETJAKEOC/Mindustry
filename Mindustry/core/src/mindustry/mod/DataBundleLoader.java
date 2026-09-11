package mindustry.mod;

import arc.Core;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.I18NBundle;
import arc.util.Log;
import arc.util.io.PropertiesUtils;
import mindustry.mod.data.BundleAsset;

public class DataBundleLoader{
    private ObjectMap<I18NBundle, ObjectMap<String, String>> originalProperties = new ObjectMap<>();

    public void load(Seq<BundleAsset> assets){
        if(assets.isEmpty()) return;

        ObjectMap<String, Seq<BundleAsset>> localeToBundles = new ObjectMap<>();
        for(var asset : assets){
            if(asset.getCacheFile() == null) continue;
            localeToBundles.get(asset.name, Seq::new).add(asset);
        }

        //add new keys to each bundle
        I18NBundle bundle = Core.bundle;
        while(bundle != null){
            String localeName = bundle.getLocale().toString();
            String targetFileName = "bundle" + (localeName.isEmpty() ? "" : "_" + localeName);

            var replacements = localeToBundles.get(targetFileName);
            if(replacements != null){
                originalProperties.put(bundle, bundle.getProperties().copy());

                for(var asset : replacements){
                    try{
                        PropertiesUtils.load(bundle.getProperties(), asset.getCacheFileNoNull().reader());
                    }catch(Throwable e){
                        Log.err("Error loading bundles", e);
                    }
                }
            }

            bundle = bundle.getParent();
        }
    }

    public void unload(){
        originalProperties.each((bundle, properties) -> bundle.getProperties().set(properties));
        originalProperties.clear();
    }
}
