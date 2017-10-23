package clb.core.whtcustom.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.wecorp.dto.WecorpResponseDTO;
import clb.core.whtcustom.dto.WhtCustomReply;

public interface IWhtCustomReplyService extends IBaseService<WhtCustomReply>, ProxySelf<IWhtCustomReplyService>{
	
	List<WhtCustomReply> selectAll(IRequest requestContext, WhtCustomReply whtCustomReply);
	
	WhtCustomReply subscribeReply(String originalId);
	
	WecorpResponseDTO subscribeReplyMsg(String appid) throws Exception;
	
	List<WecorpResponseDTO> keywordkReplyMsg(String appid,String keyword) throws Exception;
	
	List<WhtCustomReply> selectAllField(IRequest requestContext, WhtCustomReply whtCustomReply ,int page, int pagesize);
}