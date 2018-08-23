package nl.kleisauke.compactcalendarviewtoolbar;

public class TaskItem {
    private int id;
    private String taskString;
    private String dateString;
    private int isStriked;


    public TaskItem(int id, String taskString, String dateString) {
        this.id = id;
        this.taskString = taskString;
        this.dateString = dateString;
    }

    public TaskItem(String taskString, String dateString) {

        this.taskString = taskString;
        this.dateString = dateString;
    }

    public TaskItem() {
    }

    public int getId() {
        return id;
    }

    public String getTaskString() {
        return taskString;
    }

    public void setTaskString(String taskString) {
        this.taskString = taskString;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    @Override
    public String toString() {
        return "TaskItem{" + "id=" + id + ", taskString='" + taskString + '\'' + ", dateString='" + dateString + '\'' + '}';
    }
}