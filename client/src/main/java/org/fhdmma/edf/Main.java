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
        for (int i = 0; i < 10; i++) {
            tasks.add(new EDFTask("Task " + i, i*2, i));
        }
        launch();
    }

}
