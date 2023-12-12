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
import javafx.scene.layout.Priority;
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
    private final Consumer<Runnable> taskDetailsDisplayer;

    public ViewBuilder(Model model, Consumer<Runnable> taskAdder, Consumer<Runnable> taskDisplayer) {
        this.model = model;
        this.taskAdder = taskAdder;
        this.taskDetailsDisplayer = taskDisplayer;
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

        vbox.getChildren().add(setClientTasks(taskDetailsDisplayer));
        VBox.setVgrow(vbox.getChildren().get(0), Priority.ALWAYS);

        vbox.getChildren().add(setAddTaskButton(taskAdder));

        vbox.getStyleClass().add("leftbox");
        return vbox;
    }

    private Node createRight() {
        VBox vbox = new VBox(5);
        vbox.setMinWidth(200);

        vbox.getChildren().add(setTaskDetails());

        vbox.getStyleClass().add("rightbox");
        return vbox;
    }

    private Node setClientTasks(Consumer<Runnable> displayTaskDetails) {
        ListView<EDFTask> listView = new ListView<>();
        listView.setItems(model.getTaskList());

        listView.setCellFactory(lv -> {
            return new ListCell<EDFTask>() {
                private HBox content;
                private Text name;
                private Text period;
                private Text duration;

                {
                    name = new Text();
                    period = new Text();
                    duration = new Text();
                    VBox vBox = new VBox(period, duration);
                    content = new HBox(name, vBox);
                }

                @Override
                protected void updateItem(EDFTask item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        name.setText(item.getName());
                        period.setText("Period: " + item.getPeriod());
                        duration.setText("Duration: " + item.getDuration());
                        setGraphic(content);
                    } else {
                        setGraphic(null);
                    }
                }
            };
        });

        listView.setOnMouseClicked(evt -> {
            EDFTask t = listView.getSelectionModel().getSelectedItem();
            if (t != null) {
                model.setSelectedTask(t);
                displayTaskDetails.accept(() -> {
                });
            }
        });

        return listView;
    }

    private Node setTaskDetails() {
        return new VBox(boundLabel(model.selectedTitleProperty()),
                boundLabel(model.selectedDurationProperty()),
                boundLabel(model.selectedPeriodProperty()));
    }

    private Node setAddTaskButton(Consumer<Runnable> addTask) {
        Button button = new Button("Add task");

        button.setOnAction(evt -> {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);

            HBox title = new HBox(6, new Label("Task name:"), boundTextField(model.titleProperty()));
            HBox duration = new HBox(6, new Label("Task duration:"),
                    boundIntegerField(model.durationProperty()));
            HBox period = new HBox(6, new Label("Task period:"),
                    boundIntegerField(model.periodProperty()));
            Button confirm = new Button("Confirm");
            confirm.setOnAction(evt2 -> {
                addTask.accept(() -> dialog.close());
            });

            VBox vbox = new VBox(title, duration, period, confirm);

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

    private Node boundLabel(StringProperty boundProperty) {
        Label label = new Label();
        label.textProperty().bind(boundProperty);
        return label;
    }

    private Node headingLabel(String string) {
        Label label = new Label("EDF Visualizer");
        label.getStyleClass().add("main-label");
        return label;
    }
}
