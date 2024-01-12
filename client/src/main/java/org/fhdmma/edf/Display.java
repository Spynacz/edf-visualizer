package org.fhdmma.edf;
import java.util.HashMap;

public final class Display {
    private static HashMap<Long, Integer> prevPeriods = null;
    private static HashMap<Long, Boolean> prevColors = null;
    private static TimeFrame prev = null;

    private Display() {}

    public static void show(TimeFrame tf) {
        if(prevPeriods == null)
            prevPeriods = new HashMap<>();
        if(prevColors == null)
            prevColors = new HashMap<>();
        Integer prevPeriod;
        for(var id: tf.getTasks().keySet()) {
            if(prevPeriods.containsKey(id)) {
                prevPeriod = prevPeriods.get(id);
                prevPeriods.replace(id, tf.getTimeFramesNeeded().get(id));
                if(prevPeriods.get(id)!=null) {
                    if(prevPeriod-1 != prevPeriods.get(id))
                        prevColors.replace(id, !prevColors.get(id));
                    if(prevColors.get(id))
                        System.out.print("\u001B[96m");
                }
            } else {
                prevPeriods.put(id, tf.getTimeFramesNeeded().get(id));
                prevColors.put(id, false);
            }
            if(tf.getStates().get(id) == TimeFrame.State.RUNNING) {
                System.out.print(" |");
            } else {
                System.out.print("| ");
            }
            System.out.print("\u001B[00m ");
        }
        prev = tf;
        System.out.println("");
    }
}
