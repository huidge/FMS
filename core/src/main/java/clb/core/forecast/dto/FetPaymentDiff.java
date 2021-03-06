package clb.core.forecast.dto;

import javax.persistence.Column;
/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;

import clb.core.common.annotations.Title;

import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
@ExtensionAttribute(disable=true)
@Table(name = "fms_fet_payment_diff")
public class FetPaymentDiff extends BaseDTO {
     @Id
     @GeneratedValue
      private Long paymentDiffId;

     @NotEmpty
      private String paymentPeriod;
     
     @NotEmpty
     private String receiveCompanyType;
     
     @Transient
     private String receiveCompanyName;
      
     @Transient
     private String paymentSupplierName;
     
     @NotNull
     private Long receiveCompanyId;

    @NotNull
     private Long paymentSupplierId;
     


     @NotEmpty
     @Title(title="fet.actual.payment.paymentType",index=0)
      private String paymentType;


     //订单编号
     @Transient
     @Title(title="fet.actual.payment.orderNumber",index=1)
     private String orderNumber;
     
     @Transient
     @Title(title="fet.actual.payment.applicantname",index=2)
     private String applicantName;
     
     //产品名称
     @Transient
     @Title(title="fet.actual.payment.itemName",index=3)
     private String itemName;
     
     //供款期
     @Transient
     @Title(title="fet.actual.payment.contributionPeriod",index=4)
     private String contributionPeriod;
     
     //币种
     @Title(title="fet.actual.payment.orderCurrency",index=5)
     private String orderCurrency;
     
     @Title(title="fet.actual.payment.payableHkd",index=6)
     private BigDecimal payableHkd;

    @Title(title="fet.actual.payment.actualPaymentHkd",index=7)
     private BigDecimal actualPaymentHkd;

    @Title(title="fet.actual.payment.diffHkd",index=8)
     private BigDecimal diffHkd;

    @Title(title="fet.actual.payment.diffRate",index=9)
     private BigDecimal diffRate;

    @Title(title="fet.actual.payment.dueDate",index=10)
     private Date dueDate;

    @Title(title="fet.actual.payment.actualDate",index=11)
     private Date actualDate;
    
    @Title(title="fet.actual.payment.comments",index=12)
    private String comments;

    @NotNull
    private Long orderId;
    

    @NotNull
    @Column
    private Long itemId;

    private Long version;
    
    //当前渠道/供应商状态
    @Transient
    private String paramStatus;

    //合并序列号
    private String mergeSeqence;

	public Long getPaymentDiffId() {
		return paymentDiffId;
	}

	public void setPaymentDiffId(Long paymentDiffId) {
		this.paymentDiffId = paymentDiffId;
	}

	public String getPaymentPeriod() {
		return paymentPeriod;
	}

	public void setPaymentPeriod(String paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getContributionPeriod() {
		return contributionPeriod;
	}

	public void setContributionPeriod(String contributionPeriod) {
		this.contributionPeriod = contributionPeriod;
	}

	public String getOrderCurrency() {
		return orderCurrency;
	}

	public void setOrderCurrency(String orderCurrency) {
		this.orderCurrency = orderCurrency;
	}

	public BigDecimal getPayableHkd() {
		return payableHkd;
	}

	public void setPayableHkd(BigDecimal payableHkd) {
		this.payableHkd = payableHkd;
	}

	public BigDecimal getActualPaymentHkd() {
		return actualPaymentHkd;
	}

	public void setActualPaymentHkd(BigDecimal actualPaymentHkd) {
		this.actualPaymentHkd = actualPaymentHkd;
	}

	public BigDecimal getDiffHkd() {
		return diffHkd;
	}

	public void setDiffHkd(BigDecimal diffHkd) {
		this.diffHkd = diffHkd;
	}

	public BigDecimal getDiffRate() {
		return diffRate;
	}

	public void setDiffRate(BigDecimal diffRate) {
		this.diffRate = diffRate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getActualDate() {
		return actualDate;
	}

	public void setActualDate(Date actualDate) {
		this.actualDate = actualDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getReceiveCompanyType() {
		return receiveCompanyType;
	}

	public void setReceiveCompanyType(String receiveCompanyType) {
		this.receiveCompanyType = receiveCompanyType;
	}

	public String getReceiveCompanyName() {
		return receiveCompanyName;
	}

	public void setReceiveCompanyName(String receiveCompanyName) {
		this.receiveCompanyName = receiveCompanyName;
	}

	public String getPaymentSupplierName() {
		return paymentSupplierName;
	}

	public void setPaymentSupplierName(String paymentSupplierName) {
		this.paymentSupplierName = paymentSupplierName;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getReceiveCompanyId() {
		return receiveCompanyId;
	}

	public void setReceiveCompanyId(Long receiveCompanyId) {
		this.receiveCompanyId = receiveCompanyId;
	}

	public Long getPaymentSupplierId() {
		return paymentSupplierId;
	}

	public void setPaymentSupplierId(Long paymentSupplierId) {
		this.paymentSupplierId = paymentSupplierId;
	}

	public String getMergeSeqence() {
		return mergeSeqence;
	}

	public void setMergeSeqence(String mergeSeqence) {
		this.mergeSeqence = mergeSeqence;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getParamStatus() {
		return paramStatus;
	}

	public void setParamStatus(String paramStatus) {
		this.paramStatus = paramStatus;
	}
	
	
	
}
