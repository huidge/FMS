package clb.core.channel.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
@ExtensionAttribute(disable=true)
@Table(name = "fms_cnl_contract_role")
public class CnlContractRole extends BaseDTO {
    @Id
    @GeneratedValue
    private Long contractRoleId;

    @NotNull
    private Long channelContractId;

    private String role;

    private Long roleUserId;

    private String benefit;

    private Long programId;

    private Long requestId;

    @Transient
    private Long channelCommissionLineId;

    @Transient
    private String name;

    public void setContractRoleId(Long contractRoleId){
        this.contractRoleId = contractRoleId;
    }

    public Long getContractRoleId(){
        return contractRoleId;
    }

    public void setChannelContractId(Long channelContractId){
        this.channelContractId = channelContractId;
    }

    public Long getChannelContractId(){
        return channelContractId;
    }

    public void setRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }

    public Long getRoleUserId() {
        return roleUserId;
    }

    public void setRoleUserId(Long roleUserId) {
        this.roleUserId = roleUserId;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setBenefit(String benefit){
        this.benefit = benefit;
    }

    public String getBenefit(){
        return benefit;
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

    public Long getChannelCommissionLineId() {
        return channelCommissionLineId;
    }

    public void setChannelCommissionLineId(Long channelCommissionLineId) {
        this.channelCommissionLineId = channelCommissionLineId;
    }
}
