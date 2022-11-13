public class Main {

    public static void main(String[] args) {
        // ������������
        Manager manager = new Manager();

        // �������� 2 ������
        Task task1 = new Task("task 1", " ������ ������� ������ 1");
        Task task2 = new Task("task 2", " ������ ������� ������ 2");
        long taskId1 = manager.createTask(task1);
        long taskId2 = manager.createTask(task2);

        // ���� ���� � 2 �����������
        Epic epic1 = new Epic("epic 1", "������ ����� 1");
        long epicId1 = manager.createEpic(epic1);
        Subtask subtask1 = new Subtask("subtask 1", "������ ��������� 1", epicId1);
        Subtask subtask2 = new Subtask("subtask 2", "������ ��������� 2", epicId1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        // � ������ ���� � 1 ����������
        Epic epic2 = new Epic("epic 2", "������ ����� 2");
        long epicId2 = manager.createEpic(epic2);
        Subtask subtask3 = new Subtask("subtask 3", "������ ��������� 3", epicId2);
        manager.createSubtask(subtask3);

        // ������������ ������ ������, ����� � ��������, ����� System.out.println(..)

        printout(manager);

        System.out.println();

        // �������� ������� ��������� ��������, ������������.
        // ���������, ��� ������ ������ � ��������� ����������,
        // � ������ ����� ����������� �� �������� ��������.

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

        System.out.println("��������� ��� ��� Task � ������� Done, ��� Subtask � ������� InProgress, ��� Epic � ������ InProgress");
        printout(manager);

        System.out.println();

        // �, �������, ���������� ������� ���� �� ����� � ���� �� ������
        System.out.println("������� Task1 � Epic1");
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
            for(long id: epic.getSubtasks()){
                Subtask subtask = manager.getSubtask(id);
                System.out.printf("%9d %9s %15s %s\n",
                        subtask.getId(),subtask.getStateName(),subtask.getName(),subtask.getDescription());
            }
        }
        System.out.println();
    }
}
