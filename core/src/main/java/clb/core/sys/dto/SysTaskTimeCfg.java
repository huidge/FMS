package clb.core.sys.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
@ExtensionAttribute(disable=true)
@Table(name = "FMS_SYS_TASK_TIME_CFG")
public class SysTaskTimeCfg extends BaseDTO {
	private static final long serialVersionUID = -3429108373780731560L;

	@Id
     @GeneratedValue
      private Long cfgId;

     @NotBlank
      private String jobClass;

      private Long itemId;

      private String additionalRiskFlag;
      
     @NotBlank
      private String extractFlag;

     @NotBlank
      private String highMedicalFlag;

      private Double taskTime;

	public Long getCfgId() {
		return cfgId;
	}

	public void setCfgId(Long cfgId) {
		this.cfgId = cfgId;
	}

	public String getJobClass() {
		return jobClass;
	}

	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getExtractFlag() {
		return extractFlag;
	}

	public void setExtractFlag(String extractFlag) {
		this.extractFlag = extractFlag;
	}

	public String getHighMedicalFlag() {
		return highMedicalFlag;
	}

	public void setHighMedicalFlag(String highMedicalFlag) {
		this.highMedicalFlag = highMedicalFlag;
	}

	public Double getTaskTime() {
		return taskTime;
	}

	public void setTaskTime(Double taskTime) {
		this.taskTime = taskTime;
	}

	@Transient
	private String itemName;

	private String bigClass;

	private String midClass;

	private String minClass;

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getBigClass() {
		return bigClass;
	}

	public void setBigClass(String bigClass) {
		this.bigClass = bigClass;
	}

	public String getMidClass() {
		return midClass;
	}

	public void setMidClass(String midClass) {
		this.midClass = midClass;
	}

	public String getMinClass() {
		return minClass;
	}

	public void setMinClass(String minClass) {
		this.minClass = minClass;
	}
	
	@Transient
	private Long userId;
	@Transient
	private String params;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getAdditionalRiskFlag() {
		return additionalRiskFlag;
	}

	public void setAdditionalRiskFlag(String additionalRiskFlag) {
		this.additionalRiskFlag = additionalRiskFlag;
	}
	
}
