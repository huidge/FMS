package clb.core.whtcustom.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.whtcustom.dto.WhtMsgTemplate;

public interface IWhtMsgTemplateService extends IBaseService<WhtMsgTemplate>, ProxySelf<IWhtMsgTemplateService>{
	
	List<WhtMsgTemplate> selectAll(IRequest requestContext, WhtMsgTemplate WhtMsgTemplate);
	
	List<WhtMsgTemplate> selectAllField(IRequest requestContext, WhtMsgTemplate WhtMsgTemplate ,int page, int pagesize);
}