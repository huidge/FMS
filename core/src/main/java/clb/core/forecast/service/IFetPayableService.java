package clb.core.forecast.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import clb.core.forecast.dto.FetReceivable;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.exceptions.CommonException;
import clb.core.forecast.dto.FetPayable;

public interface IFetPayableService extends IBaseService<FetPayable>, ProxySelf<IFetPayableService>{

	List<FetPayable> getData(IRequest iRequest,FetPayable dto,int page,int pagesize);
	
	List<FetPayable> summaryQuery(IRequest iRequest,FetPayable dto,int page,int pagesize);
	
	void exportData(IRequest iRequest, String sqlId, Object obj,HttpServletRequest request,HttpServletResponse response) throws CommonException;

	/**
	 * 批量操作方法
	 *
	 * @param list
	 * @param iRequest
	 * @return
	 */
	List<FetPayable> batchHandle(List<FetPayable> list, IRequest iRequest);
	
}