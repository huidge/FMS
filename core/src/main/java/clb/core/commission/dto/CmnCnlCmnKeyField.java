package clb.core.commission.dto;
/**
 * 渠道佣金表key字段
 * Created by jiaolong.li on 2017-05-24.
 */
public class CmnCnlCmnKeyField {

    private Long supplierId;

    private Long superiorSupplierId;

    private String channelTypeCode;

    private Long itemId;

    private String contributionPeriod;

    private String currency;

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

    private String payMethod;

    private Long channelId;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }


    private Long supplierCommissionId;

    private Long channelContractId;

    public Long getSuperiorSupplierId() {
        return superiorSupplierId;
    }

    public void setSuperiorSupplierId(Long superiorSupplierId) {
        this.superiorSupplierId = superiorSupplierId;
    }

}