package clb.core.prc.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.prc.dto.PrcAttributeSet;

public interface IPrcAttributeSetService extends IBaseService<PrcAttributeSet>, ProxySelf<IPrcAttributeSetService>{

	List<PrcAttributeSet> selectSetName();
	
	List<PrcAttributeSet> selectByCondition(IRequest requestContext,PrcAttributeSet dto,int page,int pageSize);

}