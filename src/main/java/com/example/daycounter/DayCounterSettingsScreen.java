package com.example.daycounter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class DayCounterSettingsScreen extends Screen {

    // ── Presets ────────────────────────────────────────────────────────────────
    private enum Preset {
        TOP_LEFT   ("Top-Left",    2,  2),
        TOP_CENTER ("Top-Center", -1,  2),
        TOP_RIGHT  ("Top-Right",  -2,  2),
        BOT_LEFT   ("Bot-Left",    2, -1),
        BOT_RIGHT  ("Bot-Right",  -2, -1);

        final String label;
        final int rawX, rawY;

        Preset(String label, int rx, int ry) { this.label=label; rawX=rx; rawY=ry; }
    }

    // ── State ──────────────────────────────────────────────────────────────────
    private final Screen           parent;
    private final DayCounterConfig cfg;

    private int                counterX, counterY;
    private float              scale;
    private DayCounterLanguage language;
    private DayCounterFont     currentFont;
    private DayCounterTextCase currentCase;

    // Drag
    private boolean dragging;
    private int     dragOffX, dragOffY;

    // Preview box metrics (at scale 1.0)
    private static final int PAD = 4;
    private int labelW, labelH;

    // Buttons that need their text updated at runtime
    private Button scaleDisplayBtn;
    private Button langDisplayBtn;
    private Button fontDisplayBtn;
    private Button caseDisplayBtn;

    // ── Constructor ────────────────────────────────────────────────────────────
    public DayCounterSettingsScreen(Screen parent, DayCounterConfig cfg) {
        super(Component.literal("Day Counter Settings"));
        this.parent      = parent;
        this.cfg         = cfg;
        this.counterX    = cfg.x;
        this.counterY    = cfg.y;
        this.scale       = cfg.scale;
        this.language    = cfg.getLanguage();
        this.currentFont = cfg.getFont();
        this.currentCase = cfg.getTextCase();
    }

    // ── Lifecycle ──────────────────────────────────────────────────────────────
    @Override
    protected void init() {
        recomputeLabelSize();
        if (cfg.refW > 0 && cfg.refH > 0) {
            counterX = Math.round(cfg.x * (float) width  / cfg.refW);
            counterY = Math.round(cfg.y * (float) height / cfg.refH);
        }
        counterX = clampX(counterX);
        counterY = clampY(counterY);

        int gap     = 4;
        int smallW  = 24;
        int midW    = 90;
        int centerY = height / 2;

        // ── Language row  ‹ | English | › ────────────────────────────────────
        int langRowY   = centerY - 78;
        int langTotalW = smallW + gap + midW + gap + smallW;
        int langStartX = (width - langTotalW) / 2;

        addRenderableWidget(Button.builder(Component.literal("‹"),
            b -> cycleLanguage(-1)
        ).bounds(langStartX, langRowY, smallW, 20).build());

        langDisplayBtn = addRenderableWidget(Button.builder(
            Component.literal(language.displayName), b -> {}
        ).bounds(langStartX + smallW + gap, langRowY, midW, 20).build());
        langDisplayBtn.active = false;

        addRenderableWidget(Button.builder(Component.literal("›"),
            b -> cycleLanguage(+1)
        ).bounds(langStartX + smallW + gap + midW + gap, langRowY, smallW, 20).build());

        // ── Font row  ‹ | Default | › ────────────────────────────────────────
        int fontRowY   = centerY - 52;
        int fontTotalW = smallW + gap + midW + gap + smallW;
        int fontStartX = (width - fontTotalW) / 2;

        addRenderableWidget(Button.builder(Component.literal("‹"),
            b -> cycleFont(-1)
        ).bounds(fontStartX, fontRowY, smallW, 20).build());

        fontDisplayBtn = addRenderableWidget(Button.builder(
            Component.literal(currentFont.displayName), b -> {}
        ).bounds(fontStartX + smallW + gap, fontRowY, midW, 20).build());
        fontDisplayBtn.active = false;

        addRenderableWidget(Button.builder(Component.literal("›"),
            b -> cycleFont(+1)
        ).bounds(fontStartX + smallW + gap + midW + gap, fontRowY, smallW, 20).build());

        // ── Text case row  ‹ | Normal | › ────────────────────────────────────
        int caseRowY   = centerY - 26;
        int caseTotalW = smallW + gap + midW + gap + smallW;
        int caseStartX = (width - caseTotalW) / 2;

        addRenderableWidget(Button.builder(Component.literal("‹"),
            b -> cycleCase(-1)
        ).bounds(caseStartX, caseRowY, smallW, 20).build());

        caseDisplayBtn = addRenderableWidget(Button.builder(
            Component.literal(currentCase.displayName), b -> {}
        ).bounds(caseStartX + smallW + gap, caseRowY, midW, 20).build());
        caseDisplayBtn.active = false;

        addRenderableWidget(Button.builder(Component.literal("›"),
            b -> cycleCase(+1)
        ).bounds(caseStartX + smallW + gap + midW + gap, caseRowY, smallW, 20).build());

        // ── Scale row  − | 1.00x | + ──────────────────────────────────────────
        int scaleRowY    = centerY;
        int scaleMidW    = 70;
        int scaleTotalW  = smallW + gap + scaleMidW + gap + smallW;
        int scaleStartX  = (width - scaleTotalW) / 2;

        addRenderableWidget(Button.builder(Component.literal("−"),
            b -> changeScale(-DayCounterConfig.SCALE_STEP)
        ).bounds(scaleStartX, scaleRowY, smallW, 20).build());

        scaleDisplayBtn = addRenderableWidget(Button.builder(
            Component.literal(scaleText()), b -> {}
        ).bounds(scaleStartX + smallW + gap, scaleRowY, scaleMidW, 20).build());
        scaleDisplayBtn.active = false;

        addRenderableWidget(Button.builder(Component.literal("+"),
            b -> changeScale(+DayCounterConfig.SCALE_STEP)
        ).bounds(scaleStartX + smallW + gap + scaleMidW + gap, scaleRowY, smallW, 20).build());

        // ── Preset buttons ─────────────────────────────────────────────────────
        int btnW     = 90, btnH = 20;
        int presetY  = centerY + 26;
        Preset[] presets = Preset.values();
        int totalW   = presets.length * btnW + (presets.length - 1) * 8;
        int startX   = (width - totalW) / 2;

        for (int i = 0; i < presets.length; i++) {
            final Preset p = presets[i];
            addRenderableWidget(Button.builder(
                Component.literal(p.label), b -> applyPreset(p)
            ).bounds(startX + i * (btnW + 8), presetY, btnW, btnH).build());
        }

        // ── Center + Done row ──────────────────────────────────────────────────
        int doneY    = centerY + 52;
        int ctrW     = 80;
        int gap2     = 8;
        int trioW    = ctrW + gap2 + ctrW + gap2 + 100;
        int trioX    = (width - trioW) / 2;

        addRenderableWidget(Button.builder(Component.literal("Center H"),
            b -> { counterX = clampX((width - scaledW()) / 2); }
        ).bounds(trioX, doneY, ctrW, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Center V"),
            b -> { counterY = clampY((height - scaledH()) / 2); }
        ).bounds(trioX + ctrW + gap2, doneY, ctrW, 20).build());

        addRenderableWidget(Button.builder(
            Component.literal("Done"), b -> saveAndClose()
        ).bounds(trioX + ctrW + gap2 + ctrW + gap2, doneY, 100, 20).build());
    }

    // ── Rendering ──────────────────────────────────────────────────────────────
    @Override
    public void extractRenderState(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float delta) {
        gfx.centeredText(font, title, width / 2, 10, 0xFFFFFFFF);
        gfx.centeredText(font,
            "Drag to move  •  Scroll or +/\u2212 to resize  •  Arrow keys to nudge",
            width / 2, 24, 0xFFAAAAAA);

        // Row labels (left of each control row)
        int labelColor = 0xFFCCCCCC;
        gfx.centeredText(font, "Language:", width / 2 - 120, height / 2 - 73, labelColor);
        gfx.centeredText(font, "Font:",     width / 2 - 120, height / 2 - 47, labelColor);
        gfx.centeredText(font, "Case:",     width / 2 - 120, height / 2 - 21, labelColor);
        gfx.centeredText(font, "Size:",     width / 2 - 120, height / 2 +  5, labelColor);

        drawPreviewLabel(gfx);

        super.extractRenderState(gfx, mouseX, mouseY, delta);
    }

    private void drawPreviewLabel(GuiGraphicsExtractor gfx) {
        int sw = scaledW(), sh = scaledH();

        gfx.fill(counterX, counterY, counterX + sw, counterY + sh, 0xAA000000);
        gfx.outline(counterX, counterY, sw, sh, 0xFF44AAFF);

        // Draw preview text using chosen font and case
        String preview = currentCase.apply(language.format(0));

        var ps = gfx.pose();
        ps.pushMatrix();
        ps.translate(counterX + PAD * scale, counterY + PAD * scale);
        ps.scale(scale, scale);
        gfx.text(currentFont.getFont(this.font, minecraft.fontFilterFishy), preview, 0, 0, 0xFFFFFFFF, true);
        ps.popMatrix();
    }

    // ── Mouse ──────────────────────────────────────────────────────────────────
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && isOverLabel((int) event.x(), (int) event.y())) {
            dragging = true;
            dragOffX = (int) event.x() - counterX;
            dragOffY = (int) event.y() - counterY;
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0 && dragging) { dragging = false; return true; }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (dragging) {
            counterX = clampX((int) event.x() - dragOffX);
            counterY = clampY((int) event.y() - dragOffY);
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        if (isOverLabel((int) mx, (int) my)) {
            changeScale((float) scrollY * DayCounterConfig.SCALE_STEP);
            return true;
        }
        return super.mouseScrolled(mx, my, scrollX, scrollY);
    }

    // ── Keyboard ───────────────────────────────────────────────────────────────
    @Override
    public boolean keyPressed(KeyEvent event) {
        int step = (event.modifiers() & 1) != 0 ? 5 : 1;
        switch (event.key()) {
            case 262 -> { counterX = clampX(counterX + step); return true; }
            case 263 -> { counterX = clampX(counterX - step); return true; }
            case 264 -> { counterY = clampY(counterY + step); return true; }
            case 265 -> { counterY = clampY(counterY - step); return true; }
        }
        return super.keyPressed(event);
    }

    // ── Close ──────────────────────────────────────────────────────────────────
    @Override
    public void onClose() { saveAndClose(); }

    // ── Helpers ────────────────────────────────────────────────────────────────
    private void recomputeLabelSize() {
        Font f = currentFont.getFont(this.font, minecraft.fontFilterFishy);
        int maxW = 0;
        for (DayCounterLanguage l : DayCounterLanguage.values()) {
            maxW = Math.max(maxW, f.width(currentCase.apply(l.format(8888))));
        }
        labelW = maxW + PAD * 2;
        labelH = f.lineHeight + PAD * 2;
    }

    private int scaledW() { return Math.round(labelW * scale); }
    private int scaledH() { return Math.round(labelH * scale); }

    private boolean isOverLabel(int mx, int my) {
        return mx >= counterX && mx <= counterX + scaledW()
            && my >= counterY && my <= counterY + scaledH();
    }

    private int clampX(int x) { return Math.max(0, Math.min(x, width  - scaledW())); }
    private int clampY(int y) { return Math.max(0, Math.min(y, height - scaledH())); }

    private void cycleLanguage(int dir) {
        language = (dir > 0) ? language.next() : language.prev();
        if (langDisplayBtn != null)
            langDisplayBtn.setMessage(Component.literal(language.displayName));
        recomputeLabelSize();
        counterX = clampX(counterX);
        counterY = clampY(counterY);
    }

    private void cycleFont(int dir) {
        currentFont = (dir > 0) ? currentFont.next() : currentFont.prev();
        if (fontDisplayBtn != null)
            fontDisplayBtn.setMessage(Component.literal(currentFont.displayName));
        recomputeLabelSize();
        counterX = clampX(counterX);
        counterY = clampY(counterY);
    }

    private void cycleCase(int dir) {
        currentCase = (dir > 0) ? currentCase.next() : currentCase.prev();
        if (caseDisplayBtn != null)
            caseDisplayBtn.setMessage(Component.literal(currentCase.displayName));
        recomputeLabelSize();
        counterX = clampX(counterX);
        counterY = clampY(counterY);
    }

    private void changeScale(float delta) {
        scale = Math.round(
            Math.max(DayCounterConfig.SCALE_MIN,
                     Math.min(DayCounterConfig.SCALE_MAX, scale + delta))
            / DayCounterConfig.SCALE_STEP
        ) * DayCounterConfig.SCALE_STEP;

        counterX = clampX(counterX);
        counterY = clampY(counterY);

        if (scaleDisplayBtn != null)
            scaleDisplayBtn.setMessage(Component.literal(scaleText()));
    }

    private String scaleText() { return String.format("%.2fx", scale); }

    private void applyPreset(Preset p) {
        int x = p.rawX, y = p.rawY;
        switch (p) {
            case TOP_CENTER -> x = (width  - scaledW()) / 2;
            case TOP_RIGHT  -> x =  width  - scaledW() - 2;
            case BOT_LEFT   -> y =  height - scaledH() - 2;
            case BOT_RIGHT  -> { x = width - scaledW() - 2; y = height - scaledH() - 2; }
            default         -> {}
        }
        counterX = clampX(x);
        counterY = clampY(y);
    }

    private void saveAndClose() {
        cfg.x     = counterX;
        cfg.y     = counterY;
        cfg.refW  = width;
        cfg.refH  = height;
        cfg.scale = scale;
        cfg.setLanguage(language);
        cfg.setFont(currentFont);
        cfg.setTextCase(currentCase);
        cfg.save();
        Minecraft.getInstance().setScreen(parent);
    }
}
