package com.example.zwq.assistant.been;

public class WebSocketMsg<T> {

    /**
     * Anno消息
     */
    public static final int WebSocktMsgAnnoMsg = 1;

    private int action;
    private T data;
    private int fromUid;
    private int toUid;

    public WebSocketMsg(){}

    public WebSocketMsg(int action, T data, int fromUid, int toUid) {
        this.action = action;
        this.data = data;
        this.fromUid = fromUid;
        this.toUid = toUid;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getFromUid() {
        return fromUid;
    }

    public void setFromUid(int fromUid) {
        this.fromUid = fromUid;
    }

    public int getToUid() {
        return toUid;
    }

    public void setToUid(int toUid) {
        this.toUid = toUid;
    }
}
