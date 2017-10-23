package clb.core.production.dto;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.hand.hap.system.dto.BaseDTO;

/*****
 * @author tiansheng.ye
 * @Date 2017/07/21
 * @Desc 保费调整表
 */
@Table(name = "FMS_PRD_PREMIUM_ADJUST")
public class PrdPremiumAdjust extends BaseDTO {

	private static final long serialVersionUID = -5674929230854679193L;

	@Id
	@GeneratedValue(generator = "IDENTITY")
	@Column
	private Long adjustId;

	@Column
	@NotNull
	private Long itemId;

	@Column
	@NotEmpty
	private String currency;

	@Column
	@NotNull
	private Long sublineId;
	
	@Column
    private Double insuranceCoverageStart;

	@Column
    private Double insuranceCoverageEnd;

	@Column
	private Double premium;

	@Transient
	private String sublineItemName;
	@Transient
	private Double insuranceCoverage;

	public Long getAdjustId() {
		return adjustId;
	}

	public void setAdjustId(Long adjustId) {
		this.adjustId = adjustId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Long getSublineId() {
		return sublineId;
	}

	public void setSublineId(Long sublineId) {
		this.sublineId = sublineId;
	}

	public Double getInsuranceCoverageStart() {
		return insuranceCoverageStart;
	}

	public void setInsuranceCoverageStart(Double insuranceCoverageStart) {
		this.insuranceCoverageStart = insuranceCoverageStart;
	}

	public Double getInsuranceCoverageEnd() {
		return insuranceCoverageEnd;
	}

	public void setInsuranceCoverageEnd(Double insuranceCoverageEnd) {
		this.insuranceCoverageEnd = insuranceCoverageEnd;
	}

	public Double getPremium() {
		return premium;
	}

	public void setPremium(Double premium) {
		this.premium = premium;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Double getInsuranceCoverage() {
		return insuranceCoverage;
	}

	public void setInsuranceCoverage(Double insuranceCoverage) {
		this.insuranceCoverage = insuranceCoverage;
	}

	public String getSublineItemName() {
		return sublineItemName;
	}

	public void setSublineItemName(String sublineItemName) {
		this.sublineItemName = sublineItemName;
	}
}
