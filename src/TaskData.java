import java.util.Objects;

public class TaskData {
    protected String name;
    protected String description;
    protected int id;
    protected TaskData.statuses status;
    enum statuses {
        NEW,
        IN_PROGRESS,
        DONE
    }

    TaskData(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = statuses.NEW;
    }

    protected int getId() {
        return id;
    }

    public TaskData.statuses getStatus() {
        return status;
    }

    public void setStatus(TaskData.statuses status) {
        this.status = status;
    }



    void setId(int id) {
        this.id = id;
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
