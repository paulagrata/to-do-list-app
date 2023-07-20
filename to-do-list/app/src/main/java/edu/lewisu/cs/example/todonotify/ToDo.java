package edu.lewisu.cs.example.todonotify;

public class ToDo {
    private String uid;
    private String title;
    private int priority;
    private boolean complete;

    public ToDo() {
    }

    public ToDo(String uid) {
        this.uid = uid;
    }

    public ToDo(String uid, String title, int priority, boolean complete) {
        this.uid = uid;
        this.title = title;
        this.priority = priority;
        this.complete = complete;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
