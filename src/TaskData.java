public class TaskData {
    String name;
    String description;
    int id;
    String status;

    TaskData() {
        status = "NEW";
    }

    TaskData(String name, String description, int id, String status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }


}
