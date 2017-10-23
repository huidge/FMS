package clb.core.order.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.order.dto.SysOrderCfgOperation;
import clb.core.order.mapper.SysOrderCfgOperationMapper;
import clb.core.order.service.ISysOrderCfgOperationService;

@Service
@Transactional
public class SysOrderCfgOperationServiceImpl extends BaseServiceImpl<SysOrderCfgOperation> implements ISysOrderCfgOperationService{

	@Autowired
	private SysOrderCfgOperationMapper sysOrderCfgOperationMapper;
	
	@Override
	public List<SysOrderCfgOperation> selectOperationByCfgId(IRequest requestContext, Long cfgId, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return sysOrderCfgOperationMapper.selectOperationByCfgId(cfgId);
	}

	@Override
	public List<SysOrderCfgOperation> selectOperationInfo(Long cfgId) {
		return sysOrderCfgOperationMapper.selectOperationInfo(cfgId);
	}

}