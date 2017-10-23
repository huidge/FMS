package clb.core.order.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ExtensionAttribute(disable=true)
@Table(name = "fms_ord_pending")
public class OrdPending extends BaseDTO {
    @Id
    @GeneratedValue
    private Long pendingId;

    @NotNull
    private String pendingNumber;

    @NotNull
    private Long orderId;

    @NotNull
    private Long templateId;

    private String status;

    private String dealPerson;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dealEndDate;

    private Long programId;

    private Long requestId;

    @Transient
    private String policyNumber;

    @Transient
    private String orderInfo;

    @Transient
    private String channelName;

    @Transient
    private String applyClassCode;

    @Transient
    private String applyItem;

    @Transient
    private String applicant;

    @Transient
    private String insurant;

    @Transient
    private Long cb;

    @Transient
    private String  cbName;

    @Transient
    private Date lud;

    @Transient
    private String dealPersonName;

    @Transient
    private String reserveSupplierName;

    @Transient
    private String applyClassDesc;

    @Transient
    private String person;

    @Transient
    private String statusDesc;

    @Transient
    private String orderNumber;

    @Transient
    private Date reserveDate;

    @Transient
    private Long parentChannelId;

    @Transient
    private Long channelId;

    @Transient
    private String matchUserFlag;

    @Transient
    private Long currentUserId;

    @Transient
    private String orderStatus;

    @Transient
    private String orderType;

    @Transient
    private Long contractRoleId;

    @Transient
    private String roleName;

    @Transient
    private String orderBy;
    
    @Transient
    private String  preName;//对接业务行政
    
    @Transient
    private String  stairSell;//所属一级
    
    @Transient
    private String  signSupplierName;//签单公司
    
    @Transient
    private Long  channelContractId;//签单公司

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getMatchUserFlag() {
        return matchUserFlag;
    }

    public void setMatchUserFlag(String matchUserFlag) {
        this.matchUserFlag = matchUserFlag;
    }

    public void setPendingId(Long pendingId){
        this.pendingId = pendingId;
    }

    public Long getPendingId(){
        return pendingId;
    }

    public void setTemplateId(Long templateId){
        this.templateId = templateId;
    }

    public Long getTemplateId(){
        return templateId;
    }

    public String getPendingNumber() {
        return pendingNumber;
    }

    public void setPendingNumber(String pendingNumber) {
        this.pendingNumber = pendingNumber;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

    public void setDealPerson(String dealPerson){
        this.dealPerson = dealPerson;
    }

    public String getDealPerson(){
        return dealPerson;
    }

    public Date getDealEndDate() {
        return dealEndDate;
    }

    public void setDealEndDate(Date dealEndDate) {
        this.dealEndDate = dealEndDate;
    }

    public void setProgramId(Long programId){
        this.programId = programId;
    }

    public Long getProgramId(){
        return programId;
    }

    public void setRequestId(Long requestId){
        this.requestId = requestId;
    }

    public Long getRequestId(){
        return requestId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getApplyClassCode() {
        return applyClassCode;
    }

    public void setApplyClassCode(String applyClassCode) {
        this.applyClassCode = applyClassCode;
    }

    public String getApplyItem() {
        return applyItem;
    }

    public void setApplyItem(String applyItem) {
        this.applyItem = applyItem;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getInsurant() {
        return insurant;
    }

    public void setInsurant(String insurant) {
        this.insurant = insurant;
    }

    public Long getCb() {
        return cb;
    }

    public void setCb(Long cb) {
        this.cb = cb;
    }

    public String getCbName() {
        return cbName;
    }

    public void setCbName(String cbName) {
        this.cbName = cbName;
    }

    public Date getLud() {
        return lud;
    }

    public void setLud(Date lud) {
        this.lud = lud;
    }

    public String getDealPersonName() {
        return dealPersonName;
    }

    public void setDealPersonName(String dealPersonName) {
        this.dealPersonName = dealPersonName;
    }

    public String getReserveSupplierName() {
        return reserveSupplierName;
    }

    public void setReserveSupplierName(String reserveSupplierName) {
        this.reserveSupplierName = reserveSupplierName;
    }

    public String getApplyClassDesc() {
        return applyClassDesc;
    }

    public void setApplyClassDesc(String applyClassDesc) {
        this.applyClassDesc = applyClassDesc;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Date getReserveDate() {
        return reserveDate;
    }

    public void setReserveDate(Date reserveDate) {
        this.reserveDate = reserveDate;
    }

    public Long getParentChannelId() {
        return parentChannelId;
    }

    public void setParentChannelId(Long parentChannelId) {
        this.parentChannelId = parentChannelId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Long getContractRoleId() {
        return contractRoleId;
    }

    public void setContractRoleId(Long contractRoleId) {
        this.contractRoleId = contractRoleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

	public String getPreName() {
		return preName;
	}

	public void setPreName(String preName) {
		this.preName = preName;
	}

	public String getStairSell() {
		return stairSell;
	}

	public void setStairSell(String stairSell) {
		this.stairSell = stairSell;
	}

	public String getSignSupplierName() {
		return signSupplierName;
	}

	public void setSignSupplierName(String signSupplierName) {
		this.signSupplierName = signSupplierName;
	}

	public Long getChannelContractId() {
		return channelContractId;
	}

	public void setChannelContractId(Long channelContractId) {
		this.channelContractId = channelContractId;
	}
    
}
