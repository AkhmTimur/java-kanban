
import java.util.ArrayList;

public class EpicData extends TaskData {
    private ArrayList<Integer> subTaskIdList = new ArrayList<>();

    EpicData(String epicName, String description, int id) {
        super(epicName, description, id);
    }

    void addSubTask(SubTaskData subTaskData) {
        subTaskIdList.add(subTaskData.id);
    }
}
