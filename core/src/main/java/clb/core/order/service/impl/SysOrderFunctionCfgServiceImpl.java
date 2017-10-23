package clb.core.order.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.order.dto.SysOrderFunctionCfg;
import clb.core.order.mapper.SysOrderFunctionCfgMapper;
import clb.core.order.service.ISysOrderFunctionCfgService;

@Service
@Transactional
public class SysOrderFunctionCfgServiceImpl extends BaseServiceImpl<SysOrderFunctionCfg> implements ISysOrderFunctionCfgService{

	@Autowired
	private SysOrderFunctionCfgMapper sysOrderFunctionCfgMapper;
	
	@Override
	public List<SysOrderFunctionCfg> selectFunctionInfo(IRequest requestContext, SysOrderFunctionCfg dto, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return sysOrderFunctionCfgMapper.selectFunctionInfo(dto);
	}

	@Override
	public List<SysOrderFunctionCfg> queryByHeaderId(Long cfgId) {
		return sysOrderFunctionCfgMapper.queryByHeaderId(cfgId);
	}

	@Override
	public List<SysOrderFunctionCfg> selectByFunctionId(SysOrderFunctionCfg dto) {
		return sysOrderFunctionCfgMapper.selectFunctionInfo(dto);
	}

}