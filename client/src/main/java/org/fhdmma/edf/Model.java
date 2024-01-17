package org.fhdmma.edf;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class Model {

    private final StringProperty title = new SimpleStringProperty("");
    private final IntegerProperty duration = new SimpleIntegerProperty();
    private final IntegerProperty period = new SimpleIntegerProperty();
    private final ObservableList<Task> taskList = FXCollections.observableArrayList();
    private final ObservableList<String> taskListNames = FXCollections.observableArrayList();
    private final ObjectProperty<Task> selectedTask = new SimpleObjectProperty<Task>();
    private final StringProperty selectedTitle = new SimpleStringProperty("");
    private final StringProperty selectedDuration = new SimpleStringProperty("");
    private final StringProperty selectedPeriod = new SimpleStringProperty("");
    private final BooleanProperty taskSelected = new SimpleBooleanProperty(false);

    private final BooleanProperty okToAdd = new SimpleBooleanProperty(false);

    private final BooleanProperty connected = new SimpleBooleanProperty(false);
    private final BooleanProperty okToConnect = new SimpleBooleanProperty(false);
    private final BooleanProperty connectionError = new SimpleBooleanProperty(false);
    private final StringProperty connectionErrorMessage = new SimpleStringProperty("");
    private final StringProperty connectButtonLabel = new SimpleStringProperty("Connect");

    private final StringProperty serverIp = new SimpleStringProperty("");
    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");

    private final StringProperty currentTask = new SimpleStringProperty("");
    private final IntegerProperty numberTimeframes = new SimpleIntegerProperty();

    private final ObservableList<Long> schedule = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Series<Number, String>> chartData = FXCollections.observableArrayList();
    private final IntegerProperty chartSize = new SimpleIntegerProperty();

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public int getDuration() {
        return duration.get();
    }

    public IntegerProperty durationProperty() {
        return duration;
    }

    public void setDuration(int time) {
        this.duration.set(time);
    }

    public int getPeriod() {
        return period.get();
    }

    public IntegerProperty periodProperty() {
        return period;
    }

    public void setPeriod(int period) {
        this.period.set(period);
    }

    public ObservableList<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> tasks) {
        this.taskList.setAll(tasks);
    }

    public ObservableList<String> getTaskListNames() {
        return taskListNames;
    }

    public void setTaskListNames(List<String> tasks) {
        this.taskListNames.setAll(tasks);
    }

    public Task getSelectedTask() {
        return selectedTask.get();
    }

    public ObjectProperty<Task> selectedTaskProperty() {
        return selectedTask;
    }

    public void setSelectedTask(Task task) {
        this.selectedTask.set(task);
    }

    public String getSelectedTitle() {
        return selectedTitle.get();
    }

    public StringProperty selectedTitleProperty() {
        return selectedTitle;
    }

    public void setSelectedTitle(String title) {
        this.selectedTitle.set(title);
    }

    public String getSelectedDuration() {
        return selectedDuration.get();
    }

    public StringProperty selectedDurationProperty() {
        return selectedDuration;
    }

    public void setSelectedDuration(String time) {
        this.selectedDuration.set(time);
    }

    public String getSelectedPeriod() {
        return selectedPeriod.get();
    }

    public StringProperty selectedPeriodProperty() {
        return selectedPeriod;
    }

    public void setSelectedPeriod(String period) {
        this.selectedPeriod.set(period);
    }

    public Boolean isTaskSelected() {
        return taskSelected.get();
    }

    public void setTaskSelected(Boolean value) {
        taskSelected.set(value);
    }

    public BooleanProperty taskSelectedProperty() {
        return taskSelected;
    }

    public Boolean isOkToAdd() {
        return okToAdd.get();
    }

    public void setOkToAdd(Boolean value) {
        okToAdd.set(value);
    }

    public BooleanProperty okToAddProperty() {
        return okToAdd;
    }

    public Boolean isConnected() {
        return connected.get();
    }

    public void setConnected(Boolean value) {
        connected.set(value);
    }

    public BooleanProperty connectedProperty() {
        return connected;
    }

    public Boolean isOkToConnect() {
        return okToConnect.get();
    }

    public void setOkToConnect(Boolean value) {
        okToConnect.set(value);
    }

    public BooleanProperty okToConnectProperty() {
        return okToConnect;
    }

    public String getServerIp() {
        return serverIp.get();
    }

    public StringProperty serverIpProperty() {
        return serverIp;
    }

    public void setServerIp(String ip) {
        this.serverIp.set(ip);
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getCurrentTask() {
        return currentTask.get();
    }

    public StringProperty currentTaskProperty() {
        return currentTask;
    }

    public void setCurrentTask(String task) {
        this.currentTask.set(task);
    }

    public Boolean isConnectionError() {
        return connectionError.get();
    }

    public void setConnectionError(Boolean value) {
        connectionError.set(value);
    }

    public BooleanProperty connectionErrorProperty() {
        return connectionError;
    }

    public String getConnetionErrorMessage() {
        return connectionErrorMessage.get();
    }

    public void setConnectionErrorMessage(String message) {
        this.connectionErrorMessage.set(message);
    }

    public StringProperty connectionErrorMessageProperty() {
        return connectionErrorMessage;
    }

    public String getConnectButtonLabel() {
        return connectButtonLabel.get();
    }

    public void setConnectButtonLabel(String label) {
        this.connectButtonLabel.set(label);
    }

    public StringProperty connectButtonLabelProperty() {
        return connectButtonLabel;
    }

    public int getNumberTimeFrames() {
        return numberTimeframes.get();
    }

    public void setNumberTimeFrames(int num) {
        this.numberTimeframes.set(num);
    }

    public IntegerProperty numberTimeFramesProperty() {
        return numberTimeframes;
    }

    public ObservableList<Long> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<Long> list) {
        this.schedule.setAll(list);
    }

    public ObservableList<XYChart.Series<Number, String>> getChartData() {
        return chartData;
    }

    public void setChartData(List<XYChart.Series<Number, String>> data) {
        this.chartData.setAll(data);
    }

    public int getChartSize() {
        return chartSize.get();
    }

    public void setChartSize(int size) {
        this.chartSize.set(size);
    }

    public IntegerProperty chartSizeProperty() {
        return chartSize;
    }
}
