package clb.core.wecorp.mapper;

import clb.core.wecorp.dto.WecorpKeyword;
import com.hand.hap.mybatis.common.Mapper;

import java.util.List;

/**
 * Created by lp on 2016/11/18.
 */
public interface WecorpKeywordMapper extends Mapper<WecorpKeyword> {
    List<WecorpKeyword> getKeywordByRuleId(String ruleId);
    int delRule(String ruleId);
}
