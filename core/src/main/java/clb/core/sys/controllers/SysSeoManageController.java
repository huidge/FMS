package clb.core.sys.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.exceptions.CommonException;
import clb.core.sys.dto.SysSeoManage;
import clb.core.sys.service.ISysSeoManageService;
/*****
 * @author FengWanJun
 * @Date 2017-09-06
 */
@Controller
@RequestMapping(value = "/fms/sys/seo/manage")
public class SysSeoManageController extends BaseController{

    @Autowired
    private ISysSeoManageService sysSeoManageService;

    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(SysSeoManage dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<SysSeoManage> data = sysSeoManageService.selectAll(requestContext);
        return new ResponseData(data);
    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody SysSeoManage dto, BindingResult result) throws CommonException{
    	getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        IRequest requestCtx = createRequestContext(request);
        if(dto.getSeoId() == null){
            sysSeoManageService.insertSelective(requestCtx, dto);
        }else{
            sysSeoManageService.updateByPrimaryKey(requestCtx, dto);
        }
        List<SysSeoManage> list = new ArrayList<SysSeoManage>();
        list.add(dto);
        return new ResponseData(list);
    }
}