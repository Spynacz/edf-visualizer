package sqlite.connect.net;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.io.File;
import javax.security.auth.login.FailedLoginException;

import org.fhdmma.edf.Task;
import org.fhdmma.edf.TimeFrame;
import org.fhdmma.edf.User;

class Database {
    static Connection connection;
    static Statement statement;

    public static boolean exists() {
        return (new File("sever.db")).isFile();
    }

    public static void connect() throws SQLException {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:server.db");
            statement = connection.createStatement();
            statement.setQueryTimeout(30);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() throws SQLException {
        connection.close();
    }

    public static Boolean isValid() {
        try {
            return connection.isValid(100);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void createDatabase() throws SQLException {
        try {
            statement.executeUpdate("DROP TABLE IF EXISTS users");
            statement.executeUpdate("DROP TABLE IF EXISTS tasks");
            statement.executeUpdate("DROP TABLE IF EXISTS timeframes");
            statement.executeUpdate("DROP TABLE IF EXISTS timeframes_tasks");
            statement.executeUpdate("DROP TABLE IF EXISTS periods");
            statement.executeUpdate("DROP TABLE IF EXISTS states");
            statement.executeUpdate("DROP TABLE IF EXISTS actions");

            statement.executeUpdate("CREATE TABLE users" +
                    "(id INTEGER PRIMARY KEY, " +
                    "username TEXT, " +
                    "password TEXT);");
            statement.executeUpdate("CREATE TABLE tasks" +
                    "(id INTEGER PRIMARY KEY, " +
                    "duration INTEGER, " +
                    "period INTEGER, " +
                    "name TEXT, " +
                    "user_id INTEGER, " +
                    "FOREIGN KEY(user_id) REFERENCES users(id));");
            statement.executeUpdate("CREATE TABLE timeframes" +
                    "(id INTEGER PRIMARY KEY, " +
                    "user_id INTEGER NULL, " +
                    "parent_id INTEGER NULL, " +
                    "active_task INTEGER, " +
                    "time_left INTEGER, " +
                    "FOREIGN KEY(parent_id) REFERENCES timeframes(id)," +
                    "FOREIGN KEY(user_id) REFERENCES users(id));");
            statement.executeUpdate("CREATE TABLE timeframes_tasks" +
                    "(timeframe_id INTEGER," +
                    "task_id INTEGER," +
                    "PRIMARY KEY(timeframe_id, task_id)," +
                    "FOREIGN KEY(timeframe_id) REFERENCES timeframes(id)," +
                    "FOREIGN KEY(task_id) REFERENCES tasks(id));");
            statement.executeUpdate("CREATE TABLE periods" +
                    "(timeframe_id INTEGER," +
                    "task_id INTEGER," +
                    "timeframes_needed INTEGER," +
                    "PRIMARY KEY(timeframe_id, task_id)," +
                    "FOREIGN KEY(timeframe_id) REFERENCES timeframes(id)," +
                    "FOREIGN KEY(task_id) REFERENCES tasks(id));");
            statement.executeUpdate("CREATE TABLE states" +
                    "(timeframe_id INTEGER," +
                    "task_id INTEGER," +
                    "state TEXT CHECK(state in ('DONE', 'RUNNING', 'WAITING'))," +
                    "PRIMARY KEY(timeframe_id, task_id)," +
                    "FOREIGN KEY(timeframe_id) REFERENCES timeframes(id)," +
                    "FOREIGN KEY(task_id) REFERENCES tasks(id));");
            statement.executeUpdate("CREATE TABLE actions" +
                    "(id INTEGER PRIMARY KEY," +
                    "timeframe_id INTEGER," +
                    "task_id INTEGER," +
                    "action TEXT CHECK(action in ('ADD', 'REMOVE', NULL)) NULL DEFAULT NULL," +
                    "FOREIGN KEY(timeframe_id) REFERENCES timeframes(id)," +
                    "FOREIGN KEY(task_id) REFERENCES tasks(id));");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertChange(long timeframe_id, long task_id, String action) {
        try (PreparedStatement ps = connection
                .prepareStatement("INSERT OR IGNORE INTO actions(timeframe_id, task_id, action) VALUES(?, ?, ?);")) {
            ps.setLong(1, timeframe_id);
            ps.setLong(2, task_id);
            ps.setString(3, action);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertM2M(long timeframe_id, long task_id) {
        try (PreparedStatement ps = connection
                .prepareStatement("INSERT OR IGNORE INTO timeframes_tasks VALUES(?, ?)")) {
            ps.setLong(1, timeframe_id);
            ps.setLong(2, task_id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertPeriod(long timeframe_id, long task_id, int timeframes_needed) {
        try (PreparedStatement ps = connection.prepareStatement("INSERT OR IGNORE INTO periods VALUES(?, ?, ?);")) {
            ps.setLong(1, timeframe_id);
            ps.setLong(2, task_id);
            ps.setInt(3, timeframes_needed);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertState(long timeframe_id, long task_id, String state) {
        try (PreparedStatement ps = connection.prepareStatement("INSERT OR IGNORE INTO states VALUES(?, ?, ?);")) {
            ps.setLong(1, timeframe_id);
            ps.setLong(2, task_id);
            ps.setString(3, state);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertTask(long timeframe_id, Task t) {
        try (PreparedStatement ps = connection
                .prepareStatement("INSERT OR IGNORE INTO tasks VALUES(?, ?, ?, ?, ?);")) {
            ps.setLong(1, t.getId());
            ps.setInt(2, t.getDuration());
            ps.setInt(3, t.getPeriod());
            ps.setString(4, t.getName());
            ps.setLong(5, t.getUserId());
            ps.executeUpdate();

            insertM2M(timeframe_id, t.getId());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertTimeFrame(TimeFrame tf) {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO timeframes VALUES(?, ?, ?, ?, ?)")) {
            ps.setLong(1, tf.getId());
            ps.setLong(2, tf.getUser());
            ps.setLong(3, tf.getParent());
            ps.setLong(4, tf.getCurrentTask());
            ps.setInt(5, tf.getTimeLeft());
            ps.executeUpdate();
        } catch (

        SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean insertUser(String username, String password) {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO users(username, password) VALUES(?,?);")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            ps.close();

            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<TimeFrame.Action> retrieveChanges(long timeframe_id) {
        List<TimeFrame.Action> list = new LinkedList<>();
        try (PreparedStatement ps = connection
                .prepareStatement("SELECT * FROM actions NATURAL JOIN tasks WHERE timeframe_id = ?")) {
            ps.setLong(1, timeframe_id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (rs.getString("action").equals("ADD")) {
                    list.add(new TimeFrame.AddTask(new Task(
                            rs.getLong("task_id"),
                            rs.getString("name"),
                            rs.getInt("duration"),
                            rs.getInt("period"),
                            rs.getInt("user_id"))));
                } else if (rs.getString("action").equals("REMOVE")) {
                    list.add(new TimeFrame.RemoveTask(rs.getLong("task_id")));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Task retrieveLatestTask() {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM tasks ORDER BY id DESC LIMIT 1")) {
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return null;
            }

            return new Task(rs.getLong("id"), rs.getString("name"), rs.getInt("duration"), rs.getInt("period"),
                    rs.getInt("user_id"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TimeFrame retrieveLatestTimeFrame(long uid) throws SQLException {
        try (PreparedStatement ps = connection
                .prepareStatement("SELECT * FROM timeframes WHERE user_id = ? ORDER BY id DESC LIMIT 1")) {
            ps.setLong(1, uid);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long id = rs.getLong("id");
                return new TimeFrame(
                        id,
                        retrieveTasksList(id),
                        retrievePeriod(id),
                        retrieveStates(id),
                        retrieveChanges(id),
                        rs.getLong("parent_id"),
                        rs.getLong("active_task"),
                        rs.getInt("time_left"),
                        uid);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<Long, Integer> retrievePeriod(long timeframe_id) {
        HashMap<Long, Integer> period = new HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM periods WHERE timeframe_id = ?;")) {
            ps.setLong(1, timeframe_id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                period.put(
                        rs.getLong("task_id"),
                        rs.getInt("timeframes_needed"));
            }

            return period;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<Long, TimeFrame.State> retrieveStates(long timeframe_id) {
        HashMap<Long, TimeFrame.State> states = new HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM states WHERE timeframe_id = ?;")) {
            ps.setLong(1, timeframe_id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                states.put(
                        rs.getLong("task_id"),
                        TimeFrame.State.valueOf(rs.getString("state")));
            }

            return states;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Task retrieveTask(int period, int duration) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT id, duration, period, name FROM tasks " +
                "WHERE duration = ? AND period = ? ORDER BY id DESC LIMIT 1");) {
            // TODO: Race condition possible.
            ps.setInt(1, duration);
            ps.setInt(2, period);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return null;
            }

            return new Task(rs.getLong("id"), rs.getString("name"), rs.getInt("duration"), rs.getInt("period"),
                    rs.getInt("user_id"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<Long, Task> retrieveTasksList(long timeframe_id) {
        HashMap<Long, Task> tasks = new HashMap<>();
        try (PreparedStatement ps = connection
                .prepareStatement("SELECT * FROM timeframes_tasks NATURAL JOIN tasks WHERE timeframe_id = ?;")) {
            ps.setLong(1, timeframe_id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tasks.put(
                        rs.getLong("task_id"),
                        new Task(rs.getLong("task_id"), rs.getString("name"), rs.getInt("duration"),
                                rs.getInt("period"), rs.getInt("user_id")));
            }

            return tasks;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static User retrieveUser(String username, String password) throws FailedLoginException {
        try (PreparedStatement ps = connection
                .prepareStatement("SELECT * FROM users WHERE username = ?;")) {
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            int id = rs.getInt("id");
            if (rs.wasNull())
                return null;
            String uname = rs.getString("username");
            String pass = rs.getString("password");
            if (password.equals(pass)) {
                return new User(id, uname, pass);
            } else {
                throw new FailedLoginException("Invalid password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean isUsernameUnique(String username) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT username FROM users WHERE username = ?;")) {
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            }

            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void printTasks() throws SQLException {
        Task t;
        try (ResultSet rs = statement.executeQuery("SELECT * FROM tasks")) {
            while (rs.next()) {
                t = new Task(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getInt("duration"),
                        rs.getInt("period"),
                        rs.getInt("user_id"));

                System.out.println(t);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printTimeFrames() throws SQLException {
        long curr;
        String active;
        try (ResultSet rs = statement.executeQuery("SELECT * FROM timeframes")) {
            while (rs.next()) {
                curr = rs.getLong("activeTask");
                active = ((rs.wasNull()) ? "null" : String.valueOf(curr));
                System.out.println(
                        "{ id: " + rs.getLong("id") +
                                ", activeTask: " + active +
                                ", timeleft: " + rs.getInt("timeleft") + " }");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Task> retrieveTasksByUserId(long uid) {
        List<Task> taskList = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM tasks WHERE user_id = ?;")) {
            ps.setLong(1, uid);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                taskList.add(
                        new Task(rs.getLong("id"), rs.getString("name"), rs.getInt("duration"), rs.getInt("period"),
                                rs.getLong("user_id")));
            }

            return taskList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
