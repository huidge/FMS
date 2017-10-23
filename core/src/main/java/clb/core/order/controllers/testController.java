package clb.core.order.controllers;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.utils.DateUtil;
import clb.core.order.dto.OrdAddition;
import clb.core.order.dto.OrdOrder;
import clb.core.order.mapper.OrdOrderMapper;
import clb.core.order.service.IOrdAdditionService;
import clb.core.order.service.IOrdOrderService;
import net.bytebuddy.dynamic.scaffold.MethodRegistry.Handler.ForAbstractMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

    @Controller
    public class testController extends BaseController {

        @Autowired
        private OrdOrderMapper orderMapper;
        
        @Autowired
        private IOrdOrderService orderService;


        @RequestMapping(value = "/fms/ord/test/update")
        @ResponseBody 
        public ResponseData update(HttpServletRequest request) {
            IRequest requestContext = createRequestContext(request);
            OrdOrder dto = new OrdOrder();
            List<OrdOrder> orders = orderMapper.querySublineItemName(dto);
            List<OrdOrder> resData = new ArrayList<>();
            for(OrdOrder order:orders){
            	String payMethod = order.getPayMethod();
                if("FJ".equals(payMethod)){
                	//当前时间
                	Date now = new Date();
                	//续保到期日
                	Date renewalDueDate = order.getRenewalDueDate();
                	//首期保费日
                	Date firstPayDate = order.getFirstPayDate();
                	//当前期数
                	Long payPeriods = order.getPayPeriods();
                	//年期
                	String sublineItemName = order.getSublineItemName();
                	Long sublineItem = 0L;
                	try{
                		sublineItem = Long.valueOf(sublineItemName); 
                	}catch(Exception e){
                		sublineItem = 0L;
                	}
                	Date compareDate = null;
                	//如果续保到期日为空,让首期保费日加一年，再比较
                	if(renewalDueDate == null){
                		if(firstPayDate != null){
                			compareDate = DateUtil.addYear(firstPayDate);
                		}
                	}else{
                		compareDate = renewalDueDate;
                	}
                	if(compareDate != null){
                		if(DateUtil.isSameYearMonthDay(compareDate,now)){
                			if(sublineItem != 0){
                				//若当前期数为空，设置当前期数为1
                				if(payPeriods == null){
                					//续期保费日加一年
                        			order.setRenewalDueDate(DateUtil.addYear(compareDate));
                        			//当前期数加一
                        			order.setPayPeriods(1L);
                        			order.set__status(DTOStatus.UPDATE);
                        			resData.add(order);
                				}
                				else if(payPeriods < sublineItem){
                					//续期保费日加一年
                        			order.setRenewalDueDate(DateUtil.addYear(compareDate));
                        			//当前期数加一
                        			order.setPayPeriods(payPeriods+1);
                        			order.set__status(DTOStatus.UPDATE);
                        			resData.add(order);
                				}
                			}
                		}
                	}
                }
                
            } 
            IRequest iRequest = RequestHelper.getCurrentRequest(true);
            orderService.batchUpdate(iRequest,resData);
            return new ResponseData(resData);
        }

        
 }