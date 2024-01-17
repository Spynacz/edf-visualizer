package org.fhdmma.edf;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

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
}

