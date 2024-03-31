package committee.nova.mods.linker.mixin;

import committee.nova.mods.linker.Linker;
import committee.nova.mods.linker.utils.LinkUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * ServerPlayerMixin
 *
 * @author cnlimiter
 * @version 1.0
 * @description
 * @date 2024/3/30 23:35
 */
@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "interactOn", cancellable = true)
    void onInteract(Entity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = (Player) (Object) this;
        if (entity instanceof AbstractMinecart linkable) {
            if (level().isClientSide) return;
            cir.setReturnValue(interactMinecart(player, hand, linkable) ? InteractionResult.SUCCESS : super.interact(player, hand));
        }
    }

    @Unique
    boolean interactMinecart(Player player, InteractionHand hand, AbstractMinecart entity) {
        ItemStack itemInHand = player.getItemInHand(hand);
        boolean validItem = itemInHand.isEmpty() || itemInHand.is(Linker.LINKERS);
        if (entity.linker$getMaster() != null && ((entity.linker$getMaster() == player && validItem) || itemInHand.is(Items.SHEARS))) {
            entity.boatLinker$resetMaster();
            entity.playSound(SoundEvents.CHAIN_BREAK);
            entity.spawnAtLocation(entity.linker$getLinkItem());
            return true;
        } else if (entity.linker$getMaster() == null) {
            if (player.isShiftKeyDown() && validItem && LinkUtils.linkTo(player, player.level(), entity)) {
                return true;
            } else if (itemInHand.is(Linker.LINKERS)) {
                entity.linker$setMaster(player);
                entity.playSound(SoundEvents.CHAIN_PLACE);
                itemInHand.shrink(1);
                return true;
            }
        }
        return false;
    }
}
