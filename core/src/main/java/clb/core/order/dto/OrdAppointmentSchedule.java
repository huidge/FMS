package clb.core.order.dto;

import java.util.Date;
import java.util.List;

/**
 * @name 预约日程表Dto
 * @author bo.wu@hand-china.com 2017年6月24日16:32:39
 */
public class OrdAppointmentSchedule{

	/**
	 * 唯一序列号
	 */
	private static final long serialVersionUID = 1L;
	
	//预约时间
	private Date appointmentTime;
	
	//类型1
	private String levelOneType;
	
	//类型2
	private String levelTwoType;
	
	//预约数据
	private List<?> appointmentData;
	
	//上午的数据条数
	private Long morningDataNumber;
	
	//下午的数据条数
	private Long afternoonDataNumber;
	
	//总条数
	private Long totalDataNumber;

	public Date getAppointmentTime() {
		return appointmentTime;
	}

	public void setAppointmentTime(Date appointmentTime) {
		this.appointmentTime = appointmentTime;
	}

	public String getLevelOneType() {
		return levelOneType;
	}

	public void setLevelOneType(String levelOneType) {
		this.levelOneType = levelOneType;
	}

	public String getLevelTwoType() {
		return levelTwoType;
	}

	public void setLevelTwoType(String levelTwoType) {
		this.levelTwoType = levelTwoType;
	}

	public List<?> getAppointmentData() {
		return appointmentData;
	}

	public void setAppointmentData(List<?> appointmentData) {
		this.appointmentData = appointmentData;
	}

	public Long getMorningDataNumber() {
		return morningDataNumber;
	}

	public void setMorningDataNumber(Long morningDataNumber) {
		this.morningDataNumber = morningDataNumber;
	}

	public Long getAfternoonDataNumber() {
		return afternoonDataNumber;
	}

	public void setAfternoonDataNumber(Long afternoonDataNumber) {
		this.afternoonDataNumber = afternoonDataNumber;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getTotalDataNumber() {
		return totalDataNumber;
	}

	public void setTotalDataNumber(Long totalDataNumber) {
		this.totalDataNumber = totalDataNumber;
	}
	
	
	
}
