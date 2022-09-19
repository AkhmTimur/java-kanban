package dataClasses;

import enums.DataTypes;
import enums.Statuses;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class TaskData {
    private String name;
    private String description;
    private int id;
    protected Statuses status;
    private Duration duration;
    private LocalDateTime startDate;

    public TaskData(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Statuses.NEW;
    }

    public TaskData(String name, String description, int id, Statuses status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
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

    public Statuses getStatus() {
        return status;
    }

    public DataTypes getType() {
        return DataTypes.TASK;
    }

    public LocalDateTime getEndTime() {
        return startDate.plus(duration);
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDuration(long minutes) {
        duration = Duration.ofMinutes(minutes);
    }

    public void setStartDate(int year, int month, int day) {
        startDate = LocalDateTime.of(year, month, day, 0, 0);
    }

    public void setStartDate(LocalDateTime dateTime) {
        startDate = dateTime;
    }

    public void calcDurationByEndTime(LocalDateTime endTime) {
        duration = Duration.between(this.startDate, endTime);
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
