package clb.core.order.dto;

import javax.persistence.Column;
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
@Table(name = "fms_sys_order_function_cfg")
public class SysOrderFunctionCfg extends BaseDTO {
     @Id
     @GeneratedValue
      private Long cfgId;

     @NotNull
      private Long functionId;

      private String comments;

     @NotEmpty
      private String status;

      private Long programId;

      private Long requestId;
      
      @Transient
      private String functionName;

      
     public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public void setCfgId(Long cfgId){
         this.cfgId = cfgId;
     }

     public Long getCfgId(){
         return cfgId;
     }

     public void setFunctionId(Long functionId){
         this.functionId = functionId;
     }

     public Long getFunctionId(){
         return functionId;
     }

     public void setComments(String comments){
         this.comments = comments;
     }

     public String getComments(){
         return comments;
     }

     public void setStatus(String status){
         this.status = status;
     }

     public String getStatus(){
         return status;
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
