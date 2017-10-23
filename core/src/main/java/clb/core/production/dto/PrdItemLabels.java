package clb.core.production.dto;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.*;

/**
 * Created by Rex.Hua on 17/4/12.
 */
@Table(name="fms_prd_item_labels")
public class PrdItemLabels extends BaseDTO {
    private static final long serialVersionUID = -2252377040632593943L;

    @Id
    @GeneratedValue(generator = "IDENTITY")
    @Column
    private Long labelId;

    @Column
    private String bigClass;

    @Column
    private String labelName;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public String getBigClass() {
        return bigClass;
    }

    public void setBigClass(String bigClass) {
        this.bigClass = bigClass;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}