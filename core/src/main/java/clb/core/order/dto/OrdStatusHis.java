package clb.core.order.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;
import java.util.Date;
@ExtensionAttribute(disable=true)
@Table(name = "fms_ord_status_his")
public class OrdStatusHis extends BaseDTO {
    @Id
    @GeneratedValue
    private Long statusHisId;

    private Long orderId;

    private Date statusDate;

    private String status;

    private String description;

    private Long operatorUserId;//操作人

    private Long programId;

    private Long requestId;

    @Transient
    private String meaning;

    @Transient
    private String operatorUserName;//操作人

    @Transient
    private String statusConfirm;//状态的确认情况
    

    public void setStatusHisId(Long statusHisId){
        this.statusHisId = statusHisId;
    }

    public Long getStatusHisId(){
        return statusHisId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setStatusDate(Date statusDate){
        this.statusDate = statusDate;
    }

    public Date getStatusDate(){
        return statusDate;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public Long getOperatorUserId() {
        return operatorUserId;
    }

    public void setOperatorUserId(Long operatorUserId) {
        this.operatorUserId = operatorUserId;
    }

    public String getOperatorUserName() {
        return operatorUserName;
    }

    public void setOperatorUserName(String operatorUserName) {
        this.operatorUserName = operatorUserName;
    }

    public String getStatusConfirm() {
        return statusConfirm;
    }

    public void setStatusConfirm(String statusConfirm) {
        this.statusConfirm = statusConfirm;
    }
}