package been;


public class WebSocketMsg<T> {

    /**
     * Anno消息
     */
    public static final int WebSocktMsgAnnoMsg = 1;
    /**
     * 发起请假
     */
    public static final int WebSocktMsgLevelMsgCreate = 2;
    /**
     * 请假被审批
     */
    public static final int WebSocktMsgLevelMsgRatify = 3;

    /**
     * 新签到
     */
    public static final int WebSocktMsgLevelMsgPubSign = 3;


    /**
     * 发布奖学金评比
     */
    public static final int WebSocktMsgLevelMsgPubAward = 4;


    /**
     * 有人奖学金评比
     */
    public static final int WebSocktMsgLevelMsgAwardSign = 5;


    /**
     * 奖学金评比被评论
     */
    public static final int WebSocktMsgLevelMsgAwardComment = 6;




    private int action;
    private T data;
    private int fromUid;
    private int toUid;
    private long time;

    public WebSocketMsg(){}

    public WebSocketMsg(int action, T data, int fromUid, int toUid) {
        this.action = action;
        this.data = data;
        this.fromUid = fromUid;
        this.toUid = toUid;
        time = System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
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
