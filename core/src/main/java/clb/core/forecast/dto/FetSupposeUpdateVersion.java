package clb.core.forecast.dto;

/**
 * 更新全部版本参数
 */
public class FetSupposeUpdateVersion{
	//期间Id	
	private Long periodId;		
	//请求类型
	private String requestType;
	//付款方类型
	private String paymentCompanyType;
	//付款方Id
	private Long paymentCompanyId;
	//收款方类型
	private String receiveCompanyType;
	//收款方Id
	private Long receiveCompanyId;
	public Long getPeriodId() {
		return periodId;
	}
	public void setPeriodId(Long periodId) {
		this.periodId = periodId;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
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
	
	
}
