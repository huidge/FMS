package clb.core.forecast.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.exceptions.CommonException;
import clb.core.forecast.dto.FetChannelCheck;

public interface IFetChannelCheckService extends IBaseService<FetChannelCheck>, ProxySelf<IFetChannelCheckService>{

	List<FetChannelCheck> getData(IRequest iRequest,FetChannelCheck dto,int page,int pagesize);
	
	List<FetChannelCheck> summaryQuery(IRequest iRequest,FetChannelCheck dto,int page,int pagesize);
	
	void exportData(IRequest iRequest, String sqlId, Object obj,HttpServletRequest request,HttpServletResponse response) throws CommonException;
	
	List<FetChannelCheck> enSure(IRequest iRequest,FetChannelCheck dto);

	/**
	 * 批量操作方法
	 *
	 * @param list
	 * @param iRequest
	 * @return
	 */
	List<FetChannelCheck> batchHandle(List<FetChannelCheck> list, IRequest iRequest);

	/**
	 * 获取对账单角标
	 *
	 * @param paramStatus
	 * @param status
	 * @param userId
	 * @return
	 */
	Integer getCheckCount(String paramStatus, String status,Long userId);
}