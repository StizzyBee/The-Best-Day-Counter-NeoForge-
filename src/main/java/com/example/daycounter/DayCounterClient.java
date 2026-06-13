package com.example.daycounter;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Holder for shared client state and the HUD render routine.
 *
 * <p>This class is intentionally loader-agnostic: it depends only on vanilla
 * Minecraft types so that {@link DayCounterSettingsScreen} and the loader
 * entrypoint can both reference {@link #CONFIG} and {@link #renderDayCounter}
 * without pulling in any Forge- or NeoForge-specific API.</p>
 */
public final class DayCounterClient {

    public static final String MOD_ID = "day_counter";

    public static DayCounterConfig CONFIG;

    private DayCounterClient() {}

    public static void renderDayCounter(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.options.hideGui) return;

        long   day  = client.level.getGameTime() / 24_000L;
        String text = CONFIG.getTextCase().apply(CONFIG.getLanguage().format(day));
        float  s    = CONFIG.scale;

        float  px   = CONFIG.x;
        float  py   = CONFIG.y;
        if (CONFIG.refW > 0 && CONFIG.refH > 0) {
            px = CONFIG.x * (graphics.guiWidth()  / (float) CONFIG.refW);
            py = CONFIG.y * (graphics.guiHeight() / (float) CONFIG.refH);
        }

        var ps = graphics.pose();
        ps.pushMatrix();
        ps.translate(px, py);
        ps.scale(s, s);
        graphics.text(CONFIG.getFont().getFont(client.font, client.fontFilterFishy), text, 0, 0, 0xFFFFFFFF, true);
        ps.popMatrix();
    }
}
