package clb.core.forecast.dto;

import java.math.BigDecimal;
import java.util.Date;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;

import clb.core.common.annotations.Title;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
@ExtensionAttribute(disable=true)
@Table(name = "fms_fet_actual_payment_summary")
public class FetActualPaymentSummary extends BaseDTO {
     @Id
     @GeneratedValue
      private Long paymentSummaryId;

     @NotEmpty
     @Title(title="fet.actual.payment.paymentperiod",index=1)
      private String paymentPeriod;

     @NotEmpty
     @Title(title="fet.actual.payment.receiveCompanyType",index=2)
      private String receiveCompanyType;
     
     @Transient
     @Title(title="fet.actual.payment.receiveCompanyName",index=3)
     private String receiveCompanyName;
     
     @Transient
     @Title(title="fet.actual.payment.paymentsupplierid",index=4)
     private String paymentSupplierName;
     
     //付款类型
     @Transient
     @Title(title="fet.actual.payment.paymentType",index=5)
      private String paymentType;
     

     //订单编号
     @Transient
     @Title(title="fet.actual.payment.orderNumber",index=6)
     private String orderNumber;
     
     //产品名称
     @Transient
     @Title(title="fet.actual.payment.itemName",index=7)
     private String itemName;
     
     //币种
     @Transient
     @Title(title="fet.actual.payment.orderCurrency",index=8)
     private String orderCurrency;

     //金额
     @Transient
     @Title(title="fet.actual.payment.amount",index=9)
     private BigDecimal amount;
     
     @Transient
     @Title(title="fet.actual.payment.rate",index=10)
      private BigDecimal rate;

     @Transient
     @Title(title="fet.actual.payment.exchangeRate",index=11)
      private BigDecimal exchangeRate;
     
     @Transient
     @Title(title="fet.actual.payment.actualDate",index=12)
     private Date actualDate;

     @Transient
     @Title(title="fet.actual.payment.comments",index=13)
     private String comments;
     
     @Transient
     private Long orderId;

     @NotNull
      private Long receiveCompanyId;

     @NotNull
      private Long paymentSupplierId;

     @NotNull
      private Long version;

      private BigDecimal hkdAmount;

      private Long certificateFileId;

      private Long programId;

      private Long requestId;

      @Transient
      private String fileToken;
      
      @Transient
      private Long itemId;
      
      @Transient
      private Long oldFileId;

	public Long getPaymentSummaryId() {
		return paymentSummaryId;
	}

	public void setPaymentSummaryId(Long paymentSummaryId) {
		this.paymentSummaryId = paymentSummaryId;
	}

	public String getPaymentPeriod() {
		return paymentPeriod;
	}

	public void setPaymentPeriod(String paymentPeriod) {
		this.paymentPeriod = paymentPeriod;
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

	public String getOrderCurrency() {
		return orderCurrency;
	}

	public void setOrderCurrency(String orderCurrency) {
		this.orderCurrency = orderCurrency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
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

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
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

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	
	public BigDecimal getHkdAmount() {
		return hkdAmount;
	}

	public void setHkdAmount(BigDecimal hkdAmount) {
		this.hkdAmount = hkdAmount;
	}

	public Long getCertificateFileId() {
		return certificateFileId;
	}

	public void setCertificateFileId(Long certificateFileId) {
		this.certificateFileId = certificateFileId;
	}

	public Long getProgramId() {
		return programId;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public String getFileToken() {
		return fileToken;
	}

	public void setFileToken(String fileToken) {
		this.fileToken = fileToken;
	}

	public Long getOldFileId() {
		return oldFileId;
	}

	public void setOldFileId(Long oldFileId) {
		this.oldFileId = oldFileId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

      
     
     

}
