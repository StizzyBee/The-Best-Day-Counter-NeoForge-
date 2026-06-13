package com.example.daycounter;

public enum DayCounterTextCase {
    NORMAL("normal", "Normal"),
    UPPER("upper", "ALL CAPS"),
    LOWER("lower", "lowercase");

    public final String id;
    public final String displayName;

    DayCounterTextCase(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String apply(String text) {
        return switch (this) {
            case NORMAL -> text;
            case UPPER  -> text.toUpperCase();
            case LOWER  -> text.toLowerCase();
        };
    }

    public static DayCounterTextCase fromId(String id) {
        for (DayCounterTextCase c : values()) {
            if (c.id.equalsIgnoreCase(id)) return c;
        }
        return NORMAL;
    }

    public DayCounterTextCase next() {
        DayCounterTextCase[] v = values();
        return v[(ordinal() + 1) % v.length];
    }

    public DayCounterTextCase prev() {
        DayCounterTextCase[] v = values();
        return v[(ordinal() + v.length - 1) % v.length];
    }
}
