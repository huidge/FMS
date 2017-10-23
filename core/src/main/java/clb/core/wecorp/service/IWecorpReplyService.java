package clb.core.wecorp.service;

import clb.core.wecorp.dto.WecorpCustomReply;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Created by lp on 2016/11/4.
 */
public interface IWecorpReplyService extends IBaseService<WecorpCustomReply>, ProxySelf<IWecorpReplyService> {
    JSONObject uploadReply(String url, String appId);
    String saveReplyMsg(String reply_type,JSONObject content,String account_num,String msg_type) throws Exception;
    List getContent(String account_num,String reply_type);
    String saveKeywordRuleProcess(JSONObject content,String reply_type) throws Exception;
    List getKeywordRule(String account_num,String reply_type);
    WecorpCustomReply findByAccountNumAndReplyType(String accountNum,String replyType);
    JSONArray getKeywordReplyContents(String account_num,String reply_type,String msg);
}
