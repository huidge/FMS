package clb.core.order.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.order.dto.SysOrderCfgField;
import clb.core.order.mapper.SysOrderCfgFieldMapper;
import clb.core.order.service.ISysOrderCfgFieldService;

@Service
@Transactional
public class SysOrderCfgFieldServiceImpl extends BaseServiceImpl<SysOrderCfgField> implements ISysOrderCfgFieldService{

	@Autowired
	private SysOrderCfgFieldMapper sysOrderCfgFieldMapper;
	
	@Override
	public List<SysOrderCfgField> selectFieldByCfgId(IRequest requestContext, Long cfgId, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return sysOrderCfgFieldMapper.selectFieldByCfgId(cfgId);
	}

	@Override
	public List<SysOrderCfgField> selectFieldInfo(Long cfgId) {
		return sysOrderCfgFieldMapper.selectFieldInfo(cfgId);
	}

}