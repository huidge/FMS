package clb.core.customer.dto;

/**
 * created by xiaoyong.luo@hand-china.com at 2017/06/13
 **/

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import clb.core.core.annotation.SecurityField;
import clb.core.order.dto.OrdCustomer;
import clb.core.system.dto.ClbUser;

import com.hand.hap.core.annotation.Children;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;

import java.util.Date;
import java.util.List;

@ExtensionAttribute(disable = true)
@Table(name = "fms_ctm_customer")
public class CtmCustomer extends BaseDTO {
    @Id
    @GeneratedValue
    private Long customerId;

    private String customerCode;

    private String chineseName;

    private String englishName;

    private Date birthDate;

    private String sex;

    private String nationality;

    private Long height;

    private Long weight;

    @SecurityField
    private String phone;

    @SecurityField
    private String phoneCode;

    private String email;

    private String companyName;

    private String industry;

    private String companyNation;

    private String companyProvince;

    private String companyCity;

    private String companyAddress;

    private String city;

    private Long income;

    private String job;

    private String position;

    private String education;

    private String marriageStatus;

    private String smokeFlag;

    private String americanCitizenFlag;

    private String identityNumber;

    private String identityEffectiveDate;   //由 date 改成 string

    private String certificateType;

    private String certificateNumber;

    private String certificateEffectiveDate;  //由 date 改成 string

    private String identityAddress;

    private String identityNation;

    private String identityProvince;

    private String identityCity;

    private String identityFlag;

    private String postAddress;

    private String postNation;

    private String postProvince;

    private String postCity;

    private String premiumSource;

    private Double amountPerMonth;

    private Double currentAssets;

    private Double fixedAssets;

    private Double liabilities;

    private Double premiumRate;

    private String badFlag;

    private String compensateFlag;

    private String badInsuranceName;

    private String badInsuranceType;

    private Date badEffactiveDate;

    private String compensateInsuranceName;

    private String compensateInsuranceType;

    private Date compensateEffactiveDate;

    private String smokeDesc;

    private String drinkFlag;

    private String drinkDesc;

    private String drugFlag;

    private String drugDesc;

    private String dangerousFlag;

    private String dangerousDesc;

    private String abroadFlag;

    private String abroadDesc;

    private String disabilityFlag;

    private String disabilityDesc;

    private String spiritFlag;

    private String spiritDesc;

    private String endocrineFlag;

    private String endocrineDesc;

    private String faceFlag;

    private String faceDesc;

    private String respirationFlag;

    private String respirationDesc;

    private String threeFlag;

    private String threeDesc;

    private String cycleFlag;

    private String cycleDesc;

    private String digestionFlag;

    private String digestionDesc;

    private String liverFlag;

    private String liverDesc;

    private String reproductionFlag;

    private String reproductionDesc;

    private String jointFlag;

    private String jointDesc;

    private String tumorFlag;

    private String tumorDesc;

    private String bloodFlag;

    private String bloodDesc;

    private String aidsFlag;

    private String aidsDesc;

    private String aidsTestFlag;

    private String aidsTestDesc;

    private String skinFlag;

    private String skinDesc;

    private String otherFlag;

    private String otherDesc;

    private String otherTreatFlag;

    private String otherTreatDesc;

    private String examinationFlag;

    private String examinationDesc;

    private String hereditaryFlag;

    private String hereditaryDesc;

    private String pregnancyFlag;

    private String pregnancyDesc;

    private String downTestFlag;

    private String downTestDesc;

    private String gynecologyFlag;

    private String gynecologyDesc;

    private String complicationFlag;

    private String complicationDesc;

    private String gynecologyOthFlag;

    private String gynecologyOthDesc;

    private Long programId;

    private Long requestId;


    @Transient
    private String canal;           //渠道 (后台查看 连起来的起到)
    @Transient
    private String incomeLevel;     //收入等级查询
    @Transient
    private Long orderCount;        //订单数量
    @Transient
    private Long moneySum;          //消费总额
    @Transient
    private String orderBy;         //排序

    @Transient
    private Long channelId;

    @Transient
    @Children
    private List<CtmCustomerFamily> customerFamilyList;     //客户家庭成员


    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getIncome() {
        return income;
    }

