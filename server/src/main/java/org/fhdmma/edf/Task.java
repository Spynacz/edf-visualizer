package org.fhdmma.edf;

import java.io.Serializable;

import lombok.Getter;

@Getter
public final class Task implements Serializable {
    final private static long serialVersionUID = 1l;

    private final long id;
    private final int duration;
    private final int period;

    public Task(long i, int d, int p) {
        id = i;
        duration = d;
        period = p;
    }

    public Task(int d, int p) {
        id = generateId();
        duration = d;
        period = p;
    }

    public String toString() {
        return "{ id: " + id +
                ", duration: " + duration +
                ", period: " + period + " }";
    }

    private long generateId() {
        return System.currentTimeMillis() * 1000 +
                Utility.rand.nextInt() % 1000000;
    }
}
