package been;

import online.sanen.cdm.template.jpa.Id;
import online.sanen.cdm.template.jpa.NoInsert;
import online.sanen.cdm.template.jpa.Priority;
import online.sanen.cdm.template.jpa.Table;

@Table(name = "annoreceive")
@Priority
public class AnnoReceive {
    @Id
    @NoInsert
    private int id;
    private int classID;
    private int annoID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public int getAnnoID() {
        return annoID;
    }

    public void setAnnoID(int annoID) {
        this.annoID = annoID;
    }
}
