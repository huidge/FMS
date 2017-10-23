package clb.core.whtcustom.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.whtcustom.dto.WhtMsgTemplate;

public interface WhtMsgTemplateMapper extends Mapper<WhtMsgTemplate>{
	
	List<WhtMsgTemplate> selectAllField(WhtMsgTemplate WhtMsgTemplate);
}