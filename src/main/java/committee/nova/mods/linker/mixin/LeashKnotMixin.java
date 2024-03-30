package committee.nova.mods.linker.mixin;

import committee.nova.mods.linker.api.Linkable;
import committee.nova.mods.linker.utils.LinkUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * LeashKnotMixin
 *
 * @author cnlimiter
 * @version 1.0
 * @description
 * @date 2024/3/31 2:12
 */
@Mixin(LeashFenceKnotEntity.class)
public abstract class LeashKnotMixin extends HangingEntity implements Linkable {

    protected LeashKnotMixin(EntityType<? extends HangingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public Entity linker$getMaster() {
        return null;
    }

    @Override
    public void linker$setMaster(Entity master) {
    }

    @Override
    public ItemStack linker$getLinkItem() {
        return null;
    }

    @Override
    public void linker$setLinkItem(ItemStack linkItem) {
    }

    @Override
    public @NotNull Vec3 getRopeHoldPosition(float p_31863_) {
        return this.getPosition(p_31863_).add(0.0D, 0.2D, 0.0D);
    }

    @Override
    public float getEyeHeight(Pose p_31839_, EntityDimensions p_31840_) {
        return 0.0625F;
    }


    @Inject(method = "interact", at = @At("RETURN"), cancellable = true)
    public void interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(interactKnot(player, hand, this) ? InteractionResult.SUCCESS : super.interact(player, hand));
    }

    @Unique
    boolean interactKnot(Player player, InteractionHand hand, Entity entity) {
        if (!this.level().isClientSide && LinkUtils.linkTo(entity, level(), player)) {
            this.discard();
            return true;
        }
        return false;
    }


}
