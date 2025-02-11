package dev.tr7zw.firstperson.fabric.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.firstperson.FirstPersonModelCore;
import dev.tr7zw.firstperson.fabric.FirstPersonModelMod;
import dev.tr7zw.firstperson.fabric.features.layers.BodyLayerFeatureRenderer;
import dev.tr7zw.firstperson.fabric.features.layers.HeadLayerFeatureRenderer;
import dev.tr7zw.firstperson.mixinbase.ModelPartBase;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Hides body parts and layers where needed
 *
 */
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRenderMixin
		extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	
	/**
	 * Just needed because of the extends
	 * 
	 * @param entityRenderDispatcher_1
	 * @param entityModel_1
	 * @param float_1
	 */
	public PlayerRenderMixin(EntityRenderDispatcher entityRenderDispatcher_1,
			PlayerEntityModel<AbstractClientPlayerEntity> entityModel_1, float float_1) {
		super(entityRenderDispatcher_1, entityModel_1, float_1);
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;setModelPose(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)V"))
	private void setModelPoseRedirect(PlayerEntityRenderer playerEntityRenderer,
			AbstractClientPlayerEntity abstractClientPlayerEntity,
			AbstractClientPlayerEntity abstractClientPlayerEntity_1, float f, float g, MatrixStack matrixStack,
			VertexConsumerProvider vertexConsumerProvider, int i) {
		setModelPose(abstractClientPlayerEntity);
		boolean bodyLayer = BodyLayerFeatureRenderer.isEnabled(abstractClientPlayerEntity);
		if (FirstPersonModelCore.instance.isFixActive(abstractClientPlayerEntity, matrixStack)) {
			PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel_1 = this.getModel();
			playerEntityModel_1.head.visible = false;
			playerEntityModel_1.helmet.visible = false;
			((ModelPartBase)playerEntityModel_1.head).setHidden();
			if (FirstPersonModelMod.config.firstPerson.vanillaHands) {
				playerEntityModel_1.leftArm.visible = false;
				playerEntityModel_1.leftSleeve.visible = false;
				playerEntityModel_1.rightArm.visible = false;
				playerEntityModel_1.rightSleeve.visible = false;
			} else {
				
			}
		} else {
			playerEntityRenderer.getModel().helmet.visible = HeadLayerFeatureRenderer
					.isEnabled(abstractClientPlayerEntity) ? false : playerEntityRenderer.getModel().helmet.visible;
		}
		playerEntityRenderer.getModel().leftSleeve.visible = bodyLayer ? false
				: playerEntityRenderer.getModel().leftSleeve.visible;
		playerEntityRenderer.getModel().rightSleeve.visible = bodyLayer ? false
				: playerEntityRenderer.getModel().rightSleeve.visible;
		playerEntityRenderer.getModel().leftPantLeg.visible = bodyLayer ? false
				: playerEntityRenderer.getModel().leftPantLeg.visible;
		playerEntityRenderer.getModel().rightPantLeg.visible = bodyLayer ? false
				: playerEntityRenderer.getModel().rightPantLeg.visible;
		playerEntityRenderer.getModel().jacket.visible = bodyLayer ? false
				: playerEntityRenderer.getModel().jacket.visible;
	}
	
	@Inject(method = "render", at = @At(value = "RETURN"))
	public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack,
			VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
		((ModelPartBase)this.getModel().head).showAgain();
		if (FirstPersonModelCore.instance.isFixActive(abstractClientPlayerEntity, matrixStack)) {
			FirstPersonModelMod.clearHeadStack();
		}
	}

	@Shadow
	abstract void setModelPose(AbstractClientPlayerEntity abstractClientPlayerEntity_1);

	// @Inject(at = @At("HEAD"), method = "getPositionOffset", cancellable = true)

}
