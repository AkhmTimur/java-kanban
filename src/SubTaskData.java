import java.util.Objects;

public class SubTaskData extends TaskData {
    String epicName;

    SubTaskData(String epicName, TaskData taskData) {
        super(taskData.name, taskData.description, taskData.id, taskData.status);
        this.epicName = epicName;
        this.status = "NEW";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTaskData that = (SubTaskData) o;
        return epicName.equals(that.epicName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(epicName);
    }
}
