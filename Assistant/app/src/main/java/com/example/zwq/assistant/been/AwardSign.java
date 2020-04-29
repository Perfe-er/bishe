package com.example.zwq.assistant.been;


public class AwardSign {

    private int awardSignID;
    private int awardsPubId;
    private int uid;
    private String word;
    private long date;
    private int pass;   //1：通过，2：未通过

    private Awards awardsPub; //奖学金发布对应的信息



    public Awards getAwardsPub() {
        return awardsPub;
    }

    public void setAwardsPub(Awards awardsPub) {
        this.awardsPub = awardsPub;
    }

    public int getAwardSignID() {
        return awardSignID;
    }

    public void setAwardSignID(int awardSignID) {
        this.awardSignID = awardSignID;
    }

    public int getAwardsPubId() {
        return awardsPubId;
    }

    public void setAwardsPubId(int awardsPubId) {
        this.awardsPubId = awardsPubId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getPass() {
        return pass;
    }

    public void setPass(int pass) {
        this.pass = pass;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
