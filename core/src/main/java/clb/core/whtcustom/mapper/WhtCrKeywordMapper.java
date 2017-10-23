package clb.core.whtcustom.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.whtcustom.dto.WhtCrKeyword;

public interface WhtCrKeywordMapper extends Mapper<WhtCrKeyword>{
	
	List<WhtCrKeyword> selectAllField(WhtCrKeyword WhtCrKeyword);
	
	List<WhtCrKeyword> keywordkReplyMsg(String keyword);
}