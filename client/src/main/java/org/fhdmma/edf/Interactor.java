package org.fhdmma.edf;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class Interactor {

    private final Model model;
    private Task selectedTask;
    private String connectionErrorMessage;
    private boolean connectionError = false;

    public Interactor(Model model) {
        this.model = model;
        model.okToAddProperty().bind(Bindings.createBooleanBinding(this::isTaskValid, model.titleProperty(),
                model.durationProperty(), model.periodProperty()));
        model.okToConnectProperty().bind(Bindings.createBooleanBinding(this::isDataValid, model.serverIpProperty(),
                model.usernameProperty(), model.passwordProperty()));
        model.chartSizeProperty().bind(Bindings.createIntegerBinding(() -> model.getSchedule().size(), model.getSchedule()));
    }

    private boolean isTaskValid() {
        return model.getTitle() != "" && model.getPeriod() > 0 && model.getDuration() > 0;
    }

    private Boolean isDataValid() {
        return model.getServerIp() != "" && model.getUsername() != "" && model.getPassword() != "";
    }

    public void addTask() {
        Task newTask = new Task(model.getTitle(), model.getDuration(), model.getPeriod());

        // change to different storage
        Main.addTask(newTask);
        Client.sendTask(newTask);
    }

    public void removeTask() {
        Main.removeTask(model.getSelectedTask());
    }

    public void getSelectedTaskDetails() {
        selectedTask = model.getSelectedTask();
    }

    public void updateTaskListModel() {
        model.setTaskList(Main.tasks);
        List<String> names = new ArrayList<>();
        for (Task task : Main.tasks) {
            names.add(task.getName());
        }
        model.setTaskListNames(names);
    }

    public void updateSelectedModel() {
        model.setSelectedTitle(selectedTask.getName());
        model.setSelectedPeriod(String.valueOf(selectedTask.getPeriod()));
        model.setSelectedDuration(String.valueOf(selectedTask.getDuration()));
    }

    public void connectToServer() throws IOException {
        connectionError = false;
        connectionErrorMessage = "";
        try {
            Client.connect(model.getServerIp(), model.getUsername(), model.getPassword());
            System.out.println(connectionError + " " + connectionErrorMessage);
        } catch (ConnectException e) {
            connectionError = true;
            connectionErrorMessage = e.getMessage();
            throw e;
        } catch (UnknownHostException e) {
            connectionError = true;
            connectionErrorMessage = "Unknown host: " + e.getMessage();
            throw e;
        } catch (IOException e) {
            connectionError = true;
            connectionErrorMessage = "Error: " + e.getMessage();
            throw e;
        }
    }

    public void updateConnectionErrorModel() {
        model.setConnectionError(connectionError);
        model.setConnectionErrorMessage(connectionErrorMessage);
    }

    public void disconnectFromServer() throws IOException {
        Client.disconnect();
    }

    public void updateConnectButtonLabel(String label) {
        model.setConnectButtonLabel(label);
    }

    public void updateConnectedModel(boolean value) {
        model.setConnected(value);
    }

    public void scheduleTasks() throws Exception {
        try {
            model.setSchedule(Client.scheduleTasks(model.getNumberTimeFrames()));
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void updateChartModel() {
        for (Task t : model.getTaskList()) {
            ObservableList<XYChart.Data<Number, String>> seriesData = FXCollections.observableArrayList();
            for (int i = 0; i < model.getSchedule().size(); i++) {
                if (t.getId() == model.getSchedule().get(i))
                    seriesData.add(new XYChart.Data<>(i, t.getName()));
            }
            model.getChartData().add(new XYChart.Series<>(seriesData));
        }
    }
}