    public void setIncome(Long income) {
        this.income = income;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getMarriageStatus() {
        return marriageStatus;
    }

    public void setMarriageStatus(String marriageStatus) {
        this.marriageStatus = marriageStatus;
    }

    public String getSmokeFlag() {
        return smokeFlag;
    }

    public void setSmokeFlag(String smokeFlag) {
        this.smokeFlag = smokeFlag;
    }

    public String getAmericanCitizenFlag() {
        return americanCitizenFlag;
    }

    public void setAmericanCitizenFlag(String americanCitizenFlag) {
        this.americanCitizenFlag = americanCitizenFlag;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getIdentityEffectiveDate() {
        return identityEffectiveDate;
    }

    public void setIdentityEffectiveDate(String identityEffectiveDate) {
        this.identityEffectiveDate = identityEffectiveDate;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public String getCertificateEffectiveDate() {
        return certificateEffectiveDate;
    }

    public void setCertificateEffectiveDate(String certificateEffectiveDate) {
        this.certificateEffectiveDate = certificateEffectiveDate;
    }

    public String getIdentityAddress() {
        return identityAddress;
    }

    public void setIdentityAddress(String identityAddress) {
        this.identityAddress = identityAddress;
    }

    public String getIdentityFlag() {
        return identityFlag;
    }

    public void setIdentityFlag(String identityFlag) {
        this.identityFlag = identityFlag;
    }

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }

    public String getPremiumSource() {
        return premiumSource;
    }

    public void setPremiumSource(String premiumSource) {
        this.premiumSource = premiumSource;
    }

    public Double getAmountPerMonth() {
        return amountPerMonth;
    }

    public void setAmountPerMonth(Double amountPerMonth) {
        this.amountPerMonth = amountPerMonth;
    }

    public Double getCurrentAssets() {
        return currentAssets;
    }

    public void setCurrentAssets(Double currentAssets) {
        this.currentAssets = currentAssets;
    }

    public Double getFixedAssets() {
        return fixedAssets;
    }

    public void setFixedAssets(Double fixedAssets) {
        this.fixedAssets = fixedAssets;
    }

    public Double getLiabilities() {
        return liabilities;
    }

    public void setLiabilities(Double liabilities) {
        this.liabilities = liabilities;
    }

    public Double getPremiumRate() {
        return premiumRate;
    }

    public void setPremiumRate(Double premiumRate) {
        this.premiumRate = premiumRate;
    }

    public String getBadFlag() {
        return badFlag;
    }

    public void setBadFlag(String badFlag) {
        this.badFlag = badFlag;
    }

    public String getCompensateFlag() {
        return compensateFlag;
    }

    public void setCompensateFlag(String compensateFlag) {
        this.compensateFlag = compensateFlag;
    }

    public String getBadInsuranceName() {
        return badInsuranceName;
    }

    public void setBadInsuranceName(String badInsuranceName) {
        this.badInsuranceName = badInsuranceName;
    }

    public String getBadInsuranceType() {
        return badInsuranceType;
    }

    public void setBadInsuranceType(String badInsuranceType) {
        this.badInsuranceType = badInsuranceType;
    }

    public Date getBadEffactiveDate() {
        return badEffactiveDate;
    }

    public void setBadEffactiveDate(Date badEffactiveDate) {
        this.badEffactiveDate = badEffactiveDate;
    }

    public String getCompensateInsuranceName() {
        return compensateInsuranceName;
    }

    public void setCompensateInsuranceName(String compensateInsuranceName) {
        this.compensateInsuranceName = compensateInsuranceName;
    }

    public String getCompensateInsuranceType() {
        return compensateInsuranceType;
    }

    public void setCompensateInsuranceType(String compensateInsuranceType) {
        this.compensateInsuranceType = compensateInsuranceType;
    }

    public Date getCompensateEffactiveDate() {
        return compensateEffactiveDate;
    }

    public void setCompensateEffactiveDate(Date compensateEffactiveDate) {
        this.compensateEffactiveDate = compensateEffactiveDate;
    }

    public String getSmokeDesc() {
        return smokeDesc;
    }

    public void setSmokeDesc(String smokeDesc) {
        this.smokeDesc = smokeDesc;
    }

    public String getDrinkFlag() {
        return drinkFlag;
    }

    public void setDrinkFlag(String drinkFlag) {
        this.drinkFlag = drinkFlag;
    }

    public String getDrinkDesc() {
        return drinkDesc;
    }

    public void setDrinkDesc(String drinkDesc) {
        this.drinkDesc = drinkDesc;
    }

    public String getDrugFlag() {
        return drugFlag;
    }

    public void setDrugFlag(String drugFlag) {
        this.drugFlag = drugFlag;
    }

    public String getDrugDesc() {
        return drugDesc;
    }

    public void setDrugDesc(String drugDesc) {
        this.drugDesc = drugDesc;
    }

    public String getDangerousFlag() {
        return dangerousFlag;
    }

    public void setDangerousFlag(String dangerousFlag) {
        this.dangerousFlag = dangerousFlag;
    }

    public String getDangerousDesc() {
        return dangerousDesc;
    }

    public void setDangerousDesc(String dangerousDesc) {
        this.dangerousDesc = dangerousDesc;
    }

    public String getAbroadFlag() {
        return abroadFlag;
    }

    public void setAbroadFlag(String abroadFlag) {
        this.abroadFlag = abroadFlag;
    }

    public String getAbroadDesc() {
        return abroadDesc;
    }

    public void setAbroadDesc(String abroadDesc) {
        this.abroadDesc = abroadDesc;
    }

    public String getDisabilityFlag() {
        return disabilityFlag;
    }

    public void setDisabilityFlag(String disabilityFlag) {
        this.disabilityFlag = disabilityFlag;
    }

    public String getDisabilityDesc() {
        return disabilityDesc;
    }

    public void setDisabilityDesc(String disabilityDesc) {
        this.disabilityDesc = disabilityDesc;
    }

    public String getSpiritFlag() {
        return spiritFlag;
    }

    public void setSpiritFlag(String spiritFlag) {
        this.spiritFlag = spiritFlag;
    }

    public String getSpiritDesc() {
        return spiritDesc;
    }

    public void setSpiritDesc(String spiritDesc) {
        this.spiritDesc = spiritDesc;
    }

    @Override
    public Long getProgramId() {
        return programId;
    }

    @Override
    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    @Override
    public Long getRequestId() {
        return requestId;
    }

    @Override
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getEndocrineFlag() {
        return endocrineFlag;
    }

    public void setEndocrineFlag(String endocrineFlag) {
        this.endocrineFlag = endocrineFlag;
    }

    public String getEndocrineDesc() {
        return endocrineDesc;
    }

    public void setEndocrineDesc(String endocrineDesc) {
        this.endocrineDesc = endocrineDesc;
    }

    public String getFaceFlag() {
        return faceFlag;
    }

    public void setFaceFlag(String faceFlag) {
        this.faceFlag = faceFlag;
    }

    public String getFaceDesc() {
        return faceDesc;
    }

    public void setFaceDesc(String faceDesc) {
        this.faceDesc = faceDesc;
    }

    public String getRespirationFlag() {
        return respirationFlag;
    }

    public void setRespirationFlag(String respirationFlag) {
        this.respirationFlag = respirationFlag;
    }

    public String getRespirationDesc() {
        return respirationDesc;
    }

    public void setRespirationDesc(String respirationDesc) {
        this.respirationDesc = respirationDesc;
    }

    public String getThreeFlag() {
        return threeFlag;
    }

    public void setThreeFlag(String threeFlag) {
        this.threeFlag = threeFlag;
    }

    public String getThreeDesc() {
        return threeDesc;
    }

    public void setThreeDesc(String threeDesc) {
        this.threeDesc = threeDesc;
    }

    public String getCycleFlag() {
        return cycleFlag;
    }

    public void setCycleFlag(String cycleFlag) {
        this.cycleFlag = cycleFlag;
    }

    public String getCycleDesc() {
        return cycleDesc;
    }

    public void setCycleDesc(String cycleDesc) {
        this.cycleDesc = cycleDesc;
    }

    public String getDigestionFlag() {
        return digestionFlag;
    }

    public void setDigestionFlag(String digestionFlag) {
        this.digestionFlag = digestionFlag;
    }

    public String getDigestionDesc() {
        return digestionDesc;
    }

    public void setDigestionDesc(String digestionDesc) {
        this.digestionDesc = digestionDesc;
    }

    public String getLiverFlag() {
        return liverFlag;
    }

    public void setLiverFlag(String liverFlag) {
        this.liverFlag = liverFlag;
    }

    public String getLiverDesc() {
        return liverDesc;
    }

    public void setLiverDesc(String liverDesc) {
        this.liverDesc = liverDesc;
    }

    public String getReproductionFlag() {
        return reproductionFlag;
    }

    public void setReproductionFlag(String reproductionFlag) {
        this.reproductionFlag = reproductionFlag;
    }

    public String getReproductionDesc() {
        return reproductionDesc;
    }

    public void setReproductionDesc(String reproductionDesc) {
        this.reproductionDesc = reproductionDesc;
    }

    public String getJointFlag() {
        return jointFlag;
    }

    public void setJointFlag(String jointFlag) {
        this.jointFlag = jointFlag;
    }

    public String getJointDesc() {
        return jointDesc;
    }

    public void setJointDesc(String jointDesc) {
        this.jointDesc = jointDesc;
    }

    public String getTumorFlag() {
        return tumorFlag;
    }

    public void setTumorFlag(String tumorFlag) {
        this.tumorFlag = tumorFlag;
    }

    public String getTumorDesc() {
        return tumorDesc;
    }

    public void setTumorDesc(String tumorDesc) {
        this.tumorDesc = tumorDesc;
    }

    public String getBloodFlag() {
        return bloodFlag;
    }

    public void setBloodFlag(String bloodFlag) {
        this.bloodFlag = bloodFlag;
    }

    public String getBloodDesc() {
        return bloodDesc;
    }

    public void setBloodDesc(String bloodDesc) {
        this.bloodDesc = bloodDesc;
    }

    public String getAidsFlag() {
        return aidsFlag;
    }

    public void setAidsFlag(String aidsFlag) {
        this.aidsFlag = aidsFlag;
    }

    public String getAidsDesc() {
        return aidsDesc;
    }

    public void setAidsDesc(String aidsDesc) {
        this.aidsDesc = aidsDesc;
    }

    public String getAidsTestFlag() {
        return aidsTestFlag;
    }

    public void setAidsTestFlag(String aidsTestFlag) {
        this.aidsTestFlag = aidsTestFlag;
    }

    public String getAidsTestDesc() {
        return aidsTestDesc;
    }

    public void setAidsTestDesc(String aidsTestDesc) {
        this.aidsTestDesc = aidsTestDesc;
    }

    public String getSkinFlag() {
        return skinFlag;
    }

    public void setSkinFlag(String skinFlag) {
        this.skinFlag = skinFlag;
    }

    public String getSkinDesc() {
        return skinDesc;
    }

    public void setSkinDesc(String skinDesc) {
        this.skinDesc = skinDesc;
    }

    public String getOtherFlag() {
        return otherFlag;
    }

    public void setOtherFlag(String otherFlag) {
        this.otherFlag = otherFlag;
    }

    public String getOtherDesc() {
        return otherDesc;
    }

    public void setOtherDesc(String otherDesc) {
        this.otherDesc = otherDesc;
    }

    public String getOtherTreatFlag() {
        return otherTreatFlag;
    }

    public void setOtherTreatFlag(String otherTreatFlag) {
        this.otherTreatFlag = otherTreatFlag;
    }

    public String getOtherTreatDesc() {
        return otherTreatDesc;
    }

    public void setOtherTreatDesc(String otherTreatDesc) {
        this.otherTreatDesc = otherTreatDesc;
    }

    public String getExaminationFlag() {
        return examinationFlag;
    }

    public void setExaminationFlag(String examinationFlag) {
        this.examinationFlag = examinationFlag;
    }

    public String getExaminationDesc() {
        return examinationDesc;
    }

    public void setExaminationDesc(String examinationDesc) {
        this.examinationDesc = examinationDesc;
    }

    public String getHereditaryFlag() {
        return hereditaryFlag;
    }

    public void setHereditaryFlag(String hereditaryFlag) {
        this.hereditaryFlag = hereditaryFlag;
    }

    public String getHereditaryDesc() {
        return hereditaryDesc;
    }

    public void setHereditaryDesc(String hereditaryDesc) {
        this.hereditaryDesc = hereditaryDesc;
    }

    public String getPregnancyFlag() {
        return pregnancyFlag;
    }

    public void setPregnancyFlag(String pregnancyFlag) {
        this.pregnancyFlag = pregnancyFlag;
    }

    public String getPregnancyDesc() {
        return pregnancyDesc;
    }

    public void setPregnancyDesc(String pregnancyDesc) {
        this.pregnancyDesc = pregnancyDesc;
    }

    public String getDownTestFlag() {
        return downTestFlag;
    }

    public void setDownTestFlag(String downTestFlag) {
        this.downTestFlag = downTestFlag;
    }

    public String getDownTestDesc() {
        return downTestDesc;
    }

    public void setDownTestDesc(String downTestDesc) {
        this.downTestDesc = downTestDesc;
    }

    public String getGynecologyFlag() {
        return gynecologyFlag;
    }

    public void setGynecologyFlag(String gynecologyFlag) {
        this.gynecologyFlag = gynecologyFlag;
    }

    public String getGynecologyDesc() {
        return gynecologyDesc;
    }

    public void setGynecologyDesc(String gynecologyDesc) {
        this.gynecologyDesc = gynecologyDesc;
    }

    public String getComplicationFlag() {
        return complicationFlag;
    }

    public void setComplicationFlag(String complicationFlag) {
        this.complicationFlag = complicationFlag;
    }

    public String getComplicationDesc() {
        return complicationDesc;
    }

    public void setComplicationDesc(String complicationDesc) {
        this.complicationDesc = complicationDesc;
    }

    public String getGynecologyOthFlag() {
        return gynecologyOthFlag;
    }

    public void setGynecologyOthFlag(String gynecologyOthFlag) {
        this.gynecologyOthFlag = gynecologyOthFlag;
    }

    public String getGynecologyOthDesc() {
        return gynecologyOthDesc;
    }

    public void setGynecologyOthDesc(String gynecologyOthDesc) {
        this.gynecologyOthDesc = gynecologyOthDesc;
    }

    public String getIdentityNation() {
        return identityNation;
    }

    public void setIdentityNation(String identityNation) {
        this.identityNation = identityNation;
    }

    public String getIdentityProvince() {
        return identityProvince;
    }

    public void setIdentityProvince(String identityProvince) {
        this.identityProvince = identityProvince;
    }

    public String getIdentityCity() {
        return identityCity;
    }

    public void setIdentityCity(String identityCity) {
        this.identityCity = identityCity;
    }

    public String getPostNation() {
        return postNation;
    }

    public void setPostNation(String postNation) {
        this.postNation = postNation;
    }

    public String getPostProvince() {
        return postProvince;
    }

    public void setPostProvince(String postProvince) {
        this.postProvince = postProvince;
    }

    public String getPostCity() {
        return postCity;
    }

    public void setPostCity(String postCity) {
        this.postCity = postCity;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public Long getMoneySum() {
        return moneySum;
    }

    public void setMoneySum(Long moneySum) {
        this.moneySum = moneySum;
    }

    public String getIncomeLevel() {
        return incomeLevel;
    }

    public void setIncomeLevel(String incomeLevel) {
        this.incomeLevel = incomeLevel;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public List<CtmCustomerFamily> getCustomerFamilyList() {
        return customerFamilyList;
    }

    public void setCustomerFamilyList(List<CtmCustomerFamily> customerFamilyList) {
        this.customerFamilyList = customerFamilyList;
    }

	public String getPhoneCode() {
		return phoneCode;
	}

	public void setPhoneCode(String phoneCode) {
		this.phoneCode = phoneCode;
	}

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getCompanyNation() {
        return companyNation;
    }

    public void setCompanyNation(String companyNation) {
        this.companyNation = companyNation;
    }

    public String getCompanyProvince() {
        return companyProvince;
    }

    public void setCompanyProvince(String companyProvince) {
        this.companyProvince = companyProvince;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public void setCompanyCity(String companyCity) {
        this.companyCity = companyCity;
    }

    
	@Transient
	private String verifiCode;
	public String getVerifiCode() {
		return verifiCode;
	}
	public void setVerifiCode(String verifiCode) {
		this.verifiCode = verifiCode;
	}
	@Transient
    private ClbUser user;

	public ClbUser getUser() {
		return user;
	}
	public void setUser(ClbUser user) {
		this.user = user;
	}
}
