class Managers {

    static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    static HistoryManager getHistoryDefault() {
        return new InMemoryHistoryManager();
    }
}
