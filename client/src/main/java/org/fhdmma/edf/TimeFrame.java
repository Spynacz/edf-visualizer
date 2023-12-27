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

    final private long id;
    final private HashMap<Long, Task> tasks;
    final private HashMap<Long, Integer> nextPeriod;
    final private HashMap<Long, State> states;
    final private List<Action> changes;
    final private long parent;
    private long current;
    private int left;

    private TimeFrame() {
        id = -1;
        tasks = new HashMap<>();
        nextPeriod = new HashMap<>();
        states = new HashMap<>();
        changes = new LinkedList<>();
        parent = -1;
        current = -1;
        left = -1;
    }

    public HashMap<Long, Task> getTasks() { return tasks; }
    public HashMap<Long, Integer> getTimeFramesNeeded() { return nextPeriod; }
    public HashMap<Long, State> getStates() { return states; }
    public int getTimeLeft() { return left; }
    public long getId() { return id; }
    public long getCurrentTask() { return current; }
    public long getParent() { return parent; }
    public List<Action> getChanges() { return changes; }

    public String toString() {
        return "{ tasks: " + tasks + ", states: " + states + ", nextPeriod: " +
            nextPeriod + ", left: " + left + " }";
    }
}
