
package clb.core.commission.dto;

/**
 * Created by jiaolong.li on 2017-04-24.
 */


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.activiti.rest.common.util.DateToStringSerializer;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@ExtensionAttribute(disable = true)
@Table(name = "fms_cmn_channel_commission")
public class CmnChannelCommission extends BaseDTO {
    @Id
    @GeneratedValue
    private Long lineId;

    @NotNull
    private Long supplierId;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    @NotNull
    private Long channelId;

    @NotEmpty
    private String channelTypeCode;

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    @Transient
    private String levelName;
    
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

    @Transient
    private String supplierName;

    @Transient
    private String channelTypeName;

    @Transient
    private String itemSupplierName;

    @Transient
    private String itemCode;

    public Long getPolicyholdersAge() {
        return policyholdersAge;
    }

    public void setPolicyholdersAge(Long policyholdersAge) {
        this.policyholdersAge = policyholdersAge;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Transient
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date effectiveDate;

    @Transient
    private Long policyholdersAge;

    //add by zhaojun
    @Transient
    private Long ownSupplierId;

    @Transient
    private String ownSupplierName;

    @Transient
    private Long reserveSupplierId;

    @Transient
    private String reserveSupplierName;

    @Transient
    private String middleSupplierName;

    @Transient
    private Long signSupplierId;

    @Transient
    private String insuranceSupplierName;

    @Transient
    private String signSupplierName;

    @Transient
    private Long insurantAge;

    @Transient
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reserveDate;

    @Transient
    private String reserveDateSting;

    @Transient
    private String addItems;

    @Transient
    private Long addCount;

    @Transient
    private String supplierLimit;

    @Transient
    private String orderBy;

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
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

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getPriceTypeName() {
        return priceTypeName;
    }

    public void setPriceTypeName(String priceTypeName) {
        this.priceTypeName = priceTypeName;
    }

    @Transient
    private String priceTypeName;

    @Transient
    private String itemName;

    @Transient
    private String channelName;

    public String getParentPartyName() {
        return parentPartyName;
    }

    public void setParentPartyName(String parentPartyName) {
        this.parentPartyName = parentPartyName;
    }

    @Transient
    private String parentPartyType;

    public String getParentPartyType() {
        return parentPartyType;
    }

    public void setParentPartyType(String parentPartyType) {
        this.parentPartyType = parentPartyType;
    }

    public Long getParentPartyId() {
        return parentPartyId;
    }

    public void setParentPartyId(Long parentPartyId) {
        this.parentPartyId = parentPartyId;
    }

    @Transient
    private Long parentPartyId;

    @Transient
    private String parentPartyName;

    @Transient
    private String payMethodName;

    @Transient
    private Long itemSupplierId;

    @Transient
    private String itemRange;

    public String getItemRange() {
        return itemRange;
    }

    public void setItemRange(String itemRange) {
        this.itemRange = itemRange;
    }

    public String getSupplierRange() {
        return supplierRange;
    }

    public void setSupplierRange(String supplierRange) {
        this.supplierRange = supplierRange;
    }

    @Transient
    private String supplierRange;

    public String getAdditionalRiskFlag() {
        return additionalRiskFlag;
    }

    public void setAdditionalRiskFlag(String additionalRiskFlag) {
        this.additionalRiskFlag = additionalRiskFlag;
    }

    @Transient
    private String additionalRiskFlag;

    private Long supplierCommissionId;

    private Long channelContractId;

    @Transient
    private Long roleUserId;

    private Long channelLevelId;

    public String getChannelLevelName() {
        return channelLevelName;
    }

    public void setChannelLevelName(String channelLevelName) {
        this.channelLevelName = channelLevelName;
    }

    @Transient
    private String channelLevelName;

    private Long relationLevelNumber;

    private String priceType;

    public Long getSupplierCommissionId() {
        return supplierCommissionId;
    }

    public void setSupplierCommissionId(Long supplierCommissionId) {
        this.supplierCommissionId = supplierCommissionId;
    }

    public Long getChannelContractId() {
        return channelContractId;
    }

    public void setChannelContractId(Long channelContractId) {
        this.channelContractId = channelContractId;
    }

    public Long getRoleUserId() {
        return roleUserId;
    }

    public void setRoleUserId(Long roleUserId) {
        this.roleUserId = roleUserId;
    }

    public Long getChannelLevelId() {
        return channelLevelId;
    }

    public void setChannelLevelId(Long channelLevelId) {
        this.channelLevelId = channelLevelId;
    }

    public Long getRelationLevelNumber() {
        return relationLevelNumber;
    }

    public void setRelationLevelNumber(Long relationLevelNumber) {
        this.relationLevelNumber = relationLevelNumber;
    }

    public void setAtt(CmnChannelCommission channelCommission) {
        this.supplierId = channelCommission.getSupplierId();
        this.channelId = channelCommission.getChannelId();
        this.supplierCommissionId = channelCommission.getSupplierCommissionId();
        this.channelContractId = channelCommission.getChannelContractId();
        this.relationLevelNumber = channelCommission.getRelationLevelNumber();
        this.channelTypeCode = channelCommission.getChannelTypeCode();
        this.effectiveStartDate = channelCommission.getEffectiveStartDate();
        this.effectiveEndDate = channelCommission.getEffectiveEndDate();
    }

    public Long getChannelRateId() {
        return channelRateId;
    }

    public void setChannelRateId(Long channelRateId) {
        this.channelRateId = channelRateId;
    }

    @Transient
    private Long channelRateId;

    @Transient
    private BigDecimal channelProportion1;

    @Transient
    private BigDecimal channelProportion2;

    @Transient
    private BigDecimal channelProportion3;

    @Transient
    private BigDecimal channelProportion4;

    public BigDecimal getChannelProportion1() {
        return channelProportion1;
    }

    public void setChannelProportion1(BigDecimal channelProportion1) {
        this.channelProportion1 = channelProportion1;
    }

    public BigDecimal getChannelProportion2() {
        return channelProportion2;
    }

    public void setChannelProportion2(BigDecimal channelProportion2) {
        this.channelProportion2 = channelProportion2;
    }

    public BigDecimal getChannelProportion3() {
        return channelProportion3;
    }

    public void setChannelProportion3(BigDecimal channelProportion3) {
        this.channelProportion3 = channelProportion3;
    }

    public BigDecimal getChannelProportion4() {
        return channelProportion4;
    }

    public void setChannelProportion4(BigDecimal channelProportion4) {
        this.channelProportion4 = channelProportion4;
    }

    public BigDecimal getChannelProportion5() {
        return channelProportion5;
    }

    public void setChannelProportion5(BigDecimal channelProportion5) {
        this.channelProportion5 = channelProportion5;
    }

    public BigDecimal getChannelProportion6() {
        return channelProportion6;
    }

    public void setChannelProportion6(BigDecimal channelProportion6) {
        this.channelProportion6 = channelProportion6;
    }

    public BigDecimal getChannelProportion7() {
        return channelProportion7;
    }

    public void setChannelProportion7(BigDecimal channelProportion7) {
        this.channelProportion7 = channelProportion7;
    }

    public BigDecimal getChannelProportion8() {
        return channelProportion8;
    }

    public void setChannelProportion8(BigDecimal channelProportion8) {
        this.channelProportion8 = channelProportion8;
    }

    public BigDecimal getChannelProportion9() {
        return channelProportion9;
    }

    public void setChannelProportion9(BigDecimal channelProportion9) {
        this.channelProportion9 = channelProportion9;
    }

    public BigDecimal getChannelProportion10() {
        return channelProportion10;
    }

    public void setChannelProportion10(BigDecimal channelProportion10) {
        this.channelProportion10 = channelProportion10;
    }

    @Transient
    private BigDecimal channelProportion5;

    @Transient
    private BigDecimal channelProportion6;

    @Transient
    private BigDecimal channelProportion7;

    @Transient
    private BigDecimal channelProportion8;

    @Transient
    private BigDecimal channelProportion9;

    @Transient
    private BigDecimal channelProportion10;

    public String getBigClass() {
        return bigClass;
    }

    public void setBigClass(String bigClass) {
        this.bigClass = bigClass;
    }

    @Transient
    private String bigClass;

    @Transient
    private String midClass;

    public String getMidClass() {
        return midClass;
    }

    public void setMidClass(String midClass) {
        this.midClass = midClass;
    }

    public String getMinClass() {
        return minClass;
    }

    public void setMinClass(String minClass) {
        this.minClass = minClass;
    }

    @Transient
    private String minClass;

    @Transient
    private String existFlag;

    public String getExistFlag() {
        return existFlag;
    }

    public void setExistFlag(String existFlag) {
        this.existFlag = existFlag;
    }

    public Long getParentLineId() {
        return parentLineId;
    }

    public void setParentLineId(Long parentLineId) {
        this.parentLineId = parentLineId;
    }

    private Long parentLineId;

    public Long getOwnSupplierId() {
        return ownSupplierId;
    }

    public void setOwnSupplierId(Long ownSupplierId) {
        this.ownSupplierId = ownSupplierId;
    }

    public String getOwnSupplierName() {
        return ownSupplierName;
    }

    public void setOwnSupplierName(String ownSupplierName) {
        this.ownSupplierName = ownSupplierName;
    }

    public Long getReserveSupplierId() {
        return reserveSupplierId;
    }

    public void setReserveSupplierId(Long reserveSupplierId) {
        this.reserveSupplierId = reserveSupplierId;
    }

    public String getReserveSupplierName() {
        return reserveSupplierName;
    }

    public void setReserveSupplierName(String reserveSupplierName) {
        this.reserveSupplierName = reserveSupplierName;
    }

    public String getMiddleSupplierName() {
        return middleSupplierName;
    }

    public void setMiddleSupplierName(String middleSupplierName) {
        this.middleSupplierName = middleSupplierName;
    }

    public Long getSignSupplierId() {
        return signSupplierId;
    }

    public void setSignSupplierId(Long signSupplierId) {
        this.signSupplierId = signSupplierId;
    }

    public String getInsuranceSupplierName() {
        return insuranceSupplierName;
    }

    public void setInsuranceSupplierName(String insuranceSupplierName) {
        this.insuranceSupplierName = insuranceSupplierName;
    }

    public String getSignSupplierName() {
        return signSupplierName;
    }

    public void setSignSupplierName(String signSupplierName) {
        this.signSupplierName = signSupplierName;
    }

    public String getDealPath() {
        return dealPath;
    }

    public void setDealPath(String dealPath) {
        this.dealPath = dealPath;
    }

    private String dealPath;
    
    @Transient
    private String dealPathChannel;
    @Transient
    private String dealPathSupplier;

    public String getDealPathName() {
        return dealPathName;
    }

    public void setDealPathName(String dealPathName) {
        this.dealPathName = dealPathName;
    }

    private String dealPathName;

    public Long getInsurantAge() {
        return insurantAge;
    }

    public void setInsurantAge(Long insurantAge) {
        this.insurantAge = insurantAge;
    }

    public Date getReserveDate() {
        return reserveDate;
    }

    public void setReserveDate(Date reserveDate) {
        this.reserveDate = reserveDate;
    }

    public String getReserveDateSting() {
        return reserveDateSting;
    }

    public void setReserveDateSting(String reserveDateSting) {
        this.reserveDateSting = reserveDateSting;
    }

    public String getAddItems() {
        return addItems;
    }

    public void setAddItems(String addItems) {
        this.addItems = addItems;
    }

    public Long getAddCount() {
        return addCount;
    }

    public void setAddCount(Long addCount) {
        this.addCount = addCount;
    }

    public String getSupplierLimit() {
        return supplierLimit;
    }

    public void setSupplierLimit(String supplierLimit) {
        this.supplierLimit = supplierLimit;
    }

    public String getCalculateFlag() {
        return calculateFlag;
    }

    public void setCalculateFlag(String calculateFlag) {
        this.calculateFlag = calculateFlag;
    }

    private String calculateFlag;

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    @Transient
    private String channelCode;

    @Transient
    private Long totalLineQty;

    public Long getTotalLineQty() {
        return totalLineQty;
    }

    public void setTotalLineQty(Long totalLineQty) {
        this.totalLineQty = totalLineQty;
    }

    public Long getBatchCount() {
        return batchCount;
    }

    public void setBatchCount(Long batchCount) {
        this.batchCount = batchCount;
    }

    @Transient
    private Long batchCount;

    public Long getMinLineId() {
        return minLineId;
    }

    public void setMinLineId(Long minLineId) {
        this.minLineId = minLineId;
    }

    @Transient
    private Long minLineId;

    public Long getStartLineId() {
        return startLineId;
    }

    public void setStartLineId(Long startLineId) {
        this.startLineId = startLineId;
    }

    @Transient
    private Long startLineId;

    public Long getBasicId() {
        return basicId;
    }

    public void setBasicId(Long basicId) {
        this.basicId = basicId;
    }

    @Transient
    private Long basicId;

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    private Long batchId;

    public boolean getRequestFirst() {
        return requestFirst;
    }

    public void setRequestFirst(boolean requestFirst) {
        this.requestFirst = requestFirst;
    }

    @Transient
    private boolean requestFirst;

	public String getDealPathChannel() {
		return dealPathChannel;
	}

	public void setDealPathChannel(String dealPathChannel) {
		this.dealPathChannel = dealPathChannel;
	}

	public String getDealPathSupplier() {
		return dealPathSupplier;
	}

	public void setDealPathSupplier(String dealPathSupplier) {
		this.dealPathSupplier = dealPathSupplier;
	}
}

