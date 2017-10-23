package clb.core.order.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.order.dto.OrdAfterLog;

public interface IOrdAfterLogService extends IBaseService<OrdAfterLog>, ProxySelf<IOrdAfterLogService>{
	/**
	 * 查询订单日志
	 * @param requestCtx
	 * @param dto
	 * @return
	 */
	List<OrdAfterLog> query(IRequest requestCtx, OrdAfterLog dto);

}