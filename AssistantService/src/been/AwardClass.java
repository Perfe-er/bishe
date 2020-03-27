package been;

import online.sanen.cdm.template.jpa.Id;
import online.sanen.cdm.template.jpa.NoInsert;
import online.sanen.cdm.template.jpa.Priority;
import online.sanen.cdm.template.jpa.Table;

@Table(name = "awardclass")
@Priority
public class AwardClass {
    @Id
    @NoInsert
    private int awardsRecID;
    private int awardsID;
    private int classID;

    public int getAwardsRecID() {
        return awardsRecID;
    }

    public void setAwardsRecID(int awardsRecID) {
        this.awardsRecID = awardsRecID;
    }

    public int getAwardsID() {
        return awardsID;
    }

    public void setAwardsID(int awardsID) {
        this.awardsID = awardsID;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }
}
