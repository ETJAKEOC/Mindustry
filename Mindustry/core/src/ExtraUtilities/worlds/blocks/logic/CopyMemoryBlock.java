package ExtraUtilities.worlds.blocks.logic;

import mindustry.world.blocks.logic.MemoryBlock;

public class CopyMemoryBlock extends MemoryBlock {
    public CopyMemoryBlock(String name) {
        super(name);

        config(Object[].class, (CopyMemoryBuild e, Object[] ds) -> {
            for(int i = 0; i < ds.length; i++){
                if(ds[i] instanceof Double d)
                    e.numberMemory[i] = d;
            }
        });
    }

    public class CopyMemoryBuild extends MemoryBuild {
        public Object[] objects = new Object[memoryCapacity];

        public void updateMemory() {
            for (int i = 0; i < numberMemory.length; i++) {
                objects[i] = numberMemory[i];
            }
        }

        @Override
        public Object[] config() {
            updateMemory();
            return objects;
        }
    }
}
