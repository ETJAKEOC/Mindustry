package mindustry.world.blocks.production;

import arc.util.Nullable;
import mindustry.content.Blocks;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BlockProducer;

public class SingleBlockProducer extends BlockProducer {
	public Block result = Blocks.router;

	public SingleBlockProducer(String name) {
		super(name);
	}

	public class SingleBlockProducerBuild extends BlockProducerBuild {

		@Nullable
		@Override
		public Block recipe() {
			return result;
		}
	}
}
