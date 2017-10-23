package clb.core.forecast.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.exceptions.CommonException;
import clb.core.forecast.dto.FetActualReceipt;
import clb.core.forecast.dto.FetReceivable;
import clb.core.supplier.exceptions.SpeException;

public interface IFetReceivableService extends IBaseService<FetReceivable>, ProxySelf<IFetReceivableService>{

	List<FetReceivable> getData(IRequest iRequest,FetReceivable dto,int page,int pagesize);
	
	List<FetReceivable> summaryQuery(IRequest iRequest,FetReceivable dto,int page,int pagesize);
	
	void exportData(IRequest iRequest, String sqlId, Object obj,HttpServletRequest request,HttpServletResponse response) throws CommonException;
	
	List<FetReceivable> fetReceivableBatchUpdate(IRequest iRequest, List<FetReceivable> data) throws CommonException;

	/**
	 * 批量操作方法
	 *
	 * @param list
	 * @param iRequest
	 * @return
	 */
	List<FetReceivable> batchHandle(List<FetReceivable> list, IRequest iRequest);
}