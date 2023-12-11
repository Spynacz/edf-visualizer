package org.fhdmma.edf;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;

public class TimeFrame
{
    enum State {
        DONE,
        RUNNING,
        WAITING
    };

    //TODO: Use task ids instead of indexes in Lists
    private int id;
    private List<Task> tasks;
    private HashMap<Integer, Integer> nextPeriod;
    private HashMap<Integer, State> states;
    private int current;
    private int left;

    TimeFrame(List<Task> t) {
        id = 0;
        nextPeriod = new HashMap<>();
        states = new HashMap<>();
        tasks = t;
        for(var task: tasks) {
            nextPeriod.put(task.id, task.period);
            states.put(task.id, State.WAITING);
        }
        startTask();
    }

    TimeFrame(TimeFrame tf) {
        id = tf.id+1;
        nextPeriod = new HashMap<>();
        states = new HashMap<>();
        tasks = tf.tasks;
        left = tf.left-((tf.left>0)?1:0);
        current = tf.current;
        states.putAll(tf.states);
        int next;
        for(var n: tf.nextPeriod.keySet()) {
            next = tf.nextPeriod.get(n);
            if(next-1!=0) {
                nextPeriod.put(n, next-1);
            } else {
                nextPeriod.put(n, tasks.get(n).period);
                states.replace(n, State.WAITING);
            }
        }
        if(left == 0) {
            if(current != -1)
                states.replace(current, State.DONE);
            startTask();
        }
    }

    private void startTask() {
        current = getEDId();
        if(current != -1) {
            left = tasks.get(current).duration;
            states.replace(current, State.RUNNING);
        }
    }

    public int getTimeLeft() { return left; }

    public int getId() { return id; }

    public int getCurrentTask() { return current; }

    public String toString() {
        return "{ tasks: " + tasks + ", states: " + states + ", nextPeriod: " +
            nextPeriod + ", left: " + left + " }";
    }

    private int getEDId() {
        int index = 0;
        int min_id = -1;
        Task t;
        Integer min = Integer.MAX_VALUE;
        var i = tasks.iterator();
        while(i.hasNext()) {
            t = i.next();
            if(states.get(t.id) == State.WAITING && min > t.period) {
                min = t.period;
                min_id = t.id;
            }
        }
        return min_id;
    }
}
