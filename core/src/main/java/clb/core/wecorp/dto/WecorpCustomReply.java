package clb.core.wecorp.dto;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Administrator on 2017/6/20.
 */
@Table(name="woa_account_custom_reply")
public class WecorpCustomReply extends BaseDTO {

    @Id
    @GeneratedValue(
            generator = "UUID"
    )
    private String customReplyId;

    @Column
    private String accountNum;

    @Column
    private String msgType;

    @Column
    private String content;

    @Column
    private String replyType;

    public String getCustomReplyId() {
        return customReplyId;
    }

    public void setCustomReplyId(String customReplyId) {
        this.customReplyId = customReplyId;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReplyType() {
        return replyType;
    }

    public void setReplyType(String replyType) {
        this.replyType = replyType;
    }
}
