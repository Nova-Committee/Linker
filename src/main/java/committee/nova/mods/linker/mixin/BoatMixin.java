package committee.nova.mods.linker.mixin;

import committee.nova.mods.linker.Linker;
import committee.nova.mods.linker.api.Linkable;
import committee.nova.mods.linker.utils.LinkUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

/**
 * BoatMixin
 *
 * @author cnlimiter
 * @version 1.0
 * @description
 * @date 2024/3/30 22:16
 */
@Mixin(Boat.class)
public abstract class BoatMixin extends Entity implements Linkable {
    @Unique
    private static final EntityDataAccessor<Integer> MASTER_ID = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.INT);
    @Unique
    String MASTER_UUID = "Linker-Master";
    @Unique
    String LINK_ITEM = "Linker-Item";
    @Unique
    private Entity linker$master;
    @Unique
    private UUID linker$masterUUID;
    @Unique
    private ItemStack linker$itemStack = ItemStack.EMPTY;
    public BoatMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void linker$tick(CallbackInfo ci) {
        if (level().isClientSide) {
            int id = entityData.get(MASTER_ID);
            if (id == Integer.MIN_VALUE) {
                resetMaster();
            } else if (this.linker$master == null || this.linker$master.getId() != id) {
                Entity entity = this.level().getEntity(id);
                this.linker$setMaster(entity);
            }
        } else {
            if (this.linker$master != null && !this.linker$master.isAlive()) {
                linkerBreak(false);
                return;
            }
            if (this.linker$master == null && this.linker$masterUUID != null) {
                Entity entityNew = null;
                List<Entity> entitiesOfClass = level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(24.0D), entity -> entity != this);
                for (Entity entity : entitiesOfClass) {
                    if (entity.getUUID().equals(uuid)) entityNew = entity;
                }
                if (entityNew == null) {
                    linkerBreak(false);
                } else {
                    this.linker$setMaster(entityNew);
                }
            }
        }
        tickPull();
    }

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    public void defineData(CallbackInfo info) {
        this.entityData.define(MASTER_ID, Integer.MIN_VALUE);
    }

    @Inject(at = @At("RETURN"), method = "addAdditionalSaveData")
    private void linker$write(CompoundTag nbt, CallbackInfo ci) {
        if (this.linker$master != null) nbt.putUUID(MASTER_UUID, this.linker$getMaster().getUUID());
        if (this.linker$itemStack != null) nbt.put(LINK_ITEM, this.linker$itemStack.save(new CompoundTag()));
    }

    @Inject(at = @At("RETURN"), method = "readAdditionalSaveData")
    private void linker$read(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains(MASTER_UUID)) this.linker$masterUUID = nbt.getUUID(MASTER_UUID);
        if (nbt.contains(LINK_ITEM)) this.linker$itemStack = ItemStack.of(nbt.getCompound(LINK_ITEM));
    }

    @Inject(method = "interact", at = @At("RETURN"), cancellable = true)
    public void interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        cir.setReturnValue(interactBoat(player, hand, this) ? InteractionResult.SUCCESS : super.interact(player, hand));
    }

    @Inject(method = "destroy", at = @At("RETURN"))
    public void destroy(DamageSource source, CallbackInfo ci) {
        if (this.linker$master != null) {
            linkerBreak(false);
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (this.linker$master != null) {
            linkerBreak(false);
        }
    }

    @Override
    public @NotNull Vec3 getRopeHoldPosition(float f) {
        float f1 = Mth.lerp(f, this.yRotO, this.getYRot()) * ((float) Math.PI / 180F);
        return this.getPosition(f).add(new Vec3(0.0D, (double) this.getBbHeight() * 0.5D, -this.getBbWidth() * 0.6F).yRot(-f1));
    }

    @Override
    protected @NotNull Vec3 getLeashOffset() {
        return new Vec3(0.0D, 0.2F, this.getBbWidth() * 0.6F);
    }


    @Override
    public Entity linker$getMaster() {
        return this.linker$master;
    }

    @Override
    public void linker$setMaster(Entity master) {
        this.linker$master = master;
        this.linker$masterUUID = master != null ? master.getUUID() : null;
        this.entityData.set(MASTER_ID, this.linker$master != null ? this.linker$master.getId() : Integer.MIN_VALUE);
    }

    @Override
    public ItemStack linker$getLinkItem() {
        return this.linker$itemStack;
    }

    @Override
    public void linker$setLinkItem(ItemStack linkItem) {
        this.linker$itemStack = linkItem == null ? ItemStack.EMPTY : linkItem;
    }

    @Unique
    public void resetMaster() {
        this.linker$master = null;
        this.linker$masterUUID = null;
        this.entityData.set(MASTER_ID, Integer.MIN_VALUE);
    }

    @Unique
    public void linkerBreak(boolean shouldPlaySound) {
        this.playSound(SoundEvents.CHAIN_BREAK);
        if (shouldPlaySound) {
            this.linker$master.playSound(SoundEvents.CHAIN_BREAK);
        }
        resetMaster();
        spawnAtLocation(this.linker$itemStack);
    }

    @Unique
    public void tickPull() {
        if (this.linker$master != null) {
            Vec3 subtract = this.linker$master.position().subtract(this.position());
            double length = subtract.length();
            double thisSpeed = this.getDeltaMovement().length();
            double masterSpeed = this.linker$master.getDeltaMovement().length();
            if (length < 5) return;
            if (!this.level().isClientSide && length > Linker.config.distance + 12 * masterSpeed) {
                if (masterSpeed > 1.5) {
                    linkerBreak(true);
                    return;
                } else {
                    this.linker$master.addDeltaMovement(this.linker$master.getDeltaMovement().scale(-0.1));
                }

            }
            double f = 0.06D + length / 100 + (masterSpeed - thisSpeed) / 4;
            Vec3 v = subtract.normalize().scale(f);
            if (subtract.horizontalDistance() > 4) {
                float yRotNeo = (float) (Mth.atan2(subtract.z, subtract.x) * (double) (180F / (float) Math.PI)) - 90.0F;
                if (this.getYRot() != yRotNeo) {
                    float yRot = this.getYRot();
                    float rot = Mth.wrapDegrees(yRotNeo - yRot) / 5;
                    this.setYRot(yRot + rot);
                }
            }

            this.setDeltaMovement(getDeltaMovement().add(v));
        }
    }

    @Unique
    boolean interactBoat(Player player, InteractionHand hand, Entity entity) {
        ItemStack itemInHand = player.getItemInHand(hand);
        boolean validItem = itemInHand.isEmpty() || itemInHand.is(Linker.LINKERS);
        if (this.linker$getMaster() != null && ((this.linker$getMaster() == player && validItem) || itemInHand.is(Items.SHEARS))) {
            resetMaster();
            entity.playSound(SoundEvents.CHAIN_BREAK);
            entity.spawnAtLocation(Items.CHAIN);
            return true;
        } else if (this.linker$getMaster() == null) {
            if (player.isShiftKeyDown() && validItem && LinkUtils.linkTo(player, player.level(), entity)) {
                return true;
            } else if (itemInHand.is(Linker.LINKERS)) {
                this.linker$setMaster(player);
                entity.playSound(SoundEvents.CHAIN_PLACE);
                itemInHand.shrink(1);
                return true;
            }
        }
        return false;
    }


}
