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
@Table(name = "fms_pln_e_plan_template_big")
public class PlnEPlanTemplateBig extends BaseDTO {
	@Id
	@GeneratedValue
	private Long bigId;

	@NotNull
	private Long seqNum;

	@NotNull
	private Long templateId;

	@NotEmpty
	private String bigTitle;

	@Transient
	@Children
	private List<PlnEPlanTemplateSmall> plnEPlanTemplateSmallList;

	private Long programId;

	private Long requestId;

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

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public String getBigTitle() {
		return bigTitle;
	}

	public void setBigTitle(String bigTitle) {
		this.bigTitle = bigTitle;
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

	public List<PlnEPlanTemplateSmall> getPlnEPlanTemplateSmallList() {
		return plnEPlanTemplateSmallList;
	}

	public void setPlnEPlanTemplateSmallList(List<PlnEPlanTemplateSmall> plnEPlanTemplateSmallList) {
		this.plnEPlanTemplateSmallList = plnEPlanTemplateSmallList;
	}

}
