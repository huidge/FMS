package clb.core.sys.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.sys.dto.IfInvokeInbound;
import clb.core.sys.mapper.IfInvokeInboundMapper;
import clb.core.sys.service.IIfInvokeInboundService;

@Service
@Transactional
public class IfInvokeInboundServiceImpl extends BaseServiceImpl<IfInvokeInbound> implements IIfInvokeInboundService{
	
	@Autowired
	private IfInvokeInboundMapper mapper;
	/**
	 * 定期清除    sys_if_invoke_inbound、sys_if_invoke_outbound表中的数据
	 */
	@Override
	public void truncateTable(IRequest request, int days) {
		String sql = "delete from sys_if_invoke_inbound where datediff(now(),CREATION_DATE) > " + days;
		mapper.truncateTable(sql);
	}
}