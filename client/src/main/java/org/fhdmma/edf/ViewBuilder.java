package org.fhdmma.edf;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Builder;

public class ViewBuilder implements Builder<Region> {

    private final Model model;
    private final Runnable taskAdder;
    private final Runnable taskDisplayer;

    public ViewBuilder(Model model, Runnable taskAdder, Runnable taskDisplayer) {
        this.model = model;
        this.taskAdder = taskAdder;
        this.taskDisplayer = taskDisplayer;
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

        vbox.getChildren().addAll(setClientTasks(taskDisplayer));

        vbox.getChildren().add(setAddTaskButton(taskAdder));

        vbox.getStyleClass().add("leftbox");
        return vbox;
    }

    private Node setAddTaskButton(Runnable addTask) {
        Button button = new Button("Add task");
        button.setOnAction(evt -> addTask.run());
        return button;
    }

    private List<EDFTask> fetchClientTasks() {
        List<EDFTask> tasks = new ArrayList<>();
        for (EDFTask t : Main.tasks) {
            tasks.add(t);
        }
        return tasks;
    }

    private List<Node> setClientTasks(Runnable displayTask) {
        List<EDFTask> tasks = fetchClientTasks();
        List<Node> tasksGUI = new ArrayList<>();
        for (EDFTask t : tasks) {
            Label name = new Label(t.getName());
            Label duration = new Label("Duration " + String.valueOf(t.getDuration()));
            Label deadline = new Label("Deadline " + String.valueOf(t.getDeadline()));

            VBox times = new VBox(duration, deadline);

            HBox task = new HBox(name, times);
            task.getStyleClass().add("task-entry");

            task.setOnMouseClicked(evt -> {
                for (Node t2 : tasksGUI) {
                    t2.setStyle("-fx-background-color: none");
                }
                task.setStyle("-fx-background-color: pink");
                displayTask.run();
            });

            tasksGUI.add(task);
        }

        return tasksGUI;
    }

    private Node headingLabel(String string) {
        Label label = new Label("EDF Visualizer");
        label.getStyleClass().add("main-label");
        return label;
    }
}
