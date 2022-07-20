import java.util.Objects;

public class TaskData {
    protected String name;
    protected String description;
    protected int id;
    protected Statuses.statuses status;

    TaskData(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Statuses.statuses.NEW;
    }

    protected int getId() {
        return id;
    }

    public Statuses.statuses getStatus() {
        return status;
    }

    public void setStatus(Statuses.statuses status) {
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
