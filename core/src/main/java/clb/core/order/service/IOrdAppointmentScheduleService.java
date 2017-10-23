package clb.core.order.service;

import com.hand.hap.core.IRequest;
import clb.core.order.dto.OrdAppointmentSchedule;

import java.util.List;

public interface IOrdAppointmentScheduleService{
    
	
	List<OrdAppointmentSchedule> getData(IRequest request, OrdAppointmentSchedule ordAddition);
}