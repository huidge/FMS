package clb.core.production.dto;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.*;

/**
 * Created by Rex.Hua on 17/4/12.
 */
@Table(name="FMS_PRD_ITEM_SUBLINE")
public class PrdItemSubline extends BaseDTO {
    private static final long serialVersionUID = -2252377040632593943L;

    @Id
    @GeneratedValue(generator = "IDENTITY")
    @Column
    private Long sublineId;

    @Column
    private Long itemId;

    @Column
    private String sublineItemName;

    @Column
    private String sublineItemType;

    @Column
    private Long age;

    @Column
    private String price;

    @Column
    private String onlinePaymentFlag;

    @Column
    private String yearPeriod;

    @Column
    private String comments;

    @Column
    private String enabledFlag;

    @Column
    private Long objectVersionNumber;

    @Transient
    private Long channelId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getSublineId() {
        return sublineId;
    }

    public void setSublineId(Long sublineId) {
        this.sublineId = sublineId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getSublineItemName() {
        return sublineItemName;
    }

    public void setSublineItemName(String sublineItemName) {
        this.sublineItemName = sublineItemName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getSublineItemType() {
        return sublineItemType;
    }

    public void setSublineItemType(String sublineItemType) {
        this.sublineItemType = sublineItemType;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOnlinePaymentFlag() {
        return onlinePaymentFlag;
    }

    public void setOnlinePaymentFlag(String onlinePaymentFlag) {
        this.onlinePaymentFlag = onlinePaymentFlag;
    }

    public String getYearPeriod() {
        return yearPeriod;
    }

    public void setYearPeriod(String yearPeriod) {
        this.yearPeriod = yearPeriod;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
    
    @Transient
    private String itemName;

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
    
}