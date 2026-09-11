package mindustry.io;

import arc.Core;
import arc.assets.AssetManager;
import arc.assets.loaders.TextureLoader;
import arc.files.Fi;

public class SavePreviewLoader extends TextureLoader{

    public SavePreviewLoader(){
        super(Core.files::absolute);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, Fi file, TextureParameter parameter){
        try{
            super.loadAsync(manager, fileName, file.sibling(file.nameWithoutExtension()), parameter);
        }catch(Exception e){
            e.printStackTrace();
            file.sibling(file.nameWithoutExtension()).delete();
        }
    }
}
