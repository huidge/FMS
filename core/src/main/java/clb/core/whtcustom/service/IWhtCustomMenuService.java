package clb.core.whtcustom.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.wecorp.dto.WecorpResponseDTO;
import clb.core.whtcustom.dto.WhtCustomMenu;

public interface IWhtCustomMenuService extends IBaseService<WhtCustomMenu>, ProxySelf<IWhtCustomMenuService>{
	
	List<WhtCustomMenu> selectAll(IRequest requestContext, WhtCustomMenu WhtCustomMenu);
	
	WecorpResponseDTO menuClick(String appId ,String key) throws Exception;
}