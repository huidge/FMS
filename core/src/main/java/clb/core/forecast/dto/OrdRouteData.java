package clb.core.forecast.dto;

import java.math.BigDecimal;

import clb.core.order.dto.OrdCommission;

public class OrdRouteData {

	//收款结点
	private OrdCommission receiveNode;
	
	//付款结点
	private OrdCommission paymentNode;
	
	private BigDecimal actualRate;
	
	//付款类型
	private String paymentType;

	public OrdCommission getReceiveNode() {
		return receiveNode;
	}

	public void setReceiveNode(OrdCommission receiveNode) {
		this.receiveNode = receiveNode;
	}

	public OrdCommission getPaymentNode() {
		return paymentNode;
	}

	public void setPaymentNode(OrdCommission paymentNode) {
		this.paymentNode = paymentNode;
	}

	public BigDecimal getActualRate() {
		return actualRate;
	}

	public void setActualRate(BigDecimal actualRate) {
		this.actualRate = actualRate;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	
	
}
