package org.fhdmma.edf;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.management.InvalidAttributeValueException;

public class Database {
    static Connection connection;
    static Statement statement;

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:server.db");
        statement = connection.createStatement();
        statement.setQueryTimeout(30);
        statement.executeUpdate("DROP TABLE IF EXISTS tasks");
        statement.executeUpdate("DROP TABLE IF EXISTS timeframes");
        statement.executeUpdate("DROP TABLE IF EXISTS users");

        statement.executeUpdate("CREATE TABLE users"+
                "(id INTEGER PRIMARY KEY, "+
                "username TEXT, "+
                "password TEXT);");
        statement.executeUpdate("CREATE TABLE tasks" +
                "(id INTEGER PRIMARY KEY, "+
                "duration INTEGER, "+
                "period INTEGER, "+
                "user_id INTEGER, "+
                "FOREIGN KEY(user_id) REFERENCES users(id));");
        statement.executeUpdate("CREATE TABLE timeframes" +
                "(id INTEGER PRIMARY KEY, "+
                "active_task INTEGER, "+
                "time_left INTEGER, "+
                "task_id INTEGER, "+
                "FOREIGN KEY(task_id) REFERENCES tasks(id));");
    }

    public static User userLogin(String username, String password) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?;")) {
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Boolean userRegister(String username, String password1, String password2) throws InvalidAttributeValueException {
        if (!password2.equals(password1)) {
            throw new InvalidAttributeValueException("Passwords are not the same!");
        }
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT username FROM users WHERE username = ?;");
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {     
                throw new InvalidAttributeValueException("Username is taken.");
            }
            System.out.println("Jest git.");

            ps = connection.prepareStatement("INSERT INTO users(username, password) VALUES(?,?);");
            ps.setString(1, username);
            ps.setString(2, password1);
            ps.executeUpdate();
            ps.close();
            return true;
        } 
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    // TODO: Add user_id
    public static Task addTask(int duration, int period) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO tasks(duration, period) VALUES(?, ?)",
                                                               Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, duration);
            ps.setInt(2, period);

            ps.executeUpdate();

            try {
                // TODO: Race condition possible.
                ps = connection.prepareStatement("SELECT id, duration, period FROM tasks WHERE duration = ? AND period = ?");
                ps.setInt(1, duration);
                ps.setInt(2, period);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return new Task(rs.getInt("id"), rs.getInt("duration"), rs.getInt("period"));
                }
                return null;
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static TimeFrame getLatestTimeFrame() throws SQLException {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM timeframes ORDER BY id DESC LIMIT 1");
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new TimeFrame(
                    rs.getInt("id"),
                    null,
                    null,
                    null,
                    null,
                    rs.getInt("active_task"),
                    rs.getInt("time_left")
                );
            }
            throw new SQLException("Getting timeframe failed, no rows obtained.");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addTimeFrame(TimeFrame tf) throws SQLException {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO timeframes(id, active_task, time_left) VALUES(?, ?, ?)");
            ps.setInt(1, tf.getId());
            ps.setInt(2, tf.getCurrentTask());
            ps.setInt(3, tf.getTimeLeft());

            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printTasks() throws SQLException {
        Task t;
        try(ResultSet rs = statement.executeQuery("SELECT * FROM tasks")) {
            while(rs.next()) {
                t = new Task(
                    rs.getInt("id"),
                    rs.getInt("duration"),
                    rs.getInt("period"));

                System.out.println(t);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printTimeFrames() throws SQLException {
        int curr;
        String active;
        try (ResultSet rs = statement.executeQuery("SELECT * FROM timeframes")) {
            while(rs.next()) {
                curr = rs.getInt("activeTask");
                active = ((rs.wasNull())?"null":String.valueOf(curr));
                System.out.println(
                    "{ id: "  + rs.getInt("id") +
                    ", activeTask: " + active +
                    ", timeleft: " + rs.getInt("timeleft") + " }");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void disconnect() throws SQLException {
        connection.close();
    }
}
