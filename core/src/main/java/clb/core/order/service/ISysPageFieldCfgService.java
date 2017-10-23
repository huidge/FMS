package clb.core.order.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.order.dto.MenuAuth;
import clb.core.order.dto.SysPageFieldCfg;

public interface ISysPageFieldCfgService extends IBaseService<SysPageFieldCfg>, ProxySelf<ISysPageFieldCfgService>{

	/**
	 * 通过角色信息查看配置信息
	 * @param requestContext
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<SysPageFieldCfg> selectSettingInfo(IRequest requestContext,SysPageFieldCfg dto,int page,int pageSize);
	
	/**
	 * 通过角色查询菜单信息
	 * @param roleCode
	 * @return
	 */
	public List<MenuAuth> accessMenuAuthList(String roleCode);
	
	/**
	 * 更新标示状态
	 * @param sysPageFieldCfg
	 * @return
	 */
	int updateFlag(SysPageFieldCfg sysPageFieldCfg);
}