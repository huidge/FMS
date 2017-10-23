package clb.core.supplier.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.supplier.dto.SpeCalendar;

public interface SpeCalendarMapper extends Mapper<SpeCalendar>{
	
	public List<SpeCalendar> selectData(SpeCalendar dto);

}