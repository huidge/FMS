package clb.core.channel.dto;

import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CnlMyTeamData {

	private Long channelId;

	// 本周，本月，本年
	private String dateType;

	// 订单类型
	private String orderType;

	// 页码
	private Integer page;

	// 每页数量
	private Integer pageSize;

	// 数量、金额
	private String rankDataType;

	// ASC、DESC
	private String orderingRule;

	public String getRankDataType() {
		return rankDataType;
	}

	public void setRankDataType(String rankDataType) {
		this.rankDataType = rankDataType;
	}

	public String getOrderingRule() {
		return orderingRule;
	}

	public void setOrderingRule(String orderingRule) {
		this.orderingRule = orderingRule;
	}

	// 成员数量
	private Long memberQty;

	// 跟进数量（保险）
	private Long insuranceInProcessQty;

	// 跟进数量（债券）
	private Long bondInProcessQty;

	// 跟进数量（投资移民）
	private Long immigrationInProcessQty;

	// 跟进数量（基金）
	private Long fundInProcessQty;

	// 跟进数量（总量）
	private Long totalInProcessQty;

	// pengding数量
	private Long pengdingQty;

	// 新增订单数量
	private Long newOrderQty;

	// 签单数量
	private Long signQty;

	// 签单总额
	private BigDecimal totalSignAmount;

	// 预计成员收益
	private BigDecimal memberIncome;

	// 团队分成
	private BigDecimal teamDividend;

	private List<CnlMyTeamRank> quantityRank;

	private List<CnlMyTeamRank> amountRank;

	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Long getMemberQty() {
		return memberQty;
	}

	public void setMemberQty(Long memberQty) {
		this.memberQty = memberQty;
	}

	public Long getInsuranceInProcessQty() {
		return insuranceInProcessQty;
	}

	public void setInsuranceInProcessQty(Long insuranceInProcessQty) {
		this.insuranceInProcessQty = insuranceInProcessQty;
	}

	public Long getBondInProcessQty() {
		return bondInProcessQty;
	}

	public void setBondInProcessQty(Long bondInProcessQty) {
		this.bondInProcessQty = bondInProcessQty;
	}

	public Long getImmigrationInProcessQty() {
		return immigrationInProcessQty;
	}

	public void setImmigrationInProcessQty(Long immigrationInProcessQty) {
		this.immigrationInProcessQty = immigrationInProcessQty;
	}

	public Long getFundInProcessQty() {
		return fundInProcessQty;
	}

	public void setFundInProcessQty(Long fundInProcessQty) {
		this.fundInProcessQty = fundInProcessQty;
	}

	public Long getTotalInProcessQty() {
		return totalInProcessQty;
	}

	public void setTotalInProcessQty(Long totalInProcessQty) {
		this.totalInProcessQty = totalInProcessQty;
	}

	public Long getPengdingQty() {
		return pengdingQty;
	}

	public void setPengdingQty(Long pengdingQty) {
		this.pengdingQty = pengdingQty;
	}

	public Long getNewOrderQty() {
		return newOrderQty;
	}

	public void setNewOrderQty(Long newOrderQty) {
		this.newOrderQty = newOrderQty;
	}
	
	public Long getSignQty() {
		return signQty;
	}

	public void setSignQty(Long signQty) {
		this.signQty = signQty;
	}
	
	public BigDecimal getTotalSignAmount() {
		return totalSignAmount;
	}

	public void setTotalSignAmount(BigDecimal totalSignAmount) {
		this.totalSignAmount = totalSignAmount;
	}

	public BigDecimal getMemberIncome() {
		return memberIncome;
	}

	public void setMemberIncome(BigDecimal memberIncome) {
		this.memberIncome = memberIncome;
	}

	public BigDecimal getTeamDividend() {
		return teamDividend;
	}

	public void setTeamDividend(BigDecimal teamDividend) {
		this.teamDividend = teamDividend;
	}

	public List<CnlMyTeamRank> getQuantityRank() {
		return quantityRank;
	}

	public void setQuantityRank(List<CnlMyTeamRank> quantityRank) {
		this.quantityRank = quantityRank;
	}

	public List<CnlMyTeamRank> getAmountRank() {
		return amountRank;
	}

	public void setAmountRank(List<CnlMyTeamRank> amountRank) {
		this.amountRank = amountRank;
	}

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

}
