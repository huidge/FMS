package clb.core.production.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.StringUtils;

import com.hand.hap.system.dto.BaseDTO;

/*****
 * @author tiansheng.ye
 * @Date 2017/05/15
 * @Desc 现金价值表
 */
@Table(name="FMS_PRD_CASH_VALUE")
public class PrdCashValue extends BaseDTO{
	private static final long serialVersionUID = -2252377040632593944L;

	@Id
    @GeneratedValue(generator="IDENTITY")
    @Column
    private Long cashValueId;
	
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
    private Double insuranceCoverage;
	
	@Column
    private Double premium;
	
	@Column
    @NotNull
    private Long age;
	
	@Column
    @NotNull
    private Long insuranceYear;
	
	@Column
	private String smokeFlag;
	
	@Column
	@NotEmpty
	private String gender;
	
	@Column
	@NotEmpty
	private Double surrenderDeposit;
	
	@Column
	@NotEmpty
	private Double surrenderNotBonus;
	
	@Column
	@NotEmpty
	private Double dieDeposit;
	
	@Column 
	@NotEmpty
	private Double dieExtra;
	
	@Column
	@NotEmpty
	private Double dieNotBonus;

	public Long getCashValueId() {
		return cashValueId;
	}

	public void setCashValueId(Long cashValueId) {
		this.cashValueId = cashValueId;
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

	public Double getInsuranceCoverage() {
		return insuranceCoverage;
	}

	public void setInsuranceCoverage(Double insuranceCoverage) {
		this.insuranceCoverage = insuranceCoverage;
	}

	public Double getPremium() {
		return premium;
	}

	public void setPremium(Double premium) {
		this.premium = premium;
	}

	public Long getAge() {
		return age;
	}

	public void setAge(Long age) {
		this.age = age;
	}

	public Long getInsuranceYear() {
		return insuranceYear;
	}

	public void setInsuranceYear(Long insuranceYear) {
		this.insuranceYear = insuranceYear;
	}

	public String getSmokeFlag() {
		return smokeFlag;
	}

	public void setSmokeFlag(String smokeFlag) {
		this.smokeFlag = smokeFlag;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Double getSurrenderDeposit() {
		return surrenderDeposit;
	}

	public void setSurrenderDeposit(Double surrenderDeposit) {
		this.surrenderDeposit = surrenderDeposit;
	}

	public Double getSurrenderNotBonus() {
		return surrenderNotBonus;
	}

	public void setSurrenderNotBonus(Double surrenderNotBonus) {
		this.surrenderNotBonus = surrenderNotBonus;
	}

	public Double getDieDeposit() {
		return dieDeposit;
	}

	public void setDieDeposit(Double dieDeposit) {
		this.dieDeposit = dieDeposit;
	}

	public Double getDieExtra() {
		return dieExtra;
	}

	public void setDieExtra(Double dieExtra) {
		this.dieExtra = dieExtra;
	}

	public Double getDieNotBonus() {
		return dieNotBonus;
	}

	public void setDieNotBonus(Double dieNotBonus) {
		this.dieNotBonus = dieNotBonus;
	}
	
	//产品名称
	@Transient
	private String itemName;
	@Transient 
	String sublineItemName;
	@Transient
	private String ageStr;
	@Transient
	private Long ageMin;
	@Transient
	private Long ageMax;
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getAgeStr() {
		return ageStr;
	}

	public void setAgeStr(String ageStr) {
		this.ageStr = ageStr;
	}

	public Long getAgeMin() {
		if(!StringUtils.isEmpty(ageStr)){
			try {
				return Long.valueOf(ageStr.substring(0, 2));
			} catch (NumberFormatException e) {
				return null;
			}
		}else{
			return ageMin;
		}
	}

	public void setAgeMin(Long ageMin) {
		this.ageMin = ageMin;
	}

	public Long getAgeMax() {
		if(!StringUtils.isEmpty(ageStr)){
			try {
				return Long.valueOf(ageStr.substring(2, 4));
			} catch (NumberFormatException e) {
				return null;
			}
		}else{
			return ageMax;
		}
	}

	public void setAgeMax(Long ageMax) {
		this.ageMax = ageMax;
	}

	public String getSublineItemName() {
		return sublineItemName;
	}

	public void setSublineItemName(String sublineItemName) {
		this.sublineItemName = sublineItemName;
	}
	
}
