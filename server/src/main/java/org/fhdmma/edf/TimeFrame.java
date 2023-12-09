package org.fhdmma.edf;
import java.util.List;
import java.util.LinkedList;

public class TimeFrame 
{
    enum State {
        DONE,
        RUNNING,
        WAITING
    };

    private int id;
    private List<Task> tasks;
    private List<Integer> nextPeriod;
    private List<State> states;
    private int current;
    private int left;

    TimeFrame(List<Task> t) {
        id = 0;
        nextPeriod = new LinkedList<>();
        states = new LinkedList<>();
        tasks = t;
        for(var task: tasks) {
            nextPeriod.add(task.period);
            states.add(State.WAITING);
        }
        startTask();
    }

    TimeFrame(TimeFrame tf) {
        id = tf.id+1;
        nextPeriod = new LinkedList<>();
        states = new LinkedList<>();
        tasks = tf.tasks;
        left = tf.left-((tf.left>0)?1:0);
        int index = 0;
        current = tf.current;
        var state_i = tf.states.iterator();
        for(var n: tf.nextPeriod) {
            if(n-1!=0) {
                nextPeriod.add(n-1);
                states.add(state_i.next());
            } else {
                nextPeriod.add(tasks.get(index).period);
                states.add(State.WAITING);
                state_i.next();
            }
            index++;
        }
        if(left == 0) {
            if(current != -1)
                states.set(current, State.DONE);
            startTask();
        }
    }

    private void startTask() {
        current = getEDIndex();
        if(current != -1) {
            left = tasks.get(current).duration;
            states.set(current, State.RUNNING);
        }
    }

    public int getTimeLeft() { return left; }

    public int getId() { return id; }

    public int getCurrentTask() { return current; }

    public String toString() {
        return "{ tasks: " + tasks + ", states: " + states + ", nextPeriod: " +
            nextPeriod + ", left: " + left + " }";
    }

    private int getEDIndex() {
        int index = 0;
        int min_index = -1;
        Integer t;
        Integer min = Integer.MAX_VALUE;
        var i = tasks.iterator();
        while(i.hasNext()) {
            t = i.next().period;
            if(states.get(index) == State.WAITING && min > t) {
                min = t;
                min_index = index;
            }
            index+=1;
        }
        return min_index;
    }

}
