package clb.core.forecast.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.exceptions.CommonException;
import clb.core.forecast.dto.FetReceiptDiff;

public interface IFetReceiptDiffService extends IBaseService<FetReceiptDiff>, ProxySelf<IFetReceiptDiffService>{

	List<FetReceiptDiff> getData(IRequest iRequest,FetReceiptDiff dto,int page,int pagesize);
	
	List<FetReceiptDiff> summaryQuery(IRequest iRequest,FetReceiptDiff dto,int page,int pagesize);
	
	void exportData(IRequest iRequest, String sqlId, Object obj,HttpServletRequest request,HttpServletResponse response) throws CommonException;
	
	List<FetReceiptDiff> batchUpdateVesion(IRequest iRequest,List<FetReceiptDiff> dtos) throws CommonException;

	List<FetReceiptDiff> updateAllVersion(IRequest iRequest,Long paymentCompanyId,Long periodId) throws CommonException;
	
	List<FetReceiptDiff> mergeOffset(IRequest iRequest,List<FetReceiptDiff> dtos) throws CommonException;

}