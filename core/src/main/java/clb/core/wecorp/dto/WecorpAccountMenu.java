package clb.core.wecorp.dto;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/6/19.
 */
@Table(name="woa_account_menu")
public class WecorpAccountMenu extends BaseDTO {

    @Id
    @GeneratedValue(
            generator = "UUID"
    )
    private String accountMenuId;

    @Column
    private String content;

    @Column
    private String accountNum;

    public String getAccountMenuId() {
        return accountMenuId;
    }

    public void setAccountMenuId(String accountMenuId) {
        this.accountMenuId = accountMenuId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }
}
