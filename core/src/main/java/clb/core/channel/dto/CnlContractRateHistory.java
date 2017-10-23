package clb.core.channel.dto;

import java.util.Date;

/*
 * Created by wanjun.feng on 2017-09-18.
 */
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.hand.hap.system.dto.BaseDTO;

import org.hibernate.validator.constraints.NotEmpty;
@ExtensionAttribute(disable=true)
@Table(name = "fms_cnl_contract_rate_history")
public class CnlContractRateHistory extends BaseDTO {
    private static final long serialVersionUID = 3195479098763418167L;

    @Id
    @GeneratedValue
    private Long rateHisId;
    
    @NotNull
    private Long channelContractId;

    @NotEmpty
    private String rateLevelName;

    private String comments;
    
    @NotNull
    private Long createdBy;
    
    @Transient
    private String userName;
    
    private Date creationDate;
    
    @NotNull
    private Long objectVersionNumber;

    public Long getRateHisId() {
        return rateHisId;
    }

    public void setRateHisId(Long rateHisId) {
        this.rateHisId = rateHisId;
    }

    public Long getChannelContractId() {
        return channelContractId;
    }

    public void setChannelContractId(Long channelContractId) {
        this.channelContractId = channelContractId;
    }

    public String getRateLevelName() {
        return rateLevelName;
    }

    public void setRateLevelName(String rateLevelName) {
        this.rateLevelName = rateLevelName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
