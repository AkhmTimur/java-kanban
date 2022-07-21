import dataClasses.EpicData;
import dataClasses.SubTaskData;
import dataClasses.TaskData;
import enums.Statuses;
import interfaces.HistoryManager;
import interfaces.TaskManager;

import java.util.Scanner;

import managers.Managers;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        TaskManager inMemoryTaskManager = Managers.getDefault();
        HistoryManager inMemoryHistoryManager = Managers.getHistoryDefault();

        TaskData newTaskData = new TaskData("Победить в чемпионате по поеданию бургеров", "Нужно тренироваться, едим бургеры!");
        TaskData newTaskData1 = new TaskData("Пробежать марафон", "Попробовать свои силы на марафоне который будет осенью");
        TaskData newTaskData2 = new TaskData("Съездить в отпуск", "На марс");
        TaskData newTaskData3 = new TaskData("Съездить в отпуск", "На марс");
        TaskData newTaskData4 = new TaskData("Съездить в отпуск", "На марс");
        TaskData newTaskData5 = new TaskData("Съездить в отпуск", "На марс");
        TaskData newTaskData6 = new TaskData("Съездить в отпуск", "На марс");
        TaskData newTaskData7 = new TaskData("Съездить в отпуск", "На марс");
        TaskData newTaskData8 = new TaskData("Съездить в отпуск", "На марс");
        TaskData newTaskData9 = new TaskData("Съездить в отпуск", "На марс");

        printMenu();
        int command = scan.nextInt();

        while (command != 0) {
            if (command == 1) {
                inMemoryTaskManager.addToTasks(newTaskData);
                inMemoryTaskManager.addToTasks(newTaskData1);
                inMemoryTaskManager.addToTasks(newTaskData2);
                inMemoryTaskManager.addToTasks(newTaskData3);
                inMemoryTaskManager.addToTasks(newTaskData4);
                inMemoryTaskManager.addToTasks(newTaskData5);
                inMemoryTaskManager.addToTasks(newTaskData6);
                inMemoryTaskManager.addToTasks(newTaskData7);
                inMemoryTaskManager.addToTasks(newTaskData8);
                inMemoryTaskManager.addToTasks(newTaskData9);
                inMemoryTaskManager.addToTasks(newTaskData9);

            } else if (command == 2) {
                String epicName = "Победить в чемпионате по поеданию бургеров";
                String subTaskName = "Поддерживать здоровье";
                String desc = "Не забываем двигаться, нужно гулять!";

                newTaskData = new TaskData(subTaskName, desc);
                SubTaskData subTask = new SubTaskData(newTaskData.getName(), newTaskData.getDescription());
                EpicData epic = new EpicData(epicName, subTask.getDescription(), Statuses.NEW);
                int epicId = inMemoryTaskManager.addToEpics(epic);
                subTask.setEpicId(epicId);
                inMemoryTaskManager.addToSubTasks(subTask);
                epic.addSubTask(subTask);

                for (int i = 0; i < inMemoryTaskManager.getAllTasks().size(); i++) {
                    if (inMemoryTaskManager.getAllTasks().get(i).getName().equals(epicName)) {
                        inMemoryTaskManager.updateTask(inMemoryTaskManager.getAllTasks().get(i));
                    }
                }

                for (EpicData epicItem : inMemoryTaskManager.getAllEpics()) {
                    if (!epicItem.getName().equals(epicName)) {
                        newTaskData = new TaskData(subTaskName, desc);
                        SubTaskData newSubTask = new SubTaskData(newTaskData.getName(), newTaskData.getDescription());
                        inMemoryTaskManager.addToSubTasks(newSubTask);
                    } else {
                        System.out.println("Такой задачи нет в списке");
                    }
                }
            } else if (command == 3) {
                inMemoryTaskManager.getAllTasks();
                inMemoryTaskManager.getAllEpics();
            } else if (command == 4) {
                System.out.println("Вы точо хотите удалить все задачи?");
                String choice = scan.next();

                if (choice.equals("Да") || choice.equals("да")) {
                    inMemoryTaskManager.deleteAllTasks();
                } else {
                    System.out.println("Задача не была удалена, попробуйте еще раз.");
                }
            } else if (command == 5) {
                System.out.println("1 - Удалить все задачи");
                System.out.println("2 - Удалить все эпики");
                System.out.println("3 - Удалить все подзадачи");
                int choice = scan.nextInt();

                if (choice == 1) {
                    inMemoryTaskManager.deleteAllEpics();
                } else if (choice == 2) {
                    inMemoryTaskManager.deleteAllTasks();
                } else if (choice == 3) {
                    inMemoryTaskManager.deleteAllSubTasks();
                }
            } else if (command == 6) {
                System.out.println("1 - Удалить задачу");
                System.out.println("2 - Удалить эпик");
                System.out.println("3 - Удалить подзадачу");
                int choice = scan.nextInt();
                System.out.println("Введите название задачи/эпика/подзадачи");

                if (choice == 1) {
                    for (int i = 0; i < inMemoryTaskManager.getAllTasks().size(); i++) {
                        if (inMemoryTaskManager.getAllTasks().get(i).getName().equals(scan.next())) {
                            inMemoryTaskManager.deleteTaskById(inMemoryTaskManager.getAllTasks().get(i).getId());
                        }
                    }
                } else if (choice == 2) {
                    for (int i = 0; i < inMemoryTaskManager.getAllTasks().size(); i++) {
                        if (inMemoryTaskManager.getAllTasks().get(i).getName().equals(scan.next())) {
                            inMemoryTaskManager.deleteEpicById(inMemoryTaskManager.getAllTasks().get(i).getId());
                        }
                    }
                } else if (choice == 3) {
                    for (int i = 0; i < inMemoryTaskManager.getAllTasks().size(); i++) {
                        if (inMemoryTaskManager.getAllTasks().get(i).getName().equals(scan.next())) {
                            inMemoryTaskManager.deleteSubTaskById(inMemoryTaskManager.getAllTasks().get(i).getId());
                        }
                    }
                }
            } else if (command == 7) {
                inMemoryTaskManager.getAllSubTasks();
            } else if (command == 8) {
                System.out.println("Какой задаче вы хотите изменить статус?");
                String taskName = scan.next();
                String desc = "";

                for (TaskData task : inMemoryTaskManager.getAllTasks()) {
                    if (taskName.equals(task.getName())) {
                        desc = task.getDescription();
                    } else {
                        break;
                    }
                }
                newTaskData = new TaskData(taskName, desc);
                inMemoryTaskManager.updateTask(newTaskData);
            } else if (command == 9) {
                System.out.println("Какой подзадаче нужно изменить статус?");
                String subTaskName = scan.next();
                System.out.println("Введите статус подзадачи");
                System.out.println("1 - В процессе");
                System.out.println("2 - Завершена");
                int status = scan.nextInt();

                newTaskData = new TaskData(subTaskName, null);
                SubTaskData subTaskData = new SubTaskData(newTaskData.getName(), newTaskData.getDescription());

                if (status == 1) {
                    inMemoryTaskManager.updateSubTask(subTaskData);
                } else {
                    inMemoryTaskManager.updateSubTask(subTaskData);
                }
            } else if (command == 10) {

                System.out.println("1 - Получить задачу");
                System.out.println("2 - Получить эпик");
                System.out.println("3 - Получить подзадачу");
                int type = scan.nextInt();

                if (type == 1) {
                    for (int i = 0; i < 10; i++) {
                        inMemoryTaskManager.getTaskById(i);
                        for (TaskData histItem : inMemoryHistoryManager.getHistory()) {
                            System.out.println((i + 1) + ": " + histItem.getName() + "id: " + histItem.getId());
                        }
                    }
                } else if (type == 2) {
                    int id = scan.nextInt();
                    inMemoryTaskManager.getEpicById(id);
                    inMemoryHistoryManager.getHistory();
                } else if (type == 3) {
                    int id = scan.nextInt();
                    inMemoryTaskManager.getSubTaskById(id);
                }


            } else {
                System.out.println("Такой команды нет");
            }

            printMenu();
            command = scan.nextInt();
        }
    }

    private static void printMenu() {
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
