package clb.core.whtcustom.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.whtcustom.dto.WhtCustomMenu;
import clb.core.whtcustom.dto.WhtCustomReply;

public interface WhtCustomMenuMapper extends Mapper<WhtCustomMenu>{
	
	List<WhtCustomMenu> selectAllField(WhtCustomMenu WhtCustomMenu);
	
	WhtCustomMenu menuClick(String key);
	
}