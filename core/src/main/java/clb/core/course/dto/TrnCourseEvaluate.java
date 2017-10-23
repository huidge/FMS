package clb.core.course.dto;

import java.util.Date;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;

import clb.core.core.annotation.SecurityField;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
@ExtensionAttribute(disable=true)
@Table(name = "fms_trn_course_evaluate")
public class TrnCourseEvaluate extends BaseDTO {
     @Id
     @GeneratedValue
      private Long evaluateId;
     
     @NotNull
     private Long courseId;

     @NotNull
      private Long channelId;
     
     @SecurityField
      private String phoneCode;
     
     @SecurityField
      private String mobile;

     @NotNull
      private Long courseContent;

     @NotNull
      private Long courseUsability;

     @NotNull
      private Long courseEnvironment;

     @NotNull
      private Long lecturerPresentation;

     @NotNull
      private Long lecturerProfessional;

     @NotNull
      private Long lecturerAppetency;

      private String evaluateContent;

      private Long programId;

      private Long requestId;

      @Transient
  	  private String channelName;
      
      @Transient
  	  private String courseEvaluate;
      
      @Transient
  	  private String lecturerEvaluate; 
      
      @Transient
  	  private String evaluateDate; 
      
      @Transient
  	  private String contentFlag;
      
      @Transient
      private String contactPhoneComb;
      
     public String getContentFlag() {
		return contentFlag;
	}

	public void setContentFlag(String contentFlag) {
		this.contentFlag = contentFlag;
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

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getCourseEvaluate() {
		return courseEvaluate;
	}

	public void setCourseEvaluate(String courseEvaluate) {
		this.courseEvaluate = courseEvaluate;
	}

	public String getLecturerEvaluate() {
		return lecturerEvaluate;
	}

	public void setLecturerEvaluate(String lecturerEvaluate) {
		this.lecturerEvaluate = lecturerEvaluate;
	}

	public String getEvaluateDate() {
		return evaluateDate;
	}

	public void setEvaluateDate(String evaluateDate) {
		this.evaluateDate = evaluateDate;
	}

	public Long getCourseId() {
		return courseId;
	}

	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}

	public void setEvaluateId(Long evaluateId){
         this.evaluateId = evaluateId;
     }

     public Long getEvaluateId(){
         return evaluateId;
     }

     public void setChannelId(Long channelId){
         this.channelId = channelId;
     }

     public Long getChannelId(){
         return channelId;
     }

     public void setMobile(String mobile){
         this.mobile = mobile;
     }

     public String getMobile(){
         return mobile;
     }

     public void setCourseContent(Long courseContent){
         this.courseContent = courseContent;
     }

     public Long getCourseContent(){
         return courseContent;
     }

     public void setCourseUsability(Long courseUsability){
         this.courseUsability = courseUsability;
     }

     public Long getCourseUsability(){
         return courseUsability;
     }

     public void setCourseEnvironment(Long courseEnvironment){
         this.courseEnvironment = courseEnvironment;
     }

     public Long getCourseEnvironment(){
         return courseEnvironment;
     }

     public void setLecturerPresentation(Long lecturerPresentation){
         this.lecturerPresentation = lecturerPresentation;
     }

     public Long getLecturerPresentation(){
         return lecturerPresentation;
     }

     public void setLecturerProfessional(Long lecturerProfessional){
         this.lecturerProfessional = lecturerProfessional;
     }

     public Long getLecturerProfessional(){
         return lecturerProfessional;
     }

     public void setLecturerAppetency(Long lecturerAppetency){
         this.lecturerAppetency = lecturerAppetency;
     }

     public Long getLecturerAppetency(){
         return lecturerAppetency;
     }

     public void setEvaluateContent(String evaluateContent){
         this.evaluateContent = evaluateContent;
     }

     public String getEvaluateContent(){
         return evaluateContent;
     }

     public void setProgramId(Long programId){
         this.programId = programId;
     }

     public Long getProgramId(){
         return programId;
     }

     public void setRequestId(Long requestId){
         this.requestId = requestId;
     }

     public Long getRequestId(){
         return requestId;
     }

     }

     
