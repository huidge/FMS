package clb.core.production.dto;

import com.hand.hap.system.dto.BaseDTO;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

/**
 * Created by Rex.Hua on 17/4/12.
 */
@Table(name="fms_prd_item_label_lines")
public class PrdItemLabelLines extends BaseDTO {
    private static final long serialVersionUID = -2252377040632593943L;

    @Id
    @GeneratedValue(generator = "IDENTITY")
    @Column
    private Long lineId;

    @Column
    private Long itemId;

    @Column
    private Long labelId;

    @Transient
    private String labelName;

    @Transient
    private String bigClass;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getBigClass() {
        return bigClass;
    }

    public void setBigClass(String bigClass) {
        this.bigClass = bigClass;
    }
}