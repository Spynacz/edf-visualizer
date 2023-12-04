package org.fhdmma.edf;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    static List<EDFTask> tasks = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(new Controller().getView(), 1280, 720));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void addTask(EDFTask newTask) {
        tasks.add(newTask);
        // System.out.println(tasks);
    }

}
