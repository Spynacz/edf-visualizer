package org.fhdmma.edf;
import java.io.Serializable;

public final class Task implements Serializable
{
    final private static long serialVersionUID = 1l;

    private final long id;
    private final int duration;
    private final int period;

    public Task(long i, int d, int p) {
        duration = d;
        period = p;
        id = i;
    }

    public Task(int d, int p) {
        duration = d;
        period = p;
        id = generateId();
    }

    public long getId() { return id; }
    public int getDuration() { return duration; }
    public int getPeriod() { return period; }

    public String toString() {
        return "{ id: " + id +
            ", duration: " + duration +
            ", period: " + period + " }";
    }

    private long generateId() {
        return System.currentTimeMillis()*1000 +
            Utility.rand.nextInt()%1000000;
    }
}
