package clb.core.channel.dto;

import java.math.BigDecimal;

/**
 * 我的业绩->业绩统计
 *
 **/
public class CnlProfitStructure {

    private Long channelId;

    private Long year;

    private Long month;

    private String quarter;

    private String week;

    private BigDecimal directCustomerAmount;

    private BigDecimal teamAmount;

    private BigDecimal referralFeeAmount;

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

    public BigDecimal getDirectCustomerAmount() {
        return directCustomerAmount;
    }

    public void setDirectCustomerAmount(BigDecimal directCustomerAmount) {
        this.directCustomerAmount = directCustomerAmount;
    }

    public BigDecimal getTeamAmount() {
        return teamAmount;
    }

    public void setTeamAmount(BigDecimal teamAmount) {
        this.teamAmount = teamAmount;
    }

    public BigDecimal getReferralFeeAmount() {
        return referralFeeAmount;
    }

    public void setReferralFeeAmount(BigDecimal referralFeeAmount) {
        this.referralFeeAmount = referralFeeAmount;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }
}
