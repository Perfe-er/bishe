package com.example.zwq.assistant.been;

import java.util.Date;

public class ActSign {

    private int actSignID;
    private int receiveID;
    private Date signDate;
    private int id;
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

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }


    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
