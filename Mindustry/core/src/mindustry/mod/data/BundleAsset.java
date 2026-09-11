package mindustry.mod.data;

import arc.files.Fi;
import arc.struct.OrderedMap;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.io.PropertiesUtils;

public class BundleAsset extends DataAsset{
    public @Nullable OrderedMap<String, String> cachedBundle;

    public void tryLoadCache(){
        if(cachedBundle != null) return;
        try{
            cachedBundle = new OrderedMap<>();
            Fi file = getCacheFile();
            if(file != null){
                PropertiesUtils.load(cachedBundle, file.reader());
            }
        }catch(Exception e){
            Log.err(e);
        }
    }

    @Override
    public void updateData(byte[] data){
        super.updateData(data);
        this.cachedBundle = null;
    }

    @Override
    public DataAssetType getType(){
        return DataAssetType.bundle;
    }
}
