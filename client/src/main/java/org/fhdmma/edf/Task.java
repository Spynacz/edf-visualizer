package org.fhdmma.edf;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Task {
    private String name;
    private int deadline;
    private int duration;
}
