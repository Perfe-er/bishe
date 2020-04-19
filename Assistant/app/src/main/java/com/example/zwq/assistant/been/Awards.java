package com.example.zwq.assistant.been;

public class Awards {

    private int awardsID;
    private int releaseID;
    private String awardsTitle;
    private String awardsContent;
    private String word;
    private long startTime;
    private long endTime;

    public int getAwardsID() {
        return awardsID;
    }

    public void setAwardsID(int awardsID) {
        this.awardsID = awardsID;
    }

    public String getAwardsTitle() {
        return awardsTitle;
    }

    public void setAwardsTitle(String awardsTitle) {
        this.awardsTitle = awardsTitle;
    }

    public String getAwardsContent() {
        return awardsContent;
    }

    public void setAwardsContent(String awardsContent) {
        this.awardsContent = awardsContent;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getReleaseID() {
        return releaseID;
    }

    public void setReleaseID(int releaseID) {
        this.releaseID = releaseID;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
