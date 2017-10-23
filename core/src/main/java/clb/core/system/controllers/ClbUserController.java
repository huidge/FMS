package clb.core.system.controllers;

/**
 * Created by jiaolong.li on 2017-05-10.
 */

import com.hand.hap.security.PasswordManager;
import com.hand.hap.system.service.IProfileService;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.runtime.parser.node.ASTElseIfStatement;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.account.dto.User;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.dto.ResponseData;

import clb.core.course.dto.TrnCourse;
import clb.core.production.dto.PrdItems;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.service.ISpeSupplierService;
import clb.core.system.dto.ClbUser;
import clb.core.system.service.IClbUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ClbUserController extends BaseController {

    @Autowired
    private IClbUserService service;
    private String userName;
    @Autowired
    private IProfileService profileService;
    @Autowired
    private PasswordManager passwordManager;
    @Autowired
    private ISpeSupplierService speSupplierService;
    
    private List<ClbUser> lists;
    
    @RequestMapping(value = "/clb/sys/user/query")
    @ResponseBody
    public ResponseData query(ClbUser dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAllFields(requestContext, dto, page, pageSize));
    }
    
    @RequestMapping(value = "/clb/sys/user/selectAll")
    @ResponseBody
    public ResponseData selectAll(ClbUser dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	List lists = service.selectAll(requestContext, dto);
    	return new ResponseData(lists);
    }
    
    @RequestMapping(value = "/clb/sys/user/selectLecturer")
    @ResponseBody
    public ResponseData selectLecturer(ClbUser dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(service.selectLecturer(requestContext, dto));
    }
    
    @RequestMapping(value = {"/clb/sys/user/lecturerFlag"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData lecturerFlag(HttpServletRequest request, @RequestParam("labelLinesList[]") List<Long> labelLinesList)  {
        /*getValidator().validate(dtoList, result);
        if (result.hasErrors()) {
          ResponseData rd = new ResponseData(false);
          rd.setMessage(getErrorMessage(result, request));
          return rd;
        }*/
        IRequest iRequest = createRequestContext(request);
        for (Long userId : labelLinesList) {
        	ClbUser clbUser = new ClbUser();
        	clbUser.setUserId(userId);
        	clbUser.setLecturerFlag("Y");
        	ClbUser clbUser2 = service.updateByPrimaryKeySelective(iRequest, clbUser);
		}
    	return new ResponseData();
        //return new ResponseData(prdItemsService.batchUpdate(iRequest, dtoList));
    }
    
    @RequestMapping(value = {"/clb/sys/user/delectList"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData delectList(HttpServletRequest request, @RequestParam("delectList[]") List<Long> delectList)  {
    	IRequest iRequest = createRequestContext(request);
    	for (Long userId : delectList) {
    		ClbUser clbUser = new ClbUser();
    		clbUser.setUserId(userId);
    		clbUser.setLecturerFlag("N");
    		ClbUser clbUser2 = service.updateByPrimaryKeySelective(iRequest, clbUser);
    	}
    	return new ResponseData();
    	//return new ResponseData(prdItemsService.batchUpdate(iRequest, dtoList));
    }
    
    
    @RequestMapping(value = "/clb/sys/user/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<ClbUser> dto) {
        IRequest requestCtx = createRequestContext(request);
        userName = dto.get(0).getUserName();
        String validateStr = "^[A-Za-z]+$";
        
        /*Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(userName);
        if (m.find()) {
        	ResponseData responseData = new ResponseData();
			responseData.setSuccess(false);
			responseData.setMessage("用户名中包含有中文，保存失败");
			return responseData;
        }*/
        
        String newString="";
        for(int i=0;i<userName.length();i++)
        {
            if(Character.isUpperCase(userName.charAt(i)))
            {
                newString+=Character.toLowerCase(userName.charAt(i));
            }
            else{
                newString+=userName.charAt(i);
            }
        }
        dto.get(0).setUserName(newString);
        //System.out.println(newString);
        
        for (ClbUser user : dto) {
        	//没有密码,使用默认密码
            if ( StringUtils.isEmpty(user.getPassword())
                        && StringUtils.isEmpty(user.getPasswordEncrypted()) ) {
                    user.setPasswordEncrypted(this.passwordManager.encode(
                            this.passwordManager.getDefaultPassword()
                    ));
            } else if( !StringUtils.isEmpty(user.getPassword())) {
                    user.setPasswordEncrypted(this.passwordManager.encode(
                            user.getPassword()
                    ));
            }

            // 清空LOV值操作
            if (user.getRelatedPartyId() == null && user.getUserId() != null) {
                ClbUser usr = new ClbUser();
                usr.setUserId(user.getUserId());
                usr = service.selectByPrimaryKey(requestCtx, usr);
                usr.setRelatedPartyId(null);
                service.updateByPrimaryKey(requestCtx, usr);
            }
        }

        /*List<ClbUser> arrayList = new ArrayList<>();
        for(ClbUser clbUser : dto){
        	if(DTOStatus.ADD.equals(clbUser.get__status())){
        		//System.out.println(clbUser.getUserType());
        		//System.out.println(clbUser.getRelatedPartyId());
        		for(ClbUser user : lists){
        			//System.out.println(user.getUserType());
        			//System.out.println(user.getRelatedPartyId());
        			if(user.getUserType().equals(clbUser.getUserType()) && user.getRelatedPartyId().equals(clbUser.getRelatedPartyId())){
        				ResponseData responseData = new ResponseData();
        		            responseData.setMessage("关联方姓名不能重复！");
        		            responseData.setSuccess(false);
        		            //wb.close();
        		            return responseData;
        			}
        		}
        	}
        }*/
        
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    /*private boolean matcher(String validateStr, String userName2) {
		// TODO Auto-generated method stub
		return false;
	}*/
    
    @RequestMapping(value = "/clb/sys/user/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<ClbUser> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }
    @RequestMapping(value = "/clb/sys/user/isAdmin")
    public boolean isAdmin(HttpServletRequest request){
    	return service.isAdmin(createRequestContext(request));
    }

    @RequestMapping(value = "/clb/sys/user/isImporter")
    public boolean isImporter(HttpServletRequest request){
        return service.isImporter(createRequestContext(request));
    }

    @RequestMapping(value = "/clb/sys/user/getSupplierInfo")
    public ResponseData getSupplierInfo(HttpServletRequest request){
    	ResponseData response=new ResponseData(true);
    	IRequest irequest=createRequestContext(request);
    	Long supplierId=service.getSupplierId(irequest);
    	if(supplierId==null){
    		response.setSuccess(false);
    		response.setMessage("非不能供应商！");
    		return null;
    	}else{
    		SpeSupplier supplier=new SpeSupplier();
    		supplier.setSupplierId(supplierId);
    		supplier=speSupplierService.selectByPrimaryKey(irequest, supplier);
    		response.setCode(supplierId+"");
    		response.setMessage(supplier.getName());
    		return response;
    	}
    }

    
}