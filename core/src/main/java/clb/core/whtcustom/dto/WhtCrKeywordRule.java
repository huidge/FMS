package clb.core.whtcustom.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.NotEmpty;
@ExtensionAttribute(disable=true)
@Table(name = "fms_wht_cr_keyword_rule")
public class WhtCrKeywordRule extends BaseDTO {
     @Id
     @GeneratedValue
      private Long ruleId;

      private String accountNum;
     
      private String originalId;
      
      private String ruleOperation;

     @NotEmpty
      private String ruleName;

     @NotEmpty
      private String replyAllFlag;

      private String content;

      private String rulePictureId;

      private String ruleVoiceId;

      private String ruleVideoId;

      private String rulePictureText;
      
      private String whtRuleAttachContent;

      private Long programId;

      private Long requestId;

      
     public String getWhtRuleAttachContent() {
		return whtRuleAttachContent;
	}

	public void setWhtRuleAttachContent(String whtRuleAttachContent) {
		this.whtRuleAttachContent = whtRuleAttachContent;
	}

	public String getOriginalId() {
		return originalId;
	}

	public void setOriginalId(String originalId) {
		this.originalId = originalId;
	}

	public String getRuleOperation() {
		return ruleOperation;
	}

	public void setRuleOperation(String ruleOperation) {
		this.ruleOperation = ruleOperation;
	}

	public void setRuleId(Long ruleId){
         this.ruleId = ruleId;
     }

     public Long getRuleId(){
         return ruleId;
     }

     public void setAccountNum(String accountNum){
         this.accountNum = accountNum;
     }

     public String getAccountNum(){
         return accountNum;
     }

     public void setRuleName(String ruleName){
         this.ruleName = ruleName;
     }

     public String getRuleName(){
         return ruleName;
     }

     public void setReplyAllFlag(String replyAllFlag){
         this.replyAllFlag = replyAllFlag;
     }

     public String getReplyAllFlag(){
         return replyAllFlag;
     }

     public void setContent(String content){
         this.content = content;
     }

     public String getContent(){
         return content;
     }

     public void setRulePictureId(String rulePictureId){
         this.rulePictureId = rulePictureId;
     }

     public String getRulePictureId(){
         return rulePictureId;
     }

     public void setRuleVoiceId(String ruleVoiceId){
         this.ruleVoiceId = ruleVoiceId;
     }

     public String getRuleVoiceId(){
         return ruleVoiceId;
     }

     public void setRuleVideoId(String ruleVideoId){
         this.ruleVideoId = ruleVideoId;
     }

     public String getRuleVideoId(){
         return ruleVideoId;
     }

     public void setRulePictureText(String rulePictureText){
         this.rulePictureText = rulePictureText;
     }

     public String getRulePictureText(){
         return rulePictureText;
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
