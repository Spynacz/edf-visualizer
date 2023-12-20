package org.fhdmma.edf;
import java.io.Serializable;
import lombok.Getter;

@Getter
public final class Task implements Serializable
{
    public final int id;
    public final int duration;
    public final int period;

    public Task(int i, int d, int p) {
        duration = d;
        period = p;
        id = i;
    }

    public String toString() {
        return "{ id: " + id +
            ", duration: " + duration +
            ", period: " + period + " }";
    }
}
