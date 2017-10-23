package clb.core.wecorp.dto;

/**
 * Created by Administrator on 2017/6/14.
 */

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;


@ExtensionAttribute(disable = true)
@Table(name = "sys_user")
public class WecorpUser extends BaseDTO {
    @Id
    @GeneratedValue
    private Long userId;

    private String userType;

    @NotEmpty
    private String userName;

    private String passwordEncrypted;

    private String email;

    private String phone;

    private Date startActiveDate;

    private Date endActiveDate;

    private Long employeeId;

    public Long getRelatedPartyId() {
        return relatedPartyId;
    }

    public void setRelatedPartyId(Long relatedPartyId) {
        this.relatedPartyId = relatedPartyId;
    }

    private Long relatedPartyId;

    public String getRelatedPartyName() {
        return relatedPartyName;
    }

    public void setRelatedPartyName(String relatedPartyName) {
        this.relatedPartyName = relatedPartyName;
    }

    @Transient
    private String relatedPartyName;

    private String status;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setPasswordEncrypted(String passwordEncrypted) {
        this.passwordEncrypted = passwordEncrypted;
    }

    public String getPasswordEncrypted() {
        return passwordEncrypted;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setStartActiveDate(Date startActiveDate) {
        this.startActiveDate = startActiveDate;
    }

    public Date getStartActiveDate() {
        return startActiveDate;
    }

    public void setEndActiveDate(Date endActiveDate) {
        this.endActiveDate = endActiveDate;
    }

    public Date getEndActiveDate() {
        return endActiveDate;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public Long getPlanQuota() {
        return planQuota;
    }

    public void setPlanQuota(Long planQuota) {
        this.planQuota = planQuota;
    }

    private Long planQuota;

    @Transient
    private String password;
    @Transient
    private String repPassword;
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRepPassword() {
        return repPassword;
    }
    public void setRepPassword(String repPassword) {
        this.repPassword = repPassword;
    }

}
