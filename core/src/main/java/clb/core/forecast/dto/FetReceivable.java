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
@Table(name = "fms_fet_receivable")
public class FetReceivable extends BaseDTO {
     @Id
     @GeneratedValue
      private Long receivableId;

     @NotEmpty
      private String receiptPeriod;

     @NotEmpty
     @Title(title="fet.actual.receipt.receiptType",index=1)
      private String receiptType;
     
     
     @NotEmpty
     private String paymentCompanyType;
     
     @NotEmpty
     private String receiveCompanyType;
     
     @NotNull
      private Long paymentCompanyId;
     
     @NotNull
     private Long receiveCompanyId;

     @NotNull
      private Long orderId;

     @NotNull
     @Column
     private Long itemId;

     //币种
     @NotNull
     @Title(title="fet.actual.receipt.orderCurrency",index=6)
     private String orderCurrency;

     //金额
     @NotNull
     @Title(title="fet.actual.receipt.amount",index=7)
     private BigDecimal amount;
     
      @Title(title="fet.actual.receipt.rate",index=8)
      private BigDecimal rate;

      @Title(title="fet.actual.receipt.exchangeRate",index=9)
      private BigDecimal exchangeRate;

      @Title(title="fet.actual.receipt.hkdAmount",index=10)
      private BigDecimal hkdAmount;

      @Title(title="fet.actual.receipt.dueDate",index=12)
      private Date dueDate;

      private Long version;

      @Title(title="fet.actual.receipt.comments",index=13)
      private String comments;
      
      //订单编号
      @Transient
      @Title(title="fet.actual.receipt.orderNumber",index=2)
      private String orderNumber;
      
      @Transient
      @Title(title="fet.actual.payment.applicantname",index=3)
      private String applicantName;
      
      //基准日期
      @Transient
      @Title(title="fet.actual.receipt.expectcooldate",index=11)
      private Date baseDate;
      
      //订单类型
      @Transient
      private String orderType;
      
      //付款方名称
      @Transient
      private String paymentCompanyName;
      
      //收款方名称
      @Transient
      private String receiveCompanyName;
      
      //产品名称
      @Transient
      @Title(title="fet.actual.receipt.itemName",index=4)
      private String itemName;
      
      //供款期
      @Transient
      @Title(title="fet.actual.receipt.contributionPeriod",index=5)
      private String contributionPeriod;
      
      //期间Id
      @Transient
      private Long periodId;
      
      //当前渠道/供应商状态
      @Transient
      private String paramStatus;
      
     public void setReceivableId(Long receivableId){
         this.receivableId = receivableId;
     }

     public Long getReceivableId(){
         return receivableId;
     }

     public void setReceiptPeriod(String receiptPeriod){
         this.receiptPeriod = receiptPeriod;
     }

     public String getReceiptPeriod(){
         return receiptPeriod;
     }

     public void setReceiptType(String receiptType){
         this.receiptType = receiptType;
     }

     public String getReceiptType(){
         return receiptType;
     }

     public void setOrderId(Long orderId){
         this.orderId = orderId;
     }

     public Long getOrderId(){
         return orderId;
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

	public BigDecimal getHkdAmount() {
		return hkdAmount;
	}

	public void setHkdAmount(BigDecimal hkdAmount) {
		this.hkdAmount = hkdAmount;
	}

	public void setDueDate(Date dueDate){
         this.dueDate = dueDate;
     }

     public Date getDueDate(){
         return dueDate;
     }
     

     public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public void setComments(String comments){
         this.comments = comments;
     }

     public String getComments(){
         return comments;
     }

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getPaymentCompanyName() {
		return paymentCompanyName;
	}

	public void setPaymentCompanyName(String paymentCompanyName) {
		this.paymentCompanyName = paymentCompanyName;
	}

	public String getReceiveCompanyName() {
		return receiveCompanyName;
	}

	public void setReceiveCompanyName(String receiveCompanyName) {
		this.receiveCompanyName = receiveCompanyName;
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPaymentCompanyType() {
		return paymentCompanyType;
	}

	public void setPaymentCompanyType(String paymentCompanyType) {
		this.paymentCompanyType = paymentCompanyType;
	}

	public String getReceiveCompanyType() {
		return receiveCompanyType;
	}

	public void setReceiveCompanyType(String receiveCompanyType) {
		this.receiveCompanyType = receiveCompanyType;
	}

	public Long getPaymentCompanyId() {
		return paymentCompanyId;
	}

	public void setPaymentCompanyId(Long paymentCompanyId) {
		this.paymentCompanyId = paymentCompanyId;
	}

	public Long getReceiveCompanyId() {
		return receiveCompanyId;
	}

	public void setReceiveCompanyId(Long receiveCompanyId) {
		this.receiveCompanyId = receiveCompanyId;
	}

	public Long getPeriodId() {
		return periodId;
	}

	public void setPeriodId(Long periodId) {
		this.periodId = periodId;
	}


	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public Date getBaseDate() {
		return baseDate;
	}

	public void setBaseDate(Date baseDate) {
		this.baseDate = baseDate;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getParamStatus() {
		return paramStatus;
	}

	public void setParamStatus(String paramStatus) {
		this.paramStatus = paramStatus;
	}

	

     
}
