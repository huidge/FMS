package clb.core.supplier.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.supplier.dto.SpeCalendarLine;
import clb.core.supplier.exceptions.SpeException;
import clb.core.supplier.mapper.SpeCalendarLineMapper;
import clb.core.supplier.service.ISpeCalendarLineService;

@Service
@Transactional
public class SpeCalendarLineServiceImpl extends BaseServiceImpl<SpeCalendarLine> implements ISpeCalendarLineService{
	private static Logger log = LoggerFactory.getLogger(SpeCalendarLineServiceImpl.class);

	@Autowired
	private SpeCalendarLineMapper calendarLineMapper;
	
	@Override
	public int deleteByCalendarId(Long CalendarId) {
		return calendarLineMapper.deleteByCalendarId(CalendarId);
	}

	@Override
	public List<SpeCalendarLine> getCalendarData(SpeCalendarLine record) throws SpeException {
		List<SpeCalendarLine> data = calendarLineMapper.select(record);
		String date = "";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		for(SpeCalendarLine d:data){
			date = String.valueOf(d.getTheYear())+"-"+String.valueOf(d.getTheMonth())+"-"
			+String.valueOf(d.getTheDay());
			try {
				Date dateData = simpleDateFormat.parse(date);
				Long dateNumber = dateData.getTime();
				d.setDateNumber(dateNumber);
			} catch (ParseException e) {
				log.error("获取日期失败!",e);
				throw new SpeException("SPE","获取日期失败",null);
			}
		}
		//compareTo方法，调用者比参数小，返回值小于0，比参数大，返回值大于0
		//因此，如果arg0调用的话，为正序排序，如果arg1调用，为倒序排序
		/*Collections.sort(data,new Comparator<SpeCalendarLine>() {
            public int compare(SpeCalendarLine arg0, SpeCalendarLine arg1) {
                return arg0.getDateNumber().compareTo(arg1.getDateNumber());
        }});*/
		
		Collections.sort(data,(SpeCalendarLine arg0, SpeCalendarLine arg1)
				->arg0.getDateNumber().compareTo(arg1.getDateNumber()));
		return data;
	}

}