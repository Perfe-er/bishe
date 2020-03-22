package been;

import online.sanen.cdm.template.jpa.Id;
import online.sanen.cdm.template.jpa.NoInsert;
import online.sanen.cdm.template.jpa.Priority;
import online.sanen.cdm.template.jpa.Table;

@Table(name = "awardsign")
@Priority
public class AwardSign {
    @Id
    @NoInsert
    private int awardSignID;
    private int awardsRecID;
    private int id;
    private String word;
    private String evaluation;
    private int pass;   //1：通过，2：未通过

    public int getAwardSignID() {
        return awardSignID;
    }

    public void setAwardSignID(int awardSignID) {
        this.awardSignID = awardSignID;
    }

    public int getAwardsRecID() {
        return awardsRecID;
    }

    public void setAwardsRecID(int awardsRecID) {
        this.awardsRecID = awardsRecID;
    }


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public int getPass() {
        return pass;
    }

    public void setPass(int pass) {
        this.pass = pass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
