package com.example.zwq.assistant.been;

public class Class {

    private int classID;
    private String className;
    private int founderID;

    public Class(String className, int founderID) {
        this.className = className;
        this.founderID = founderID;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    public int getFounderID() {
        return founderID;
    }

    public void setFounderID(int founderId) {
        this.founderID = founderId;
    }
}
