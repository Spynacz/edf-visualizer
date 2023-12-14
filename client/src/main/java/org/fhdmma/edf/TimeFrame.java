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
    final private List<Action> change;
    final private int parent;
    private int current;
    private int left;

    public HashMap<Integer, Task> getTasks() { return tasks; }
    public HashMap<Integer, Integer> getTimeFramesNeeded() { return nextPeriod; }
    public HashMap<Integer, State> getStates() { return states; }
    public int getTimeLeft() { return left; }
    public int getId() { return id; }
    public int getCurrentTask() { return current; }
    public int getParent() { return parent; }
    public List<Action> getChange() { return change; }

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
