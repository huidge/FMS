package clb.core.forecast.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.forecast.dto.FetPeriod;

public interface IFetPeriodService extends IBaseService<FetPeriod>, ProxySelf<IFetPeriodService>{
	
	List<FetPeriod> addFetPeriod(IRequest requestCtx, List<FetPeriod> dto)throws Exception;

}