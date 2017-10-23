package clb.core.commission.dto;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;

/**
 * @jiaolong.li@hand-china.com
 * @create 2017-04-28 15:24
 **/
public class CmnSpeCmnKeyField {

    private Long supplierId;

    private Long superiorSupplierId;

    private String channelTypeCode;

    private Long itemId;

    private String contributionPeriod;

    private String currency;

    private String payMethod;

    private Date effectiveStartDate;

    private Date effectiveEndDate;

    private String priceType;

    private Long policyholdersMinAge;

    private Long policyholdersMaxAge;

    public Long getParentOverrideId() {
        return parentOverrideId;
    }

    public void setParentOverrideId(Long parentOverrideId) {
        this.parentOverrideId = parentOverrideId;
    }

    private Long parentOverrideId;

    public Long getPolicyholdersMinAge() {
        return policyholdersMinAge;
    }

    public void setPolicyholdersMinAge(Long policyholdersMinAge) {
        this.policyholdersMinAge = policyholdersMinAge;
    }

    public Long getPolicyholdersMaxAge() {
        return policyholdersMaxAge;
    }

    public void setPolicyholdersMaxAge(Long policyholdersMaxAge) {
        this.policyholdersMaxAge = policyholdersMaxAge;
    }

    public Date getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(Date effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public Date getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(Date effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getChannelTypeCode() {
        return channelTypeCode;
    }

    public void setChannelTypeCode(String channelTypeCode) {
        this.channelTypeCode = channelTypeCode;
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

    public Long getSuperiorSupplierId() {
        return superiorSupplierId;
    }

    public void setSuperiorSupplierId(Long superiorSupplierId) {
        this.superiorSupplierId = superiorSupplierId;
    }

}
