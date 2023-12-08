package org.fhdmma.edf;

import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.util.Builder;

public class Controller {

    private Builder<Region> viewBuilder;
    private Interactor interactor;

    public Controller() {
        Model edfTaskModel = new Model();
        this.interactor = new Interactor(edfTaskModel);
        this.viewBuilder = new ViewBuilder(edfTaskModel, this::addTask, this::displayTaskDetails);
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

