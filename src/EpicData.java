import java.util.HashMap;

public class EpicData extends TaskData {
    private HashMap<Integer, SubTaskData> subTasks = new HashMap<>();
    private String status = "NEW";

    EpicData(String epicName, String description, int id) {
        this.name = epicName;
        this.id = id;
        this.description = description;
    }

    public HashMap<Integer, SubTaskData> getSubTasks() {
        return subTasks;
    }

    void addSubTask(SubTaskData subTaskData) {
        subTasks.put(subTaskData.id, subTaskData);
    }


}
