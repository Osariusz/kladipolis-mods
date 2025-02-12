package kladipolis.apocalypsemobs;
import net.minecraft.server.level.ServerLevel;

public class WorldDataManager {
    public static WorldData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                WorldData.FACTORY,  // Use the factory
                "apocalypsemobs"         // Unique identifier
        );
    }
}