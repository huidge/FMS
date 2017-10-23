package clb.core.whtcustom.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.wecorp.dto.WecorpResponseDTO;
import clb.core.whtcustom.dto.WhtCustomReply;

public interface WhtCustomReplyMapper extends Mapper<WhtCustomReply>{
	
	List<WhtCustomReply> selectAllField(WhtCustomReply whtCustomReply);
	
	WhtCustomReply subscribeReply(String originalId);
	
}