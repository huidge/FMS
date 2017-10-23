package clb.core.forecast.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.exceptions.CommonException;
import clb.core.forecast.dto.FetPaymentDiff;

public interface IFetPaymentDiffService extends IBaseService<FetPaymentDiff>, ProxySelf<IFetPaymentDiffService>{

	List<FetPaymentDiff> getData(IRequest iRequest,FetPaymentDiff dto,int page,int pagesize);
	
	List<FetPaymentDiff> summaryQuery(IRequest iRequest,FetPaymentDiff dto,int page,int pagesize);
	
	void exportData(IRequest iRequest, String sqlId, Object obj,HttpServletRequest request,HttpServletResponse response) throws CommonException;
	
	List<FetPaymentDiff> batchUpdateVesion(IRequest iRequest,List<FetPaymentDiff> dtos) throws CommonException;

	List<FetPaymentDiff> updateAllVersion(IRequest iRequest, Long paymentCompanyId, Long periodId) throws CommonException;
	
	List<FetPaymentDiff> mergeOffset(IRequest iRequest,List<FetPaymentDiff> dtos) throws CommonException;
	
}