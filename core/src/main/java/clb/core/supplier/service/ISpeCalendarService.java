package clb.core.supplier.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.ImportUtil.ImportMessage;
import clb.core.supplier.dto.SpeCalendar;
import clb.core.supplier.exceptions.SpeException;

public interface ISpeCalendarService extends IBaseService<SpeCalendar>, ProxySelf<ISpeCalendarService>{

	public List<ImportMessage> calendarUpdate(HttpServletRequest request,IRequest iRequest,SpeCalendar dto) throws CommonException;
	
	public List<SpeCalendar> selectData(SpeCalendar dto,int page,int pagesize);
	
}