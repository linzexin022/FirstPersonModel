package dev.tr7zw.firstperson.fabric.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.tr7zw.firstperson.FirstPersonModelCore;
import dev.tr7zw.firstperson.config.PaperDollSettings.DollHeadMode;
import dev.tr7zw.firstperson.fabric.FirstPersonModelMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

/**
 * Paper doll rendering
 *
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {

	private final MinecraftClient mc = MinecraftClient.getInstance();

	@Inject(at = @At("HEAD"), method = "render")
	public void render(MatrixStack matrixStack, float delta, CallbackInfo info) {
		if (FirstPersonModelMod.config.paperDoll.dollEnabled && !mc.options.debugEnabled) {
			int xpos = 25 + FirstPersonModelMod.config.paperDoll.dollXOffset;
			int ypos = 55 + FirstPersonModelMod.config.paperDoll.dollYOffset;
			int size = 25 + FirstPersonModelMod.config.paperDoll.dollSize;
			int lookSides = -FirstPersonModelMod.config.paperDoll.dollLookingSides;
			int lookUpDown = FirstPersonModelMod.config.paperDoll.dollLookingUpDown;
			if (mc.player.isFallFlying() || mc.player.isUsingRiptide()) {
				lookSides = 0;
				lookUpDown = 40;
			}
			LivingEntity playerEntity = mc.player;
			if (mc.getCameraEntity() != playerEntity && mc.getCameraEntity() instanceof LivingEntity) {
				playerEntity = (LivingEntity) mc.getCameraEntity();
			}
			drawEntity(xpos, ypos, size, lookSides, lookUpDown, playerEntity, delta,
					FirstPersonModelCore.config.paperDoll.dollHeadMode == DollHeadMode.LOCKED);
		}
	}

	// Modified version from InventoryScreen
	private void drawEntity(int i, int j, int k, float f, float g, LivingEntity livingEntity, float delta,
			boolean lockHead) {
		float h = (float) Math.atan((double) (f / 40.0F));
		float l = (float) Math.atan((double) (g / 40.0F));
		RenderSystem.pushMatrix();
		RenderSystem.translatef((float) i, (float) j, 1050.0F);
		RenderSystem.scalef(1.0F, 1.0F, -1.0F);
		MatrixStack matrixStack = new MatrixStack();
		FirstPersonModelMod.setPaperDollStack(matrixStack); // To not hide head if rendering this
		matrixStack.translate(0.0D, 0.0D, 1000.0D);
		matrixStack.scale((float) k, (float) k, (float) k);
		Quaternion quaternion = Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
		Quaternion quaternion2 = Vector3f.POSITIVE_X.getDegreesQuaternion(l * 20.0F);
		quaternion.hamiltonProduct(quaternion2);
		matrixStack.multiply(quaternion);
		float m = livingEntity.bodyYaw;
		float renderYaw = livingEntity.yaw;
		float prevRenderYaw = livingEntity.prevYaw;
		float prevBodyYaw = livingEntity.prevBodyYaw;
		float n = livingEntity.yaw;
		float prevYaw = livingEntity.prevYaw;
		float o = livingEntity.pitch;
		float prevPitch = livingEntity.prevPitch;
		float p = livingEntity.prevHeadYaw;
		float q = livingEntity.headYaw;
		Vec3d vel = livingEntity.getVelocity();
		livingEntity.bodyYaw = 180.0F + h * 20.0F;
		livingEntity.yaw = 180.0F + h * 40.0F;
		livingEntity.prevBodyYaw = livingEntity.bodyYaw;
		livingEntity.prevYaw = livingEntity.yaw;
		livingEntity.setVelocity(Vec3d.ZERO);
		if (lockHead) {
			livingEntity.pitch = -l * 20.0F;
			livingEntity.prevPitch = livingEntity.pitch;
			livingEntity.headYaw = livingEntity.yaw;
			livingEntity.prevHeadYaw = livingEntity.yaw;
		} else {
			if(FirstPersonModelCore.config.paperDoll.dollHeadMode == DollHeadMode.FREE) {
				livingEntity.headYaw = 180.0F + h * 40.0F - (m - q);
				livingEntity.prevHeadYaw = 180.0F + h * 40.0F - (prevBodyYaw - p);
			}else {
				livingEntity.headYaw = 180.0F + h * 40.0F - (renderYaw - q);
				livingEntity.prevHeadYaw = 180.0F + h * 40.0F - (prevRenderYaw - p);
			}
		}
		EntityRenderDispatcher entityRenderDispatcher = mc.getEntityRenderDispatcher();
		quaternion2.conjugate();
		entityRenderDispatcher.setRotation(quaternion2);
		entityRenderDispatcher.setRenderShadows(false);
		VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
		// Mc renders the player in the inventory without delta, causing it to look "laggy". Good luck unseeing this :)
		entityRenderDispatcher.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, delta, matrixStack, immediate, 15728880);
		immediate.draw();
		entityRenderDispatcher.setRenderShadows(true);
		livingEntity.bodyYaw = m;
		livingEntity.prevBodyYaw = prevBodyYaw;
		livingEntity.yaw = n;
		livingEntity.prevYaw = prevYaw;
		livingEntity.pitch = o;
		livingEntity.prevPitch = prevPitch;
		livingEntity.prevHeadYaw = p;
		livingEntity.headYaw = q;
		livingEntity.setVelocity(vel);
		RenderSystem.popMatrix();
	}

}
