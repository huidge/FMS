package clb.core.forecast.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.forecast.dto.FetPeriodHead;

public interface IFetPeriodHeadService extends IBaseService<FetPeriodHead>, ProxySelf<IFetPeriodHeadService>{
	
	public List<FetPeriodHead> selectAllFiled(IRequest requestContext, FetPeriodHead dto, int page, int pageSize);

	/* 复制财务期间 */
	void copyPeriod(IRequest request, FetPeriodHead dto);
}