package clb.core.course.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import clb.core.core.annotation.SecurityField;

@ExtensionAttribute(disable = true)
@Table(name = "fms_trn_support")
public class TrnSupport extends BaseDTO {
	@Id
	@GeneratedValue
	private Long supportId;
	@Condition(operator = "LIKE")
	private String supportNum;

	@NotNull
	private Long channelId;

	@NotEmpty
	private String supportType;

	@NotEmpty
	private String createrName;

	@SecurityField
	private String phoneCode;

	/* @NotEmpty */
	@SecurityField
	private String contactPhone;

	@NotEmpty
	private String contactEmail;

	private BigDecimal amount;

	@NotEmpty
	private String status;

	private String trainContent;// '培训内容'
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date trainStartDate;// '培训开始时间'
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date trainEndDate;// '培训结束时间'
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date amountOperationDate;// '支付生效时间'

	private String trainTeacher;// '培训讲师'

	private String trainAddress;// '培训详细地址'

	private String trainOther;// '培训其他要求'

	private String costTopic;// '会销主题'

	private String costParType;// '会销参与人类型'

	private Long costParTotal;// '会销参与人人数'

	private String trainMainType;// '培训参与人性质'

	private String trainPeopleNum;// '培训参与人人数'
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date costStartDate;// '会销开始时间'
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date costEndDate;// '会销结束时间'

	private String province;// '会销省'

	private String city;// '会销市'

	private String costAddress;// '会销详细地址'

	private String costTeacher;// '会销讲师'

	private String costTarget;// '会销目标'

	private String other;// '会销其他要求'

	@Transient
	private String runningTime;// 进行时间

	@Transient
	private String contactPhoneComb;

	public Date getAmountOperationDate() {
		return amountOperationDate;
	}

	public void setAmountOperationDate(Date amountOperationDate) {
		this.amountOperationDate = amountOperationDate;
	}

	public String getPhoneCode() {
		return phoneCode;
	}

	public void setPhoneCode(String phoneCode) {
		this.phoneCode = phoneCode;
	}

	public String getContactPhoneComb() {
		return contactPhoneComb;
	}

	public void setContactPhoneComb(String contactPhoneComb) {
		this.contactPhoneComb = contactPhoneComb;
	}

	public String getRunningTime() {
		return runningTime;
	}

	public void setRunningTime(String runningTime) {
		this.runningTime = runningTime;
	}

	public String getTrainOther() {
		return trainOther;
	}

	public void setTrainOther(String trainOther) {
		this.trainOther = trainOther;
	}

	public String getCostParType() {
		return costParType;
	}

	public void setCostParType(String costParType) {
		this.costParType = costParType;
	}

	public Long getCostParTotal() {
		return costParTotal;
	}

	public void setCostParTotal(Long costParTotal) {
		this.costParTotal = costParTotal;
	}

	public String getGuestOther() {
		return guestOther;
	}

	public void setGuestOther(String guestOther) {
		this.guestOther = guestOther;
	}

	private String costContent;// '申请取消内容'
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date guestDate;// '会客时间'
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date guestEndDate;// '会客结束时间'

	private String guestAddress;// '会客地点'

	private String guestTeacher;// '会客讲师'

	private String guestBackground;// '会客客户背景描述'

	private String guestOther;// 会客其他要求

	private Long guestParTotal;// '会客参与人人数'

	private String guestDiscussContent;// '会客洽谈内容'

	private Double guestPercent;// '会客愿意佣金提取比例'

	private Long programId;

	private Long requestId;

	@Transient
	private String channelName;

	@Transient
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date submitDate;

	@Transient
	private String area;

	@Transient
	private String provinceMeaning;

	@Transient
	private String cityMeaning;

	@Transient
	private String startDate;

	@Transient
	private String endDate;

	@Transient
	private Date startFrom;

	@Transient
	private Date startTo;

	public Date getStartFrom() {
		return startFrom;
	}

	public void setStartFrom(Date startFrom) {
		this.startFrom = startFrom;
	}

	public Date getStartTo() {
		return startTo;
	}

	public void setStartTo(Date startTo) {
		this.startTo = startTo;
	}

	public Date getGuestEndDate() {
		return guestEndDate;
	}

