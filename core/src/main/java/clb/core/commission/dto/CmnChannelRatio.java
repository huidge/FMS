package clb.core.commission.dto;

/**
 * Created by wanjun.feng on 2017-09-11.
 */

import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Table(name = "fms_cmn_channel_ratio")
public class CmnChannelRatio extends BaseDTO {
    private static final long serialVersionUID = -4099408259290407518L;

    @Id
    @GeneratedValue
    private Long ratioId;

    @NotEmpty
    private String ratioName;

    @NotNull
    private Long channelId;
    
    @Transient
    private String channelName;
    
    @NotNull
    private Long objectVersionNumber;
    
    @Transient
    private String roleCode;
    
    @Transient
    private Long userId;

    public Long getRatioId() {
        return ratioId;
    }

    public void setRatioId(Long ratioId) {
        this.ratioId = ratioId;
    }

    public String getRatioName() {
        return ratioName;
    }

    public void setRatioName(String ratioName) {
        this.ratioName = ratioName;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

