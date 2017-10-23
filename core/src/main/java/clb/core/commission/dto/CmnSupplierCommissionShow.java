package clb.core.commission.dto;

/**
 * Created by jiaolong.li on 2017-04-24.
 */


import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import javax.persistence.Table;

import com.hand.hap.system.dto.BaseDTO;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.activiti.rest.common.util.DateToStringSerializer;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

@ExtensionAttribute(disable = true)
@Table(name = "fms_cmn_supplier_commission_show")
public class CmnSupplierCommissionShow extends BaseDTO {
    @Id
    @GeneratedValue
    private Long lineId;

    @NotNull
    private Long supplierId;

    public Long getSuperiorSupplierId() {
        return superiorSupplierId;
    }

    public void setSuperiorSupplierId(Long superiorSupplierId) {
        this.superiorSupplierId = superiorSupplierId;
    }

    private Long superiorSupplierId;

    @NotEmpty
    private String channelTypeCode;

    @NotNull
    private Long itemId;

    @NotNull
    private String contributionPeriod;

    @NotEmpty
    private String currency;

    @NotEmpty
    private String payMethod;

    @NotNull
    private Long policyholdersMinAge;

    @NotNull
    private Long policyholdersMaxAge;

    private BigDecimal theFirstYear;

    private BigDecimal theSecondYear;

    private BigDecimal theThirdYear;

    private BigDecimal theFourthYear;

    private BigDecimal theFifthYear;

    private BigDecimal theSixthYear;

    private BigDecimal theSeventhYear;

    private BigDecimal theEightYear;

    private BigDecimal theNinthYear;

    private BigDecimal theTenthYear;

    private String version;

    private Date effectiveStartDate;

    private Date effectiveEndDate;

    private Long programId;

    private Long requestId;

    private Long basicId;

    private Long extraId;

    private Long overrideId;

    @Transient
    private String supplierName;

    @Transient
    private String channelTypeName;

    @Transient
    private String itemSupplierName;

    @Transient
    private String itemCode;

    @Transient
    private String bigClass;

    private String dealPathName;

    private String dealPath;

    public String getDealPath() {
        return dealPath;
    }

    public void setDealPath(String dealPath) {
        this.dealPath = dealPath;
    }

    public String getDealPathName() {
        return dealPathName;
    }

    public void setDealPathName(String dealPathName) {
        this.dealPathName = dealPathName;
    }

    public String getBigClass() {
        return bigClass;
    }

    public void setBigClass(String bigClass) {
        this.bigClass = bigClass;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    @Override
    public Long getProgramId() {
        return programId;
    }

    @Override
    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    @Override
    public Long getRequestId() {
        return requestId;
    }

    @Override
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getBasicId() {
        return basicId;
    }

    public void setBasicId(Long basicId) {
        this.basicId = basicId;
    }

    public Long getExtraId() {
        return extraId;
    }

    public void setExtraId(Long extraId) {
        this.extraId = extraId;
    }

    public Long getOverrideId() {
        return overrideId;
    }

    public void setOverrideId(Long overrideId) {
        this.overrideId = overrideId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getChannelTypeName() {
        return channelTypeName;
    }

    public void setChannelTypeName(String channelTypeName) {
        this.channelTypeName = channelTypeName;
    }

    public String getItemSupplierName() {
        return itemSupplierName;
    }

    public void setItemSupplierName(String itemSupplierName) {
        this.itemSupplierName = itemSupplierName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPayMethodName() {
        return payMethodName;
    }

    public void setPayMethodName(String payMethodName) {
        this.payMethodName = payMethodName;
    }

    public Long getItemSupplierId() {
        return itemSupplierId;
    }

    public void setItemSupplierId(Long itemSupplierId) {
        this.itemSupplierId = itemSupplierId;
    }

    @Transient
    private String itemName;

    @Transient
    private String payMethodName;

    @Transient
    private Long itemSupplierId;

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    private String priceType;

    public String getPriceTypeName() {
        return priceTypeName;
    }

    public void setPriceTypeName(String priceTypeName) {
        this.priceTypeName = priceTypeName;
    }

    @Transient
    private String priceTypeName;

    public Long getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(Long levelNumber) {
        this.levelNumber = levelNumber;
    }

    private Long levelNumber;

    private String commissionNum;

    public String getCommissionNum() {
        return commissionNum;
    }

    public void setCommissionNum(String commissionNum) {
        this.commissionNum = commissionNum;
    }

    public String getParentCommissionNum() {
        return parentCommissionNum;
    }

    public void setParentCommissionNum(String parentCommissionNum) {
        this.parentCommissionNum = parentCommissionNum;
    }

    private String parentCommissionNum;

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Transient
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date effectiveDate;

}
