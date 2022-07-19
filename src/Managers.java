import java.util.ArrayList;


public class Managers {
    public TaskManager getDefault() {
        return new TaskManager() {
            @Override
            public void addToTasks(TaskData taskData) {

            }

            @Override
            public int addToEpics(EpicData epicData) {
                return 0;
            }

            @Override
            public void addToSubTasks(SubTaskData subTaskData) {

            }

            @Override
            public TaskData getTaskById(int id) {
                return null;
            }

            @Override
            public EpicData getEpicById(int id) {
                return null;
            }

            @Override
            public SubTaskData getSubTaskById(int id) {
                return null;
            }

            @Override
            public TaskData deleteTaskById(int id) {
                return null;
            }

            @Override
            public void deleteEpicById(int id) {

            }

            @Override
            public void deleteSubTaskById(int id) {

            }

            @Override
            public ArrayList<TaskData> getAllTasks() {
                return null;
            }

            @Override
            public ArrayList<EpicData> getAllEpics() {
                return null;
            }

            @Override
            public ArrayList<SubTaskData> getAllSubTasks() {
                return null;
            }

            @Override
            public void deleteAllTasks() {

            }

            @Override
            public void deleteAllEpics() {

            }

            @Override
            public void deleteAllSubTasks() {

            }

            @Override
            public void updateTask(TaskData taskData) {

            }

            @Override
            public void updateEpic(EpicData epicData) {

            }

            @Override
            public void updateSubTask(SubTaskData subTaskData) {

            }

            @Override
            public void updateEpicStatus(int id) {

            }


        };
    }

    public HistoryManager getHistoryDefault() {
        return new InMemoryHistoryManager();
    }
}
