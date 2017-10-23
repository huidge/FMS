package clb.core.whtcustom.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.whtcustom.dto.WhtCrKeywordRule;

public interface IWhtCrKeywordRuleService extends IBaseService<WhtCrKeywordRule>, ProxySelf<IWhtCrKeywordRuleService>{
	
	List<WhtCrKeywordRule> selectAll(IRequest requestContext, WhtCrKeywordRule WhtCrKeywordRule);
	
	List<WhtCrKeywordRule> selectAllField(IRequest requestContext, WhtCrKeywordRule WhtCrKeywordRule ,int page, int pagesize);
}