package clb.core.system.dto;

import java.util.Date;

/**
 * Created by jiaolong.li on 2017-05-10.
 */

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import clb.core.core.annotation.SecurityField;
import org.hibernate.validator.constraints.NotEmpty;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

@Table(name = "sys_user")
public class ClbUser extends BaseDTO {
    /**
     *
     */
    private static final long serialVersionUID = -1716532080058997350L;

    @Id
    @GeneratedValue
    private Long userId;

    private String userType;

    @NotEmpty
    private String userName;

    private String passwordEncrypted;

    private String email;

    @SecurityField
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

    @Transient
    private String employeeName;

    @Transient
    private String ownershipType;

    @Transient
    private Long ownershipId;

    @Transient
    private String roleCode;

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getOwnershipType() {
        return ownershipType;
    }

    public void setOwnershipType(String ownershipType) {
        this.ownershipType = ownershipType;
    }

    public Long getOwnershipId() {
        return ownershipId;
    }

    public void setOwnershipId(Long ownershipId) {
        this.ownershipId = ownershipId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

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
    //原来的密码
    @Transient
    private String prePassword;

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

    public String getPrePassword() {
		return prePassword;
	}

	public void setPrePassword(String prePassword) {
		this.prePassword = prePassword;
	}

    @SecurityField
	private String phoneCode;

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    @Transient
    private String verifiCode;

    public String getVerifiCode() {
        return verifiCode;
    }

    public void setVerifiCode(String verifiCode) {
        this.verifiCode = verifiCode;
    }

    public String ntcFlag;
    public String emailFlag;
    public String smsFlag;
    public String wechatFlag;
    public String platformFlag;

    public String getNtcFlag() {
        return ntcFlag;
    }

    public void setNtcFlag(String ntcFlag) {
        this.ntcFlag = ntcFlag;
    }

    public String getEmailFlag() {
        return emailFlag;
    }

    public void setEmailFlag(String emailFlag) {
        this.emailFlag = emailFlag;
    }

    public String getSmsFlag() {
        return smsFlag;
    }

    public void setSmsFlag(String smsFlag) {
        this.smsFlag = smsFlag;
    }

    public String getWechatFlag() {
        return wechatFlag;
    }

    public void setWechatFlag(String wechatFlag) {
        this.wechatFlag = wechatFlag;
    }

    public String getPlatformFlag() {
        return platformFlag;
    }

    public void setPlatformFlag(String platformFlag) {
        this.platformFlag = platformFlag;
    }

    public String lecturerFlag;


    public String getLecturerFlag() {
        return lecturerFlag;
    }

    public void setLecturerFlag(String lecturerFlag) {
        this.lecturerFlag = lecturerFlag;
    }

    @Transient
    private Long roleId;
    
    @Transient
    private String roleName;

    @Transient
    private String backgroundAppid;
    @Transient
    private String wechatOpenid;
    @Transient
    private String wechatBindType;
    @Transient
    private String wechatConcernType;
    
    @Column
    private String attribute1;

    @Column
    private String attribute2;
	public String getWechatOpenid() {
		return wechatOpenid;
	}
	public void setWechatOpenid(String wechatOpenid) {
		this.wechatOpenid = wechatOpenid;
	}
	public String getWechatBindType() {
		return wechatBindType;
	}
	public void setWechatBindType(String wechatBindType) {
		this.wechatBindType = wechatBindType;
	}
	public String getWechatConcernType() {
		return wechatConcernType;
	}
	public void setWechatConcernType(String wechatConcernType) {
		this.wechatConcernType = wechatConcernType;
	}
	public String getBackgroundAppid() {
		return backgroundAppid;
	}
	public void setBackgroundAppid(String backgroundAppid) {
		this.backgroundAppid = backgroundAppid;
	}

    public String getNewUserName() {
        return newUserName;
    }

    public void setNewUserName(String newUserName) {
        this.newUserName = newUserName;
    }

    @Transient
    private String newUserName;

    public String getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(String appUserId) {
        this.appUserId = appUserId;
    }

    @Transient
    private String appUserId;

    @Override
    public String getAttribute1() {
        return attribute1;
    }

    @Override
    public void setAttribute1(String attribute1) {
        this.attribute1 = attribute1;
    }

    @Override
    public String getAttribute2() {
        return attribute2;
    }

    @Override
    public void setAttribute2(String attribute2) {
        this.attribute2 = attribute2;
    }

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    @Transient
    private Long appId;
    
}
