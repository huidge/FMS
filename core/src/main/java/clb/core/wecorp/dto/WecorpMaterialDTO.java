package clb.core.wecorp.dto;

/**
 * Created by shanhd on 2016/10/30.
 */
public class WecorpMaterialDTO {

    String purpose;
    String enableFlag;
    String materialType;
    String lastUpdateDateSort;
    String creationDateSort;

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getLastUpdateDateSort() {
        return lastUpdateDateSort;
    }

    public void setLastUpdateDateSort(String lastUpdateDateSort) {
        this.lastUpdateDateSort = lastUpdateDateSort;
    }

    public String getCreationDateSort() {
        return creationDateSort;
    }

    public void setCreationDateSort(String creationDateSort) {
        this.creationDateSort = creationDateSort;
    }
}
