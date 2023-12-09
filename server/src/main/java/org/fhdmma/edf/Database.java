package org.fhdmma.edf;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    static Connection connection;
    static Statement statement;

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:server.db");
        statement = connection.createStatement();
        statement.setQueryTimeout(30);
        statement.executeUpdate("drop table if exists task");
        statement.executeUpdate("drop table if exists timeframe");
        statement.executeUpdate("create table task " +
                "(id INTEGER PRIMARY KEY, "+
                "duration INTEGER, period INTEGER)");
        statement.executeUpdate("create table timeframe " +
                "(id INTEGER PRIMARY KEY, "+
                "activetask INTEGER, timeleft INTEGER)");
    }

    public static void addTask(Task t) throws SQLException {
        statement.executeUpdate("insert into task values(" +
                t.id + ", " + t.duration + ", " + t.period + ");");
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
