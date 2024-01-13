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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
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
    private final Consumer<Runnable> taskRemover;
    private final Consumer<Runnable> connector;
    private final Runnable disconnector;
    private final Runnable tasksScheduler;

    public ViewBuilder(Model model, Consumer<Runnable> taskAdder, Consumer<Runnable> taskRemover,
            Consumer<Runnable> connector, Runnable disconnector, Runnable taskScheduler) {
        this.model = model;
        this.taskAdder = taskAdder;
        this.connector = connector;
        this.disconnector = disconnector;
        this.taskRemover = taskRemover;
        this.tasksScheduler = taskScheduler;
    }

    @Override
    public Region build() {
        BorderPane mainPane = new BorderPane();
        mainPane.getStylesheets()
                .add(Objects.requireNonNull(this.getClass().getResource("/css/main.css")).toExternalForm());
        mainPane.setTop(headingLabel("EDF Visualizer"));
        mainPane.setLeft(createLeft());
        mainPane.setCenter(createCenter());
        return mainPane;
    }

    private Node createLeft() {
        VBox vbox = new VBox();
        vbox.getChildren().add(setClientTasks());
        VBox.setVgrow(vbox.getChildren().get(0), Priority.ALWAYS);

        vbox.getChildren().add(setAddTaskButton());
        vbox.getChildren().add(setRemoveTaskButton(taskRemover));
        vbox.getChildren().add(setScheduleSection(tasksScheduler));
        vbox.getChildren().add(setConnectButton(disconnector));

        vbox.getStyleClass().add("leftbox");
        return vbox;
    }

    private Node createCenter() {
        VBox vbox = new VBox(setTasksChart());
        VBox.setVgrow(vbox.getChildren().get(0), Priority.ALWAYS);
        vbox.getStyleClass().add("centerbox");
        return vbox;
    }

    private Node setTasksChart() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(false);
        xAxis.setMinorTickCount(0);
        xAxis.setLowerBound(0);
        xAxis.upperBoundProperty().bind(model.chartSizeProperty());
        xAxis.setTickUnit(1);

        CategoryAxis yAxis = new CategoryAxis();
        yAxis.setAutoRanging(false);
        yAxis.setCategories(model.getTaskListNames());

        ScatterChart<Number, String> chart = new ScatterChart<>(xAxis, yAxis);
        chart.setTitle("Task execution history");
        chart.setLegendVisible(false);

        chart.setData(model.getChartData());
        return chart;
    }

    // private Node setCurrentTask() {
    // HBox hbox = new HBox(5, new Label("Current task:"),
    // boundLabel(model.currentTaskProperty()));
    // hbox.getStyleClass().add("current-task");
    // return hbox;
    // }

    private Node setClientTasks() {
        ListView<Task> listView = new ListView<>();
        listView.setItems(model.getTaskList());

        listView.setCellFactory(lv -> {
            return new ListCell<Task>() {
                private VBox content;
                private Text name;
                private Text period;
                private Text duration;

                {
                    name = new Text();
                    name.getStyleClass().add("name");
                    period = new Text();
                    duration = new Text();
                    HBox hbox = new HBox(5, duration, period);
                    hbox.getStyleClass().add("stats");
                    content = new VBox(10, name, hbox);
                }

                @Override
                protected void updateItem(Task item, boolean empty) {
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
            Task t = listView.getSelectionModel().getSelectedItem();
            if (t != null) {
                model.setSelectedTask(t);
                model.setTaskSelected(true);
            }
        });

        listView.getStyleClass().add("task-list");

        return listView;
    }

    private Node setAddTaskButton() {
        Button button = new Button("Add task");
        button.disableProperty().bind(model.connectedProperty().not());

        button.setOnAction(evt -> {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);

            BorderPane borderPane = new BorderPane();
            borderPane.getStylesheets()
                    .add(Objects.requireNonNull(this.getClass().getResource("/css/main.css")).toExternalForm());
            Node center = setAddTaskDialog(taskAdder, dialog);
            borderPane.setCenter(center);
            BorderPane.setMargin(center, new Insets(50));

            Scene dialogScene = new Scene(borderPane);
            dialog.setScene(dialogScene);
            dialog.setResizable(false);
            dialog.show();
        });

        button.getStyleClass().add("button");

        return button;
    }

    private Node setRemoveTaskButton(Consumer<Runnable> remove) {
        Button button = new Button("Remove task");
        button.disableProperty().bind(model.taskSelectedProperty().not());

        button.setOnAction(evt -> {
            remove.accept(() -> model.setTaskSelected(false));
        });

        button.getStyleClass().add("button");

        return button;
    }

    private Node setAddTaskDialog(Consumer<Runnable> addTask, Stage stage) {
        Node periodText = boundIntegerField(model.periodProperty(), "Period");
        Node durationText = boundIntegerField(model.durationProperty(), "Duration", periodText);
        Node titleText = boundTextField(model.titleProperty(), "Title", durationText);

        periodText.getStyleClass().add("dialog-text-field");
        durationText.getStyleClass().add("dialog-text-field");
        titleText.getStyleClass().add("dialog-text-field");

        HBox title = new HBox(6, new Label("Title:"), hboxSpacer(), titleText);
        HBox duration = new HBox(6, new Label("Duration:"), hboxSpacer(), durationText);
        HBox period = new HBox(6, new Label("Period:"), hboxSpacer(), periodText);

        title.getChildren().get(0).getStyleClass().add("dialog-label");
        duration.getChildren().get(0).getStyleClass().add("dialog-label");
        period.getChildren().get(0).getStyleClass().add("dialog-label");

        Button confirm = new Button("Confirm");
        confirm.disableProperty().bind(model.okToAddProperty().not());
        confirm.setOnAction(evt -> {
            addTask.accept(() -> stage.close());
        });

        confirm.setDefaultButton(true);
        confirm.getStyleClass().add("confirm-button");

        VBox vbox = new VBox(10, title, duration, period, confirm);
        vbox.setAlignment(Pos.CENTER);

        return vbox;
    }

    private Node setScheduleSection(Runnable scheduleTasks) {
        TextField text = (TextField) boundIntegerField(model.numberTimeFramesProperty(), "");
        text.getStyleClass().add("schedule-field");
        text.setMaxWidth(70);
        text.disableProperty().bind(model.connectedProperty().not());

        Button button = new Button("Schedule");
        button.disableProperty().bind(model.connectedProperty().not());

        button.setOnAction(evt -> {
            scheduleTasks.run();
        });

        button.setDefaultButton(true);
        button.getStyleClass().add("button");
        button.setMaxWidth(99999);

        HBox schedule = new HBox(text, button);
        HBox.setHgrow(schedule.getChildren().get(1), Priority.ALWAYS);

        return schedule;
    }

    private Node setConnectButton(Runnable disconnect) {
        Button button = new Button();
        button.textProperty().bindBidirectional(model.connectButtonLabelProperty());

        button.setOnAction(evt -> {
            if (model.isConnected()) {
                disconnect.run();
            } else {
                model.setConnectionError(false);
                model.setConnectionErrorMessage("");

                final Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);

                BorderPane borderPane = new BorderPane();
                borderPane.getStylesheets()
                        .add(Objects.requireNonNull(this.getClass().getResource("/css/main.css")).toExternalForm());
                Node center = setConnectDialog(connector, dialog);
                borderPane.setCenter(center);
                BorderPane.setMargin(center, new Insets(50));

                Scene dialogScene = new Scene(borderPane);
                dialog.setScene(dialogScene);
                dialog.setResizable(false);
                dialog.show();
            }
        });

        button.getStyleClass().add("button");

        return button;
    }

    private Node setConnectDialog(Consumer<Runnable> connect, Stage stage) {
        Node passwordText = boundPasswordField(model.passwordProperty(), "Password");
        Node usernameText = boundTextField(model.usernameProperty(), "Username", passwordText);
        Node serverIpText = boundTextField(model.serverIpProperty(), "Server IP", usernameText);

        passwordText.getStyleClass().add("dialog-text-field");
        usernameText.getStyleClass().add("dialog-text-field");
        serverIpText.getStyleClass().add("dialog-text-field");

        HBox serverIp = new HBox(6, new Label("Address:"), hboxSpacer(), serverIpText);
        HBox username = new HBox(6, new Label("Username:"), hboxSpacer(), usernameText);
        HBox password = new HBox(6, new Label("Password:"), hboxSpacer(), passwordText);

        serverIp.getChildren().get(0).getStyleClass().add("dialog-label");
        username.getChildren().get(0).getStyleClass().add("dialog-label");
        password.getChildren().get(0).getStyleClass().add("dialog-label");

        Button confirm = new Button("Connect");
        confirm.disableProperty().bind(model.okToConnectProperty().not());
        confirm.setOnAction(evt -> {
            connect.accept(() -> {
                if (!model.isConnectionError())
                    stage.close();
            });
        });

        confirm.setDefaultButton(true);
        confirm.getStyleClass().add("confirm-button");

        VBox vbox = new VBox(10, serverIp, username, password, confirm,
                boundLabel(model.connectionErrorMessageProperty()));
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
