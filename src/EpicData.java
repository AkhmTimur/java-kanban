import java.util.ArrayList;

public class EpicData extends TaskData {
    ArrayList<SubTaskData> subTasks = new ArrayList<>();

    EpicData(String epicName, TaskData taskData) {
        this.name = epicName;
        this.status = taskData.status;
        this.id = taskData.id;
        this.description = taskData.description;
    }

    void addSubTask(SubTaskData subTaskData) {
        subTasks.add(subTaskData);
    }


}
