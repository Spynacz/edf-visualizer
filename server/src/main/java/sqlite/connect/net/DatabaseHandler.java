package sqlite.connect.net;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.InvalidAttributeValueException;

import org.fhdmma.edf.Task;
import org.fhdmma.edf.TimeFrame;
import org.fhdmma.edf.User;

public class DatabaseHandler {
    public static void connect() throws SQLException {
        Database.connect();
        Database.createDatabase();
    }

    public static void disconnect() throws SQLException {
        Database.disconnect();
    }

    public static Boolean isValid() {
        return Database.isValid();
    }

    public static User userLogin(String username, String password) throws SQLException {
        return Database.retrieveUser(username, password);
    }

    public static Boolean userRegister(String username, String password1, String password2) throws InvalidAttributeValueException {
        if (!password2.equals(password1)) {
            throw new InvalidAttributeValueException("Passwords are not the same!");
        }

        if (Database.isUsernameUnique(username)){
            throw new InvalidAttributeValueException("Username is taken.");
        }

        return Database.insertUser(username, password1);
    }

    private static void addChangeList(long timeframe_id, List<TimeFrame.Action> queue) {
        for (TimeFrame.Action action : queue) {
            if (action instanceof TimeFrame.AddTask){
                Database.insertChange(timeframe_id, ((TimeFrame.AddTask)action).task.getId(), "ADD");
            }
            else if (action instanceof TimeFrame.RemoveTask){
                Database.insertChange(timeframe_id, ((TimeFrame.RemoveTask)action).id, "REMOVE");
            }
        }
    }

    private static void addPeriodList(long timeframe_id, HashMap<Long, Integer> periods){
        for (Map.Entry<Long, Integer> period : periods.entrySet()) {
            Database.insertPeriod(timeframe_id, period.getKey(), period.getValue());
        }
    }

    private static void addStateList(long timeframe_id, HashMap<Long, TimeFrame.State> states){
        for (Map.Entry<Long, TimeFrame.State> state : states.entrySet()) {
            Database.insertState(timeframe_id, state.getKey(), state.getValue().toString());
        }
    }

    public static Task addTask(long timeframe_id, int duration, int period) {
        return Database.insertTask(timeframe_id, duration, period);
    }

    public static Task addTask(long timeframe_id, Task task) {
        return Database.insertTask(timeframe_id, task.getId(), task.getDuration(), task.getPeriod());
    }

    private static void addTaskList(long timeframe_id, HashMap<Long, Task> tasks) {
        for (Task task : tasks.values()) {
            Database.insertTask(timeframe_id, task.getDuration(), task.getPeriod());
        }
    }

    public static void addTimeFrame(TimeFrame tf) {
        Database.insertTimeFrame(tf);
        addTaskList(tf.getId(), tf.getTasks());
        addPeriodList(tf.getId(), tf.getNextPeriod());
        addStateList(tf.getId(), tf.getStates());
        addChangeList(tf.getId(), tf.getChanges());
    }

    public static Task getLatestTask() {
        return Database.retrieveLatestTask();
    }

    public static TimeFrame getLatestTimeFrame() throws SQLException {
        return Database.retrieveLatestTimeFrame();
    }

    public static void printTasks() throws SQLException {
        Database.printTasks();
    }

    public static void printTimeFrames() throws SQLException {
        Database.printTimeFrames();
    }
}
