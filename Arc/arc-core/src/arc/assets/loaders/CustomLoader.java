package arc.assets.loaders;

import arc.Core;
import arc.assets.AssetDescriptor;
import arc.assets.AssetLoaderParameters;
import arc.assets.AssetManager;
import arc.files.Fi;
import arc.struct.Seq;

public abstract class CustomLoader extends AsynchronousAssetLoader {
	public Runnable loaded = () -> {
	};

	public CustomLoader() {
		super(Core.files::internal);
	}

	@Override
	public Object loadSync(AssetManager manager, String fileName, Fi file, AssetLoaderParameters parameter) {
		loaded.run();
		return this;
	}

	@Override
	public Seq<AssetDescriptor> getDependencies(String fileName, Fi file, AssetLoaderParameters parameter) {
		return null;
	}
}
