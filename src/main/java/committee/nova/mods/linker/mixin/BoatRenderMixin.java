package committee.nova.mods.linker.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import committee.nova.mods.linker.client.IChainRender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * BoatRenderMixin
 *
 * @author cnlimiter
 * @version 1.0
 * @description
 * @date 2024/3/31 0:58
 */
@Environment(EnvType.CLIENT)
@Mixin(BoatRenderer.class)
public class BoatRenderMixin {
    @Inject(method = "render(Lnet/minecraft/world/entity/vehicle/Boat;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("RETURN"))
    public void render(Boat boat, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (boat != null && boat.linker$getMaster() != null)
            IChainRender.renderChain(boat, g, poseStack, multiBufferSource, boat.linker$getMaster());
    }

}
