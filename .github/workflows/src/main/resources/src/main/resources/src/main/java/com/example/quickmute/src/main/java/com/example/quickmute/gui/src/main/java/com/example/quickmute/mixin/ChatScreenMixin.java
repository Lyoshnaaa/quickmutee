package com.example.quickmute.mixin;

import com.example.quickmute.gui.MuteScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {

    @Shadow
    protected abstract Style getTextStyleAt(double x, double y);

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onChatRightClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button == 1) {
            Style style = this.getTextStyleAt(mouseX, mouseY);

            if (style != null) {
                String targetName = null;

                if (style.getInsertion() != null && !style.getInsertion().isEmpty()) {
                    targetName = style.getInsertion();
                } else if (style.getClickEvent() != null) {
                    String val = style.getClickEvent().getValue();
                    if (val.startsWith("/msg ") || val.startsWith("/w ") || val.startsWith("/tell ")) {
                        String[] parts = val.split(" ");
                        if (parts.length > 1) targetName = parts[1];
                    }
                }

                if (targetName != null) {
                    targetName = targetName.replace("@", "").trim();
                    MinecraftClient.getInstance().setScreen(new MuteScreen(targetName));
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
