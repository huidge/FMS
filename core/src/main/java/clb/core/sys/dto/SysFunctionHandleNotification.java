package clb.core.sys.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.hand.hap.system.dto.BaseDTO;
import com.sun.istack.NotNull;
/*****
 * @author tiansheng.ye
 * @Date 2017-05-25
 */
@Table(name="FMS_SYS_FUNCTION_HANDLE_NOTIFICATION")
public class SysFunctionHandleNotification extends BaseDTO{

	private static final long serialVersionUID = 8297522854006852180L;
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column
	@NotNull
	private Long ruleId;
	@Column
	@NotNull
	private Long templateId;
	@Column
	@NotEmpty
	private String userType;
	@Column
	@NotEmpty
	private String isAlive;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getRuleId() {
		return ruleId;
	}
	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getIsAlive() {
		return isAlive;
	}
	public void setIsAlive(String isAlive) {
		this.isAlive = isAlive;
	}
}
