package org.fhdmma.edf;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(new Controller().getView()));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
