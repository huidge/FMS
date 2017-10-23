package clb.core.channel.dto;

import java.math.BigDecimal;

public class CnlMyTeamRank {

	private Long rankNum;

	private Long channelId;

	private String channelName;

	private Long signOrderQty;

	private BigDecimal signAmount;

	private Double variinePercent;

	private String photoFilePath;

	public Long getRankNum() {
		return rankNum;
	}

	public void setRankNum(Long rankNum) {
		this.rankNum = rankNum;
	}

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public Long getSignOrderQty() {
		return signOrderQty;
	}

	public void setSignOrderQty(Long signOrderQty) {
		this.signOrderQty = signOrderQty;
	}

	public BigDecimal getSignAmount() {
		return signAmount;
	}

	public void setSignAmount(BigDecimal signAmount) {
		this.signAmount = signAmount;
	}

	public Double getVariinePercent() {
		return variinePercent;
	}

	public void setVariinePercent(Double variinePercent) {
		this.variinePercent = variinePercent;
	}

	public String getPhotoFilePath() {
		return photoFilePath;
	}

	public void setPhotoFilePath(String photoFilePath) {
		this.photoFilePath = photoFilePath;
	}
}
