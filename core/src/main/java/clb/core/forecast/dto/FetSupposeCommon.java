package clb.core.forecast.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hand.hap.system.dto.BaseDTO;


/**
 * 应付和应收订单和交易路线数据
 */
public class FetSupposeCommon extends BaseDTO {

	/* --------------订单信息-------------- */
	
	//订单Id	
	private Long orderId;	
	//主险Id	
	private Long itemId;
	//订单编号	
	private String orderNumber;		
	//预计冷静期	
	@JsonFormat(pattern = "yyyy-MM-dd")    
	private Date expectCoolDate;
	//签约日期/发行日期
	@JsonFormat(pattern = "yyyy-MM-dd")    
	private Date issueDate;
	//续保到期日	
	@JsonFormat(pattern = "yyyy-MM-dd")    
	private Date renewalDueDate;
	//保额
	private BigDecimal policyAmount;
	//年缴费额度
	private BigDecimal yearPayAmount;
	//签单费
	private BigDecimal signFee;
	//刷卡费
    private BigDecimal cardFee;
    //当前期数
  	private Long payPeriods;
	//订单币种
	private String orderCurrency;
	//保单编号
	private String policyNumber;
	//订单类型
	private String orderType;
	
	/* --------------订单信息-------------- */
	
	
	/* --------------产品信息-------------- */
	
	//产品名称	
	private String itemName;
	//产品分类
    private String bigClass;
	//产品公司
	private Long itemCompanyId;
	//产品公司名称
	private String itemCompanyName;
	
	/* --------------产品信息-------------- */
	
	/* --------------其他信息-------------- */
	
	//供款期	
	private String sublineItemName;	
	
	//期间,用作参数
	@Transient
	private Long periodId;
	
	//期间名称,用作参数
	@Transient
	private String periodName;
	
	//请求类型,用作参数
	@Transient
	private String requestType;
	
	//版本,用作参数
	@Transient
	private Long version;
	
	/* --------------其他信息-------------- */

	//付款方
	@Transient
	private Long paymentCompanyId;
	
	//收款方
	@Transient
	private Long receiveCompanyId;
	
	//付款方类型
	@Transient
	private String paymentCompanyType;
	
	//收款方类型
	@Transient
	private String receiveCompanyType;
	

	//付款方名称
	@Transient
	private String paymentCompanyName;
	

	//付款方名称
	@Transient
	private String receiveCompanyName;
	
	//收入/付款类型
	@Transient
	private String paymentType;
	
	//金额
	@Transient
	private BigDecimal amount;
	
	//费率
	@Transient
	private BigDecimal rate;   
	
	//费率
	@Transient
	private BigDecimal exchangeRate; 
	
	//应付日期
	@Transient
	private Date dueDate;
	
	//备注
	@Transient
	private String comments;
	
	//实际拿的费率
	@Transient
	private BigDecimal actualRate;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Date getExpectCoolDate() {
		return expectCoolDate;
	}

	public void setExpectCoolDate(Date expectCoolDate) {
		this.expectCoolDate = expectCoolDate;
	}

	public BigDecimal getYearPayAmount() {
		return yearPayAmount;
	}

	public void setYearPayAmount(BigDecimal yearPayAmount) {
		this.yearPayAmount = yearPayAmount;
	}

	public BigDecimal getSignFee() {
		return signFee;
	}

	public void setSignFee(BigDecimal signFee) {
		this.signFee = signFee;
	}

	public BigDecimal getCardFee() {
		return cardFee;
	}

	public void setCardFee(BigDecimal cardFee) {
		this.cardFee = cardFee;
	}

	public Long getPayPeriods() {
		return payPeriods;
	}

	public void setPayPeriods(Long payPeriods) {
		this.payPeriods = payPeriods;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getBigClass() {
		return bigClass;
	}

	public void setBigClass(String bigClass) {
		this.bigClass = bigClass;
	}

	public Long getItemCompanyId() {
		return itemCompanyId;
	}

	public void setItemCompanyId(Long itemCompanyId) {
		this.itemCompanyId = itemCompanyId;
	}

	public String getSublineItemName() {
		return sublineItemName;
	}

	public void setSublineItemName(String sublineItemName) {
		this.sublineItemName = sublineItemName;
	}
	
	public Long getPeriodId() {
		return periodId;
	}

	public void setPeriodId(Long periodId) {
		this.periodId = periodId;
	}

	public String getOrderCurrency() {
		return orderCurrency;
	}

	public void setOrderCurrency(String orderCurrency) {
		this.orderCurrency = orderCurrency;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	
	

	public Long getPaymentCompanyId() {
		return paymentCompanyId;
	}

	public void setPaymentCompanyId(Long paymentCompanyId) {
		this.paymentCompanyId = paymentCompanyId;
	}

	public String getPaymentCompanyType() {
		return paymentCompanyType;
	}

	public void setPaymentCompanyType(String paymentCompanyType) {
		this.paymentCompanyType = paymentCompanyType;
	}

	public Long getReceiveCompanyId() {
		return receiveCompanyId;
	}

	public void setReceiveCompanyId(Long receiveCompanyId) {
		this.receiveCompanyId = receiveCompanyId;
	}

	public String getReceiveCompanyType() {
		return receiveCompanyType;
	}

	public void setReceiveCompanyType(String receiveCompanyType) {
		this.receiveCompanyType = receiveCompanyType;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
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

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
	
	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public BigDecimal getActualRate() {
		return actualRate;
	}

	public void setActualRate(BigDecimal actualRate) {
		this.actualRate = actualRate;
	}

	public String getPeriodName() {
		return periodName;
	}

	public void setPeriodName(String periodName) {
		this.periodName = periodName;
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

	public Date getRenewalDueDate() {
		return renewalDueDate;
	}

	public void setRenewalDueDate(Date renewalDueDate) {
		this.renewalDueDate = renewalDueDate;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public BigDecimal getPolicyAmount() {
		return policyAmount;
	}

	public void setPolicyAmount(BigDecimal policyAmount) {
		this.policyAmount = policyAmount;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public String getItemCompanyName() {
		return itemCompanyName;
	}

	public void setItemCompanyName(String itemCompanyName) {
		this.itemCompanyName = itemCompanyName;
	}
	
	
	
	
	
	
	
}
