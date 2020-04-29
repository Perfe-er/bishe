package been;

import online.sanen.cdm.template.jpa.Id;
import online.sanen.cdm.template.jpa.NoInsert;
import online.sanen.cdm.template.jpa.Priority;
import online.sanen.cdm.template.jpa.Table;

@Table(name = "moralreceive")
@Priority
public class MoralReceive {
    @Id
    @NoInsert
    private int moralReceiveID;
    private int moralID;
    private double number;
    private int classID;
    private int stuID;

    public int getMoralReceiveID() {
        return moralReceiveID;
    }

    public void setMoralReceiveID(int moralReceiveID) {
        this.moralReceiveID = moralReceiveID;
    }

    public int getMoralID() {
        return moralID;
    }

    public void setMoralID(int moralID) {
        this.moralID = moralID;
    }


    public int getStuID() {
        return stuID;
    }

    public void setStuID(int stuID) {
        this.stuID = stuID;
    }

    public double getNumber() {
        return number;
    }

    public void setNumber(double number) {
        this.number = number;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }
}
