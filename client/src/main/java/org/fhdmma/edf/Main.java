package org.fhdmma.edf;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    static List<Task> tasks = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(new Controller().getView(), 1280, 720));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    // temporary
    public static void addTask(Task newTask) {
        tasks.add(newTask);
    }

    public static void removeTask(Task selectedTask) {
        tasks.remove(selectedTask);
    }

    public static void clearTasks() {
        tasks.clear();
    }

    public static void setTasks(List<Task> list) {
        tasks.clear();
        tasks.addAll(list);
    }
}
