package com.example.quickmute.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class MuteScreen extends Screen {
    private final String targetPlayer;
    private TextFieldWidget reasonField;

    public MuteScreen(String targetPlayer) {
        super(Text.literal("Мут игрока"));
        this.targetPlayer = targetPlayer;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.reasonField = new TextFieldWidget(
                this.textRenderer,
                centerX - 100, centerY - 10,
                200, 20,
                Text.literal("Причина")
        );
        this.reasonField.setMaxLength(32);
        this.reasonField.setFocused(true);
        this.addSelectableChild(this.reasonField);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Выдать мут"), button -> executeMute())
                .dimensions(centerX - 100, centerY + 20, 95, 20)
                .build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Отмена"), button -> this.close())
                .dimensions(centerX + 5, centerY + 20, 95, 20)
                .build());

        this.setInitialFocus(this.reasonField);
    }

    private void executeMute() {
        String reason = this.reasonField.getText().trim();
        if (!reason.isEmpty() && MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.networkHandler
                    .sendCommand("tempmute " + this.targetPlayer + " " + reason);
            this.close();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            executeMute();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("Мут игрока: §e" + this.targetPlayer),
                this.width / 2,
                this.height / 2 - 30,
                0xFFFFFF
        );

        this.reasonField.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
