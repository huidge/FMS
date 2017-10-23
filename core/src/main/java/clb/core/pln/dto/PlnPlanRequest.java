package clb.core.pln.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

@ExtensionAttribute(disable = true)
@Table(name = "fms_pln_plan_request")
public class PlnPlanRequest extends BaseDTO {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long planId;

	@NotEmpty
	private Long channelId;

	@NotEmpty
	private String requestNumber;

	private String policyHolderName;

	@NotEmpty
	private String policyHolderNationality;

	@NotEmpty
	private String policyHolderResidence;

	@NotEmpty
	private String policyHolderGender;

	@NotEmpty
	private String policyHolderSmokeFlag;

	private Date policyHolderBirth;

	@NotEmpty
	private String policyInsurantSameFlag;

	private String insurantName;

	@NotEmpty
	private String insurantNationality;

	@Transient
	private String name;

	@NotEmpty
	private String insurantResidence;

	@NotEmpty
	private String insurantGender;

	@NotEmpty
	private String insurantSmokeFlag;

	@NotEmpty
	private String backtrackFlag;

	private Date insurantBirth;

	@NotNull
	private Long itemId;

	@NotNull
	private Long sublineId;

	private String payMethod;

	private String amount;

	private String currency;

	private String comments;

	private String province;

	private String city;

	@NotEmpty
	private String requestType;

	private String securityLevel;

	private String securityArea;

	private Long selfpayId;

	private String additionalRiskFlag;

	private String extractFlag;

	private String advancedMedicalFlag;

	private String extractType;

	private String extractMethod;

	private Long advancedMedicalItemId;

	private String advancedMedicalSecurityLevel;

	private String advancedMedicalSecurityArea;

	private Long advancedMedicalSelfpayId;

	private Date requestDate;

	private String status;

	private Long processUserId;

	private String processComments;

	private Long programId;

	private Long requestId;

	@Transient
	private Long processId;

	@Transient
	private String crawlerFlag;

	@Transient
	private String itemName;

	@Transient
	private Date startDate;

	@Transient
	private Date endDate;

	@Transient
	private Long userId;

	@Transient
	private String channelName;

	@Transient
	private String filePath;

	private String crawlerReturnMessage;

	private String crawlerState;

	private String amountType;

	private Long fileId;

	private Long createdBy;

	private String supplierName;

	@Transient
	private List<Long> teamIds;

	@Transient
	private String type;

	@Transient
	private String premium;

	@Transient
	private Long age;

	@Transient
	private String libraryAmount;

	@Transient
	List<PlnPlanRequestAdtlRisk> plnPlanRequestAdtlRiskList;

	@Transient
	List<PlnPlanRequestExtract> plnPlanRequestExtractList;

	@Transient
	private String userName;

	@Transient
	private String reviewProcessComments;

	@Transient
	private String sublineItemName;

	@Transient
	private String advancedMedicalItemName;

	@Transient
	private String selfpay;

	@Transient
	private Long disUserId;

	@Transient
	private String disUserName;

	@Transient
	private String frontWebIp;

	@Transient
	private String bigClass;

	@Transient
	private String midClass;

	@Transient
	private String minClass;

	private String downloadFlag;

	@Transient
	private String crawlerStateExt;

	@Transient
	private String crawlerMessageExt;

	@Transient
	private String orderBy;

	private String quantCalcFlag;

	@Transient
	private Integer timeFlag;
	@Transient
	private String topChannels;
	

	public String getTopChannels() {
		return topChannels;
	}

	public void setTopChannels(String topChannels) {
		this.topChannels = topChannels;
	}

	public String getQuantCalcFlag() {
		return quantCalcFlag;
	}

