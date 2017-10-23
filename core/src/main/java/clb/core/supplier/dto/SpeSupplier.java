package clb.core.supplier.dto;

import java.util.Date;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import clb.core.core.annotation.SecurityField;
import org.hibernate.validator.constraints.NotEmpty;

import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.system.dto.BaseDTO;
/**
 * @name SpeSupplier
 * @description 供应商DTO
 * @author bo.wu@hand-china.com 2017年4月18日19:36:25
 * @version 1.0 
 */

@Table(name = "fms_spe_supplier")
public class SpeSupplier extends BaseDTO {
	private static final long serialVersionUID = 675341222433642181L;

	@Id
    @GeneratedValue
     private Long supplierId;
	
     private String supplierCode;

    @NotEmpty
     private String name;

    @NotEmpty
     private String shortName;

     private String contactPerson;

    @SecurityField
  	private String phoneCode;
    
  	private String email;

    @SecurityField
     private String contactNum;

    @NotNull
     private Long calendarId;

     private Date effectiveDateFrom;

     private Date effectiveDateTo;

     @NotEmpty
     private String vocationBill;

     private String statusCode;

     private String qualification1;

     private String qualification2;

     private String qualification3;

     private String qualification4;

     private String qualification5;

     private String qualification6;

     private String qualification7;

     private String qualification8;

     private String qualification9;

     private String otherQualification;


  	@Transient
  	private Date dueDate;

  	@Transient
  	private List<SpeContract> contracts;
  	
  	@Transient
  	private String bigClass;

  	@Transient
    private Long channelId;
  	
  	private String ageCalculateStandard;
  	
  	private String signTr;
  	
  	private Integer serialNumber;
  	
  	public Integer getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(Integer serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getAgeCalculateStandard() {
		return ageCalculateStandard;
	}

	public void setAgeCalculateStandard(String ageCalculateStandard) {
		this.ageCalculateStandard = ageCalculateStandard;
	}

	public String getBigClass() {
		return bigClass;
	}

	public void setBigClass(String bigClass) {
		this.bigClass = bigClass;
	}

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setSupplierId(Long supplierId){
        this.supplierId = supplierId;
    }

    public Long getSupplierId(){
        return supplierId;
    }

    public void setSupplierCode(String supplierCode){
        this.supplierCode = supplierCode;
    }

    public String getSupplierCode(){
        return supplierCode;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setShortName(String shortName){
        this.shortName = shortName;
    }

    public String getShortName(){
        return shortName;
    }

    public void setContactPerson(String contactPerson){
        this.contactPerson = contactPerson;
    }

    public String getContactPerson(){
        return contactPerson;
    }

    public void setContactNum(String contactNum){
        this.contactNum = contactNum;
    }

    public String getContactNum(){
        return contactNum;
    }

    public void setCalendarId(Long calendarId){
        this.calendarId = calendarId;
    }

    public Long getCalendarId(){
        return calendarId;
    }

    public void setEffectiveDateFrom(Date effectiveDateFrom){
        this.effectiveDateFrom = effectiveDateFrom;
    }

    public Date getEffectiveDateFrom(){
        return effectiveDateFrom;
    }

    public void setEffectiveDateTo(Date effectiveDateTo){
        this.effectiveDateTo = effectiveDateTo;
    }

    public Date getEffectiveDateTo(){
        return effectiveDateTo;
    }

    public void setVocationBill(String vocationBill){
        this.vocationBill = vocationBill;
    }

    public String getVocationBill(){
        return vocationBill;
    }

    public void setStatusCode(String statusCode){
        this.statusCode = statusCode;
    }

    public String getStatusCode(){
        return statusCode;
    }

    public void setQualification1(String qualification1){
        this.qualification1 = qualification1;
    }

    public String getQualification1(){
        return qualification1;
    }

    public void setQualification2(String qualification2){
        this.qualification2 = qualification2;
    }

    public String getQualification2(){
        return qualification2;
    }

    public void setQualification3(String qualification3){
        this.qualification3 = qualification3;
    }

    public String getQualification3(){
        return qualification3;
    }

    public void setQualification4(String qualification4){
        this.qualification4 = qualification4;
    }

    public String getQualification4(){
        return qualification4;
    }

    public void setQualification5(String qualification5){
        this.qualification5 = qualification5;
    }

    public String getQualification5(){
        return qualification5;
    }

    public void setQualification6(String qualification6){
        this.qualification6 = qualification6;
    }

    public String getQualification6(){
        return qualification6;
    }

    public void setQualification7(String qualification7){
        this.qualification7 = qualification7;
    }

    public String getQualification7(){
        return qualification7;
    }

    public void setQualification8(String qualification8){
        this.qualification8 = qualification8;
    }

    public String getQualification8(){
        return qualification8;
    }

    public void setQualification9(String qualification9){
        this.qualification9 = qualification9;
    }

    public String getQualification9(){
        return qualification9;
    }

    public void setOtherQualification(String otherQualification){
        this.otherQualification = otherQualification;
    }

    public String getOtherQualification(){
        return otherQualification;
    }

     public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public List<SpeContract> getContracts() {
		return contracts;
	}

	public void setContracts(List<SpeContract> contracts) {
		this.contracts = contracts;
	}
     
	private String type;
	
	private String businessScope;

	private Long gracePeriod;
	
	private Long administrativePeriod;
	
	private String companyProfile;
	
	private Long fileId;
	
	private String website;
	
	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBusinessScope() {
		return businessScope;
	}

	public void setBusinessScope(String businessScope) {
		this.businessScope = businessScope;
	}

	public Long getGracePeriod() {
		return gracePeriod;
	}

	public void setGracePeriod(Long gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	public Long getAdministrativePeriod() {
		return administrativePeriod;
	}

	public void setAdministrativePeriod(Long administrativePeriod) {
		this.administrativePeriod = administrativePeriod;
	}

	public String getCompanyProfile() {
		return companyProfile;
	}

	public void setCompanyProfile(String companyProfile) {
		this.companyProfile = companyProfile;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	@Transient
	private SysFile sysFile;

	public SysFile getSysFile() {
		return sysFile;
	}

	public void setSysFile(SysFile sysFile) {
		this.sysFile = sysFile;
	}

	public String getPhoneCode() {
		return phoneCode;
	}

	public void setPhoneCode(String phoneCode) {
		this.phoneCode = phoneCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSignTr() {
		return signTr;
	}

	public void setSignTr(String signTr) {
		this.signTr = signTr;
	}
	
	
	
     
}
