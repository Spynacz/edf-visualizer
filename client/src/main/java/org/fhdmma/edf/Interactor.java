package org.fhdmma.edf;

public class Interactor {

    private Model model;

    public Interactor(Model model) {
        this.model = model;
    }

    public void addTask() {
        // TODO: Add user task storage
        EDFTask newTask = new EDFTask(model.getTitle(), model.getDeadline(), model.getExecutionTime());
        System.out.println(newTask);
    }

    public void displayTask() {
        String title = model.getTitle();
        System.out.println("Displaying details for task: " + title);
    }
}
