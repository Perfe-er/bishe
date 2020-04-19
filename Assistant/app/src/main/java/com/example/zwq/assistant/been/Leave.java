package com.example.zwq.assistant.been;

public class Leave {

    private int classID;
    private int stuID;
    private String name;
    private int sex;  //1：女，2：男
    private String reason;
    private int ratify;  //1：批准，2：不批准
    private long endDate;
    private int id;
    private int leaveID;
    private long startDate;


    public int getStuID() {
        return stuID;
    }

    public void setStuID(int stuID) {
        this.stuID = stuID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getRatify() {
        return ratify;
    }

    public void setRatify(int ratify) {
        this.ratify = ratify;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeaveID() {
        return leaveID;
    }

    public void setLeaveID(int leaveID) {
        this.leaveID = leaveID;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
    public long getStartDate() {
        return startDate;
    }

    public void getStartDate(long startDate) {
        this.startDate = startDate;
    }
}
