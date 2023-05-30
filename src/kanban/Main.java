package kanban;

import kanban.manager.Manager;
import kanban.manager.TaskManager;
import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.model.TaskState;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        testCase3(); // выполняем только новый сценарий тестирования
    }

    // сценарий тестирования по ТЗ-3
    private static void testCase1() {
        // тестирование
        TaskManager manager = Manager.getDefault();

        // Создайте 2 задачи
        Task task1 = new Task("task 1", "пример простой задачи 1");
        Task task2 = new Task("task 2", "пример простой задачи 2");
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

        for (Task task: manager.getAllTasks()) {
            task.setState(TaskState.DONE);
            manager.updateTask(task);
        }

        for (Subtask subtask: manager.getAllSubtasks()) {
            subtask.setState(TaskState.IN_PROGRESS);
            manager.updateSubtask(subtask);
        }

        for (Epic epic: manager.getAllEpics()) {
            epic.setState(TaskState.DONE);
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

    // сценарий тестирования по ТЗ-4
    private static void testCase2() {
        // тестирование
        TaskManager manager = Manager.getDefault();

        // создайте несколько задач разного типа

        // 2 задачи
        Task task1 = new Task("task 1", "пример простой задачи 1");
        Task task2 = new Task("task 2", "пример простой задачи 2");
        long taskId1 = manager.createTask(task1);
        long taskId2 = manager.createTask(task2);

        // один эпик с 2 подзадачами
        Epic epic1 = new Epic("epic 1", "пример эпика 1");
        long epicId1 = manager.createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask 1", "пример подзадачи 1", epicId1);
        Subtask subtask2 = new Subtask("subtask 2", "пример подзадачи 2", epicId1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        long subtaskId1 = subtask1.getId();
        long subtaskId2 = subtask2.getId();

        // эпик с 1 подзадачей
        Epic epic2 = new Epic("epic 2", "пример эпика 2");
        long epicId2 = manager.createEpic(epic2);
        Subtask subtask3 = new Subtask("subtask 3", "пример подзадачи 3", epicId2);
        manager.createSubtask(subtask3);
        long subtaskId3 = subtask3.getId();

        // вызовите разные методы интерфейса TaskManager
        // и напечатайте историю просмотров после каждого вызова

        // empty history
        System.out.println("--- History 1 ---\tдолжно быть записей: 0");
        printoutList(manager.getHistory());

        // + task1
        manager.getTask(taskId1);

        System.out.println("--- History 2 ---\tдолжно быть записей: 1");
        printoutList(manager.getHistory());

        // + epic1
        manager.getEpic(epicId1);

        System.out.println("--- History 3 ---\tдолжно быть записей: 2");
        printoutList(manager.getHistory());

        // + epic2 + epic1
        manager.getEpic(epicId2);
        manager.getEpic(epicId1);

        System.out.println("--- History 4 ---\tдолжно быть записей: 4");
        printoutList(manager.getHistory());

        // + subtask1 + subtask3
        manager.getSubtask(subtaskId1);
        manager.getSubtask(subtaskId3);

        System.out.println("--- History 5 ---\tдолжно быть записей: 6");
        printoutList(manager.getHistory());

        // + task2 + subtask1 + subtask2 + epic2  total: 10 tasks
        manager.getTask(taskId2);
        manager.getSubtask(subtaskId1);
        manager.getSubtask(subtaskId2);
        manager.getEpic(epicId2);

        System.out.println("--- History 6 ---\tдолжно быть записей: 10");
        printoutList(manager.getHistory());

        // + epic1 -task1 - удаляется из списка так как ограничение на 10 элементов в списке
        manager.getEpic(epicId1);

        System.out.println("--- History 7 ---\tдолжно быть записей: 10");
        printoutList(manager.getHistory());
    }

    // сценарий тестирования по ТЗ-5
    private static void testCase3() {
        // тестирование
        TaskManager manager = Manager.getDefault();

//        Task task1 = new Task("task 1", "пример простой задачи 1");

        // один эпик с 3 подзадачами
        Epic epic1 = new Epic("epic 1", "пример эпика 1");
        long epicId1 = manager.createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask 1", "пример подзадачи 1", epicId1);
        Subtask subtask2 = new Subtask("subtask 2", "пример подзадачи 2", epicId1);
        Subtask subtask3 = new Subtask("subtask 3", "пример подзадачи 3", epicId1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        long subtaskId1 = subtask1.getId();
        long subtaskId2 = subtask2.getId();
        long subtaskId3 = subtask3.getId();

        // эпик без подзадач
        Epic epic2 = new Epic("epic 2", "пример эпика 2");
        long epicId2 = manager.createEpic(epic2);

        /*
        запросите созданные задачи несколько раз в разном порядке;
        после каждого запроса выведите историю и убедитесь, что в ней нет повторов;
        */

        manager.getSubtask(subtaskId2);
        System.out.println("must be: 'subtask 2'");
        printoutList(manager.getHistory());

        manager.getEpic(epicId2);
        System.out.println("must be: 'subtask 2', 'epic 2'");
        printoutList(manager.getHistory());

        manager.getEpic(epicId1);
        System.out.println("must be: 'subtask 2', 'epic 2', 'epic 1'");
        printoutList(manager.getHistory());

        manager.getSubtask(subtaskId1);
        System.out.println("must be: 'subtask 2', 'epic 2', 'epic 1', 'subtask 1'");
        printoutList(manager.getHistory());

        manager.getSubtask(subtaskId2);
        System.out.println("must be: 'epic 2', 'epic 1', 'subtask 1', 'subtask 2'");
        printoutList(manager.getHistory());

        manager.getEpic(epicId1);
        System.out.println("must be: 'epic 2', 'subtask 1', 'subtask 2', 'epic 1'");
        printoutList(manager.getHistory());

        // удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться
        manager.removeSubtask(subtaskId2);
        System.out.println("must be: 'epic 2', 'subtask 1', 'epic 1'");
        printoutList(manager.getHistory());

        // удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик,
        // так и все его подзадачи
        manager.removeEpic(epicId1);
        System.out.println("must be: 'epic 2'");
        printoutList(manager.getHistory());


    }


    // выводим список задач с указанием типа
    private static void printoutList(List<Task> list) {
        System.out.printf("%7s %4s %9s %15s %s\n","type", "id","state","name","description");
        for (Task task: list) {
            System.out.printf("%7s %4d %9s %15s %s\n",
                    task.getClass().getSimpleName(),
                    task.getId(),task.getState(),task.getName(),task.getDescription());
        }

        System.out.println();
    }

    // выводим все списки задач менеджера по типам
    private static void printout(TaskManager manager) {
        System.out.println("--- Tasks ---");
        System.out.printf("%12s %9s %15s %s\n","id","state","name","description");
        for (Task task: manager.getAllTasks()) {
            System.out.printf("%12d %9s %15s %s\n",
                    task.getId(),task.getState(),task.getName(),task.getDescription());
        }

        System.out.println();

        System.out.println("--- Epics ---");
        System.out.printf("%12s %9s %15s %s\n","id","state","name","description");
        for (Epic epic: manager.getAllEpics()) {
            System.out.printf("epic %7d %9s %15s %s\n",
                    epic.getId(),epic.getState(),epic.getName(),epic.getDescription());
            for (Subtask subtask: epic.getSubtasks()) {
                //Subtask subtask = manager.getSubtask(id);
                System.out.printf("%12d %9s %15s %s\n",
                        subtask.getId(),subtask.getState(),subtask.getName(),subtask.getDescription());
            }
        }
        System.out.println();
    }
}