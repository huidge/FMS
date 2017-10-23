package clb.core.course.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.course.dto.TrnSupport;

public interface ITrnSupportService extends IBaseService<TrnSupport>, ProxySelf<ITrnSupportService>{
	List<TrnSupport> selectAllField(IRequest requestContext, TrnSupport trnSupport ,int page, int pagesize);

	Long supportQueryTotal(IRequest requestContext, TrnSupport trnSupport);

	void updateAmountInvalid(IRequest requestContext);
	
}