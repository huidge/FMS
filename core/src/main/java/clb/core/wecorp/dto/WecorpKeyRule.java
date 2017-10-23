package clb.core.wecorp.dto;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by lp on 2016/11/18.
 */
@Table(name = "woa_account_cr_keyword_rule")
public class WecorpKeyRule extends BaseDTO {
    @Id
    @GeneratedValue(
            generator = "UUID"
    )
    private String ruleId;
    private String ruleName;
    private String replyAllFlag;
    private String content;
    private Long version;

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getReplyAllFlag() {
        return replyAllFlag;
    }

    public void setReplyAllFlag(String replyAllFlag) {
        this.replyAllFlag = replyAllFlag;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
