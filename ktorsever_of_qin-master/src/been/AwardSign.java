package been;

import online.sanen.cdm.template.jpa.*;

@Table(name = "awardsign")
@Priority
public class AwardSign {
    @Id
    @NoInsert
    private int awardSignID;
    private int awardsPubId;
    private int uid;
    private String word;
    private int pass;   //1：通过，2：未通过

    @NoDB
    private AwardsPub awardsPub; //奖学金发布对应的信息



    public AwardsPub getAwardsPub() {
        return awardsPub;
    }

    public void setAwardsPub(AwardsPub awardsPub) {
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
}
