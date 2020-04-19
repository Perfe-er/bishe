package com.example.zwq.assistant.been;


import java.util.List;

public class Activity {

    private int actID;
    private String actTitle;
    private String actContent;
    private int actFouID;
    private long actDate;
    private List<String> classIDs;

    public int getActID() {
        return actID;
    }

    public void setActID(int actID) {
        this.actID = actID;
    }

    public String getActTitle() {
        return actTitle;
    }

    public void setActTitle(String actTitle) {
        this.actTitle = actTitle;
    }

    public String getActContent() {
        return actContent;
    }

    public void setActContent(String actContent) {
        this.actContent = actContent;
    }


    public int getActFouID() {
        return actFouID;
    }

    public void setActFouID(int actFouID) {
        this.actFouID = actFouID;
    }

    public List<String> getClassIDs() {
        return classIDs;
    }

    public void setClassIDs(List<String> classIDs) {
        this.classIDs = classIDs;
    }

    public long getActDate() {
        return actDate;
    }

    public void setActDate(long actDate) {
        this.actDate = actDate;
    }
}
