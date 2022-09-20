package dataClasses;

import enums.DataTypes;
import enums.Statuses;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class EpicData extends TaskData {
    private ArrayList<Integer> subTaskIdList = new ArrayList<>();
    private LocalDateTime endTime;

    public EpicData(String name, String description, Statuses status) {
        super(name, description);
        this.status = status;
    }

    public EpicData(String name, String description, int id, Statuses status) {
        super(name, description, id, status);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public DataTypes getType() {
        return DataTypes.EPIC;
    }

    public void clearSubTaskIdList() {
        subTaskIdList.clear();
    }

    public void removeSubTask(Integer id) {
        subTaskIdList.remove(id);
    }

    public void addSubTask(SubTaskData subTaskData) {
        subTaskIdList.add(subTaskData.getId());
    }

    public ArrayList<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }
}
