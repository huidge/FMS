package clb.core.supplier.service;

import java.util.List;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.supplier.dto.SpeCalendarLine;
import clb.core.supplier.exceptions.SpeException;

public interface ISpeCalendarLineService extends IBaseService<SpeCalendarLine>, ProxySelf<ISpeCalendarLineService>{

	public int deleteByCalendarId(Long CalendarId);
	
	public List<SpeCalendarLine> getCalendarData(SpeCalendarLine record) throws SpeException;
}