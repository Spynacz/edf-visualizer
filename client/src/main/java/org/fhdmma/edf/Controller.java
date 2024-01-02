package org.fhdmma.edf;

import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.util.Builder;

public class Controller {

    private Builder<Region> viewBuilder;
    private Interactor interactor;

    public Controller() {
        Model model = new Model();
        this.interactor = new Interactor(model);
        this.viewBuilder = new ViewBuilder(model, this::addTask, this::displayTaskDetails, this::connectToServer);

        // temporary
        interactor.updateTaskListModel();
    }

    private void connectToServer(Runnable postConnectGUIUpdate) {
        Task<Void> connectTask = new Task<>() {
            @Override
            protected Void call() {
                interactor.connectToServer();
                return null;
            }
        };

        Thread connectTaskThread = new Thread(connectTask);
        connectTaskThread.start();
    }

    private void displayTaskDetails(Runnable postFetchGUIUpdate) {
        Task<Void> getTaskTask = new Task<>() {
            @Override
            protected Void call() {
                interactor.getSelectedTaskDetails();
                return null;
            }
        };
        getTaskTask.setOnSucceeded(evt -> {
            interactor.updateSelectedModel();
            postFetchGUIUpdate.run();
        });
        Thread displayTaskThread = new Thread(getTaskTask);
        displayTaskThread.start();
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

    public Region getView() {
        return viewBuilder.build();
    }
}

