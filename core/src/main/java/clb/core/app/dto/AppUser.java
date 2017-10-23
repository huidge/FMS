package clb.core.app.dto;

import clb.core.core.annotation.SecurityField;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author 谈晟 cheng.tan@hand-china.com
 * @description
 * @time 2017/10/16
 */
@Table(name = "fms_app_user")
public class AppUser extends BaseDTO {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private Long appUserId;

    @NotNull
    private Long clbUserId;

    @NotNull
    private String appUsername;

    @NotNull
//    @SecurityField
    private String appPassword;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(Long appUserId) {
        this.appUserId = appUserId;
    }

    public Long getClbUserId() {
        return clbUserId;
    }

    public void setClbUserId(Long clbUserId) {
        this.clbUserId = clbUserId;
    }

    public String getAppUsername() {
        return appUsername;
    }

    public void setAppUsername(String appUsername) {
        this.appUsername = appUsername;
    }

    public String getAppPassword() {
        return appPassword;
    }

    public void setAppPassword(String appPassword) {
        this.appPassword = appPassword;
    }

}
