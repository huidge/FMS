package clb.core.production.dto;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by wanjun.feng on 17/4/12.
 */
@Table(name="fms_prd_image_text")
public class PrdImageText extends BaseDTO {
    private static final long serialVersionUID = -2252377040632593943L;

    @Id
    @GeneratedValue(generator="IDENTITY")
    @Column
    private Long imageId;

    @Column
    private Long itemId;
    
    @Column
    private String titleCode;
    
    @Column
    private String imageText;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getTitleCode() {
        return titleCode;
    }

    public void setTitleCode(String titleCode) {
        this.titleCode = titleCode;
    }

    public String getImageText() {
        return imageText;
    }

    public void setImageText(String imageText) {
        this.imageText = imageText;
    }
}