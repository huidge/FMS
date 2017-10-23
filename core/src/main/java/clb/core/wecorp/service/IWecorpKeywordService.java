package clb.core.wecorp.service;

import clb.core.wecorp.dto.WecorpKeyword;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

/**
 * Created by lp on 2016/11/18.
 */
public interface IWecorpKeywordService extends IBaseService<WecorpKeyword>, ProxySelf<IWecorpKeywordService> {
    List<WecorpKeyword> getKeywordByRuleId(String ruleId);
    int delRule(String ruleId);
}
