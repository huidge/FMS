package clb.core.commission.dto;

/**
 * Auto Generated By Hap Code Generator
 **/

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import javax.persistence.Table;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import java.math.BigDecimal;

@ExtensionAttribute(disable = true)
@Table(name = "fms_cmn_trade_route")
public class CmnTradeRoute extends BaseDTO {
    @Id
    @GeneratedValue
    private Long routeId;

    @NotNull
    private Long channelCommissionLineId;

    @NotNull
    private Long seqNum;

    @NotNull
    private Long companyId;

    @NotEmpty
    private String companyType;

    private Long parentRouteId;

    @Transient
    private String currency;
    @Transient
    private BigDecimal theFirstYear;
    @Transient
    private BigDecimal theSecondYear;
    @Transient
    private BigDecimal theThirdYear;
    @Transient
    private BigDecimal theFourthYear;
    @Transient
    private BigDecimal theFifthYear;
    @Transient
    private BigDecimal theSixthYear;
    @Transient
    private BigDecimal theSeventhYear;
    @Transient
    private BigDecimal theEightYear;
    @Transient
    private BigDecimal theNinthYear;
    @Transient
    private BigDecimal theTenthYear;
    @Transient
    private String channelTypeCode;
    @Transient
    private Long channelContractId;

    private Long programId;

    private Long requestId;


    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setChannelCommissionLineId(Long channelCommissionLineId) {
        this.channelCommissionLineId = channelCommissionLineId;
    }

    public Long getChannelCommissionLineId() {
        return channelCommissionLineId;
    }

    public void setSeqNum(Long seqNum) {
        this.seqNum = seqNum;
    }

    public Long getSeqNum() {
        return seqNum;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setParentRouteId(Long parentRouteId) {
        this.parentRouteId = parentRouteId;
    }

    public Long getParentRouteId() {
        return parentRouteId;
    }

    public String getChannelTypeCode() {
        return channelTypeCode;
    }

    public void setChannelTypeCode(String channelTypeCode) {
        this.channelTypeCode = channelTypeCode;
    }

    public Long getChannelContractId() {
        return channelContractId;
    }

    public void setChannelContractId(Long channelContractId) {
        this.channelContractId = channelContractId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public Long getChildRouteId() {
        return childRouteId;
    }

    public void setChildRouteId(Long childRouteId) {
        this.childRouteId = childRouteId;
    }

    private Long childRouteId;

    public Long getCompanyCommissionLineId() {
        return companyCommissionLineId;
    }

    public void setCompanyCommissionLineId(Long companyCommissionLineId) {
        this.companyCommissionLineId = companyCommissionLineId;
    }

    private Long companyCommissionLineId;

    public String getDealPath() {
        return dealPath;
    }

    public void setDealPath(String dealPath) {
        this.dealPath = dealPath;
    }

    private String dealPath;

    public String getDealPathName() {
        return dealPathName;
    }

    public void setDealPathName(String dealPathName) {
        this.dealPathName = dealPathName;
    }

    private String dealPathName;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getTheFirstYear() {
        return theFirstYear;
    }

    public void setTheFirstYear(BigDecimal theFirstYear) {
        this.theFirstYear = theFirstYear;
    }

    public BigDecimal getTheSecondYear() {
        return theSecondYear;
    }

    public void setTheSecondYear(BigDecimal theSecondYear) {
        this.theSecondYear = theSecondYear;
    }

    public BigDecimal getTheThirdYear() {
        return theThirdYear;
    }

    public void setTheThirdYear(BigDecimal theThirdYear) {
        this.theThirdYear = theThirdYear;
    }

    public BigDecimal getTheFourthYear() {
        return theFourthYear;
    }

    public void setTheFourthYear(BigDecimal theFourthYear) {
        this.theFourthYear = theFourthYear;
    }

    public BigDecimal getTheFifthYear() {
        return theFifthYear;
    }

    public void setTheFifthYear(BigDecimal theFifthYear) {
        this.theFifthYear = theFifthYear;
    }

    public BigDecimal getTheSixthYear() {
        return theSixthYear;
    }

    public void setTheSixthYear(BigDecimal theSixthYear) {
        this.theSixthYear = theSixthYear;
    }

    public BigDecimal getTheSeventhYear() {
        return theSeventhYear;
    }

    public void setTheSeventhYear(BigDecimal theSeventhYear) {
        this.theSeventhYear = theSeventhYear;
    }

    public BigDecimal getTheEightYear() {
        return theEightYear;
    }

    public void setTheEightYear(BigDecimal theEightYear) {
        this.theEightYear = theEightYear;
    }

    public BigDecimal getTheNinthYear() {
        return theNinthYear;
    }

    public void setTheNinthYear(BigDecimal theNinthYear) {
        this.theNinthYear = theNinthYear;
    }

    public BigDecimal getTheTenthYear() {
        return theTenthYear;
    }

    public void setTheTenthYear(BigDecimal theTenthYear) {
        this.theTenthYear = theTenthYear;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    private Long batchId;
}
