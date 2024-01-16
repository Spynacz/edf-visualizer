package org.fhdmma.edf;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class Task implements Serializable {
    final private static long serialVersionUID = 1l;

    private final long id;
    private final String name;
    private final int duration;
    private final int period;
    private final long userId;

    public Task(String n, int d, int p, long u) {
        id = generateId();
        name = n;
        duration = d;
        period = p;
        userId = u;
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
