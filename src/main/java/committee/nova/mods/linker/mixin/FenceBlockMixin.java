package committee.nova.mods.linker.mixin;

import committee.nova.mods.linker.Linker;
import committee.nova.mods.linker.api.Linkable;
import committee.nova.mods.linker.utils.LinkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * FenceBlockMixin
 *
 * @author cnlimiter
 * @version 1.0
 * @description
 * @date 2024/3/31 2:01
 */
@Mixin(FenceBlock.class)
public class FenceBlockMixin implements Linkable {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void linker$use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (level.isClientSide) {
            ItemStack itemInHand = player.getItemInHand(interactionHand);
            cir.setReturnValue(itemInHand.isEmpty() || itemInHand.is(Linker.LINKERS) ? InteractionResult.SUCCESS : InteractionResult.PASS);
        } else {
            cir.setReturnValue(bindNode(level, blockPos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS);
        }

    }

    @Unique
    public boolean bindNode(Level level, BlockPos blockpos, Player player) {
        List<Entity> chainedEntityOfNode = LinkUtils.getEntityOfLink(player, level);
        for (Entity entity : chainedEntityOfNode) {
            LeashFenceKnotEntity leashFenceKnot = LeashFenceKnotEntity.getOrCreateKnot(level, blockpos);
            ((Linkable) entity).linker$setMaster(leashFenceKnot);
        }
        return !chainedEntityOfNode.isEmpty();
    }


}
