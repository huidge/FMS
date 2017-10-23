package clb.core.whtcustom.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.whtcustom.dto.WhtCrKeywordRule;
import clb.core.whtcustom.mapper.WhtCrKeywordRuleMapper;
import clb.core.whtcustom.service.IWhtCrKeywordRuleService;

@Service
@Transactional
public class WhtCrKeywordRuleServiceImpl extends BaseServiceImpl<WhtCrKeywordRule> implements IWhtCrKeywordRuleService{
	
	@Autowired
    private WhtCrKeywordRuleMapper WhtCrKeywordRuleMapper;
	
	@Override
	public List<WhtCrKeywordRule> selectAllField(IRequest requestContext, WhtCrKeywordRule WhtCrKeywordRule, int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<WhtCrKeywordRule> WhtCrKeywordRule2 = WhtCrKeywordRuleMapper.selectAllField(WhtCrKeywordRule);
		return WhtCrKeywordRule2;
	}
	
	@Override
	public List<WhtCrKeywordRule> selectAll(IRequest requestContext, WhtCrKeywordRule WhtCrKeywordRule) {
		List<WhtCrKeywordRule> WhtCrKeywordRule2 = WhtCrKeywordRuleMapper.selectAllField(WhtCrKeywordRule);
		return WhtCrKeywordRule2;
	}
}