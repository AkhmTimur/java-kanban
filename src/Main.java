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

                newTaskData = new TaskData(taskName, desc);
                if (desc != null) {
                    System.out.println("Задача " + taskName + " создана. Описание - " + desc);
                    manager.addToTasks(newTaskData);
                }
            } else if (command == 2) {
                System.out.println("Какую задачу вам нужно дополнить подзадачей?");
                String epicName = scan.next();
                System.out.println("Какую подзадачу добавить?");
                String subTaskName = scan.next();
                System.out.println("Добавьте описание подзадаче");
                String desc = scan.next();

                if (!manager.getAllTasks().contains(epicName)) {
                    newTaskData = new TaskData(subTaskName, desc);
                    SubTaskData subTask = new SubTaskData(newTaskData.name, newTaskData.description);
                    EpicData epic = new EpicData(epicName, subTask.description, "NEW");
                    epic.addSubTask(subTask);
                    manager.addToEpics(epic);
                    manager.addToSubTasks(subTask);

                    for(int i = 0; i < manager.getAllTasks().size(); i++) {
                        if(manager.getAllTasks().get(i).name == epicName) {
                            manager.updateTask(manager.getAllTasks().get(i));
                        }
                    }
                } else if (!manager.getAllEpics().contains(epicName)) {
                    newTaskData = new TaskData(subTaskName, desc);
                    SubTaskData subTask = new SubTaskData(newTaskData.name, newTaskData.description);
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
                System.out.println("1 - Удалить задачу");
                System.out.println("2 - Удалить эпик");
                System.out.println("3 - Удалить подзадачу");
                int choice = scan.nextInt();
                System.out.println("Введите название задачи/эпика/подзадачи");

                if(choice == 1) {
                    for (int i = 0; i < manager.getAllTasks().size(); i++) {
                        if(manager.getAllTasks().get(i).name == scan.next()) {
                            manager.deleteTaskById(manager.getAllTasks().get(i).id);
                        }
                    }
                } else if(choice == 2) {
                    for (int i = 0; i < manager.getAllTasks().size(); i++) {
                        if(manager.getAllTasks().get(i).name == scan.next()) {
                            manager.deleteEpicById(manager.getAllTasks().get(i).id);
                        }
                    }
                } else if(choice == 3) {
                    for (int i = 0; i < manager.getAllTasks().size(); i++) {
                        if(manager.getAllTasks().get(i).name == scan.next()) {
                            manager.deleteSubTaskById(manager.getAllTasks().get(i).id);
                        }
                    }
                }
            } else if (command == 7) {
                manager.getAllSubTasks();
            } else if (command == 8) {
                System.out.println("Какой задаче вы хотите изменить статус?");
                String taskName = scan.next();
                String desc = "";

                for (TaskData task : manager.getAllTasks()) {
                    if(taskName == task.name) {
                        desc = task.description;
                    } else {
                        break;
                    }
                }
                newTaskData = new TaskData(taskName, desc);
                manager.updateTask(newTaskData);
            } else if (command == 9) {
                System.out.println("Какой подзадаче нужно изменить статус?");
                String subTaskName = scan.next();
                System.out.println("Введите статус подзадачи");
                System.out.println("1 - В процессе");
                System.out.println("2 - Завершена");
                int status = scan.nextInt();

                newTaskData = new TaskData(subTaskName, null);
                SubTaskData subTaskData = new SubTaskData(newTaskData.name, newTaskData.description);

                if (status == 1) {
                    manager.updateSubTask(subTaskData);
                } else {
                    manager.updateSubTask(subTaskData);
                }
            }  else if (command == 10) {

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
