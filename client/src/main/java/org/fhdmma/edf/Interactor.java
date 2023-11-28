package org.fhdmma.edf;

public class Interactor {

    private Model model;

    public Interactor(Model model) {
        this.model = model;
    }

    public void addTask() {
        System.out.println("Inside Interactor::addTask()");
    }

    public void displayTask() {
        System.out.println("Inside Interactor::displayTask()");
    }
}

