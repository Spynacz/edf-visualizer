package org.fhdmma.edf;

import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.util.Builder;

public class Controller {

    private Builder<Region> viewBuilder;
    private Interactor interactor;

    public Controller() {
        EDFTaskModel edfTaskModel = new EDFTaskModel();
        this.interactor = new Interactor(edfTaskModel);
        this.viewBuilder = new ViewBuilder(edfTaskModel, this::addTask, this::displayTask);
    }

    private void displayTask(Runnable postFetchGUIUpdate) {
        Task<Void> getTaskTask = new Task<>() {
            @Override
            protected Void call() {
                interactor.getTaskDetails();
                return null;
            }
        };
        getTaskTask.setOnSucceeded(evt -> {
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

