package clb.core.wecorp.dto;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/6/14.
 */
@Table(name="woa_user_bunding")
public class WecorpUserBunding extends BaseDTO {

    @Id
    @GeneratedValue(
            generator = "UUID"
    )
    private String userBundingId;

    @Column
    private String accountId;

    @Column
    private Long userId;

    @Column
    private String openId;

    public String getUserBundingId() {
        return userBundingId;
    }

    public void setUserBundingId(String userBundingId) {
        this.userBundingId = userBundingId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
