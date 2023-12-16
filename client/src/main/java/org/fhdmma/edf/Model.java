package org.fhdmma.edf;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Model {

    private final StringProperty title = new SimpleStringProperty("");
    private final IntegerProperty duration = new SimpleIntegerProperty();
    private final IntegerProperty period = new SimpleIntegerProperty();
    private final ObservableList<EDFTask> taskList = FXCollections.observableArrayList();
    //TODO: consider splitting into two models
    private final ObjectProperty<EDFTask> selectedTask = new SimpleObjectProperty<EDFTask>();
    private final StringProperty selectedTitle = new SimpleStringProperty("");
    private final StringProperty selectedDuration = new SimpleStringProperty("");
    private final StringProperty selectedPeriod = new SimpleStringProperty("");

    private final BooleanProperty okToAdd = new SimpleBooleanProperty(false);

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public int getDuration() {
        return duration.get();
    }

    public IntegerProperty durationProperty() {
        return duration;
    }

    public void setDuration(int time) {
        this.duration.set(time);
    }

    public int getPeriod() {
        return period.get();
    }

    public IntegerProperty periodProperty() {
        return period;
    }

    public void setPeriod(int period) {
        this.period.set(period);
    }

    public ObservableList<EDFTask> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<EDFTask> tasks) {
        this.taskList.setAll(tasks);
    }

    public EDFTask getSelectedTask() {
        return selectedTask.get();
    }

    public ObjectProperty<EDFTask> selectedTaskProperty() {
        return selectedTask;
    }

    public void setSelectedTask(EDFTask task) {
        this.selectedTask.set(task);
    }

    public String getSelectedTitle() {
        return selectedTitle.get();
    }

    public StringProperty selectedTitleProperty() {
        return selectedTitle;
    }

    public void setSelectedTitle(String title) {
        this.selectedTitle.set(title);
    }

    public String getSelectedDuration() {
        return selectedDuration.get();
    }

    public StringProperty selectedDurationProperty() {
        return selectedDuration;
    }

    public void setSelectedDuration(String time) {
        this.selectedDuration.set(time);
    }

    public String getSelectedPeriod() {
        return selectedPeriod.get();
    }

    public StringProperty selectedPeriodProperty() {
        return selectedPeriod;
    }

    public void setSelectedPeriod(String period) {
        this.selectedPeriod.set(period);
    }

    public Boolean isOkToAdd() {
        return okToAdd.get();
    }

    public void setOkToAdd(Boolean value) {
        okToAdd.set(value);
    }

    public BooleanProperty okToAddProperty() {
        return okToAdd;
    }
}