	public void setQuantCalcFlag(String quantCalcFlag) {
		this.quantCalcFlag = quantCalcFlag;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public String getDownloadFlag() {
		return downloadFlag;
	}

	public void setDownloadFlag(String downLoadFlag) {
		this.downloadFlag = downLoadFlag;
	}

	@Transient
	private String advancedMedicalSelfpay;

	public String getAdvancedMedicalSelfpay() {
		return advancedMedicalSelfpay;
	}

	public void setAdvancedMedicalSelfpay(String advancedMedicalSelfpay) {
		this.advancedMedicalSelfpay = advancedMedicalSelfpay;
	}

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

	public String getBigClass() {
		return bigClass;
	}

	public void setBigClass(String bigClass) {
		this.bigClass = bigClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisUserName() {
		return disUserName;
	}

	public void setDisUserName(String disUserName) {
		this.disUserName = disUserName;
	}

	public Long getDisUserId() {
		return disUserId;
	}

	public void setDisUserId(Long disUserId) {
		this.disUserId = disUserId;
	}

	public String getSelfpay() {
		return selfpay;
	}

	public void setSelfpay(String selfpay) {
		this.selfpay = selfpay;
	}

	public String getAdvancedMedicalItemName() {
		return advancedMedicalItemName;
	}

	public void setAdvancedMedicalItemName(String advancedMedicalItemName) {
		this.advancedMedicalItemName = advancedMedicalItemName;
	}

	public String getSublineItemName() {
		return sublineItemName;
	}

	public void setSublineItemName(String sublineItemName) {
		this.sublineItemName = sublineItemName;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getReviewProcessComments() {
		return reviewProcessComments;
	}

	public void setReviewProcessComments(String reviewProcessComments) {
		this.reviewProcessComments = reviewProcessComments;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}


	public Long getAge() {
		return age;
	}

	public void setAge(Long age) {
		this.age = age;
	}


	public List<PlnPlanRequestAdtlRisk> getPlnPlanRequestAdtlRiskList() {
		return plnPlanRequestAdtlRiskList;
	}

	public void setPlnPlanRequestAdtlRiskList(List<PlnPlanRequestAdtlRisk> plnPlanRequestAdtlRiskList) {
		this.plnPlanRequestAdtlRiskList = plnPlanRequestAdtlRiskList;
	}

	public List<PlnPlanRequestExtract> getPlnPlanRequestExtractList() {
		return plnPlanRequestExtractList;
	}

	public void setPlnPlanRequestExtractList(List<PlnPlanRequestExtract> plnPlanRequestExtractList) {
		this.plnPlanRequestExtractList = plnPlanRequestExtractList;
	}

	public String getCrawlerReturnMessage() {
		return crawlerReturnMessage;
	}

	public void setCrawlerReturnMessage(String crawlerReturnMessage) {
		this.crawlerReturnMessage = crawlerReturnMessage;
	}

	public String getCrawlerState() {
		return crawlerState;
	}

	public void setCrawlerState(String crawlerState) {
		this.crawlerState = crawlerState;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;
	}

	public Long getPlanId() {
		return planId;
	}

	public void setRequestNumber(String requestNumber) {
		this.requestNumber = requestNumber;
	}

	public String getRequestNumber() {
		return requestNumber;
	}

	public void setPolicyHolderName(String policyHolderName) {
		this.policyHolderName = policyHolderName;
	}

	public String getPolicyHolderName() {
		return policyHolderName;
	}

	public void setPolicyHolderNationality(String policyHolderNationality) {
		this.policyHolderNationality = policyHolderNationality;
	}

	public String getPolicyHolderNationality() {
		return policyHolderNationality;
	}

	public void setPolicyHolderResidence(String policyHolderResidence) {
		this.policyHolderResidence = policyHolderResidence;
	}

	public String getPolicyHolderResidence() {
		return policyHolderResidence;
	}

	public void setPolicyHolderGender(String policyHolderGender) {
		this.policyHolderGender = policyHolderGender;
	}

	public String getPolicyHolderGender() {
		return policyHolderGender;
	}

	public void setPolicyHolderSmokeFlag(String policyHolderSmokeFlag) {
		this.policyHolderSmokeFlag = policyHolderSmokeFlag;
	}

	public String getPolicyHolderSmokeFlag() {
		return policyHolderSmokeFlag;
	}

	public void setPolicyHolderBirth(Date policyHolderBirth) {
		this.policyHolderBirth = policyHolderBirth;
	}

	public Date getPolicyHolderBirth() {
		return policyHolderBirth;
	}

	public void setPolicyInsurantSameFlag(String policyInsurantSameFlag) {
		this.policyInsurantSameFlag = policyInsurantSameFlag;
	}

	public String getPolicyInsurantSameFlag() {
		return policyInsurantSameFlag;
	}

	public void setInsurantName(String insurantName) {
		this.insurantName = insurantName;
	}

	public String getInsurantName() {
		return insurantName;
	}

	public void setInsurantNationality(String insurantNationality) {
		this.insurantNationality = insurantNationality;
	}

	public String getInsurantNationality() {
		return insurantNationality;
	}

	public void setInsurantResidence(String insurantResidence) {
		this.insurantResidence = insurantResidence;
	}

	public String getInsurantResidence() {
		return insurantResidence;
	}

	public void setInsurantGender(String insurantGender) {
		this.insurantGender = insurantGender;
	}

	public String getInsurantGender() {
		return insurantGender;
	}

	public void setInsurantSmokeFlag(String insurantSmokeFlag) {
		this.insurantSmokeFlag = insurantSmokeFlag;
	}

	public String getInsurantSmokeFlag() {
		return insurantSmokeFlag;
	}

	public void setInsurantBirth(Date insurantBirth) {
		this.insurantBirth = insurantBirth;
	}

	public Date getInsurantBirth() {
		return insurantBirth;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setSublineId(Long sublineId) {
		this.sublineId = sublineId;
	}

	public Long getSublineId() {
		return sublineId;
	}

	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}

	public String getPayMethod() {
		return payMethod;
	}



	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCurrency() {
		return currency;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getProvince() {
		return province;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setSecurityLevel(String securityLevel) {
		this.securityLevel = securityLevel;
	}

	public String getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityArea(String securityArea) {
		this.securityArea = securityArea;
	}

	public String getSecurityArea() {
		return securityArea;
	}

	public void setSelfpayId(Long selfpayId) {
		this.selfpayId = selfpayId;
	}

	public Long getSelfpayId() {
		return selfpayId;
	}

	public void setAdditionalRiskFlag(String additionalRiskFlag) {
		this.additionalRiskFlag = additionalRiskFlag;
	}

	public String getAdditionalRiskFlag() {
		return additionalRiskFlag;
	}

	public void setExtractFlag(String extractFlag) {
		this.extractFlag = extractFlag;
	}

	public String getExtractFlag() {
		return extractFlag;
	}

	public void setAdvancedMedicalFlag(String advancedMedicalFlag) {
		this.advancedMedicalFlag = advancedMedicalFlag;
	}

	public String getAdvancedMedicalFlag() {
		return advancedMedicalFlag;
	}

	public void setExtractType(String extractType) {
		this.extractType = extractType;
	}

	public String getExtractType() {
		return extractType;
	}

	public void setExtractMethod(String extractMethod) {
		this.extractMethod = extractMethod;
	}

	public String getExtractMethod() {
		return extractMethod;
	}

	public void setAdvancedMedicalItemId(Long advancedMedicalItemId) {
		this.advancedMedicalItemId = advancedMedicalItemId;
	}

	public Long getAdvancedMedicalItemId() {
		return advancedMedicalItemId;
	}

	public void setAdvancedMedicalSecurityLevel(String advancedMedicalSecurityLevel) {
		this.advancedMedicalSecurityLevel = advancedMedicalSecurityLevel;
	}

	public String getAdvancedMedicalSecurityLevel() {
		return advancedMedicalSecurityLevel;
	}

	public void setAdvancedMedicalSecurityArea(String advancedMedicalSecurityArea) {
		this.advancedMedicalSecurityArea = advancedMedicalSecurityArea;
	}

	public String getAdvancedMedicalSecurityArea() {
		return advancedMedicalSecurityArea;
	}

	public void setAdvancedMedicalSelfpayId(Long advancedMedicalSelfpayId) {
		this.advancedMedicalSelfpayId = advancedMedicalSelfpayId;
	}

	public Long getAdvancedMedicalSelfpayId() {
		return advancedMedicalSelfpayId;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setProcessUserId(Long processUserId) {
		this.processUserId = processUserId;
	}

	public Long getProcessUserId() {
		return processUserId;
	}

	public void setProcessComments(String processComments) {
		this.processComments = processComments;
	}

	public String getProcessComments() {
		return processComments;
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

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getAmountType() {
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Long> getTeamIds() {
		return teamIds;
	}

	public void setTeamIds(List<Long> teamIds) {
		this.teamIds = teamIds;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getBacktrackFlag() {
		return backtrackFlag;
	}

	public void setBacktrackFlag(String backtrackFlag) {
		this.backtrackFlag = backtrackFlag;
	}

	public String getCrawlerFlag() {
		return crawlerFlag;
	}

	public void setCrawlerFlag(String crawlerFlag) {
		this.crawlerFlag = crawlerFlag;
	}

	public String getFrontWebIp() {
		return frontWebIp;
	}

	public void setFrontWebIp(String frontWebIp) {
		this.frontWebIp = frontWebIp;
	}

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getCrawlerStateExt() {
		return crawlerStateExt;
	}

	public void setCrawlerStateExt(String crawlerStateExt) {
		this.crawlerStateExt = crawlerStateExt;
	}

	public String getCrawlerMessageExt() {
		return crawlerMessageExt;
	}

	public void setCrawlerMessageExt(String crawlerMessageExt) {
		this.crawlerMessageExt = crawlerMessageExt;
	}

	public Integer getTimeFlag() {
		return timeFlag;
	}

	public void setTimeFlag(Integer timeFlag) {
		this.timeFlag = timeFlag;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getPremium() {
		return premium;
	}

	public void setPremium(String premium) {
		this.premium = premium;
	}

	public String getLibraryAmount() {
		return libraryAmount;
	}

	public void setLibraryAmount(String libraryAmount) {
		this.libraryAmount = libraryAmount;
	}

	
	
	
	
}