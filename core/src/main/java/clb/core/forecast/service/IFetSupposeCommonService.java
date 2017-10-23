package clb.core.forecast.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.exceptions.CommonException;
import clb.core.forecast.dto.FetPeriod;
import clb.core.forecast.dto.FetSupposeCommon;
import clb.core.forecast.dto.FetSupposeMessage;
import clb.core.forecast.dto.FetSupposeUpdateVersion;;

public interface IFetSupposeCommonService extends IBaseService<FetSupposeCommon>, ProxySelf<IFetSupposeCommonService>{

	
	List<FetSupposeMessage> batchUpdateVersion(IRequest iRequest,List<FetSupposeCommon> dto) throws CommonException;
	
	List<FetSupposeMessage> UpdateAllVersion(IRequest iRequest,FetSupposeUpdateVersion param) throws CommonException;
	
	void insertSupposeData(IRequest iRequest,List<FetPeriod> periodDatas) throws CommonException;

	List<FetSupposeCommon> getAllOrderData(FetSupposeUpdateVersion param) throws CommonException;
	
}