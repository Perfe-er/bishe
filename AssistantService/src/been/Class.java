package been;

import online.sanen.cdm.template.jpa.Id;
import online.sanen.cdm.template.jpa.NoInsert;
import online.sanen.cdm.template.jpa.Priority;
import online.sanen.cdm.template.jpa.Table;

@Table(name = "class")
@Priority
public class Class {
    @Id
    @NoInsert
    private int classID;
    private String className;
    private int founderID;

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    public int getFounderID() {
        return founderID;
    }

    public void setFounderID(int founderID) {
        this.founderID = founderID;
    }
}
