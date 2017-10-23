package clb.core.order.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.order.dto.SysOrderCfgOperation;

public interface ISysOrderCfgOperationService extends IBaseService<SysOrderCfgOperation>, ProxySelf<ISysOrderCfgOperationService>{

	/**
	 * 查询操作信息
	 * @param requestContext
	 * @param cfgId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<SysOrderCfgOperation> selectOperationByCfgId(IRequest requestContext,Long cfgId,int page,int pageSize);
	
	/**
	 * 
	 * @param cfgId
	 * @return
	 */
	List<SysOrderCfgOperation> selectOperationInfo(Long cfgId);
}