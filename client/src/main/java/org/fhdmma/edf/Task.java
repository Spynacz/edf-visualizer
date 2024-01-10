package org.fhdmma.edf;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Task implements Serializable {
    final private static long serialVersionUID = 1l;

    private final long id;
    private final String name;
    private final int duration;
    private final int period;

    public Task(String name, int duration, int period) {
        this.id = generateId(name);
        this.name = name;
        this.duration = duration;
        this.period = period;
    }

    private long generateId(String name) {
        // Name representation in radix 36
        // Consider changing to work with non ASCII characters
        return Long.parseLong(name, 36);
    }
}
