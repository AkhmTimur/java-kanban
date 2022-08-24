package dataClasses;

import enums.Statuses;
import interfaces.Printable;

import java.util.Objects;

public class TaskData implements Printable {
    private String name;
    private String description;
    private int id;
    public Statuses status;

    public TaskData(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Statuses.NEW;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(Statuses status) {
        this.status = status;
    }


    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String print() {
        return "TASK";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskData taskData = (TaskData) o;
        return id == taskData.id &&
                Objects.equals(name, taskData.name) &&
                Objects.equals(description, taskData.description) &&
                Objects.equals(status, taskData.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }
}
