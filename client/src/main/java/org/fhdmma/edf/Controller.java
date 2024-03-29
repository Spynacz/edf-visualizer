package org.fhdmma.edf;

import java.io.IOException;

import javax.security.auth.login.FailedLoginException;

import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.util.Builder;

public class Controller {

    private Builder<Region> viewBuilder;
    private Interactor interactor;

    public Controller() {
        Model model = new Model();
        this.interactor = new Interactor(model);
        this.viewBuilder = new ViewBuilder(model, this::addTask, this::removeTask, this::connectToServer,
                this::disconnectFromServer, this::scheduleTasks, this::clearSchedule);

        interactor.updateTaskListModel();
    }

    private void connectToServer(Runnable connectSuccessGUIUpdate) {
        Task<Void> connectTask = new Task<>() {
            @Override
            protected Void call() throws IOException, FailedLoginException {
                interactor.connectToServer();
                return null;
            }
        };
        connectTask.setOnSucceeded(evt -> {
            interactor.updateConnectionErrorModel();
            interactor.updateConnectButtonLabel("Disconnect");
            interactor.updateConnectedModel(true);
            interactor.updateTaskListModel();
            connectSuccessGUIUpdate.run();
        });
        connectTask.setOnFailed(evt -> {
            interactor.updateConnectionErrorModel();
        });
        Thread connectTaskThread = new Thread(connectTask);
        connectTaskThread.start();
    }

    private void disconnectFromServer() {
        Task<Void> disconnectTask = new Task<>() {
            @Override
            protected Void call() throws IOException {
                interactor.disconnectFromServer();
                return null;
            }
        };
        disconnectTask.setOnSucceeded(evt -> {
            interactor.updateConnectButtonLabel("Connect");
            interactor.updateConnectedModel(false);
            interactor.clearSchedule();
            interactor.clearTasks();
        });
        Thread disconnectTaskThread = new Thread(disconnectTask);
        disconnectTaskThread.start();
    }

    private void addTask(Runnable postAddGUIUpdate) {
        Task<Void> addTaskTask = new Task<>() {
            @Override
            protected Void call() {
                interactor.addTask();
                return null;
            }
        };
        addTaskTask.setOnSucceeded(evt -> {
            interactor.updateTaskListModel();
            postAddGUIUpdate.run();
        });
        Thread addTaskThread = new Thread(addTaskTask);
        addTaskThread.start();
    }

    private void removeTask(Runnable postRemoveGUIUpdate) {
        Task<Void> removeTaskTask = new Task<>() {
            @Override
            protected Void call() {
                interactor.removeTask();
                return null;
            }
        };
        removeTaskTask.setOnSucceeded(evt -> {
            interactor.updateTaskListModel();
            postRemoveGUIUpdate.run();
        });
        Thread removeTaskThread = new Thread(removeTaskTask);
        removeTaskThread.start();
    }

    private void scheduleTasks() {
        Task<Void> scheduleTasksTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                interactor.scheduleTasks();
                return null;
            }
        };
        scheduleTasksTask.setOnSucceeded(evt -> {
            interactor.updateChartModel();
        });
        Thread scheduleTasksThread = new Thread(scheduleTasksTask);
        scheduleTasksThread.start();
    }

    private void clearSchedule() {
        Task<Void> clearScheduleTask = new Task<>() {
            @Override
            protected Void call() {
                interactor.clearSchedule();
                return null;
            }
        };
        clearScheduleTask.setOnSucceeded(evt -> {
            interactor.updateChartModel();
        });
        Thread clearScheduleThread = new Thread(clearScheduleTask);
        clearScheduleThread.start();
    }

    public Region getView() {
        return viewBuilder.build();
    }

}
