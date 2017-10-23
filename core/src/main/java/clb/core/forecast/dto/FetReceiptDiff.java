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
@Table(name = "fms_fet_receipt_diff")
public class FetReceiptDiff extends BaseDTO {
     @Id
     @GeneratedValue
      private Long receiptDiffId;

     @NotEmpty
      private String receiptPeriod;
     
     @Transient
     private String receiveSupplierName;
     
     @Transient
     private String paymentSupplierName;


     @NotNull
      private Long receiveSupplierId;

     @NotNull
      private Long paymentSupplierId;

     @NotEmpty
     @Title(title="fet.actual.receipt.receiptType",index=0)
      private String receiptType;
     

     //订单编号
     @Transient
     @Title(title="fet.actual.receipt.orderNumber",index=1)
     private String orderNumber;
     


     @Transient
     @Title(title="fet.receiptdiff.applicantname",index=2)
     private String applicantName;
     

     //产品名称
     @Transient
     @Title(title="fet.actual.receipt.itemName",index=3)
     private String itemName;
     
     //供款期
     @Transient
     @Title(title="fet.actual.receipt.contributionPeriod",index=4)
     private String contributionPeriod;

     //币种
     @Title(title="fet.actual.receipt.orderCurrency",index=5)
     private String orderCurrency;


      @Title(title="fet.actual.receipt.receivableHkd",index=6)
      private BigDecimal receivableHkd;

      @Title(title="fet.actual.receipt.actualReceiptHkd",index=7)
      private BigDecimal actualReceiptHkd;

      @Title(title="fet.actual.receipt.diffHkd",index=8)
      private BigDecimal diffHkd;

      @NotEmpty
      @Title(title="fet.actual.receipt.diffRate",index=9)
      private BigDecimal diffRate;

      @Title(title="fet.actual.receipt.dueDate",index=10)
      private Date dueDate;

      @Title(title="fet.actual.receipt.actualDate",index=11)
      private Date actualDate;

      private Long version;

      @Title(title="fet.actual.receipt.comments",index=12)
      private String comments;

      @NotNull
      private Long orderId;

      @NotNull
      @Column
      private Long itemId;
      
      //当前渠道/供应商状态
      @Transient
      private String paramStatus;
      
      //合并序列号
      private String mergeSeqence;

	public Long getReceiptDiffId() {
		return receiptDiffId;
	}

	public void setReceiptDiffId(Long receiptDiffId) {
		this.receiptDiffId = receiptDiffId;
	}

	public String getReceiptPeriod() {
		return receiptPeriod;
	}

	public void setReceiptPeriod(String receiptPeriod) {
		this.receiptPeriod = receiptPeriod;
	}

	public String getReceiveSupplierName() {
		return receiveSupplierName;
	}

	public void setReceiveSupplierName(String receiveSupplierName) {
		this.receiveSupplierName = receiveSupplierName;
	}

	public String getPaymentSupplierName() {
		return paymentSupplierName;
	}

	public void setPaymentSupplierName(String paymentSupplierName) {
		this.paymentSupplierName = paymentSupplierName;
	}

	public Long getReceiveSupplierId() {
		return receiveSupplierId;
	}

	public void setReceiveSupplierId(Long receiveSupplierId) {
		this.receiveSupplierId = receiveSupplierId;
	}

	public Long getPaymentSupplierId() {
		return paymentSupplierId;
	}

	public void setPaymentSupplierId(Long paymentSupplierId) {
		this.paymentSupplierId = paymentSupplierId;
	}

	public String getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getOrderCurrency() {
		return orderCurrency;
	}

	public void setOrderCurrency(String orderCurrency) {
		this.orderCurrency = orderCurrency;
	}

	public BigDecimal getReceivableHkd() {
		return receivableHkd;
	}

	public void setReceivableHkd(BigDecimal receivableHkd) {
		this.receivableHkd = receivableHkd;
	}

	public BigDecimal getActualReceiptHkd() {
		return actualReceiptHkd;
	}

	public void setActualReceiptHkd(BigDecimal actualReceiptHkd) {
		this.actualReceiptHkd = actualReceiptHkd;
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
	
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
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

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
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