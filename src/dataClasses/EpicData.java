package dataClasses;

import enums.DataTypes;
import enums.Statuses;

import java.util.ArrayList;

public class EpicData extends TaskData {
    private ArrayList<Integer> subTaskIdList = new ArrayList<>();

    public EpicData(String name, String description, Statuses status) {
        super(name, description);
        this.status = status;
    }

    public DataTypes getType() {
        return DataTypes.EPIC;
    }

    @Override
    public String print() {
        return "EPIC";
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