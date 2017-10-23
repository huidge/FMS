package clb.core.supplier.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.supplier.dto.SpeCalendarLine;

public interface SpeCalendarLineMapper extends Mapper<SpeCalendarLine>{
	
	public int deleteByCalendarId(Long CalendarId);

}