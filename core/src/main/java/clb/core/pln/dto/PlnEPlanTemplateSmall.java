package clb.core.pln.dto;

import java.util.List;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.hand.hap.core.annotation.Children;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

@ExtensionAttribute(disable = true)
@Table(name = "fms_pln_e_plan_template_small")
public class PlnEPlanTemplateSmall extends BaseDTO {
	@Id
	@GeneratedValue
	private Long smallId;

	@NotNull
	private Long bigId;

	@NotNull
	private Long seqNum;

	@NotEmpty
	private String title;

	@NotEmpty
	private String amountFormula;

	@NotEmpty
	private String fixedValue;

	@Transient
	@Children
	private List<PlnEPlanTemplateDetail> plnEPlanTemplateDetailList;

	private Long programId;

	private Long requestId;

	public void setSmallId(Long smallId) {
		this.smallId = smallId;
	}

	public Long getSmallId() {
		return smallId;
	}

	public void setBigId(Long bigId) {
		this.bigId = bigId;
	}

	public Long getBigId() {
		return bigId;
	}

	public void setSeqNum(Long seqNum) {
		this.seqNum = seqNum;
	}

	public Long getSeqNum() {
		return seqNum;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setAmountFormula(String amountFormula) {
		this.amountFormula = amountFormula;
	}

	public String getAmountFormula() {
		return amountFormula;
	}

	public void setFixedValue(String fixedValue) {
		this.fixedValue = fixedValue;
	}

	public String getFixedValue() {
		return fixedValue;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	public Long getProgramId() {
		return programId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public Long getRequestId() {
		return requestId;
	}

	public List<PlnEPlanTemplateDetail> getPlnEPlanTemplateDetailList() {
		return plnEPlanTemplateDetailList;
	}

	public void setPlnEPlanTemplateDetailList(List<PlnEPlanTemplateDetail> plnEPlanTemplateDetailList) {
		this.plnEPlanTemplateDetailList = plnEPlanTemplateDetailList;
	}
	
}
