import java.util.HashMap;
import java.util.Scanner;

public class Manager {
    HashMap<String, TaskData> tasks = new HashMap<>();
    HashMap<String, EpicData> epics = new HashMap<>();


    void addToTasks(String name, TaskData taskData) {
        if(name != null && !tasks.containsKey(name)) {
            tasks.put(name, taskData);
        }
    }

    void addToEpics(String name, EpicData epicData) {
        if(name != null && !epics.containsKey(name)) {
            epics.put(name, epicData);
        }
    }

    void addSubTaskToEpics(int epicId, SubTaskData subTaskData) {
        for (String k : epics.keySet()) {
            if(epics.get(k).id == epicId) {
                epics.get(k).subTasks.add(subTaskData);
            }
        }
    }

    void getAllTasks() {
        System.out.println("Список задач: ");
        for (String k : tasks.keySet()) {
            System.out.println(k);
        }
    }

    void getAllEpics() {
        System.out.println("Список эпиков: ");
        for (String k : epics.keySet()) {
            System.out.println(k);
        }
    }

    void clearTasks() {
        tasks.clear();
        System.out.println("Задачи были удалены");
    }

    String getOrDeleteById(String opType) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Вы будете искать задачу или эпик?");
        System.out.println("1 - Задача");
        System.out.println("2 - Эпик");

        int type = scan.nextInt();
        System.out.println("Введите название задачи");
        String taskName = scan.next();
        System.out.println("Введите описание");
        String desc = scan.next();
        int id = genID(taskName, desc);
        if(opType == "delete") {
            if(type == 1) {
                for (String k : tasks.keySet()) {
                    if(tasks.get(k).id == id) {
                        tasks.remove(k);
                        return "Задача удалена";
                    }
                }
                return "Такой задачи нет";
            } else {
                for (String k : epics.keySet()) {
                    if(epics.get(k).id == id) {
                        epics.remove(k);
                        return "Эпик удален";
                    }
                }
                return "Такого эпика нет";
            }
        } else {
            if(type == 1) {
                for (String k : tasks.keySet()) {
                    if(tasks.get(k).id == id) {
                        return k;
                    }
                }
                return "Такой задачи нет";
            } else {
                for (String k : epics.keySet()) {
                    if(epics.get(k).id == id) {
                        return k;
                    }
                }
                return "Такого эпика нет";
            }
        }

    }

    void getSubTasks(String epic) {
        System.out.println("Список подзадач " + epic);
        if(epics.containsKey(epic)) {
            for (SubTaskData subTask : epics.get(epic).subTasks) {
                System.out.println(subTask.name);
            }
        } else {
            System.out.println("Такого эпика нет");
        }

    }

    void setTaskStatus(String taskName, int status) {
        if(status == 1) {
            tasks.get(taskName).status = "IN PROGRESS";
        } else {
            tasks.get(taskName).status = "DONE";
        }

    }

    void setSubTaskStatus(String epicName, String subTaskName, String status) {
        for (SubTaskData subTask : epics.get(epicName).subTasks) {
            if(subTask.name.equals(subTaskName) && subTask.status != "DONE") {
                subTask.status = status;
            }
        }
        if(isEpicDone(epicName)) {
            System.out.println("Эпик завершен!");
        } else {
            System.out.println("Остались незавершенные подзадачи");
        }
    }

    private boolean isEpicDone(String epicName) {
        boolean result = false;
        for (SubTaskData subTask : epics.get(epicName).subTasks) {
            if(subTask.status == "DONE") {
                result = true;
            } else {
                return false;
            }
        }
        return result;
    }

    int genID(String name, String desc) {
        int id = 17;
        if (name != null) {
            id = id + name.hashCode();
        }
        id = id * 31;

        if (desc != null) {
            id = id + desc.hashCode();
        }
        return id;
    }
}
