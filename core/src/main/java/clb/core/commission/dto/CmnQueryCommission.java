package clb.core.commission.dto;

import clb.core.production.dto.PrdItems;

import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 保单预约佣金查询
 * Created by jiaolong.li on 2017-06-23.
 */
public class CmnQueryCommission extends BaseDTO{

    private Long channelId;

    private Long supplierId;

    private Long itemId;

    private String contributionPeriod;

    private String currency;

    private String payMethod;

    private Date effectiveDate;

    private Date birthDate;

    private String dealPath;
    
    @Transient
    private Long prdSupplierId;

    private List<PrdItems> additionRiskList;

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public List<PrdItems> getAdditionRiskList() {
        return additionRiskList;
    }

    public void setAdditionRiskList(List<PrdItems> additionRiskList) {
        this.additionRiskList = additionRiskList;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getContributionPeriod() {
        return contributionPeriod;
    }

    public void setContributionPeriod(String contributionPeriod) {
        this.contributionPeriod = contributionPeriod;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getDealPath() {
        return dealPath;
    }

    public void setDealPath(String dealPath) {
        this.dealPath = dealPath;
    }

	public Long getPrdSupplierId() {
		return prdSupplierId;
	}

	public void setPrdSupplierId(Long prdSupplierId) {
		this.prdSupplierId = prdSupplierId;
	}
}