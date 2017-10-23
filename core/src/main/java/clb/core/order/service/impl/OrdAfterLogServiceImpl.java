package clb.core.order.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdAfterLog;
import clb.core.order.mapper.OrdAfterLogMapper;
import clb.core.order.service.IOrdAfterLogService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrdAfterLogServiceImpl extends BaseServiceImpl<OrdAfterLog> implements IOrdAfterLogService{
	@Autowired
	private OrdAfterLogMapper mapper;
	/**
	 * 查询订单日志
	 */
	@Override
	public List<OrdAfterLog> query(IRequest requestCtx, OrdAfterLog dto) {
		return mapper.query(dto);
	}
}