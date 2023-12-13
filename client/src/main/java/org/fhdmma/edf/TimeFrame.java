package org.fhdmma.edf;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Queue;
import lombok.AllArgsConstructor;
import java.io.Serializable;

public class TimeFrame implements Serializable
{
    enum State {
        DONE,
        RUNNING,
        WAITING
    };
    private interface Action extends Serializable {}
    @AllArgsConstructor
    static public class AddTask implements Action { final public Task task; }
    @AllArgsConstructor
    static public class RemoveTask implements Action { final public int id; }

    final static private long serialVersionUID = 1L;

    final private int id;
    final private HashMap<Integer, Task> tasks;
    final private HashMap<Integer, Integer> nextPeriod;
    final private HashMap<Integer, State> states;
    final private Queue<Action> actions;
    private int current;
    private int left;

    TimeFrame() {
        id = -1;
        tasks = null;
        nextPeriod = null;
        states = null;
        actions = null;
        current = -1;
        left = -1;
    }

    public HashMap<Integer, Task> getTasks() { return tasks; }
    public HashMap<Integer, Integer> getTimeFramesNeeded() { return nextPeriod; }
    public HashMap<Integer, State> getStates() { return states; }
    public int getTimeLeft() { return left; }
    public int getId() { return id; }
    public int getCurrentTask() { return current; }

    public String toString() {
        return "{ tasks: " + tasks + ", states: " + states + ", nextPeriod: " +
            nextPeriod + ", left: " + left + " }";
    }
}
