package clb.core.sys.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.hand.hap.system.dto.BaseDTO;
/*****
 * @author tiansheng.ye
 * @Date 2017-05-25
 */
@Table(name="fms_sys_function_allocation_rule")
public class SysFunctionAllocationRule extends BaseDTO{
	private static final long serialVersionUID = -1595442644824530654L;

	@Id
	@GeneratedValue
	private Long ruleId;
	
	@Column
	@NotEmpty
	private String functionCode;
	@Column
	@NotEmpty
	private String columnCode;
	@Column
	@NotEmpty
	private String columnValue;
	@Column
	private Long roleId;
	@Column
	private String allocationApi;
	@Column
	private String sendNotice;
	@Column
	@NotEmpty
	private String isAlive;

	public Long getRuleId() {
		return ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

	public String getFunctionCode() {
		return functionCode;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public String getColumnCode() {
		return columnCode;
	}

	public void setColumnCode(String columnCode) {
		this.columnCode = columnCode;
	}

	public String getColumnValue() {
		return columnValue;
	}

	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getAllocationApi() {
		return allocationApi;
	}

	public void setAllocationApi(String allocationApi) {
		this.allocationApi = allocationApi;
	}

	public String getSendNotice() {
		return sendNotice;
	}

	public void setSendNotice(String sendNotice) {
		this.sendNotice = sendNotice;
	}

	public String getIsAlive() {
		return isAlive;
	}

	public void setIsAlive(String isAlive) {
		this.isAlive = isAlive;
	}
	
	@Transient
	private String roleName;
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@Transient
	private String roleCode;
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
}
