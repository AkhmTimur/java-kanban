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

                int id = manager.genID(taskName, desc);

                newTaskData = new TaskData(taskName, desc, id, "NEW");
                if (desc != null) {
                    System.out.println("Задача " + taskName + " создана. Описание - " + desc);
                    manager.addToTasks(newTaskData.name, newTaskData);
                }
            } else if (command == 2) {
                System.out.println("Какую задачу вам нужно дополнить подзадачей?");
                String epicName = scan.next();
                System.out.println("Какую подзадачу добавить?");
                String subTaskName = scan.next();
                System.out.println("Добавьте описание подзадаче");
                String desc = scan.next();

                if (manager.tasks.containsKey(epicName)) {
                    TaskData newData = new TaskData(subTaskName, desc, manager.genID(subTaskName, desc), manager.tasks.get(epicName).name);
                    SubTaskData subTask = new SubTaskData(epicName, newData);
                    EpicData epic = new EpicData(epicName, subTask);
                    epic.addSubTask(subTask);
                    manager.addToEpics(epicName, epic);
                    if (manager.tasks.containsKey(epicName)) {
                        manager.tasks.remove(epicName);
                    }
                } else if(manager.epics.containsKey(epicName) ) {
                    TaskData newData = new TaskData(subTaskName, desc, manager.genID(subTaskName, desc), "NEW");
                    SubTaskData subTask = new SubTaskData(epicName, newData);
                    manager.addSubTaskToEpics(manager.epics.get(epicName).id, subTask);
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
                    manager.clearTasks();
                } else {
                    System.out.println("Задача не была удалена, попробуйте еще раз.");
                }
            } else if (command == 5) {
                System.out.println(manager.getOrDeleteById("get"));
            } else if (command == 6) {
                System.out.println(manager.getOrDeleteById("delete"));
            } else if (command == 7) {
                System.out.println("По какому эпику выхотите получить информацию");
                String epicName = scan.next();
                manager.getSubTasks(epicName);
            } else if (command == 8) {
                System.out.println("Какой задаче вы хотите изменить статус?");
                String taskName = scan.next();
                System.out.println("Какой статус вы хотите присвоить задаче");
                System.out.println("1 - В процессе");
                System.out.println("2 - Завершена");
                int status = scan.nextInt();
                manager.setTaskStatus(taskName, status);
            } else if (command == 9) {
                System.out.println("У какого эпика вы хотите изменить статус");
                String epicName = scan.next();
                System.out.println("Какой подзадаче нужно изменить статус?");
                String subTaskStatus = scan.next();
                System.out.println("Введите статус подзадачи");
                System.out.println("1 - В процессе");
                System.out.println("2 - Завершена");
                int status = scan.nextInt();
                if (status == 1) {
                    manager.setSubTaskStatus(epicName, subTaskStatus, "IN PROGRESS");
                } else {
                    manager.setSubTaskStatus(epicName, subTaskStatus, "DONE");
                }
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
        System.out.println("5 - Получение по ID");
        System.out.println("6 - Удаление по ID");
        System.out.println("7 - Получить список подзадач эпика");
        System.out.println("8 - Изменить статус задачи");
        System.out.println("9 - Изменить статус подзадачи эпика");

        System.out.println("0 - Выход");
    }


}
