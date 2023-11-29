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
        this.viewBuilder = new ViewBuilder(model, interactor::addTask, this::displayTask);
    }

    private void displayTask(Runnable someRunnable) {
        Task<Void> someTask = new Task<>() {
            @Override
            protected Void call() {
                interactor.displayTask();
                return null;
            }
        };
        someTask.setOnSucceeded(evt -> {
            someRunnable.run();
        });
        Thread someThread = new Thread(someTask);
        someThread.start();
    }

    public Region getView() {
        return viewBuilder.build();
    }
}

