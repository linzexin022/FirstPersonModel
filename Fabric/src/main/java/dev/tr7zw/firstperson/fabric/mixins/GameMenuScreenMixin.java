package dev.tr7zw.firstperson.fabric.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.firstperson.fabric.config.ConfigBuilder;
import dev.tr7zw.velvet.api.Velvet;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen{

	protected GameMenuScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "initWidgets", at = @At("RETURN"))
	public void initWidgets(CallbackInfo info) {
		this.addButton(new ButtonWidget(this.width - 100, 24, 98, 20,
				new TranslatableText("category.firstperson.cosmetics"), (buttonWidgetx) -> {
					ClothConfigScreen screen = (ClothConfigScreen) new ConfigBuilder().createConfigScreen(Velvet.velvet.getWrapper().wrapScreen(this)).getHandler(Screen.class);
					screen.selectedCategoryIndex = 2;
					this.client.openScreen(screen);
				}));
	}
	
}
