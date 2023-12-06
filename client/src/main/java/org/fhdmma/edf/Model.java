package org.fhdmma.edf;

import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Model {

    private final StringProperty title = new SimpleStringProperty("");
    private final IntegerProperty executionTime = new SimpleIntegerProperty();
    private final IntegerProperty deadline = new SimpleIntegerProperty();
    private final ObservableList<EDFTask> taskList = FXCollections.observableArrayList();

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public int getExecutionTime() {
        return executionTime.get();
    }

    public IntegerProperty executionTimeProperty() {
        return executionTime;
    }

    public void setExecutionTime(int time) {
        this.executionTime.set(time);
    }

    public int getDeadline() {
        return deadline.get();
    }

    public IntegerProperty deadlineProperty() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline.set(deadline);
    }

    public ObservableList<EDFTask> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<EDFTask> tasks) {
        this.taskList.setAll(tasks);
    }
}
