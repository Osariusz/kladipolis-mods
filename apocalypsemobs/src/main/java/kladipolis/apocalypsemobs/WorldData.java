package kladipolis.apocalypsemobs;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class WorldData extends SavedData {
    private CompoundTag data = new CompoundTag();

    // Required empty constructor
    public WorldData() {}

    public CompoundTag getData() {
        return this.data;
    }

    public void updateData(CompoundTag tag) {
        this.data = tag;
        this.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("mydata", data);
        return tag;
    }

    public static WorldData load(CompoundTag tag, HolderLookup.Provider registries) {
        WorldData worldData = new WorldData();
        if (tag.contains("mydata")) {
            worldData.data = tag.getCompound("mydata");
        }
        return worldData;
    }

    // Factory definition
    public static final SavedData.Factory<WorldData> FACTORY = new SavedData.Factory<>(
            WorldData::new,      // Constructor reference
            WorldData::load      // Load method reference
    );
}
