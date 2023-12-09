package org.fhdmma.edf;

public class Task 
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
