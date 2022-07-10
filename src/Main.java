import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        TaskData newTaskData;
        Manager manager = new Manager();


        printMenu();
        int command = scan.nextInt();

        while (command != 0) {
            if (command == 1) {
                System.out.println("Какую задачу добавить?");
                String taskName = scan.next();
                String desc;
                if (taskName != null) {
                    System.out.println("Добавьте описание");
                    desc = scan.next();
                } else {
                    System.out.println("Вы не введи название задачи");
                    desc = null;
                }

                int id = manager.genID(taskName);

                newTaskData = new TaskData(taskName, desc, id);
                if (desc != null) {
                    System.out.println("Задача " + taskName + " создана. Описание - " + desc);
                    manager.addToTasks(newTaskData);
                }
            } else if (command == 2) {
                System.out.println("Какую задачу вам нужно дополнить подзадачей?");
                String epicName = scan.next();
                int id = manager.genID(epicName);
                System.out.println("Какую подзадачу добавить?");
                String subTaskName = scan.next();
                System.out.println("Добавьте описание подзадаче");
                String desc = scan.next();

                if (manager.getTasks().containsKey(id)) {
                    newTaskData = new TaskData(subTaskName, desc, manager.genID(subTaskName));
                    SubTaskData subTask = new SubTaskData(id,  newTaskData.name, newTaskData.description, newTaskData.id);
                    EpicData epic = new EpicData(epicName, subTask.description, subTask.id);
                    epic.addSubTask(subTask);
                    manager.addToEpics(epic);
                    manager.addToSubTasks(subTask);
                    manager.getTasks().remove(id);
                } else if (manager.getEpics().containsKey(id)) {
                    int subTaskId = manager.genID(epicName + subTaskName);
                    newTaskData = new TaskData(subTaskName, desc, subTaskId);
                    SubTaskData subTask = new SubTaskData(subTaskId, newTaskData.name, newTaskData.description, newTaskData.id);
                    manager.addSubTaskToEpics(subTask);
                    manager.addToSubTasks(subTask);
                } else {
                    System.out.println("Такой задачи нет в списке");
                }

            } else if (command == 3) {
                manager.getAllTasks();
                manager.getAllEpics();
            } else if (command == 4) {
                System.out.println("Вы точо хотите удалить все задачи?");
                String choice = scan.next();

                if (choice.equals("Да") || choice.equals("да")) {
                    manager.deleteAllTasks();
                } else {
                    System.out.println("Задача не была удалена, попробуйте еще раз.");
                }
            } else if (command == 5) {
                System.out.println("1 - Удалить все задачи");
                System.out.println("2 - Удалить все эпики");
                System.out.println("3 - Удалить все подзадачи");
                int choice = scan.nextInt();

                if(choice == 1) {
                    manager.deleteAllEpics();
                } else if(choice == 2) {
                    manager.deleteAllTasks();
                } else if(choice == 3) {
                    manager.deleteAllSubTasks();
                }
            } else if (command == 6) {

            } else if (command == 7) {
                manager.getAllSubTasks();
            } else if (command == 8) {
                System.out.println("Какой задаче вы хотите изменить статус?");
                String taskName = scan.next();
                int id = manager.genID(taskName);
                newTaskData = new TaskData(taskName, manager.getTasks().get(id).description, id);
                manager.updateTask(newTaskData);
            } else if (command == 9) {
                System.out.println("Статус подзадачи какого эпика вы хотите изменить?");
                String epicName = scan.next();
                int epicId = manager.genID(epicName);
                System.out.println("Какой подзадаче нужно изменить статус?");
                String subTaskName = scan.next();
                System.out.println("Введите статус подзадачи");
                System.out.println("1 - В процессе");
                System.out.println("2 - Завершена");
                int status = scan.nextInt();

                newTaskData = new TaskData(subTaskName, null, manager.genID(epicName + subTaskName));
                SubTaskData subTaskData = new SubTaskData(epicId,  newTaskData.name, newTaskData.description, newTaskData.id);

                if (status == 1) {
                    manager.updateSubTask(subTaskData);
                } else {
                    manager.updateSubTask(subTaskData);
                }
            }  else if (command == 10) {
                String taskName = scan.next();
                int taskId = manager.genID(taskName);
                String epicName = scan.next();
                int epicId = manager.genID(epicName);
                String subTaskName = scan.next();
                int subTaskId = manager.genID(subTaskName + epicName);

                manager.getTaskById(taskId);
                manager.getEpicById(epicId);
                manager.getSubTaskById(subTaskId);

            } else {
                System.out.println("Такой команды нет");
            }

            printMenu();
            command = scan.nextInt();
        }


    }

    static void printMenu() {
        System.out.println("Что вы хотите сделать?");
        System.out.println("1 - Создать задачу");
        System.out.println("2 - Добавить подзадачу");
        System.out.println("3 - Выввести список всех задач");
        System.out.println("4 - Удалить все задачи");
        System.out.println("5 - Удаление заданий/эпиков/подзаданий");
        System.out.println("6 - Обновление заданий/эпиков/подзаданий");
        System.out.println("7 - Получить список подзадач эпика");
        System.out.println("8 - Изменить статус задачи");
        System.out.println("9 - Изменить статус подзадачи эпика");
        System.out.println("10 - Взять элементы по id");

        System.out.println("0 - Выход");
    }


}
