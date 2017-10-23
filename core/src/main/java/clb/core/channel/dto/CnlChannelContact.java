package clb.core.channel.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import com.hand.hap.system.dto.BaseDTO;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import com.hand.hap.core.annotation.Children;
import javax.persistence.Transient;
import java.util.List;

@ExtensionAttribute(disable=true)
@Table(name = "fms_cnl_channel_contact")
public class CnlChannelContact extends BaseDTO {
     @Id
     @GeneratedValue
      private Long channelContactId;

     @NotNull
      private Long channelId;

     @NotEmpty
      private String contactObject;

     @NotEmpty
      private String contactPerson;

      private String ilPhone;

      private String hkPhone;

      private String email;

      private Long programId;

      private Long requestId;

     public void setChannelContactId(Long channelContactId){
         this.channelContactId = channelContactId;
     }

     public Long getChannelContactId(){
         return channelContactId;
     }

     public void setChannelId(Long channelId){
         this.channelId = channelId;
     }

     public Long getChannelId(){
         return channelId;
     }

     public void setContactObject(String contactObject){
         this.contactObject = contactObject;
     }

     public String getContactObject(){
         return contactObject;
     }

     public void setContactPerson(String contactPerson){
         this.contactPerson = contactPerson;
     }

     public String getContactPerson(){
         return contactPerson;
     }

     public void setIlPhone(String ilPhone){
         this.ilPhone = ilPhone;
     }

     public String getIlPhone(){
         return ilPhone;
     }

     public void setHkPhone(String hkPhone){
         this.hkPhone = hkPhone;
     }

     public String getHkPhone(){
         return hkPhone;
     }

     public void setEmail(String email){
         this.email = email;
     }

     public String getEmail(){
         return email;
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

     //合同主体分类
     private String partyType;

     public String getPartyType() {
		return partyType;
     }
	
     public void setPartyType(String partyType) {
		this.partyType = partyType;
     }
     
}
