package clb.core.forecast.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.forecast.dto.FetQuestion;
import clb.core.forecast.dto.FetQuestionLine;

public interface IFetQuestionLineService extends IBaseService<FetQuestionLine>, ProxySelf<IFetQuestionLineService>{

	public List<FetQuestion> queryByCheckPeriod(IRequest iRequest,FetQuestion dto,HttpServletRequest request);

	public List<FetQuestion> queryByCheckPeriodWs(IRequest iRequest,FetQuestion dto,HttpServletRequest request);
	
	public List<FetQuestion> getData(IRequest iRequest,FetQuestionLine dto,HttpServletRequest request);
	
}