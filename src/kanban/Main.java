package kanban;

import kanban.manager.Manager;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;

public class Main {

    public static void main(String[] args) {
        // тестирование
        Manager manager = new Manager();

        // Создайте 2 задачи
        Task task1 = new Task("task 1", " пример простой задачи 1");
        Task task2 = new Task("task 2", " пример простой задачи 2");
        long taskId1 = manager.createTask(task1);
        long taskId2 = manager.createTask(task2);

        // один эпик с 2 подзадачами
        Epic epic1 = new Epic("epic 1", "пример эпика 1");
        long epicId1 = manager.createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask 1", "пример подзадачи 1", epicId1);
        Subtask subtask2 = new Subtask("subtask 2", "пример подзадачи 2", epicId1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        // а другой эпик с 1 подзадачей
        Epic epic2 = new Epic("epic 2", "пример эпика 2");
        long epicId2 = manager.createEpic(epic2);
        Subtask subtask3 = new Subtask("subtask 3", "пример подзадачи 3", epicId2);
        manager.createSubtask(subtask3);

        // Распечатайте списки эпиков, задач и подзадач, через System.out.println(..)

        printout(manager);

        System.out.println();

        // Измените статусы созданных объектов, распечатайте.
        // Проверьте, что статус задачи и подзадачи сохранился,
        // а статус эпика рассчитался по статусам подзадач.

        for(Task task: manager.getAllTasks()){
            task.setState(Task.DONE);
            manager.updateTask(task);
        }

        for(Subtask subtask: manager.getAllSubtasks()){
            subtask.setState(Task.IN_PROGRESS);
            manager.updateSubtask(subtask);
        }

        for(Epic epic: manager.getAllEpics()){
            epic.setState(Task.DONE);
            manager.updateEpic(epic);
        }

        System.out.println("Проверяем что все Task в статусе Done, все Subtask в статусе InProgress, все Epic в стадии InProgress");
        printout(manager);

        System.out.println();

        // И, наконец, попробуйте удалить одну из задач и один из эпиков
        System.out.println("Удаляем Task1 и Epic1");
        manager.removeTask(taskId1);
        manager.removeEpic(epicId1);

        printout(manager);
    }

    private static void printout(Manager manager) {
        System.out.println("--- Tasks ---");
        System.out.printf("%9s %9s %15s %s\n","id","state","name","description");
        for(Task task: manager.getAllTasks()){
            System.out.printf("%9d %9s %15s %s\n",
                    task.getId(),task.getStateName(),task.getName(),task.getDescription());
        }

        System.out.println();

        System.out.println("--- Epics ---");
        System.out.printf("%9s %9s %15s %s\n","id","state","name","description");
        for(Epic epic: manager.getAllEpics()){
            System.out.printf("epic %4d %9s %15s %s\n",
                    epic.getId(),epic.getStateName(),epic.getName(),epic.getDescription());
            for(long id: epic.getSubtaskIds()){
                Subtask subtask = manager.getSubtask(id);
                System.out.printf("%9d %9s %15s %s\n",
                        subtask.getId(),subtask.getStateName(),subtask.getName(),subtask.getDescription());
            }
        }
        System.out.println();
    }
}
