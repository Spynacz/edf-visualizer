package org.fhdmma.edf;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    static List<EDFTask> tasks = new ArrayList<>();

    {
        tasks.add(new EDFTask("Task1", 21, 37));
        tasks.add(new EDFTask("Task2", 420, 69));
        tasks.add(new EDFTask("Task3", 13, 37));
        tasks.add(new EDFTask("Task4", 21, 37));
        tasks.add(new EDFTask("Task5", 21, 37));
        tasks.add(new EDFTask("Task6", 21, 37));
        tasks.add(new EDFTask("Task7", 420, 69));
        tasks.add(new EDFTask("Task8", 13, 37));
        tasks.add(new EDFTask("Task9", 420, 69));
        tasks.add(new EDFTask("Task10", 13, 37));
        tasks.add(new EDFTask("Task11", 422, 69));
        tasks.add(new EDFTask("Task12", 13, 37));
        tasks.add(new EDFTask("Task13", 21, 37));
        tasks.add(new EDFTask("Task14", 22, 37));
        tasks.add(new EDFTask("Task15", 420, 69));
        tasks.add(new EDFTask("Task16", 13, 37));
        tasks.add(new EDFTask("Task17", 420, 69));
        tasks.add(new EDFTask("Task18", 13, 37));
        tasks.add(new EDFTask("Task19", 422, 69));
        tasks.add(new EDFTask("Task20", 13, 37));
    }

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(new Controller().getView(), 1280, 720));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    // temporary
    public static void addTask(EDFTask newTask) {
        tasks.add(newTask);
    }

    public static void removeTask(EDFTask selectedTask) {
        tasks.remove(selectedTask);
    }

}
