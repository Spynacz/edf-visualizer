package org.fhdmma.edf;
import java.io.Serializable;

import lombok.Data;

@Data
public class Task implements Serializable
{
    public int id;
    public int duration;
    public int period;

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
