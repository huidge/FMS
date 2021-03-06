package clb.core.order.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;
import javax.validation.constraints.NotNull;
@ExtensionAttribute(disable=true)
@Table(name = "fms_ord_template_line")
public class OrdTemplateLine extends BaseDTO {
     @Id
     @GeneratedValue
      private Long templateLineId;

     @NotNull
      private Long templateId;

      private String templateName;

      private String templateContent;

      private Long programId;

      private Long requestId;
  	@Transient
      private String templateTypeCode;
	@Transient
      private String afterType;
	@Transient
      private String afterProject;

      
     public String getTemplateTypeCode() {
		return templateTypeCode;
	}

	public void setTemplateTypeCode(String templateTypeCode) {
		this.templateTypeCode = templateTypeCode;
	}

	public String getAfterType() {
		return afterType;
	}

	public void setAfterType(String afterType) {
		this.afterType = afterType;
	}

	public String getAfterProject() {
		return afterProject;
	}

	public void setAfterProject(String afterProject) {
		this.afterProject = afterProject;
	}

	public void setTemplateLineId(Long templateLineId){
         this.templateLineId = templateLineId;
     }

     public Long getTemplateLineId(){
         return templateLineId;
     }

     public void setTemplateId(Long templateId){
         this.templateId = templateId;
     }

     public Long getTemplateId(){
         return templateId;
     }

     public void setTemplateName(String templateName){
         this.templateName = templateName;
     }

     public String getTemplateName(){
         return templateName;
     }

     public void setTemplateContent(String templateContent){
         this.templateContent = templateContent;
     }

     public String getTemplateContent(){
         return templateContent;
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
