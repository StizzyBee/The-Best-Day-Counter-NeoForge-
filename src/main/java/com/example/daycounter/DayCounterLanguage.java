package com.example.daycounter;

/**
 * The six supported display languages.
 *
 * Each entry holds:
 *   • id          – saved to config (stable, never rename these)
 *   • displayName – shown on the cycle button in the settings screen
 *   • dayWord     – the translated word for "Day"
 *
 * Minecraft's default font covers the Latin, Cyrillic, Arabic, and CJK
 * glyph ranges needed for all six languages below via its built-in
 * Unicode font fallback (force-unicode-font option or the provider chain
 * in assets/minecraft/font/default.json).  No extra font files needed.
 */
public enum DayCounterLanguage {

    ENGLISH   ("english",    "English",    "Day"),
    SPANISH   ("spanish",    "Español",    "Día"),
    PORTUGUESE("portuguese", "Português",  "Dia"),
    FRENCH    ("french",     "Français",   "Jour"),
    RUSSIAN   ("russian",    "Русский",    "День"),
    CHINESE   ("chinese",    "中文",        "天");

    public final String id;
    public final String displayName;
    public final String dayWord;

    DayCounterLanguage(String id, String displayName, String dayWord) {
        this.id          = id;
        this.displayName = displayName;
        this.dayWord     = dayWord;
    }

    /** Formats the counter string, e.g. "Day: 5" or "Día: 5". */
    public String format(long day) {
        return dayWord + ": " + day;
    }

    /** Finds by id; falls back to ENGLISH if the saved id is unknown. */
    public static DayCounterLanguage fromId(String id) {
        for (DayCounterLanguage l : values()) {
            if (l.id.equalsIgnoreCase(id)) return l;
        }
        return ENGLISH;
    }

    /** Returns the next language in the cycle (wraps around). */
    public DayCounterLanguage next() {
        DayCounterLanguage[] v = values();
        return v[(ordinal() + 1) % v.length];
    }

    /** Returns the previous language in the cycle (wraps around). */
    public DayCounterLanguage prev() {
        DayCounterLanguage[] v = values();
        return v[(ordinal() + v.length - 1) % v.length];
    }
}
