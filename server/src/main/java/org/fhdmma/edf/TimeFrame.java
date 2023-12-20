package org.fhdmma.edf;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Queue;
import java.io.Serializable;

@AllArgsConstructor
@Data
public class TimeFrame implements Serializable
{
    enum State {
        DONE,
        RUNNING,
        WAITING
    };
    public interface Action extends Serializable {}
    @AllArgsConstructor
    static public class AddTask implements Action { final public Task task; }
    @AllArgsConstructor
    static public class RemoveTask implements Action { final public int id; }

    final private static long serialVersionUID = 1l;

    final private int id;
    final private HashMap<Integer, Task> tasks;
    final private HashMap<Integer, Integer> nextPeriod;
    final private HashMap<Integer, State> states;
    final private List<Action> changes;
    final private int parent;
    private int current;
    private int left;

    TimeFrame(List<Task> t) {
        id = 0;
        nextPeriod = new HashMap<>();
        states = new HashMap<>();
        tasks = new HashMap<>();
        changes = null;
        parent = -1;
        for(var task: t) {
            tasks.put(task.id, task);
            nextPeriod.put(task.id, task.period);
            states.put(task.id, State.WAITING);
        }
        startTask();
    }

    TimeFrame() {
        id = 0;
        tasks = new HashMap<>();
        states = new HashMap<>();
        nextPeriod = new HashMap<>();
        changes = null;
        current = -1;
        left = 0;
        parent = -1;
    }

    TimeFrame(TimeFrame tf, List<Action> l) {
        nextPeriod = new HashMap<>();
        states = new HashMap<>();
        id = tf.getId()+1; //PLACEHOLDER
        changes = l;
        parent = tf.getId();
        left = tf.getTimeLeft()-((tf.getTimeLeft()>0)?1:0);
        current = tf.getCurrentTask();
        states.putAll(tf.getStates());
        if(l!=null && !l.isEmpty()) {
            tasks = tf.getTasks();
        } else {
            tasks = new HashMap<>(tf.getTasks());
        }
        int next;

        for(var n: tf.getTimeFramesNeeded().keySet()) {
            next = tf.getTimeFramesNeeded().get(n);
            if(next-1!=0) {
                nextPeriod.put(n, next-1);
            } else {
                nextPeriod.put(n, tasks.get(n).period);
                states.replace(n, State.WAITING);
            }
        }
        for(var n: l) {
            Task t;
            if(n instanceof AddTask) {
                t = ((AddTask)n).task;
                tasks.put(t.id, t);
                nextPeriod.put(t.id, t.period);
                states.put(t.id, State.WAITING);
            } else if (n instanceof RemoveTask) {
                if(current == ((RemoveTask)n).id) {
                    current = -1;
                    left = 0;
                }
                tasks.remove(((RemoveTask)n).id);
                nextPeriod.remove(((RemoveTask)n).id);
                states.remove(((RemoveTask)n).id);
            }
        }
        if(left == 0) {
            if(current != -1)
                states.replace(current, State.DONE);
            startTask();
        }
    }

    public HashMap<Integer, Task> getTasks() { return tasks; }
    public HashMap<Integer, Integer> getTimeFramesNeeded() { return nextPeriod; }
    public HashMap<Integer, State> getStates() { return states; }
    public int getTimeLeft() { return left; }
    public int getId() { return id; }
    public int getCurrentTask() { return current; }
    public int getParent() { return parent; }
    public List<Action> getChanges() { return changes; }

    public String toString() {
        return "{ tasks: " + tasks + ", states: " + states + ", nextPeriod: " +
            nextPeriod + ", left: " + left + " }";
    }

    private int getEDId() {
        int min_id = -1;
        Task t;
        int id;
        Integer period;
        Integer min = Integer.MAX_VALUE;
        var i = tasks.entrySet().iterator();
        while(i.hasNext()) {
            id = i.next().getKey();
            period = tasks.get(id).period;
            if(states.get(id) == State.WAITING && min > period) {
                min = period;
                min_id = id;
            }
        }
        return min_id;
    }

    private void startTask() {
        current = getEDId();
        if(current != -1) {
            left = tasks.get(current).duration;
            states.replace(current, State.RUNNING);
        }
    }
}
