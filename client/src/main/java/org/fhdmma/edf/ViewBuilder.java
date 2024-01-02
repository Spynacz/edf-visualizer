package org.fhdmma.edf;

import java.text.NumberFormat;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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
    private final Consumer<Runnable> connector;

    public ViewBuilder(Model model, Consumer<Runnable> taskAdder, Consumer<Runnable> taskDisplayer,
            Consumer<Runnable> connector) {
        this.model = model;
        this.taskAdder = taskAdder;
        this.taskDetailsDisplayer = taskDisplayer;
        this.connector = connector;
    }

    @Override
    public Region build() {
        BorderPane mainPane = new BorderPane();
        mainPane.getStylesheets()
                .add(Objects.requireNonNull(this.getClass().getResource("/css/main.css")).toExternalForm());
        mainPane.setTop(headingLabel("EDF Visualizer"));
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
        vbox.getChildren().add(setClientTasks(taskDetailsDisplayer));
        VBox.setVgrow(vbox.getChildren().get(0), Priority.ALWAYS);

        if (model.isConnected()) {
            vbox.getChildren().add(setAddTaskButton());
        } else {
            vbox.getChildren().add(setConnectButton());
        }

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
                // private Text period;
                // private Text duration;

                {
                    name = new Text();
                    // period = new Text();
                    // duration = new Text();
                    // VBox vBox = new VBox(period, duration);
                    content = new HBox(10, name);
                }

                @Override
                protected void updateItem(EDFTask item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        name.setText(item.getName());
                        // period.setText("Period: " + item.getPeriod());
                        // duration.setText("Duration: " + item.getDuration());
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
                    // TODO: side panel hide/unhide
                });
            }
        });

        listView.getStyleClass().add("task-list");

        return listView;
    }

    private Node setTaskDetails() {
        HBox title = new HBox(6, boundLabel(model.selectedTitleProperty()));
        // TODO: hide labels when no task selected (unless the sidebar will be hidden)
        HBox duration = new HBox(6, new Label("Duration:"), boundLabel(model.selectedDurationProperty()));
        HBox period = new HBox(6, new Label("Period:"), boundLabel(model.selectedDurationProperty()));

        title.getStyleClass().add("details-title");
        duration.getStyleClass().add("details");
        period.getStyleClass().add("details");

        return new VBox(10, title, duration, period);
    }

    private Node setAddTaskButton() {
        Button button = new Button("Add task");

        button.setOnAction(evt -> {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);

            BorderPane borderPane = new BorderPane();
            Node center = setAddTaskDialog(taskAdder, dialog);
            borderPane.setCenter(center);
            BorderPane.setMargin(center, new Insets(50));

            Scene dialogScene = new Scene(borderPane);
            dialog.setScene(dialogScene);
            dialog.setResizable(false);
            dialog.show();
        });

        button.getStyleClass().add("add-button");

        return button;
    }

    private Node setAddTaskDialog(Consumer<Runnable> addTask, Stage stage) {
        Node periodText = boundIntegerField(model.periodProperty(), "Period");
        Node durationText = boundIntegerField(model.durationProperty(), "Duration", periodText);
        Node titleText = boundTextField(model.titleProperty(), "Title", durationText);

        HBox title = new HBox(6, new Label("Title:"), hboxSpacer(), titleText);
        HBox duration = new HBox(6, new Label("Duration:"), hboxSpacer(), durationText);
        HBox period = new HBox(6, new Label("Period:"), hboxSpacer(), periodText);

        Button confirm = new Button("Confirm");
        confirm.disableProperty().bind(model.okToAddProperty().not());
        confirm.setOnAction(evt2 -> {
            addTask.accept(() -> stage.close());
        });

        confirm.setDefaultButton(true);

        VBox vbox = new VBox(10, title, duration, period, confirm);
        vbox.setAlignment(Pos.CENTER);

        return vbox;
    }

    private Node setConnectButton() {
        Button button = new Button("Connect");

        button.setOnAction(evt -> {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);

            BorderPane borderPane = new BorderPane();
            Node center = setConnectDialog(connector, dialog);
            borderPane.setCenter(center);
            BorderPane.setMargin(center, new Insets(50));

            Scene dialogScene = new Scene(borderPane);
            dialog.setScene(dialogScene);
            dialog.setResizable(false);
            dialog.show();
        });

        button.getStyleClass().add("add-button");

        return button;
    }

    private Node setConnectDialog(Consumer<Runnable> connect, Stage stage) {
        Node passwordText = boundPasswordField(model.passwordProperty(), "Password");
        Node usernameText = boundTextField(model.usernameProperty(), "Username", passwordText);
        Node serverIpText = boundTextField(model.serverIpProperty(), "Server IP", usernameText);

        HBox serverIp = new HBox(6, new Label("Address:"), hboxSpacer(), serverIpText);
        HBox username = new HBox(6, new Label("Username:"), hboxSpacer(), usernameText);
        HBox password = new HBox(6, new Label("Password:"), hboxSpacer(), passwordText);

        Button confirm = new Button("Connect");
        confirm.disableProperty().bind(model.okToConnectProperty().not());
        confirm.setOnAction(evt2 -> {
            connect.accept(() -> stage.close());
        });

        confirm.setDefaultButton(true);

        VBox vbox = new VBox(10, serverIp, username, password, confirm);
        vbox.setAlignment(Pos.CENTER);

        return vbox;
    }

    private Region hboxSpacer() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    private Node boundTextField(StringProperty boundProperty, String prompt) {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(boundProperty);
        textField.setPromptText(prompt);

        return textField;
    }

    private Node boundTextField(StringProperty boundProperty, String prompt, Node nextField) {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(boundProperty);
        textField.setPromptText(prompt);
        textField.setOnKeyPressed(evt -> {
            if (evt.getCode().equals(KeyCode.ENTER)) {
                nextField.requestFocus();
            }
        });

        return textField;
    }

    private Node boundIntegerField(IntegerProperty boundProperty, String prompt) {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(boundProperty, new NumberStringConverter() {

            @Override
            public String toString(Number value) {
                if (value == null || value.intValue() == 0) {
                    return "";
                }

                NumberFormat formatter = getNumberFormat();

                return formatter.format(value);
            }
        });
        textField.setPromptText(prompt);

        return textField;
    }

    private Node boundIntegerField(IntegerProperty boundProperty, String prompt, Node nextField) {
        TextField textField = new TextField();
        textField.textProperty().bindBidirectional(boundProperty, new NumberStringConverter() {

            @Override
            public String toString(Number value) {
                if (value == null || value.intValue() == 0) {
                    return "";
                }

                NumberFormat formatter = getNumberFormat();

                return formatter.format(value);
            }
        });

        textField.setPromptText(prompt);

        textField.setOnKeyPressed(evt -> {
            if (evt.getCode().equals(KeyCode.ENTER)) {
                nextField.requestFocus();
            }
        });

        return textField;
    }

    private Node boundPasswordField(StringProperty boundProperty, String prompt) {
        PasswordField passwordField = new PasswordField();
        passwordField.textProperty().bindBidirectional(boundProperty);
        passwordField.setPromptText(prompt);

        return passwordField;
    }

    private Node boundPasswordField(StringProperty boundProperty, String prompt, Node nextField) {
        PasswordField passwordField = new PasswordField();
        passwordField.textProperty().bindBidirectional(boundProperty);
        passwordField.setPromptText(prompt);
        passwordField.setOnKeyPressed(evt -> {
            if (evt.getCode().equals(KeyCode.ENTER)) {
                nextField.requestFocus();
            }
        });

        return passwordField;
    }

    private Node boundLabel(StringProperty boundProperty) {
        Label label = new Label();
        label.textProperty().bind(boundProperty);
        return label;
    }

    private Node boundLabel(IntegerProperty boundProperty) {
        Label label = new Label();
        label.textProperty().bindBidirectional(boundProperty, new NumberStringConverter());
        return label;
    }

    private Node headingLabel(String string) {
        Label label = new Label("EDF Visualizer");
        label.getStyleClass().add("main-label");
        label.setPrefWidth(10000);
        return label;
    }
}
