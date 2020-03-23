package been;

import online.sanen.cdm.template.jpa.Id;
import online.sanen.cdm.template.jpa.NoInsert;
import online.sanen.cdm.template.jpa.Priority;
import online.sanen.cdm.template.jpa.Table;

@Table(name = "AwardSignComment")
@Priority
public class AwardSignComment {

    @Id
    @NoInsert
    private int id;

    /**
     * 评论哪个报名
     */
    private int awardSignID;

    /**
     * 评论人
     */
    private int commentUid;
    /**
     * 评论内容
     */
    private String commentContent;

    private int commentType; //1 2不通过
    private long dateTime;

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public int getAwardSignID() {
        return awardSignID;
    }

    public void setAwardSignID(int awardSignID) {
        this.awardSignID = awardSignID;
    }

    public int getCommentUid() {
        return commentUid;
    }

    public void setCommentUid(int commentUid) {
        this.commentUid = commentUid;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public int getCommentType() {
        return commentType;
    }

    public void setCommentType(int commentType) {
        this.commentType = commentType;
    }
}
