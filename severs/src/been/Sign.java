package been;

import online.sanen.cdm.template.jpa.*;

import java.util.List;

@Table(name = "sign")
@Priority
public class Sign {
    @Id
    @NoInsert
    private int signID;
    private int originID;
    private long iniDate;
    private long endDate;
    private String signcol;//签到码
    private int signType; // 0开始签到 ,1签到结束
    @NoDB
    private List<String> toClass;


    public int getSignID() {
        return signID;
    }

    public void setSignID(int signID) {
        this.signID = signID;
    }

    public int getOriginID() {
        return originID;
    }

    public void setOriginID(int originID) {
        this.originID = originID;
    }

    public long getIniDate() {
        return iniDate;
    }

    public void setIniDate(long iniDate) {
        this.iniDate = iniDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getSigncol() {
        return signcol;
    }

    public void setSigncol(String signcol) {
        this.signcol = signcol;
    }

    public List<String> getToClass() {
        return toClass;
    }

    public void setToClass(List<String> toClass) {
        this.toClass = toClass;
    }

    public int getSignType() {
        return signType;
    }

    public void setSignType(int signType) {
        this.signType = signType;
    }
}
