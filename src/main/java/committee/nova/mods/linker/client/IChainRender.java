package committee.nova.mods.linker.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import committee.nova.mods.linker.Linker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * IChainRender
 *
 * @author cnlimiter
 * @version 1.0
 * @description
 * @date 2024/3/31 1:00
 */
@Environment(EnvType.CLIENT)
public class IChainRender {
    public static final Vec3 AXIS_Y = new Vec3(0, 1.0D, 0);
    private static final ResourceLocation CHAIN = new ResourceLocation(Linker.ID, "textures/chain.png");

    public static <E extends Entity> void renderChain(Boat boat, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, E entity) {
        poseStack.pushPose();
        Vec3 vec3 = entity.getRopeHoldPosition(v);
        double d0 = (double) (Mth.lerp(v, boat.yRotO, boat.getYRot()) * ((float) Math.PI / 180F)) + (Math.PI / 2D);
        Vec3 vec31 = boat.getLeashOffset(v);
        double d1 = Math.cos(d0) * vec31.z + Math.sin(d0) * vec31.x;
        double d2 = Math.sin(d0) * vec31.z - Math.cos(d0) * vec31.x;
        double d3 = Mth.lerp(v, boat.xo, boat.getX()) + d1;
        double d4 = Mth.lerp(v, boat.yo, boat.getY()) + vec31.y;
        double d5 = Mth.lerp(v, boat.zo, boat.getZ()) + d2;
        poseStack.translate(d1, vec31.y, d2);
        float f = (float) (vec3.x - d3);
        float f1 = (float) (vec3.y - d4);
        float f2 = (float) (vec3.z - d5);
        Vec3 subtract = new Vec3(f, f1, f2);
        Vec3 normalize = subtract.normalize().scale(0.2);
        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(CHAIN));
        int i = (int) (subtract.length() / 0.2);
        for (int i1 = 0; i1 < i; i1++) {
            IChainRender.renderSingleChain(poseStack, vertexconsumer, (float) (i1 * normalize.x), (float) (i1 * normalize.y), (float) (i1 * normalize.z), (float) ((i1 + 1) * normalize.x), (float) ((i1 + 1) * normalize.y), (float) ((i1 + 1) * normalize.z), false);
            IChainRender.renderSingleChain(poseStack, vertexconsumer, (float) ((i1 + 0.5) * normalize.x), (float) ((i1 + 0.5) * normalize.y), (float) ((i1 + 0.5) * normalize.z), (float) ((i1 + 1.5) * normalize.x), (float) ((i1 + 1.5) * normalize.y), (float) ((i1 + 1.5) * normalize.z), true);
        }
        poseStack.popPose();
    }

    public static void renderSingleChain(PoseStack poseStack, VertexConsumer vertexconsumer, float x, float y, float z, float x1, float y1, float z1, boolean cross) {
        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        float Vx = x1 - x;
        float Vy = y1 - y;
        float Vz = z1 - z;
        Vec3 vector = new Vec3(Vx, Vy, Vz);
        Vec3 vec3 = vector.cross(AXIS_Y);
        if (cross) {
            vec3 = vec3.cross(vector);
        }
        vec3 = vec3.normalize().scale(0.2);
        vertex(vertexconsumer, matrix4f, matrix3f, (float) (x - vec3.x), (float) (y - vec3.y), (float) (z - vec3.z), 0, 0);
        vertex(vertexconsumer, matrix4f, matrix3f, (float) (x + vec3.x), (float) (y + vec3.y), (float) (z + vec3.z), 1.0F, 0);
        vertex(vertexconsumer, matrix4f, matrix3f, (float) (x1 - vec3.x), (float) (y1 - vec3.y), (float) (z1 - vec3.z), 0, 1.0F);
        vertex(vertexconsumer, matrix4f, matrix3f, (float) (x1 + vec3.x), (float) (y1 + vec3.y), (float) (z1 + vec3.z), 1.0F, 1.0F);
    }

    private static void vertex(VertexConsumer p_253637_, Matrix4f p_253920_, Matrix3f p_253881_, float x, float y, float z, float uv_x, float uv_y) {
        p_253637_.vertex(p_253920_, x, y, z).color(255, 255, 255, 255).uv(uv_x, uv_y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(p_253881_, 0.0F, 1.0F, 0.0F).endVertex();
    }
}
