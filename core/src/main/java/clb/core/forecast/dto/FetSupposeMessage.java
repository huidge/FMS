package clb.core.forecast.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;

import org.apache.xpath.operations.Bool;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hand.hap.system.dto.BaseDTO;


/**
 * 应付和应收错误信息
 */
public class FetSupposeMessage extends BaseDTO {

	/* --------------订单信息-------------- */
	//订单编号	
	private String orderNumber;		
	//状态
	private String status;
	//错误信息
	private String errorMessage;
	//付款方类型
	private String paymentCompanyType;
	//付款方Id
	private Long paymentCompanyId;
	//收款方类型
	private String receiveCompanyType;
	//收款方Id
	private Long receiveCompanyId;
	//是否考虑收/付款方,默认false
	private boolean isConsiderRP;
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPaymentCompanyType() {
		return paymentCompanyType;
	}
	public void setPaymentCompanyType(String paymentCompanyType) {
		this.paymentCompanyType = paymentCompanyType;
	}
	public Long getPaymentCompanyId() {
		return paymentCompanyId;
	}
	public void setPaymentCompanyId(Long paymentCompanyId) {
		this.paymentCompanyId = paymentCompanyId;
	}
	public String getReceiveCompanyType() {
		return receiveCompanyType;
	}
	public void setReceiveCompanyType(String receiveCompanyType) {
		this.receiveCompanyType = receiveCompanyType;
	}
	public Long getReceiveCompanyId() {
		return receiveCompanyId;
	}
	public void setReceiveCompanyId(Long receiveCompanyId) {
		this.receiveCompanyId = receiveCompanyId;
	}
	public boolean isConsiderRP() {
		return isConsiderRP;
	}
	public void setConsiderRP(boolean isConsiderRP) {
		this.isConsiderRP = isConsiderRP;
	}
	
	
	
	
}