	public void setGuestEndDate(Date guestEndDate) {
		this.guestEndDate = guestEndDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getProvinceMeaning() {
		return provinceMeaning;
	}

	public void setProvinceMeaning(String provinceMeaning) {
		this.provinceMeaning = provinceMeaning;
	}

	public String getCityMeaning() {
		return cityMeaning;
	}

	public void setCityMeaning(String cityMeaning) {
		this.cityMeaning = cityMeaning;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String getTrainMainType() {
		return trainMainType;
	}

	public void setTrainMainType(String trainMainType) {
		this.trainMainType = trainMainType;
	}

	public String getTrainPeopleNum() {
		return trainPeopleNum;
	}

	public void setTrainPeopleNum(String trainPeopleNum) {
		this.trainPeopleNum = trainPeopleNum;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getSupportNum() {
		return supportNum;
	}

	public void setSupportNum(String supportNum) {
		this.supportNum = supportNum;
	}

	public void setSupportId(Long supportId) {
		this.supportId = supportId;
	}

	public Long getSupportId() {
		return supportId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public Long getChannelId() {
		return channelId;
	}

	public void setSupportType(String supportType) {
		this.supportType = supportType;
	}

	public String getSupportType() {
		return supportType;
	}

	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	public String getCreaterName() {
		return createrName;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setTrainContent(String trainContent) {
		this.trainContent = trainContent;
	}

	public String getTrainContent() {
		return trainContent;
	}

	public void setTrainStartDate(Date trainStartDate) {
		this.trainStartDate = trainStartDate;
	}

	public Date getTrainStartDate() {
		return trainStartDate;
	}

	public void setTrainEndDate(Date trainEndDate) {
		this.trainEndDate = trainEndDate;
	}

	public Date getTrainEndDate() {
		return trainEndDate;
	}

	public void setTrainTeacher(String trainTeacher) {
		this.trainTeacher = trainTeacher;
	}

	public String getTrainTeacher() {
		return trainTeacher;
	}

	public void setTrainAddress(String trainAddress) {
		this.trainAddress = trainAddress;
	}

	public String getTrainAddress() {
		return trainAddress;
	}

	public void setCostTopic(String costTopic) {
		this.costTopic = costTopic;
	}

	public String getCostTopic() {
		return costTopic;
	}

	public void setCostStartDate(Date costStartDate) {
		this.costStartDate = costStartDate;
	}

	public Date getCostStartDate() {
		return costStartDate;
	}

	public void setCostEndDate(Date costEndDate) {
		this.costEndDate = costEndDate;
	}

	public Date getCostEndDate() {
		return costEndDate;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCostAddress(String costAddress) {
		this.costAddress = costAddress;
	}

	public String getCostAddress() {
		return costAddress;
	}

	public void setCostTeacher(String costTeacher) {
		this.costTeacher = costTeacher;
	}

	public String getCostTeacher() {
		return costTeacher;
	}

	public void setCostTarget(String costTarget) {
		this.costTarget = costTarget;
	}

	public String getCostTarget() {
		return costTarget;
	}

	public void setCostContent(String costContent) {
		this.costContent = costContent;
	}

	public String getCostContent() {
		return costContent;
	}

	public void setGuestDate(Date guestDate) {
		this.guestDate = guestDate;
	}

	public Date getGuestDate() {
		return guestDate;
	}

	public void setGuestAddress(String guestAddress) {
		this.guestAddress = guestAddress;
	}

	public String getGuestAddress() {
		return guestAddress;
	}

	public void setGuestTeacher(String guestTeacher) {
		this.guestTeacher = guestTeacher;
	}

	public String getGuestTeacher() {
		return guestTeacher;
	}

	public void setGuestBackground(String guestBackground) {
		this.guestBackground = guestBackground;
	}

	public String getGuestBackground() {
		return guestBackground;
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

	public Long getGuestParTotal() {
		return guestParTotal;
	}

	public void setGuestParTotal(Long guestParTotal) {
		this.guestParTotal = guestParTotal;
	}

	public String getGuestDiscussContent() {
		return guestDiscussContent;
	}

	public void setGuestDiscussContent(String guestDiscussContent) {
		this.guestDiscussContent = guestDiscussContent;
	}

	public Double getGuestPercent() {
		return guestPercent;
	}

	public void setGuestPercent(Double guestPercent) {
		this.guestPercent = guestPercent;
	}

	private Date creationDate;
	private Date lastUpdateDate;

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	@Transient
	private List<TrnSupportTeacher> teacherList;

	public List<TrnSupportTeacher> getTeacherList() {
		return teacherList;
	}

	public void setTeacherList(List<TrnSupportTeacher> teacherList) {
		this.teacherList = teacherList;
	}

	public Date getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}

}