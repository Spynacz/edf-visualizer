package org.fhdmma.edf;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javafx.beans.binding.Bindings;

public class Interactor {

    private final Model model;
    private EDFTask selectedTask;
    private final Client client = new Client();
    private String connectionErrorMessage;

    public Interactor(Model model) {
        this.model = model;
        model.okToAddProperty().bind(Bindings.createBooleanBinding(this::isTaskValid, model.titleProperty(),
                model.durationProperty(), model.periodProperty()));
        model.okToConnectProperty().bind(Bindings.createBooleanBinding(this::isDataValid, model.serverIpProperty(),
                model.usernameProperty(), model.passwordProperty()));
    }

    private boolean isTaskValid() {
        return model.getTitle() != "" && model.getPeriod() > 0 && model.getDuration() > 0;
    }

    private Boolean isDataValid() {
        return model.getServerIp() != "" && model.getUsername() != "" && model.getPassword() != "";
    }

    public void addTask() {
        EDFTask newTask = new EDFTask(model.getTitle(), model.getPeriod(), model.getDuration());

        // change to different storage
        Main.addTask(newTask);
    }

    public void getSelectedTaskDetails() {
        selectedTask = model.getSelectedTask();
    }

    public void updateTaskListModel() {
        model.setTaskList(Main.tasks);
        for (EDFTask task : Main.tasks) {
            model.getTaskListNames().add(task.getName());
        }
    }

    public void updateSelectedModel() {
        model.setSelectedTitle(selectedTask.getName());
        model.setSelectedPeriod(String.valueOf(selectedTask.getPeriod()));
        model.setSelectedDuration(String.valueOf(selectedTask.getDuration()));
    }

    public void connectToServer() {
        try {
            model.setConnectionError(false);
            client.connect(model.getServerIp(), model.getUsername(), model.getPassword());
        } catch (ConnectException e) {
            model.setConnectionError(true);
            connectionErrorMessage = e.getMessage();
        } catch (UnknownHostException e) {
            model.setConnectionError(true);
            connectionErrorMessage = "Unknown host: " + e.getMessage();
        } catch (IOException e) {
            model.setConnectionError(true);
            connectionErrorMessage = "Error: " + e.getMessage();
        }
    }

    public void updateConnectionErrorMessageModel() {
        model.setConnectionErrorMessage(connectionErrorMessage);
    }
}
