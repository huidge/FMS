package clb.core.production.dto;

import java.util.List;

public class PrdRadarChartLine {

	private String attrName;
	
	private String detail1;
	
	private String detail2;
	
	private String compareRule;
	
	private String detail3;

	private List<PrdRadarChartLine> lineList;
	
	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public String getDetail1() {
		return detail1;
	}

	public void setDetail1(String detail1) {
		this.detail1 = detail1;
	}

	public String getDetail2() {
		return detail2;
	}

	public void setDetail2(String detail2) {
		this.detail2 = detail2;
	}

	public String getDetail3() {
		return detail3;
	}

	public void setDetail3(String detail3) {
		this.detail3 = detail3;
	}

	public List<PrdRadarChartLine> getLineList() {
		return lineList;
	}

	public void setLineList(List<PrdRadarChartLine> lineList) {
		this.lineList = lineList;
	}

	public String getCompareRule() {
		return compareRule;
	}

	public void setCompareRule(String compareRule) {
		this.compareRule = compareRule;
	}
	
}
