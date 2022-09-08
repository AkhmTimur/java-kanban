package dataClasses;

import enums.DataTypes;
import enums.Statuses;

import java.util.Objects;

public class SubTaskData extends TaskData {
    private Integer epicId;

    public SubTaskData(String name, String description) {
        super(name, description);
    }

    public SubTaskData(String name, String description, int id, Statuses status) {
        super(name, description, id, status);
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    public DataTypes getType() {
        return DataTypes.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTaskData that = (SubTaskData) o;
        return Objects.equals(epicId, that.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
