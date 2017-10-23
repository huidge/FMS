package clb.core.sys.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.sys.dto.IfInvokeInbound;

public interface IIfInvokeInboundService extends IBaseService<IfInvokeInbound>, ProxySelf<IIfInvokeInboundService>{
	/**
	 * 定期清除    sys_if_invoke_inbound、sys_if_invoke_outbound表中的数据
	 * @param request
	 * @param parseInt
	 */
	void truncateTable(IRequest request, int parseInt);

}