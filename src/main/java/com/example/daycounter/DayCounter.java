package com.example.daycounter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * NeoForge (26.1.2) entrypoint for Day Counter.
 *
 * <p>{@code dist = Dist.CLIENT} ensures this class is only constructed on the
 * physical client. The HUD layer is a mod-bus event, while the pause-screen
 * button hook listens on the game event bus.</p>
 */
@Mod(value = DayCounterClient.MOD_ID, dist = Dist.CLIENT)
public class DayCounter {

    public DayCounter(IEventBus modEventBus, ModContainer modContainer) {
        DayCounterClient.CONFIG = DayCounterConfig.load();
        modEventBus.addListener(this::onRegisterGuiLayers);
        NeoForge.EVENT_BUS.addListener(this::onScreenInitPost);
    }

    private void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
            Identifier.fromNamespaceAndPath(DayCounterClient.MOD_ID, "day_counter"),
            DayCounterClient::renderDayCounter);
    }

    private void onScreenInitPost(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof PauseScreen screen)) return;

        Button quitButton = findBottomMostButton(event);
        if (quitButton == null) return;

        int btnW = quitButton.getWidth();
        int btnX = quitButton.getX();
        int btnY = quitButton.getY();

        quitButton.setY(btnY + 24);

        event.addListener(Button.builder(
            Component.literal("Day Counter Settings"),
            b -> Minecraft.getInstance().setScreen(
                new DayCounterSettingsScreen(screen, DayCounterClient.CONFIG))
        ).bounds(btnX, btnY, btnW, 20).build());
    }

    /**
     * The pause menu's bottom-most button is "Save and Quit to Title". Anchoring
     * to it by on-screen position is locale-independent and robust against
     * translation-key changes, unlike matching the button's label text.
     */
    private static Button findBottomMostButton(ScreenEvent.Init.Post event) {
        Button bottom = null;
        for (GuiEventListener listener : event.getListenersList()) {
            if (listener instanceof Button button
                    && (bottom == null || button.getY() > bottom.getY())) {
                bottom = button;
            }
        }
        return bottom;
    }
}
