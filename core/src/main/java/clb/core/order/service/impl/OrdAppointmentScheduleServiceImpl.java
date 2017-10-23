package clb.core.order.service.impl;

import clb.core.order.mapper.OrdAdditionMapper;
import clb.core.order.mapper.OrdAppointmentScheduleMapper;
import clb.core.order.mapper.OrdOrderMapper;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import clb.core.common.utils.DateUtil;
import clb.core.common.utils.SerializeUtil;
import clb.core.course.dto.TrnSupport;
import clb.core.course.mapper.TrnSupportMapper;
import clb.core.order.dto.OrdAppointmentSchedule;
import clb.core.order.dto.OrdOrder;
import clb.core.order.service.IOrdAppointmentScheduleService;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.service.IClbCodeService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OrdAppointmentScheduleServiceImpl implements IOrdAppointmentScheduleService{

	private static Logger log = LoggerFactory.getLogger(OrdAppointmentScheduleServiceImpl.class);

	@Autowired
    private TrnSupportMapper supportMapper;
	
	@Autowired
    private OrdAppointmentScheduleMapper appointmentScheduleMapper;
	
	@Autowired
	private IClbCodeService clbCodeService;
	
	@Override
	public List<OrdAppointmentSchedule> getData(IRequest request, OrdAppointmentSchedule ordAddition) {
		List<OrdAppointmentSchedule> datas = new ArrayList<>();
		Date date = ordAddition.getAppointmentTime();
		List<Date> dates = DateUtil.getDaysByYearAndMonth(date);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if("KFB".equals(ordAddition.getLevelOneType())){
			Map<String,String> typeMap = new HashMap<>();
			List<ClbCodeValue> valueAddTypes = clbCodeService.selectCodeValuesByCodeName(request,"ORD.VALUE_ADD_TYPE");
			
			for(ClbCodeValue clbCodeValue:valueAddTypes){
				typeMap.put(clbCodeValue.getValue(),clbCodeValue.getMeaning());
			}
			
			String itemIdString = ordAddition.getLevelTwoType();
			OrdOrder dto = new OrdOrder();
			if(StringUtils.isNotEmpty(itemIdString)){
				Long itemId = Long.valueOf(itemIdString);
				dto.setItemId(itemId);
        	}
			List<OrdOrder> orders = appointmentScheduleMapper.getReserveData(dto);
			for(int i=0;i<dates.size();i++){
				Long morningData = 0L;
				Long afternoonData = 0L;
				Long totalData = 0L;
	    		String dString = dateFormat.format(dates.get(i));
	    		OrdAppointmentSchedule data = new OrdAppointmentSchedule();
	    		List<OrdOrder> ordOrders = new ArrayList<>();
	    		try {
					Date newDate = dateFormat.parse(dString);
		    		data.setAppointmentTime(newDate);
				} catch (ParseException e) {
					log.error("日期解析失败!");
				}
	    		for(OrdOrder order:orders){
	    			if(order.getReserveDate() != null){
	    				if(DateUtil.isSameYearMonthDay(order.getReserveDate(),dates.get(i))){
		    				if(DateUtil.isMorningOrAfternoon(order.getReserveDate()) == 1){
		    					morningData = morningData+1;
		    				}else{
		    					afternoonData = afternoonData+1;
		    				}
		    				order.setReserveActualDate(DateUtil.getTimeString(order.getReserveDate(),true));
		    				order.setValueaddType(typeMap.get(order.getValueaddType()));
		    				totalData = totalData+1;
		    				ordOrders.add(order);
		    			}
	    			}
	    			
	    		}
	    		data.setMorningDataNumber(morningData);
    			data.setAfternoonDataNumber(afternoonData);
    			data.setTotalDataNumber(totalData);
    			//设置数据
    			data.setAppointmentData(ordOrders);
    			datas.add(data);
	    	}
		}
		else if("QDB".equals(ordAddition.getLevelOneType())){
			String signAssistant = ordAddition.getLevelTwoType();
			OrdOrder dto = new OrdOrder();
			if(StringUtils.isNotEmpty(signAssistant)){
				dto.setSignAssistant(signAssistant);
        	}
			List<OrdOrder> orders = appointmentScheduleMapper.getOrders(dto);
	    	for(int i=0;i<dates.size();i++){
				Long morningData = 0L;
				Long afternoonData = 0L;
				Long totalData = 0L;
	    		String dString = dateFormat.format(dates.get(i));
	    		OrdAppointmentSchedule data = new OrdAppointmentSchedule();
	    		List<OrdOrder> ordOrders = new ArrayList<>();
	    		try {
					Date newDate = dateFormat.parse(dString);
		    		data.setAppointmentTime(newDate);
				} catch (ParseException e) {
					log.error("日期解析失败!");
				}
	    		for(OrdOrder order:orders){
	    			if(order.getReserveArrivalDate() != null){
	    				if(DateUtil.isSameYearMonthDay(order.getReserveArrivalDate(),dates.get(i))){
		    				if(DateUtil.isMorningOrAfternoon(order.getReserveArrivalDate()) == 1){
		    					morningData = morningData+1;
		    				}else{
		    					afternoonData = afternoonData+1;
		    				}
		    				order.setArrivalActualDate(DateUtil.getTimeString(order.getReserveArrivalDate(),true));
		    				totalData = totalData+1;
		    				ordOrders.add(order);
		    			}
	    			}
	    			
	    		}
	    		data.setMorningDataNumber(morningData);
    			data.setAfternoonDataNumber(afternoonData);
    			data.setTotalDataNumber(totalData);
    			//设置数据
    			data.setAppointmentData(ordOrders);
    			datas.add(data);
	    	}
		}else if("PXB".equals(ordAddition.getLevelOneType())){
			TrnSupport trnSupport = new TrnSupport();
        	trnSupport.setStatus("SUCCESS");
        	if(StringUtils.isNotEmpty(ordAddition.getLevelTwoType())){
        		trnSupport.setTrainTeacher(ordAddition.getLevelTwoType());
        	}
        	
        	List<TrnSupport> supports = supportMapper.selectAllField(trnSupport);
        	
        	//初始化数据
        	for(int i=0;i<dates.size();i++){
        		Long morningData = 0L;
				Long afternoonData = 0L;
				Long totalData = 0L;
	    		String dString = dateFormat.format(dates.get(i));
	    		List<TrnSupport> trnSupports = new ArrayList<>();
	    		OrdAppointmentSchedule data = new OrdAppointmentSchedule();
	    		try {
					Date newDate = dateFormat.parse(dString);
		    		data.setAppointmentTime(newDate);
				} catch (ParseException e) {
					log.error("日期解析失败!");
				}
	    		data.setMorningDataNumber(morningData);
    			data.setAfternoonDataNumber(afternoonData);
    			data.setTotalDataNumber(totalData);
    			data.setAppointmentData(trnSupports);
    			datas.add(data);
        	}
        	String startTime = "";
        	String endTime = "";
        	String commonStartTime = "00:00";
        	String commonEndTime = "23:59";
        	String format = "yyyy-MM-dd";
        	
        	for(TrnSupport originSupport:supports){
    			//开始时间和结算时间在同一天
    			if(originSupport.getStartFrom() != null && originSupport.getStartTo() != null){
    				for(OrdAppointmentSchedule schedule:datas){
    					TrnSupport support = (TrnSupport) SerializeUtil.clone(originSupport);
    					Long morningData = schedule.getMorningDataNumber();
    					Long afternoonData = schedule.getAfternoonDataNumber();
    					Long totalData = schedule.getTotalDataNumber();
    					List<TrnSupport> trnSupports = (List<TrnSupport>) schedule.getAppointmentData();
    					//当前时间
    					Date nowDate = schedule.getAppointmentTime();
    					
    					//先考虑开始日期和结束日期都相同的情况
    					if(DateUtil.isSameYearMonthDay(support.getStartFrom(),nowDate)
    					&& DateUtil.isSameYearMonthDay(support.getStartTo(),nowDate)){
    						//判断是上午还是下午
    						if(DateUtil.isMorningOrAfternoon(support.getStartFrom()) == 1){
		    					morningData = morningData+1;
		    				}else{
		    					afternoonData = afternoonData+1;
		    				}
    						startTime = DateUtil.getTimeString(support.getStartFrom(),false);
    						endTime = DateUtil.getTimeString(support.getStartTo(),false);
    						support.setRunningTime(startTime+"-"+endTime);
		    				totalData = totalData+1;
    						trnSupports.add(support);
        		    	}
    					
    					//开始日期相同,结束日期不同
    					else if(DateUtil.isSameYearMonthDay(support.getStartFrom(),nowDate)
    					&& !DateUtil.isSameYearMonthDay(support.getStartTo(),nowDate)){
    						//判断是上午还是下午
    						if(DateUtil.isMorningOrAfternoon(support.getStartFrom()) == 1){
		    					morningData = morningData+1;
		    				}else{
		    					afternoonData = afternoonData+1;
		    				}
    						startTime = DateUtil.getTimeString(support.getStartFrom(),false);
    						support.setRunningTime(startTime+"-"+commonEndTime);
    						totalData = totalData+1;
    						trnSupports.add(support);
        		    	}
    					
    					//开始日期不同,结束日期相同
    					else if(!DateUtil.isSameYearMonthDay(support.getStartFrom(),nowDate)
    					&& DateUtil.isSameYearMonthDay(support.getStartTo(),nowDate)){
    						//直接是上午
		    				morningData = morningData+1;
    						endTime = DateUtil.getTimeString(support.getStartTo(),false);
    						support.setRunningTime(commonStartTime+"-"+endTime);
    						totalData = totalData+1;
    						trnSupports.add(support);
        		    	}
    					
    					//开始日期和结束日期都不同
    					else if(!DateUtil.isSameYearMonthDay(support.getStartFrom(),nowDate)
    					&& !DateUtil.isSameYearMonthDay(support.getStartTo(),nowDate)){
    						//判断当前时间是不是在开始和结束日期之间
    						Long now = DateUtil.getFormatDate(nowDate,format).getTime();
    						Long start = DateUtil.getFormatDate(support.getStartFrom(),format).getTime();
    						Long end = DateUtil.getFormatDate(support.getStartTo(),format).getTime();
    						if(now.compareTo(start)>0 && now.compareTo(end)<0){
    							//直接是上午
    		    				morningData = morningData+1;
        						support.setRunningTime(commonStartTime+"-"+commonEndTime);
        						totalData = totalData+1;
        						trnSupports.add(support);
    						}
        		    	}
    					schedule.setMorningDataNumber(morningData);
    					schedule.setAfternoonDataNumber(afternoonData);
    					schedule.setTotalDataNumber(totalData);
    	    			//设置数据
    					schedule.setAppointmentData(trnSupports);
    				}
    			}
    		}
		}else{
			for(int i=0;i<dates.size();i++){
				OrdAppointmentSchedule data = new OrdAppointmentSchedule();
	    		String dString = dateFormat.format(dates.get(i));
	    		try {
					Date newDate = dateFormat.parse(dString);
					data.setAppointmentTime(newDate);
				} catch (ParseException e) {
					log.error("日期解析失败!");
				}
	    		datas.add(data);
			}
		}
		
		return datas;
	}

   
}