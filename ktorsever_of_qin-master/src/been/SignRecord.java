package been;

import online.sanen.cdm.template.jpa.*;

@Table(name = "signrecord")
@Priority
public class SignRecord {
    @Id
    @NoInsert
    private int signRecordId;
    private int uid;

    private int signStatus;  //1：已签到，2：未签到
    private long signDate;

    private int signId;

    @NoDB
    private Sign sign;


    public int getSignRecordId() {
        return signRecordId;
    }

    public void setSignRecordId(int signRecordId) {
        this.signRecordId = signRecordId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(int signStatus) {
        this.signStatus = signStatus;
    }

    public long getSignDate() {
        return signDate;
    }

    public void setSignDate(long signDate) {
        this.signDate = signDate;
    }

    public int getSignId() {
        return signId;
    }

    public void setSignId(int signId) {
        this.signId = signId;
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }
}
