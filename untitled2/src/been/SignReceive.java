package been;

import online.sanen.cdm.template.jpa.Id;
import online.sanen.cdm.template.jpa.NoInsert;
import online.sanen.cdm.template.jpa.Priority;
import online.sanen.cdm.template.jpa.Table;

@Table(name = "signreceive")
@Priority
public class SignReceive {
    @Id
    @NoInsert
    private int receiveID;
    private int signID;
    private int lassID;

    public int getReceiveID() {
        return receiveID;
    }

    public void setReceiveID(int receiveID) {
        this.receiveID = receiveID;
    }

    public int getSignID() {
        return signID;
    }

    public void setSignID(int signID) {
        this.signID = signID;
    }

    public int getLassID() {
        return lassID;
    }

    public void setLassID(int lassID) {
        this.lassID = lassID;
    }
}
