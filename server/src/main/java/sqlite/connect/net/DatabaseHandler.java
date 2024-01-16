package sqlite.connect.net;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.auth.login.FailedLoginException;

import org.fhdmma.edf.Task;
import org.fhdmma.edf.TimeFrame;
import org.fhdmma.edf.User;

public class DatabaseHandler {
    public static boolean exists() {
        return Database.exists();
    }

    public static void connect() throws SQLException {
        Database.connect();
    }

    public static void init() throws SQLException {
        Database.createDatabase();
    }

    public static void disconnect() throws SQLException {
        Database.disconnect();
    }

    public static Boolean isValid() {
        return Database.isValid();
    }

    public static User userLogin(String username, String password) throws SQLException, FailedLoginException {
        User u = Database.retrieveUser(username, password);
        if (u == null) {
            Database.insertUser(username, password);
            u = Database.retrieveUser(username, password);
        }

        return u;
    }

    private static void addChangeList(long timeframe_id, List<TimeFrame.Action> queue) {
        for (TimeFrame.Action action : queue) {
            if (action instanceof TimeFrame.AddTask) {
                Database.insertChange(timeframe_id, ((TimeFrame.AddTask) action).task.getId(), "ADD");
            } else if (action instanceof TimeFrame.RemoveTask) {
                Database.insertChange(timeframe_id, ((TimeFrame.RemoveTask) action).id, "REMOVE");
            }
        }
    }

    private static void addPeriodList(long timeframe_id, HashMap<Long, Integer> periods) {
        for (Map.Entry<Long, Integer> period : periods.entrySet()) {
            Database.insertPeriod(timeframe_id, period.getKey(), period.getValue());
        }
    }

    private static void addStateList(long timeframe_id, HashMap<Long, TimeFrame.State> states) {
        for (Map.Entry<Long, TimeFrame.State> state : states.entrySet()) {
            Database.insertState(timeframe_id, state.getKey(), state.getValue().toString());
        }
    }

    public static void addTask(long timeframe_id, Task t) {
        Database.insertTask(timeframe_id, t);
    }

    private static void addTaskList(long timeframe_id, HashMap<Long, Task> tasks) {
        for (Task task : tasks.values()) {
            Database.insertTask(timeframe_id, task);
        }
    }

    public static void addTimeFrame(TimeFrame tf) {
        Database.insertTimeFrame(tf);
        addTaskList(tf.getId(), tf.getTasks());
        addPeriodList(tf.getId(), tf.getNextPeriod());
        addStateList(tf.getId(), tf.getStates());
        System.out.println(tf.getChanges());
        addChangeList(tf.getId(), tf.getChanges());
    }

    public static Task getLatestTask() {
        return Database.retrieveLatestTask();
    }

    public static TimeFrame getLatestTimeFrame(long uid) throws SQLException {
        return Database.retrieveLatestTimeFrame(uid);
    }

    public static void printTasks() throws SQLException {
        Database.printTasks();
    }

    public static void printTimeFrames() throws SQLException {
        Database.printTimeFrames();
    }
}
