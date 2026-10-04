package com.example.daycounter;

import net.minecraft.client.gui.Font;

public enum DayCounterFont {
    DEFAULT("default", "Default"),
    FILTERED("filtered", "Filtered");

    public final String id;
    public final String displayName;

    DayCounterFont(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public Font getFont(Font defaultFont, Font filteredFont) {
        return this == DEFAULT ? defaultFont : filteredFont;
    }

    public static DayCounterFont fromId(String id) {
        for (DayCounterFont f : values()) {
            if (f.id.equalsIgnoreCase(id)) return f;
        }
        return DEFAULT;
    }

    public DayCounterFont next() {
        DayCounterFont[] v = values();
        return v[(ordinal() + 1) % v.length];
    }

    public DayCounterFont prev() {
        DayCounterFont[] v = values();
        return v[(ordinal() + v.length - 1) % v.length];
    }
}
