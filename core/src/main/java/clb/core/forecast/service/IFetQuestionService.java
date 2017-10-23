package clb.core.forecast.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.exceptions.CommonException;
import clb.core.forecast.dto.FetQuestion;

public interface IFetQuestionService extends IBaseService<FetQuestion>, ProxySelf<IFetQuestionService>{

	public List<FetQuestion> getData(FetQuestion dto,int page,int pagesize);
	
	public FetQuestion saveData(IRequest iRequest,FetQuestion dto) throws CommonException;
	
}