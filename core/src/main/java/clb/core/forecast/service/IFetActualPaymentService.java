package clb.core.forecast.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.exceptions.CommonException;
import clb.core.forecast.dto.FetActualPayment;

public interface IFetActualPaymentService extends IBaseService<FetActualPayment>, ProxySelf<IFetActualPaymentService>{

	List<FetActualPayment> getData(IRequest iRequest,FetActualPayment dto,int page,int pagesize);
	
	List<FetActualPayment> fetActualPaymentBatchUpdate(IRequest iRequest,List<FetActualPayment> data) throws CommonException;

	
}