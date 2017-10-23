package clb.core.order.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.order.dto.SysOrderCfgField;

public interface ISysOrderCfgFieldService extends IBaseService<SysOrderCfgField>, ProxySelf<ISysOrderCfgFieldService>{

	/**
	 * 查询字段信息
	 * @param requestContext
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<SysOrderCfgField> selectFieldByCfgId(IRequest requestContext,Long cfgId,int page,int pageSize);
	
	/**
	 * 查询剩下的值
	 * @param cfgId
	 * @return
	 */
	List<SysOrderCfgField> selectFieldInfo(Long cfgId);
}