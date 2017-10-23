package clb.core.wecorp.service.impl;

import clb.core.wecorp.dto.WecorpKeyword;
import clb.core.wecorp.mapper.WecorpKeywordMapper;
import clb.core.wecorp.service.IWecorpKeywordService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lp on 2016/11/18.
 */
@Service
public class WecorpKeywordServiceImpl extends BaseServiceImpl<WecorpKeyword> implements IWecorpKeywordService {
    @Autowired
    private WecorpKeywordMapper wecorpKeywordMapper;

    @Override
    public List<WecorpKeyword> getKeywordByRuleId(String ruleId) {
        return wecorpKeywordMapper.getKeywordByRuleId(ruleId);
    }

    @Override
    public int delRule(String ruleId) {
        return wecorpKeywordMapper.delRule(ruleId);
    }
}
