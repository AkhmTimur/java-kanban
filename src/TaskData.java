import java.util.Objects;

public class TaskData {
    protected String name;
    protected String description;
    protected int id;
    protected String status = "NEW";

    TaskData() {
        status = "NEW";
    }

    TaskData(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskData taskData = (TaskData) o;
        return id == taskData.id &&
                name.equals(taskData.name) &&
                Objects.equals(description, taskData.description) &&
                status.equals(taskData.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }
}
