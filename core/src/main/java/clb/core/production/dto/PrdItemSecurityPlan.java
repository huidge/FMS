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
@Table(name="FMS_PRD_ITEM_SECURITY_PLAN")
public class PrdItemSecurityPlan extends BaseDTO {
    private static final long serialVersionUID = -2252377040632593943L;

    @Id
    @GeneratedValue(generator = "IDENTITY")
    @Column
    private Long planId;

    @Column
    private Long itemId;

    @Column
    private String securityLevel;

    @Column
    private String securityRegion;

    @Column
    private String enabledFlag;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getSecurityLevel() {
        return StringUtils.isNoneBlank(securityLevel)?securityLevel.trim():securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getSecurityRegion() {
        return StringUtils.isNoneBlank(securityRegion)?securityRegion.trim():securityRegion;
    }

    public void setSecurityRegion(String securityRegion) {
        this.securityRegion = securityRegion;
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