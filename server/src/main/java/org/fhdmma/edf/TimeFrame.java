package org.fhdmma.edf;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TimeFrame implements Serializable {
    public enum State {
        DONE,
        RUNNING,
        WAITING
    };

    public interface Action extends Serializable {
    }

    @AllArgsConstructor
    static public class AddTask implements Action {
        final public Task task;
    }

    @AllArgsConstructor
    static public class RemoveTask implements Action {
        final public long id;
    }

    final private static long serialVersionUID = 1l;

    final private long id;
    final private HashMap<Long, Task> tasks;
    final private HashMap<Long, Integer> nextPeriod;
    final private HashMap<Long, State> states;
    final private List<Action> changes;
    final private long parent;
    private long currentTask;
    private int timeLeft;
    final private long user;

    TimeFrame(long u, List<Task> t) {
        user = u;
        parent = -1;
        id = generateId();
        nextPeriod = new HashMap<>();
        states = new HashMap<>();
        tasks = new HashMap<>();
        changes = new LinkedList<>();

        for (var task : t) {
            tasks.put(task.getId(), task);
            nextPeriod.put(task.getId(), task.getPeriod());
            states.put(task.getId(), State.WAITING);
        }
        startTask();
    }

    TimeFrame(long u) {
        user = u;
        tasks = new HashMap<>();
        states = new HashMap<>();
        nextPeriod = new HashMap<>();
        changes = new LinkedList<>();
        currentTask = -1;
        timeLeft = 0;
        parent = -1;
        id = generateId();
    }

    TimeFrame(TimeFrame tf, List<Action> l) {
        nextPeriod = new HashMap<>();
        states = new HashMap<>();
        changes = new LinkedList(l);
        user = tf.user;
        parent = tf.getId();
        id = generateId();
        timeLeft = tf.getTimeLeft() - ((tf.getTimeLeft() > 0) ? 1 : 0);
        currentTask = tf.getCurrentTask();
        states.putAll(tf.getStates());

        if (l != null && !l.isEmpty()) {
            tasks = tf.getTasks();
        } else {
            tasks = new HashMap<>(tf.getTasks());
        }

        int next;
        for (var n : tf.getNextPeriod().keySet()) {
            next = tf.getNextPeriod().get(n);
            if (next - 1 != 0) {
                nextPeriod.put(n, next - 1);
            } else {
                nextPeriod.put(n, tasks.get(n).getPeriod());
                states.replace(n, State.WAITING);
            }
        }

        for (var n : l) {
            Task t;
            if (n instanceof AddTask) {
                t = ((AddTask) n).task;
                tasks.put(t.getId(), t);
                nextPeriod.put(t.getId(), t.getPeriod());
                states.put(t.getId(), State.WAITING);
            } else if (n instanceof RemoveTask) {
                if (currentTask == ((RemoveTask) n).id) {
                    currentTask = -1;
                    timeLeft = 0;
                }
                tasks.remove(((RemoveTask) n).id);
                nextPeriod.remove(((RemoveTask) n).id);
                states.remove(((RemoveTask) n).id);
            }
        }

        if (timeLeft == 0) {
            if (currentTask != -1)
                states.replace(currentTask, State.DONE);
            startTask();
        }
    }

    public String toString() {
        return "{ tasks: " + tasks + ", states: " + states + ", nextPeriod: " +
                nextPeriod + ", left: " + timeLeft + " }";
    }

    private long generateId() {
        return System.currentTimeMillis() * 1000000 + // Trim 3 leading numbers
                parent % 1000000 + Utility.rand.nextInt() % 1000000;
    }

    private long getEDId() {
        long min_id = -1;
        long id;
        Integer period;
        Integer min = Integer.MAX_VALUE;
        var i = tasks.entrySet().iterator();

        while (i.hasNext()) {
            id = i.next().getKey();
            period = tasks.get(id).getPeriod();

            if (states.get(id) == State.WAITING && min > period) {
                min = period;
                min_id = id;
            }
        }
        return min_id;
    }

    private void startTask() {
        currentTask = getEDId();
        if (currentTask != -1) {
            timeLeft = tasks.get(currentTask).getDuration();
            states.replace(currentTask, State.RUNNING);
        }
    }
}
