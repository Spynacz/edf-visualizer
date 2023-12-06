package org.fhdmma.edf;

import java.util.Objects;
import java.util.function.Consumer;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Builder;
import javafx.util.converter.NumberStringConverter;

public class ViewBuilder implements Builder<Region> {

    private final Model model;
    private final Consumer<Runnable> taskAdder;
    private final Consumer<Runnable> taskDisplayer;

    public ViewBuilder(Model model, Consumer<Runnable> taskAdder, Consumer<Runnable> taskDisplayer) {
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

    private Node createLeft() {
        VBox vbox = new VBox();
        vbox.setMinWidth(200);

        vbox.getChildren().add(setClientTasks(taskDisplayer));
        vbox.getChildren().add(setAddTaskButton(taskAdder));

        vbox.getStyleClass().add("leftbox");
        return vbox;
    }

    private Node createRight() {
        VBox vbox = new VBox(5);
        vbox.setMinWidth(200);

        // vbox.getChildren().add(setTaskDetails(null));

        vbox.getStyleClass().add("rightbox");
        return vbox;
    }

    private Node setClientTasks(Consumer<Runnable> displayTask) {
        ListView<EDFTask> listView = new ListView<>();
        listView.setItems(model.getTaskList());

        listView.setCellFactory(lv -> {
            return new ListCell<EDFTask>() {
                private HBox content;
                private Text name;
                private Text deadline;
                private Text duration;

                {
                    name = new Text();
                    deadline = new Text();
                    duration = new Text();
                    VBox vBox = new VBox(deadline, duration);
                    content = new HBox(name, vBox);
                }

                @Override
                protected void updateItem(EDFTask item, boolean empty) {
                    if (item != null && !empty) {
                        name.setText(item.getName());
                        deadline.setText("Deadline: " + item.getDeadline());
                        duration.setText("Duration: " + item.getDuration());
                        setGraphic(content);
                    } else {
                        setGraphic(null);
                    }
                }
            };
        });

        listView.setOnMouseClicked(evt -> {
            displayTask.accept(() -> setTaskDetails(listView.getSelectionModel().getSelectedItem()));
        });

        return listView;
    }

    private Node setTaskDetails(EDFTask selectedItem) {
        Label name = new Label(selectedItem.getName());
        Label duration = new Label(String.valueOf(selectedItem.getDuration()));
        Label deadline = new Label(String.valueOf(selectedItem.getDeadline()));
        return new VBox(name, duration, deadline);
    }

    private Node setAddTaskButton(Consumer<Runnable> addTask) {
        Button button = new Button("Add task");
        button.setOnAction(evt -> {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);

            HBox title = new HBox(6, new Label("Task name:"), boundTextField(model.titleProperty()));
            HBox duration = new HBox(6, new Label("Task duration:"),
                    boundIntegerField(model.executionTimeProperty()));
            HBox deadline = new HBox(6, new Label("Task deadline:"),
                    boundIntegerField(model.deadlineProperty()));
            Button confirm = new Button("Confirm");
            confirm.setOnAction(evt2 -> {
                addTask.accept(() -> dialog.close());
            });

            VBox vbox = new VBox(title, duration, deadline, confirm);

            Scene dialogScene = new Scene(vbox, 400, 300);
            dialog.setScene(dialogScene);
            dialog.show();
        });
        return button;
    }

    private Node boundTextField(StringProperty boundProperty) {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(boundProperty);
        return textField;
    }

    private Node boundIntegerField(IntegerProperty boundProperty) {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(boundProperty, new NumberStringConverter());
        return textField;
    }

    private Node setRight(EDFTask t) {
        Label name = new Label(t.getName());
        Label duration = new Label("Duration " + String.valueOf(t.getDuration()));
        Label deadline = new Label("Deadline " + String.valueOf(t.getDeadline()));

        VBox taskDetails = new VBox(name, duration, deadline);

        return taskDetails;
    }

    private Node headingLabel(String string) {
        Label label = new Label("EDF Visualizer");
        label.getStyleClass().add("main-label");
        return label;
    }
}
