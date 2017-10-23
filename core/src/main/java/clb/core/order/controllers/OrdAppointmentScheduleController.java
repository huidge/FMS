package clb.core.order.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.hand.hap.account.dto.Role;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.mapper.RoleMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IProfileService;

import clb.core.common.utils.RefelctUtil;
import clb.core.common.utils.SpringConfigTool;
import clb.core.course.dto.TrnSupport;
import clb.core.course.mapper.TrnSupportMapper;
import clb.core.order.dto.OrdAppointmentSchedule;
import clb.core.order.mapper.OrdAppointmentScheduleMapper;
import clb.core.order.service.IOrdAppointmentScheduleService;
import clb.core.production.dto.PrdItems;
import clb.core.production.mapper.PrdItemsMapper;
import clb.core.system.dto.ClbCode;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.mapper.ClbCodeMapper;
import clb.core.system.mapper.ClbCodeValueMapper;
import clb.core.system.service.IClbCodeService;
import groovy.xml.StreamingDOMBuilder;

@Controller
@RequestMapping("/ord/appointmentschedule")
public class OrdAppointmentScheduleController extends BaseController{

    @Autowired
    private IOrdAppointmentScheduleService service;
    
    @Autowired
    private OrdAppointmentScheduleMapper appointmentScheduleMapper;
    
    @Autowired
    private TrnSupportMapper supportMapper;
    
    @Autowired
    private PrdItemsMapper itemsMapper;
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private IProfileService profileService;
    
    @Autowired
    private IClbCodeService codeService;
    
    //培训部门配置代码
    private static final String TRAINING_PROFILE = "ORD_TRAINING";
    
    //签单部门配置代码
    private static final String SITE_PROFILE = "ORD_SITE";
    
    //客服部门配置代码
    private static final String SERVICE_PROFILE = "ORD_SERVICE";
    
    //签单协助人角色代码
    private static final String SITE_ASSISTANT = "Site Assistant";
    
    private static  List<ClbCodeValue> codeValues;

    public void setValues(IRequest iRequest){
    	String code = "ORD.APPOINTMENT_TYPE";
    	codeValues = codeService.selectCodeValuesByCodeName(iRequest,code);
    } 

    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(@RequestBody OrdAppointmentSchedule dto,HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.getData(requestContext, dto));
    }
    
    @RequestMapping(value = "/queryLevelOneData")
    @ResponseBody
    public ResponseData queryLevelOneData(HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        setValues(requestContext);
        List<ClbCodeValue> res = new ArrayList<>();
        Long[] roleIds = requestContext.getAllRoleId();
        List<Role> roles= roleMapper.selectAll();
        List<Role> userRoles = new ArrayList<>();
        for(Long roleId:roleIds){
        	for(Role role:roles){
            	if(role.getRoleId().equals(roleId)){
            		userRoles.add(role);
            		break;
            	}
            }
        }
        
        
        //培训部
        String training = profileService.getProfileValue(requestContext,TRAINING_PROFILE);
        //签单部
        String site = profileService.getProfileValue(requestContext,SITE_PROFILE);
        //客服部
        String service = profileService.getProfileValue(requestContext,SERVICE_PROFILE);
        
        for(Role role:userRoles){
        	ClbCodeValue resData = new ClbCodeValue();
         	if(role.getRoleCode().equals(training)){
         		resData.setMeaning("培训部");
         		resData.setValue("PXB");
         		res.add(resData);
         	}else if(role.getRoleCode().equals(site)){
         		resData.setMeaning("签单部");
         		resData.setValue("QDB");
         		res.add(resData);
         	}else if(role.getRoleCode().equals(service)){
         		resData.setMeaning("客服部");
         		resData.setValue("KFB");
         		res.add(resData);
         	}else if("ADMIN".equals(role.getRoleCode())){
         		res = codeValues;
         	}
        }
        return new ResponseData(res);
    }
    
    @RequestMapping(value = "/getLevelTwo")
    @ResponseBody
    public ResponseData queryLevelTwoData(OrdAppointmentSchedule dto,HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<ClbCodeValue> clbCodeValues = new ArrayList<>();
        //培训部时，获取培训讲师
        if("PXB".equals(dto.getLevelOneType())){
        	TrnSupport trnSupport = new TrnSupport();
        	trnSupport.setStatus("SUCCESS");
        	List<TrnSupport> supports = supportMapper.select(trnSupport);
        	for(TrnSupport support:supports){
        		boolean flag = true;
        		for(ClbCodeValue codeValue:clbCodeValues){
        			if(codeValue.getMeaning().equals(support.getTrainTeacher())
        			&& codeValue.getValue().equals(support.getTrainTeacher())){
        				flag = false;
        				break;
        			}
        		}
        		if(flag){
        			if(StringUtils.isNotEmpty(support.getTrainTeacher())){
        				ClbCodeValue clbCodeValue = new ClbCodeValue();
                		clbCodeValue.setMeaning(support.getTrainTeacher());
                		clbCodeValue.setValue(support.getTrainTeacher());
                		clbCodeValues.add(clbCodeValue);
        			}
        		}
        	}
        }
        //签单部时，获取角色为签单协助人的用户
        if("QDB".equals(dto.getLevelOneType())){
        	//签单部
        	List<User> users = appointmentScheduleMapper.getUserByRoleName(SITE_ASSISTANT);
        	for(User user:users){
        		ClbCodeValue clbCodeValue = new ClbCodeValue();
        		clbCodeValue.setMeaning(user.getUserName());
        		clbCodeValue.setValue(user.getUserId().toString());
        		clbCodeValues.add(clbCodeValue);
        	}
        }
        //客服部时，获取增值服务中的产品
        if("KFB".equals(dto.getLevelOneType())){
        	PrdItems param = new PrdItems();
        	param.setBigClass("FW");
        	List<PrdItems> items = itemsMapper.select(param);
        	for(PrdItems item:items){
        		ClbCodeValue clbCodeValue = new ClbCodeValue();
        		clbCodeValue.setMeaning(item.getItemName());
        		clbCodeValue.setValue(item.getItemId().toString());
        		clbCodeValues.add(clbCodeValue);
        	}
        }
        return new ResponseData(clbCodeValues);
    }
    
    

}