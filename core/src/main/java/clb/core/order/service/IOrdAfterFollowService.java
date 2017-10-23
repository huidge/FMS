package clb.core.order.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.order.dto.OrdAfterFollow;

public interface IOrdAfterFollowService extends IBaseService<OrdAfterFollow>, ProxySelf<IOrdAfterFollowService>{
	/**
	 * 查询售后跟进记录  根据售后id
	 * @param dto
	 * @return
	 */
	List<OrdAfterFollow> queryByAfterId(IRequest requestCtx, OrdAfterFollow dto, int page,int pageSize);
	/**
	 * 查询最后一条记录
	 * @return
	 */
	OrdAfterFollow queryLastOrdAfterFollowByAfterId(OrdAfterFollow dto);

}