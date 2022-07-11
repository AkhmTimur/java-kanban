import java.util.Objects;

public class SubTaskData extends TaskData {
    private Integer epicId;

    SubTaskData(Integer epicId, String name, String description) {
        super(name, description);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        SubTaskData that = (SubTaskData) o;
        return epicId.equals(that.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
