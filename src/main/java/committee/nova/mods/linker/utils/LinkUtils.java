package committee.nova.mods.linker.utils;

import committee.nova.mods.linker.api.Linkable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Predicate;

/**
 * BoatUtils
 *
 * @author cnlimiter
 * @version 1.0
 * @description
 * @date 2024/3/30 22:49
 */
public class LinkUtils {
    public static Predicate<Entity> NODE_PREDICATE = entity -> entity instanceof Linkable;

    public static boolean linkTo(Entity nodeOld, Level level, Entity nodeNew) {
        for (Entity entity : getEntityOfLink(nodeOld, level)) {
            ((Linkable) entity).linker$setMaster(nodeNew);
            nodeNew.playSound(SoundEvents.CHAIN_PLACE);
            return true;
        }
        return false;
    }

    public static List<Entity> getEntityOfLink(Entity entity1, Level level) {
        return level.getEntitiesOfClass(Entity.class, entity1.getBoundingBox().inflate(7.0D), NODE_PREDICATE)
                .stream().filter(entity -> entity != entity1 && ((Linkable) entity).linker$getMaster() == entity1)
                .toList();
    }
}
