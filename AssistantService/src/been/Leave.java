package been;

import online.sanen.cdm.template.jpa.Id;
import online.sanen.cdm.template.jpa.NoInsert;
import online.sanen.cdm.template.jpa.Priority;
import online.sanen.cdm.template.jpa.Table;

@Table(name = "leave")
@Priority
public class Leave {
    @Id
    @NoInsert
    private int leaveID;
    private int stuID;
    private String name;
    private int sex;  //1：女，2：男
    private String reason;
    private int ratify;  //1：批准，2：不批准
    private long startDate;
    private long endDate;
    private int ratifyID;
    private int classID;



    public int getStuID() {
        return stuID;
    }

    public void setStuID(int stuID) {
        this.stuID = stuID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getRatify() {
        return ratify;
    }

    public void setRatify(int ratify) {
        this.ratify = ratify;
    }


    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getRatifyID() {
        return ratifyID;
    }

    public void setRatifyID(int ratifyID) {
        this.ratifyID = ratifyID;
    }

    public int getLeaveID() {
        return leaveID;
    }

    public void setLeaveID(int leaveID) {
        this.leaveID = leaveID;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }
}
