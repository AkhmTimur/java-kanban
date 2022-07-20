
import java.util.ArrayList;

class EpicData extends TaskData {
    private ArrayList<Integer> subTaskIdList = new ArrayList<>();

    EpicData(String name, String description, Statuses.statuses status) {
        super(name, description);
        this.status = status;
    }

    void clearSubTaskIdList() {
        subTaskIdList.clear();
    }

    void removeSubTask(Integer id) {
        subTaskIdList.remove(id);
    }

    void addSubTask(SubTaskData subTaskData) {
        subTaskIdList.add(subTaskData.id);
    }

    ArrayList<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }
}
