package org.fhdmma.edf;

public class Interactor {

    private Model model;
    private EDFTask selectedTask;

    public Interactor(Model model) {
        this.model = model;
    }

    public void addTask() {
        EDFTask newTask = new EDFTask(model.getTitle(), model.getDeadline(), model.getDuration());

        // change to different storage
        Main.addTask(newTask);
    }

    public void getSelectedTaskDetails() {
        selectedTask = model.getSelectedTask();
    }

    public void updateTaskListModel() {
        model.setTaskList(Main.tasks);
    }

    public void updateSelectedModel() {
        model.setSelectedTitle(selectedTask.getName());
        model.setSelectedDeadline(String.valueOf(selectedTask.getDeadline()));
        model.setSelectedDuration(String.valueOf(selectedTask.getDuration()));
    }
}
