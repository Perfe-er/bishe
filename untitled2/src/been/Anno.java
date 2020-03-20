package been;

import online.sanen.cdm.template.jpa.Id;
import online.sanen.cdm.template.jpa.NoInsert;
import online.sanen.cdm.template.jpa.Priority;
import online.sanen.cdm.template.jpa.Table;

@Table(name = "anno")
@Priority
public class Anno {
    @Id
    @NoInsert
    private int annoID;
    private String annoTitle;
    private String content;
    private int id;
    private String releDate;

    public int getAnnoID() {
        return annoID;
    }

    public void setAnnoID(int annoID) {
        this.annoID = annoID;
    }

    public String getAnnoTitle() {
        return annoTitle;
    }

    public void setAnnoTitle(String annoTitle) {
        this.annoTitle = annoTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReleDate() {
        return releDate;
    }

    public void setReleDate(String releDate) {
        this.releDate = releDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
