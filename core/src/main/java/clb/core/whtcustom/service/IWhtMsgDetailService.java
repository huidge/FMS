package clb.core.whtcustom.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.whtcustom.dto.WhtMsgDetail;

public interface IWhtMsgDetailService extends IBaseService<WhtMsgDetail>, ProxySelf<IWhtMsgDetailService>{
	
	List<WhtMsgDetail> selectAllField(IRequest requestContext, WhtMsgDetail WhtMsgDetail ,int page, int pagesize);
	
	List<WhtMsgDetail> selectTemplateName(IRequest requestContext, WhtMsgDetail WhtMsgDetail);
}