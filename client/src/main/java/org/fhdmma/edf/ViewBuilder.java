package org.fhdmma.edf;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

public class ViewBuilder implements Builder<Region> {

    private final Model model;

    public ViewBuilder(Model model) {
        this.model = model;
    }

    @Override
    public Region build() {
        BorderPane mainPane = new BorderPane();
        mainPane.getStylesheets()
                .add(Objects.requireNonNull(this.getClass().getResource("/css/main.css")).toExternalForm());
        mainPane.setTop(headingLabel("EDF visualizer"));
        mainPane.setLeft(createLeft());
        mainPane.setRight(createRight());
        mainPane.setCenter(createCenter());
        return mainPane;
    }

    private Node createCenter() {
        Label tempLabel = new Label("TODO: centre panel");
        VBox vbox = new VBox(tempLabel);
        vbox.getStyleClass().add("centerbox");
        return vbox;
    }

    private Node createRight() {
        VBox vbox = new VBox(5);
        vbox.setMinWidth(200);
        vbox.getStyleClass().add("rightbox");
        return vbox;
    }

    private Node createLeft() {
        VBox vbox = new VBox();
        vbox.setMinWidth(200);

        vbox.getChildren().addAll(getClientTasks());

        vbox.getStyleClass().add("leftbox");
        return vbox;
    }

    private List<Node> getClientTasks() {
        List<Node> tasks = new ArrayList<>();

        // temporary for testing
        for (Task t : Main.tasks) {
            HBox task = new HBox(69);
            Label name = new Label(t.getName());
            Label duration = new Label(String.valueOf(t.getDuration()));
            Label deadline = new Label(String.valueOf(t.getDeadline()));
            task.getChildren().addAll(name, duration, deadline);
            
            tasks.add(task);
        }

        return tasks;
    }

    private Node headingLabel(String string) {
        Label label = new Label("EDF Visualizer");
        label.getStyleClass().add("main-label");
        return label;
    }
}
