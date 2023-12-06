package org.fhdmma.edf;

public class Interactor {

    private Model model;

    public Interactor(Model model) {
        this.model = model;
    }

    public void addTask() {
        EDFTask newTask = new EDFTask(model.getTitle(), model.getDeadline(), model.getExecutionTime());
        Main.addTask(newTask);
    }

    public void getTaskDetails() {
        String title = model.getTitle();
        System.out.println("Displaying details for task: " + title);
    }

    public void updateTaskListModel() {
        model.setTaskList(Main.tasks);
    }
}
