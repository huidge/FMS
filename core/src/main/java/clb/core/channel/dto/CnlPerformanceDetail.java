package clb.core.channel.dto;

import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * 我的业绩->业绩明细表
 *
 **/
public class CnlPerformanceDetail {

    private Long channelId;

    private String orderNum;

    private String bigClass;

    private String bigClassName;

    private Long itemId;

    private String itemName;

    private String orderStatus;

    private BigDecimal anticipatedPayAmount;

    private String currency;

    private Double rate;

    private BigDecimal anticipatedPayHKDAmount;

    private BigDecimal anticipatedIncomeHKDAmount;

    private String incomeType;

    @Transient
    private String paymentType;

    @Transient
    private String rate1;

    @Transient
    private String hkdAmount;

    public String getHkdAmount() {
        return hkdAmount;
    }

    public void setHkdAmount(String hkdAmount) {
        this.hkdAmount = hkdAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getRate1() {
        return rate1;
    }

    public void setRate1(String rate1) {
        this.rate1 = rate1;
    }

    public String getBigClass() {
        return bigClass;
    }

    public void setBigClass(String bigClass) {
        this.bigClass = bigClass;
    }

    public String getBigClassName() {
        return bigClassName;
    }

    public void setBigClassName(String bigClassName) {
        this.bigClassName = bigClassName;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getAnticipatedPayAmount() {
        return anticipatedPayAmount;
    }

    public void setAnticipatedPayAmount(BigDecimal anticipatedPayAmount) {
        this.anticipatedPayAmount = anticipatedPayAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public BigDecimal getAnticipatedPayHKDAmount() {
        return anticipatedPayHKDAmount;
    }

    public void setAnticipatedPayHKDAmount(BigDecimal anticipatedPayHKDAmount) {
        this.anticipatedPayHKDAmount = anticipatedPayHKDAmount;
    }

    public BigDecimal getAnticipatedIncomeHKDAmount() {
        return anticipatedIncomeHKDAmount;
    }

    public void setAnticipatedIncomeHKDAmount(BigDecimal anticipatedIncomeHKDAmount) {
        this.anticipatedIncomeHKDAmount = anticipatedIncomeHKDAmount;
    }

    public String getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(String incomeType) {
        this.incomeType = incomeType;
    }
}
