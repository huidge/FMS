package clb.core.wecorp.dto;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by lp on 2016/11/18.
 */
@Table(name = "woa_account_cr_keyword")
public class WecorpKeyword extends BaseDTO {
    @Id
    @GeneratedValue(
            generator = "UUID"
    )
    private String keywordId;
    private String keywordValue;
    private String ruleId;
    private String matchingFlag;
    private Long version;

    public String getKeywordId() {
        return keywordId;
    }

    public void setKeywordId(String keywordId) {
        this.keywordId = keywordId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getMatchingFlag() {
        return matchingFlag;
    }

    public void setMatchingFlag(String matchingFlag) {
        this.matchingFlag = matchingFlag;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getKeywordValue() {
        return keywordValue;
    }

    public void setKeywordValue(String keywordValue) {
        this.keywordValue = keywordValue;
    }
}
