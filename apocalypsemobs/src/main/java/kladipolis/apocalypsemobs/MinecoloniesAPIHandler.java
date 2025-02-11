package kladipolis.apocalypsemobs;

import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.api.colony.ICitizen;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.permissions.Action;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class MinecoloniesAPIHandler {

    static final int MAX_NEARBY_COLONY_RANGE = 100;

    public static boolean isPlayerColony(Player player, IColony colony) {
        return colony.getPermissions().hasPermission(player, Action.TELEPORT_TO_COLONY);
    }

    public static IColony getPlayerColony(Player player) {
        List<IColony> allColonies = MinecoloniesAPIProxy.getInstance().getColonyManager().getAllColonies();
        List<IColony> playerColonies = allColonies.stream().filter(
                (IColony colony) -> isPlayerColony(player, colony)
        ).toList();
        return playerColonies.stream().min(
                Comparator.comparingDouble((IColony colony) -> colony.getCenter().distSqr(player.blockPosition()))
        ).orElse(null);
    }

    public static IColony nearbyColony(LivingEntity entity) {
        //TODO: rewrite to use minecolonies closest colony in API
        List<IColony> allColonies = MinecoloniesAPIProxy.getInstance().getColonyManager().getAllColonies();
        Stream<IColony> allColoniesStream = allColonies.stream().filter((IColony colony) -> colony.getCenter().distSqr(entity.blockPosition()) <= MAX_NEARBY_COLONY_RANGE);
        return allColoniesStream.min(
                Comparator.comparingDouble((IColony colony) -> colony.getCenter().distSqr(entity.blockPosition()))).orElse(null);
    }

    public static IColony getEntityColony(LivingEntity entity) {
        if(entity instanceof ICitizen citizen) {
            return citizen.getColony();
        }
        if(entity instanceof Player player) {
            return getPlayerColony(player);
        }
        else {
            return nearbyColony(entity);
        }
    }
}
