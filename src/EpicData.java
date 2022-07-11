
import java.util.ArrayList;

public class EpicData extends TaskData {
    private ArrayList<Integer> subTaskIdList = new ArrayList<>();

    public EpicData(String name, String description, String status) {
        super(name, description);
        this.status = status;
    }

    void addSubTask(SubTaskData subTaskData) {
        subTaskIdList.add(subTaskData.id);
    }

    ArrayList<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }
}
