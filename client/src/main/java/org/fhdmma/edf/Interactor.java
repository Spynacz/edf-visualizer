package org.fhdmma.edf;

public class Interactor {

    private EDFTaskModel edfTaskModel;

    public Interactor(EDFTaskModel model) {
        this.edfTaskModel = model;
    }

    public void addTask() {
        // TODO: Add user task storage
        EDFTask newTask = new EDFTask(edfTaskModel.getTitle(), edfTaskModel.getDeadline(), edfTaskModel.getExecutionTime());
        Main.addTask(newTask);
        // System.out.println("main tasks\n" + Main.tasks + "\n#####");
        // System.out.println(newTask);
    }

    public void getTaskDetails() {
        String title = edfTaskModel.getTitle();
        System.out.println("Displaying details for task: " + title);
    }

    public void updateTaskListModel() {
    }
}
