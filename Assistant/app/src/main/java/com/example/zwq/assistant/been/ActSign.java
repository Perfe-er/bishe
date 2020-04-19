package com.example.zwq.assistant.been;

public class ActSign {

    private int actSignID;
    private int receiveID;
    private long signDate;
    private int stuID;
    private int classID;
    private int sign;  //1：报名，2：未报名

    public int getActSignID() {
        return actSignID;
    }

    public void setActSignID(int actSignID) {
        this.actSignID = actSignID;
    }

    public int getReceiveID() {
        return receiveID;
    }

    public void setReceiveID(int receiveID) {
        this.receiveID = receiveID;
    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }


    public long getSignDate() {
        return signDate;
    }

    public void setSignDate(long signDate) {
        this.signDate = signDate;
    }

    public int getStuID() {
        return stuID;
    }

    public void setStuID(int stuID) {
        this.stuID = stuID;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }
}
