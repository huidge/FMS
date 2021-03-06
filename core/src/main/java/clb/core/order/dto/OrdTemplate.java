package clb.core.order.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.NotEmpty;

@ExtensionAttribute(disable = true)
@Table(name = "fms_ord_template")
public class OrdTemplate extends BaseDTO {
	@Id
	@GeneratedValue
	private Long templateId;

	@NotEmpty
	private String templateTypeCode;

	private String applyClassCode;

	private String applyItem;

	private String enableFlag;

	private Long programId;

	private Long requestId;

	@Transient
	private String applyClass;

	@Transient
	private String afterType;
	@Transient
	private String afterProject;

	public String getAfterType() {
		return afterType;
	}

	public void setAfterType(String afterType) {
		this.afterType = afterType;
	}

	public String getAfterProject() {
		return afterProject;
	}

	public void setAfterProject(String afterProject) {
		this.afterProject = afterProject;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateTypeCode(String templateTypeCode) {
		this.templateTypeCode = templateTypeCode;
	}

	public String getTemplateTypeCode() {
		return templateTypeCode;
	}

	public void setApplyClassCode(String applyClassCode) {
		this.applyClassCode = applyClassCode;
	}

	public String getApplyClassCode() {
		return applyClassCode;
	}

	public void setApplyItem(String applyItem) {
		this.applyItem = applyItem;
	}

	public String getApplyItem() {
		return applyItem;
	}

	public void setEnableFlag(String enableFlag) {
		this.enableFlag = enableFlag;
	}

	public String getEnableFlag() {
		return enableFlag;
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

	public String getApplyClass() {
		return applyClass;
	}

	public void setApplyClass(String applyClass) {
		this.applyClass = applyClass;
	}
}
