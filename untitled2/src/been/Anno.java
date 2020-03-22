package been;

import online.sanen.cdm.template.jpa.*;

import java.util.List;

@Table(name = "anno")
@Priority
public class Anno {
    @Id
    @NoInsert
    private int annoID;
    private String annoTitle;
    private String content;
    private int releaseID;
    private long releDate;
    @NoDB
    private List<String> classIds;
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

    public int getReleaseID() {
        return releaseID;
    }

    public void setReleaseID(int releaseID) {
        this.releaseID = releaseID;
    }

    public long getReleDate() {
        return releDate;
    }

    public void setReleDate(long releDate) {
        this.releDate = releDate;
    }

    public List<String> getClassIds() {
        return classIds;
    }

    public void setClassIds(List<String> classIds) {
        this.classIds = classIds;
    }
}
