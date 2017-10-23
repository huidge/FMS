package clb.core.channel.dto;

import java.math.BigDecimal;

/**
 * 我的业绩->业绩统计
 *
 **/
public class CnlPerformanceStatistics {

    private Long channelId;

    private Long year;

    private Long month;

    private Long week;

    private String quarter;

    private Long orderQty;

    private BigDecimal anticipatedIncome;

    private BigDecimal anticipatedPayAmount;

    private String classType;

    private String tabType;

    private String bigClass;

    private String middleClass;

    private Long supplierId;

    private String supplierName;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public Long getMonth() {
        return month;
    }

    public void setMonth(Long month) {
        this.month = month;
    }

    public Long getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(Long orderQty) {
        this.orderQty = orderQty;
    }

    public BigDecimal getAnticipatedIncome() {
        return anticipatedIncome;
    }

    public void setAnticipatedIncome(BigDecimal anticipatedIncome) {
        this.anticipatedIncome = anticipatedIncome;
    }

    public BigDecimal getAnticipatedPayAmount() {
        return anticipatedPayAmount;
    }

    public void setAnticipatedPayAmount(BigDecimal anticipatedPayAmount) {
        this.anticipatedPayAmount = anticipatedPayAmount;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getBigClass() {
        return bigClass;
    }

    public void setBigClass(String bigClass) {
        this.bigClass = bigClass;
    }

    public String getMiddleClass() {
        return middleClass;
    }

    public void setMiddleClass(String middleClass) {
        this.middleClass = middleClass;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public Long getWeek() {
        return week;
    }

    public void setWeek(Long week) {
        this.week = week;
    }

    public String getTabType() {
        return tabType;
    }

    public void setTabType(String tabType) {
        this.tabType = tabType;
    }
}
