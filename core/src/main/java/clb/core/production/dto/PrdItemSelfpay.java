package clb.core.production.dto;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Rex.Hua on 17/4/12.
 */
@Table(name="FMS_PRD_ITEM_SELFPAY")
public class PrdItemSelfpay extends BaseDTO {
    private static final long serialVersionUID = -2252377040632593943L;

    @Id
    @GeneratedValue(generator = "IDENTITY")
    @Column
    private Long selfpayId;

    @Column
    private Long itemId;

    @Column
    private String selfpay;

    @Column
    private String enabledFlag;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getSelfpayId() {
        return selfpayId;
    }

    public void setSelfpayId(Long selfpayId) {
        this.selfpayId = selfpayId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getSelfpay() {
        return StringUtils.isNoneBlank(selfpay)?selfpay.trim():selfpay;
    }

    public void setSelfpay(String selfpay) {
        this.selfpay = selfpay;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
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