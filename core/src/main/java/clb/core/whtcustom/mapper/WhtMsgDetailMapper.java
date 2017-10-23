package clb.core.whtcustom.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.whtcustom.dto.WhtMsgDetail;

public interface WhtMsgDetailMapper extends Mapper<WhtMsgDetail>{
	
	List<WhtMsgDetail> selectAllField(WhtMsgDetail WhtMsgDetail);
	
	List<WhtMsgDetail> selectTemplateName(WhtMsgDetail WhtMsgDetail);
}