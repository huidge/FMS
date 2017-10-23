package clb.core.wecorp.dto;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/6/14.
 */
@Table(name="woa_account")
public class WecorpAccount extends BaseDTO {

    @Id
    @GeneratedValue(
            generator = "UUID"
    )
    private String accountId;

    @Column
    private String accountNum;

    @Column
    private String accountName;

    @Column
    private String accountType;

    @Column
    private String token;

    @Column
    private String appid;

    @Column
    private String InterfaceAddr;

    @Column
    private String appSecret;

    @Column
    private String encodingAeskey;

    @Column
    private String enableFlag;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getInterfaceAddr() {
        return InterfaceAddr;
    }

    public void setInterfaceAddr(String interfaceAddr) {
        InterfaceAddr = interfaceAddr;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getEncodingAeskey() {
        return encodingAeskey;
    }

    public void setEncodingAeskey(String encodingAeskey) {
        this.encodingAeskey = encodingAeskey;
    }

    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }
}
