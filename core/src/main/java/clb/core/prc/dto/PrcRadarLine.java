package clb.core.prc.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import com.hand.hap.system.dto.BaseDTO;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
@ExtensionAttribute(disable=true)
@Table(name = "fms_prc_radar_line")
public class PrcRadarLine extends BaseDTO {
     /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	 @Id
     @GeneratedValue
     private Long lineId;

     @NotNull
     private Long serialNumber;

     @NotEmpty
     private String compDimName;

     @NotEmpty
     private String comment;

     @NotEmpty
     private String status;

      
     private Long attSetId;

      
     private Long programId;

     private Long requestId;


     public void setLineId(Long lineId){
         this.lineId = lineId;
     }

     public Long getLineId(){
         return lineId;
     }

     public void setSerialNumber(Long serialNumber){
         this.serialNumber = serialNumber;
     }

     public Long getSerialNumber(){
         return serialNumber;
     }

     public void setCompDimName(String compDimName){
         this.compDimName = compDimName;
     }

     public String getCompDimName(){
         return compDimName;
     }

     public void setComment(String comment){
         this.comment = comment;
     }

     public String getComment(){
         return comment;
     }

     public void setStatus(String status){
         this.status = status;
     }

     public String getStatus(){
         return status;
     }

     public void setAttSetId(Long attSetId){
         this.attSetId = attSetId;
     }

     public Long getAttSetId(){
         return attSetId;
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
