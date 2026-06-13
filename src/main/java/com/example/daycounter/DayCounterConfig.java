package com.example.daycounter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.*;
import java.nio.file.*;

/**
 * Persists position, scale, and language to config/day-counter.json
 */
public class DayCounterConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_FILE =
        FMLPaths.CONFIGDIR.get().resolve("day-counter.json");

    public static final float SCALE_MIN  = 0.5f;
    public static final float SCALE_MAX  = 5.0f;
    public static final float SCALE_STEP = 0.25f;

    public int    x         = 2;
    public int    y         = 2;
    public float  scale     = 1.0f;
    public int    refW      = 0;
    public int    refH      = 0;
    public String language  = DayCounterLanguage.ENGLISH.id;
    public String font      = DayCounterFont.DEFAULT.id;
    public String textCase  = DayCounterTextCase.NORMAL.id;

    // ── Derived (not serialised) ───────────────────────────────────────────────

    /** Convenience getter — resolves the stored id to the enum. */
    public DayCounterLanguage getLanguage() {
        return DayCounterLanguage.fromId(language);
    }

    public void setLanguage(DayCounterLanguage lang) {
        this.language = lang.id;
    }

    public DayCounterFont getFont() {
        return DayCounterFont.fromId(font);
    }

    public void setFont(DayCounterFont f) {
        this.font = f.id;
    }

    public DayCounterTextCase getTextCase() {
        return DayCounterTextCase.fromId(textCase);
    }

    public void setTextCase(DayCounterTextCase c) {
        this.textCase = c.id;
    }

    // ── Serialisation ──────────────────────────────────────────────────────────

    public static DayCounterConfig load() {
        if (Files.exists(CONFIG_FILE)) {
            try (Reader r = Files.newBufferedReader(CONFIG_FILE)) {
                DayCounterConfig cfg = GSON.fromJson(r, DayCounterConfig.class);
                if (cfg != null) {
                    cfg.scale = Math.max(SCALE_MIN, Math.min(SCALE_MAX, cfg.scale));
                    if (cfg.language == null) cfg.language = DayCounterLanguage.ENGLISH.id;
                    if (cfg.font == null)     cfg.font     = DayCounterFont.DEFAULT.id;
                    if (cfg.textCase == null) cfg.textCase = DayCounterTextCase.NORMAL.id;
                    return cfg;
                }
            } catch (Exception ignored) {}
        }
        return new DayCounterConfig();
    }

    public void save() {
        try (Writer w = Files.newBufferedWriter(CONFIG_FILE)) {
            GSON.toJson(this, w);
        } catch (Exception ignored) {}
    }
}
