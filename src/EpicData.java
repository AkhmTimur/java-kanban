
import java.util.ArrayList;

public class EpicData extends TaskData {
    private ArrayList<Integer> subTaskIdList = new ArrayList<>();

    public EpicData(String name, String description, TaskData.statuses status) {
        super(name, description);
        this.status = status;
    }

    void clearSubTaskIdList() {
        subTaskIdList.clear();
    }

    void removeSubTask(int id) {
        subTaskIdList.remove(id);
    }

    void addSubTask(SubTaskData subTaskData) {
        subTaskIdList.add(subTaskData.id);
    }

    ArrayList<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }
}
