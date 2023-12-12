package org.fhdmma.edf;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Queue;

@AllArgsConstructor
@Data
public class TimeFrame
{
    enum State {
        DONE,
        RUNNING,
        WAITING
    };
    private interface Action {}
    @AllArgsConstructor
    static public class AddTask implements Action { final public Task task; }
    @AllArgsConstructor
    static public class RemoveTask implements Action { final public int id; }

    final private int id;
    final private HashMap<Integer, Task> tasks;
    final private HashMap<Integer, Integer> nextPeriod;
    final private HashMap<Integer, State> states;
    final private Queue<Action> actions;
    private int current;
    private int left;

    TimeFrame(List<Task> t) {
        id = 0;
        nextPeriod = new HashMap<>();
        states = new HashMap<>();
        tasks = new HashMap<>();
        actions = new LinkedList<>();
        for(var task: t) {
            tasks.put(task.id, task);
            nextPeriod.put(task.id, task.period);
            states.put(task.id, State.WAITING);
        }
        startTask();
    }

    TimeFrame(TimeFrame tf) {
        id = tf.id+1;
        nextPeriod = new HashMap<>();
        states = new HashMap<>();
        actions = tf.actions;
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
        for(var n: actions) {
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

    public void addTask(Task t) {
        actions.add(new AddTask(t));
    }

    public void removeTask(int id) {
        actions.add(new RemoveTask(id));
    }

    public int getTimeLeft() { return left; }

    public int getId() { return id; }

    public int getCurrentTask() { return current; }

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
