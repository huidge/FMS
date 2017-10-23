package clb.core.channel.dto;

import java.util.Date;

/**
 * 我的业绩参数
 **/
public class CnlPerformanceParas {

    private Long channelId;

    private Long beginYear;

    private Long beginMonth;

    // 设定方式 “按季度”“按月度”
    private String settingType;

    // 业绩统计  “按产品分类” “按公司分类”
    private String classType;

    // 业绩分类: {"直客业绩" : "personal", "团队业绩" : "team"}
    private String performanceType;

    private Date startDate;

    private String insuranceStatusArea;

    private String bondStatusArea;

    private String immigrantStatusArea;

    private String datePeriod;

    private Date scopeStartDate;

    private Date scopeEndDate;

    private String receiptType;

    private String orderBy;

    public String getPerformanceType() {
        return performanceType;
    }

    public void setPerformanceType(String performanceType) {
        this.performanceType = performanceType;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(String receiptType) {
        this.receiptType = receiptType;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getBeginYear() {
        return beginYear;
    }

    public void setBeginYear(Long beginYear) {
        this.beginYear = beginYear;
    }

    public Long getBeginMonth() {
        return beginMonth;
    }

    public void setBeginMonth(Long beginMonth) {
        this.beginMonth = beginMonth;
    }

    public String getSettingType() {
        return settingType;
    }

    public void setSettingType(String settingType) {
        this.settingType = settingType;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getInsuranceStatusArea() {
        return insuranceStatusArea;
    }

    public void setInsuranceStatusArea(String insuranceStatusArea) {
        this.insuranceStatusArea = insuranceStatusArea;
    }

    public String getBondStatusArea() {
        return bondStatusArea;
    }

    public void setBondStatusArea(String bondStatusArea) {
        this.bondStatusArea = bondStatusArea;
    }

    public String getImmigrantStatusArea() {
        return immigrantStatusArea;
    }

    public void setImmigrantStatusArea(String immigrantStatusArea) {
        this.immigrantStatusArea = immigrantStatusArea;
    }

    public String getDatePeriod() {
        return datePeriod;
    }

    public void setDatePeriod(String datePeriod) {
        this.datePeriod = datePeriod;
    }

    public Date getScopeStartDate() {
        return scopeStartDate;
    }

    public void setScopeStartDate(Date scopeStartDate) {
        this.scopeStartDate = scopeStartDate;
    }

    public Date getScopeEndDate() {
        return scopeEndDate;
    }

    public void setScopeEndDate(Date scopeEndDate) {
        this.scopeEndDate = scopeEndDate;
    }
}
