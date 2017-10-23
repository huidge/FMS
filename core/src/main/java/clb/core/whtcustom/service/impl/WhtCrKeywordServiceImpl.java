package clb.core.whtcustom.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.whtcustom.dto.WhtCrKeyword;
import clb.core.whtcustom.mapper.WhtCrKeywordMapper;
import clb.core.whtcustom.service.IWhtCrKeywordService;

@Service
@Transactional
public class WhtCrKeywordServiceImpl extends BaseServiceImpl<WhtCrKeyword> implements IWhtCrKeywordService{
	
	@Autowired
    private WhtCrKeywordMapper WhtCrKeywordMapper;
	
	@Override
	public List<WhtCrKeyword> selectAllField(IRequest requestContext, WhtCrKeyword WhtCrKeyword, int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<WhtCrKeyword> WhtCrKeyword2 = WhtCrKeywordMapper.selectAllField(WhtCrKeyword);
		return WhtCrKeyword2;
	}
	
	@Override
	public List<WhtCrKeyword> selectAll(IRequest requestContext, WhtCrKeyword WhtCrKeyword) {
		List<WhtCrKeyword> WhtCrKeyword2 = WhtCrKeywordMapper.selectAllField(WhtCrKeyword);
		return WhtCrKeyword2;
	}
}