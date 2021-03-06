package clb.core.forecast.dto;

import java.util.List;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
@ExtensionAttribute(disable=true)
@Table(name = "fms_fet_question")
public class FetQuestion extends BaseDTO {
     @Id
     @GeneratedValue
      private Long questionId;

     @NotEmpty
      private String checkPeriod;
     
     @NotEmpty
     private String paymentCompanyType;
     
     @NotNull
     private Long paymentCompanyId;

     @NotNull
      private Long channelId;

      private Long version;

      private String questionNumber;

      private String status;
      
      //付款方名称
      @Transient
      private String paymentCompanyName;
      
      @Transient
      private String channelName;

      @Transient
      private List<SysFile> deleteFiles;
      
      @Transient
      private List<FetQuestionLine> lines;
      
      @Transient
      private Long userId;

     public void setQuestionId(Long questionId){
         this.questionId = questionId;
     }

     public Long getQuestionId(){
         return questionId;
     }

     public void setCheckPeriod(String checkPeriod){
         this.checkPeriod = checkPeriod;
     }

     public String getCheckPeriod(){
         return checkPeriod;
     }

     public void setChannelId(Long channelId){
         this.channelId = channelId;
     }

     public Long getChannelId(){
         return channelId;
     }

     public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public void setQuestionNumber(String questionNumber){
         this.questionNumber = questionNumber;
     }

     public String getQuestionNumber(){
         return questionNumber;
     }

     public void setStatus(String status){
         this.status = status;
     }

     public String getStatus(){
         return status;
     }

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public List<SysFile> getDeleteFiles() {
		return deleteFiles;
	}

	public void setDeleteFiles(List<SysFile> deleteFiles) {
		this.deleteFiles = deleteFiles;
	}

	public List<FetQuestionLine> getLines() {
		return lines;
	}

	public void setLines(List<FetQuestionLine> lines) {
		this.lines = lines;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPaymentCompanyType() {
		return paymentCompanyType;
	}

	public void setPaymentCompanyType(String paymentCompanyType) {
		this.paymentCompanyType = paymentCompanyType;
	}

	public Long getPaymentCompanyId() {
		return paymentCompanyId;
	}

	public void setPaymentCompanyId(Long paymentCompanyId) {
		this.paymentCompanyId = paymentCompanyId;
	}

	public String getPaymentCompanyName() {
		return paymentCompanyName;
	}

	public void setPaymentCompanyName(String paymentCompanyName) {
		this.paymentCompanyName = paymentCompanyName;
	}
     
     

}
