package clb.core.whtcustom.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.whtcustom.dto.WhtCrKeywordRule;

public interface WhtCrKeywordRuleMapper extends Mapper<WhtCrKeywordRule>{
	
	List<WhtCrKeywordRule> selectAllField(WhtCrKeywordRule WhtCrKeywordRule);
}