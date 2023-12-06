package org.fhdmma.edf;

public class Task 
{
    public int duration;
    public int period;

    public Task(int d, int p) {
        duration = d;
        period = p;
    }

    public String toString() {
        return "{ duration: " + duration + ", period: " + period + " }";
    }
}
