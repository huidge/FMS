package clb.core.production.dto;

import com.hand.hap.system.dto.BaseDTO;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import javax.persistence.*;

/**
 * Created by Rex.Hua on 17/4/12.
 */
@Table(name="FMS_PRD_ITEM_PAYMODE")
public class PrdItemPaymode extends BaseDTO {
    private static final long serialVersionUID = -2252377040632593943L;

    @Id
    @GeneratedValue(generator = "IDENTITY")
    @Column
    private Long paymodeId;

    @Column
    private Long itemId;

    @NotEmpty
    @Column
    private String currencyCode;

    @NotEmpty
    @Column
    private String enabledFlag;

    @Transient
    private String currency;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getPaymodeId() {
        return paymodeId;
    }

    public void setPaymodeId(Long paymodeId) {
        this.paymodeId = paymodeId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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