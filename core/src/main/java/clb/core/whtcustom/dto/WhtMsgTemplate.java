package clb.core.whtcustom.dto;

import java.util.List;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
@ExtensionAttribute(disable=true)
@Table(name = "fms_wht_msg_template")
public class WhtMsgTemplate extends BaseDTO {
     @Id
     @GeneratedValue
      private Long templateId;
     
      private String whtTemplateId;

     @NotNull
      private Long cfgId;

     @NotEmpty
     private String templateCode;
     
     @NotEmpty
      private String templateName;

      private String primaryIndustry;

      private String secondaryIndustry;

      private String detailContent;

      private String contentSample;

      private Long programId;

      private Long requestId;

      @Transient
      private String backgroundAppid;
      
      @Transient
      private List msgObject;
      
    public List getMsgObject() {
		return msgObject;
	}

	public void setMsgObject(List msgObject) {
		this.msgObject = msgObject;
	}

	public String getWhtTemplateId() {
		return whtTemplateId;
	}

	public void setWhtTemplateId(String whtTemplateId) {
		this.whtTemplateId = whtTemplateId;
	}

	public String getBackgroundAppid() {
		return backgroundAppid;
	}

	public void setBackgroundAppid(String backgroundAppid) {
		this.backgroundAppid = backgroundAppid;
	}

	public void setTemplateId(Long templateId){
         this.templateId = templateId;
     }

     public Long getTemplateId(){
         return templateId;
     }

     public void setCfgId(Long cfgId){
         this.cfgId = cfgId;
     }

     public Long getCfgId(){
         return cfgId;
     }

     public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public void setPrimaryIndustry(String primaryIndustry){
         this.primaryIndustry = primaryIndustry;
     }

     public String getPrimaryIndustry(){
         return primaryIndustry;
     }

     public void setSecondaryIndustry(String secondaryIndustry){
         this.secondaryIndustry = secondaryIndustry;
     }

     public String getSecondaryIndustry(){
         return secondaryIndustry;
     }

     public void setDetailContent(String detailContent){
         this.detailContent = detailContent;
     }

     public String getDetailContent(){
         return detailContent;
     }

     public void setContentSample(String contentSample){
         this.contentSample = contentSample;
     }

     public String getContentSample(){
         return contentSample;
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