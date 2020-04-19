package com.example.zwq.assistant.been;


import java.util.List;

public class Moral {

    private int moralID;
    private double add;
    private double fine;
    private int changeP;
    private String reason;
    private long dateTime;
    private List<String> ids;

    public int getChangeP() {
        return changeP;
    }

    public void setChangeP(int changeP) {
        this.changeP = changeP;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getMoralID() {
        return moralID;
    }

    public void setMoralID(int moralID) {
        this.moralID = moralID;
    }

    public double getAdd() {
        return add;
    }

    public void setAdd(double add) {
        this.add = add;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }
}
