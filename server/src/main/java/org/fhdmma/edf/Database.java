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
        statement.executeUpdate("DROP TABLE IF EXISTS task");
        statement.executeUpdate("DROP TABLE IF EXISTS timeframe");
        statement.executeUpdate("DROP TABLE IF EXISTS users");

        statement.executeUpdate("CREATE TABLE users"+
                "(id INTEGER PRIMARY KEY, "+
                "username TEXT, "+
                "password TEXT);");
        statement.executeUpdate("CREATE TABLE task" +
                "(id INTEGER PRIMARY KEY, "+
                "duration INTEGER, "+
                "period INTEGER, "+
                "user_id INTEGER, "+
                "FOREIGN KEY(user_id) REFERENCES users(id));");
        statement.executeUpdate("CREATE TABLE timeframe" +
                "(id INTEGER PRIMARY KEY, "+
                "activetask INTEGER, "+
                "timeleft INTEGER, "+
                "task_id INTEGER, "+
                "FOREIGN KEY(task_id) REFERENCES task(id));");
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

    public static Boolean userRegister(String username, String password1, String password2) throws InvalidAttributeValueException, SQLException {
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
            e.printStackTrace();
            return false;
        }
    }

    public static Task addTask(int duration, int period) throws SQLException {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO task(duration, period) VALUES(?, ?)",
                                                               Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, duration);
            ps.setInt(2, period);

            ps.executeUpdate();

            try {
                // TODO: Race condition possible.
                ps = connection.prepareStatement("SELECT id, duration, period FROM task WHERE duration = ? AND period = ?");
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

    public static void addTimeFrame(TimeFrame t) throws SQLException {
        int curr = t.getCurrentTask();
        String active = (curr==-1)?"NULL":String.valueOf(curr);
        statement.executeUpdate("insert into timeframe values(" +
                t.getId() + ", " + active + ", " + t.getTimeLeft() + ");");
    }

    public static void printTasks() throws SQLException {
        Task t;
        ResultSet rs = statement.executeQuery("select * from task");
        while(rs.next()) {
            t = new Task(rs.getInt("id"),
                    rs.getInt("duration"),
                    rs.getInt("period"));
            System.out.println(t);
        }
    }

    public static void printTimeFrames() throws SQLException {
        ResultSet rs = statement.executeQuery("select * from timeframe");
        int curr;
        String active;
        while(rs.next()) {
            curr = rs.getInt("activeTask");
            active = ((rs.wasNull())?"null":String.valueOf(curr));
            System.out.println("{ id: "  + rs.getInt("id") +
                    ", activeTask: " + active +
                    ", timeleft: " + rs.getInt("timeleft") + " }");
        }
    }

    public static void disconnect() throws SQLException {
        connection.close();
    }
}
