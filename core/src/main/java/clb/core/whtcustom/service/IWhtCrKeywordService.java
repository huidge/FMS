package clb.core.whtcustom.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.whtcustom.dto.WhtCrKeyword;

public interface IWhtCrKeywordService extends IBaseService<WhtCrKeyword>, ProxySelf<IWhtCrKeywordService>{
	
	List<WhtCrKeyword> selectAll(IRequest requestContext, WhtCrKeyword WhtCrKeyword);
	
	List<WhtCrKeyword> selectAllField(IRequest requestContext, WhtCrKeyword WhtCrKeyword ,int page, int pagesize);
}