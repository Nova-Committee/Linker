package committee.nova.mods.linker.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

/**
 * Linkable
 *
 * @author cnlimiter
 * @version 1.0
 * @description
 * @date 2024/3/30 22:13
 */
public interface Linkable {
    default Entity linker$getMaster() {
        throw new IllegalStateException("Implemented via mixin");
    }

    default void linker$setMaster(Entity master) {
        throw new IllegalStateException("Implemented via mixin");
    }

    default ItemStack linker$getLinkItem() {
        throw new IllegalStateException("Implemented via mixin");
    }

    default void linker$setLinkItem(ItemStack linkItem) {
        throw new IllegalStateException("Implemented via mixin");
    }
}
