package clb.core.production.dto;

import java.io.Serializable;
import java.util.List;

public class PrdRadarChart implements Serializable{
	private static final long serialVersionUID = -815681267245635259L;

	/*private Long itemId;
	
	private String itemName;
	
	private Long lineId;
	
	private Long partId;
	
	private String partName;
	
	private String attrName;
	
	private String detail;

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Long getLineId() {
		return lineId;
	}

	public void setLineId(Long lineId) {
		this.lineId = lineId;
	}

	public Long getPartId() {
		return partId;
	}

	public void setPartId(Long partId) {
		this.partId = partId;
	}

	public String getPartName() {
		return partName;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}*/
	
	private List<PrdRadarChartItem> itemList;
	
	private List<PrdRadarChartLine> scoreList;

	public List<PrdRadarChartItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<PrdRadarChartItem> itemList) {
		this.itemList = itemList;
	}

	public List<PrdRadarChartLine> getScoreList() {
		return scoreList;
	}

	public void setScoreList(List<PrdRadarChartLine> scoreList) {
		this.scoreList = scoreList;
	}
	
}
