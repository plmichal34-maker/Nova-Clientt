package dev.novaclient.util;

/** Czas dla animacji. */
public final class Time {
    private static final long START = System.nanoTime();
    private static long last = 0;

    private Time() {}

    /** Sekundy od startu (zawijane co godzine, zeby nie tracic precyzji float). */
    public static float seconds() {
        return (float) (((System.nanoTime() - START) / 1_000_000_000.0) % 3600.0);
    }

    /** Czas od poprzedniego wywolania w sekundach (max 0.1). */
    public static float frameDelta() {
        long now = System.nanoTime();
        float dt = last == 0 ? 0.016f : Math.min(0.1f, (now - last) / 1e9f);
        last = now;
        return dt;
    }

    public static float hash01(int a, int b, int c) {
        int h = a * 73856093 ^ b * 19349663 ^ c * 83492791;
        h ^= h >>> 13;
        h *= 0x5bd1e995;
        h ^= h >>> 15;
        return (h & 0xFFFF) / 65535f;
    }
}
