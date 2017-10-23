package clb.core.notification.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.notification.dto.NtnNotificationTemplate;
import clb.core.notification.service.INtnNotificationTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

    @Controller
    public class NtnNotificationTemplateController extends BaseController{

    @Autowired
    private INtnNotificationTemplateService service;


    @RequestMapping(value = "/fms/ntn/notification/template/query")
    @ResponseBody
    public ResponseData query(NtnNotificationTemplate dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAllField(requestContext,dto,page,pageSize));
    }
    
    @RequestMapping(value = "/fms/ntn/notification/template/selectAll")
    @ResponseBody
    public ResponseData selectAll(NtnNotificationTemplate dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(service.selectAll(requestContext,dto));
    }

    @RequestMapping(value = "/fms/ntn/notification/template/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<NtnNotificationTemplate> dto){
        IRequest requestCtx = createRequestContext(request);
        NtnNotificationTemplate ntnNotificationTemplate3 = dto.get(0);
        NtnNotificationTemplate ntnNotificationTemplate = service.updateByPrimaryKey(requestCtx, ntnNotificationTemplate3);
        List<NtnNotificationTemplate> list = new ArrayList<>();
        list.add(ntnNotificationTemplate);
        /*getValidator().validate(dto, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestContext = createRequestContext(request);*/
        return new ResponseData(list);
    }
    
	@RequestMapping(value = "/fms/ntn/notification/template/add")
	@ResponseBody
	public ResponseData add(HttpServletRequest request, @RequestBody List<NtnNotificationTemplate> dto) {
		IRequest requestCtx = createRequestContext(request);
		// 设置默认的发布状态为未发布
		NtnNotificationTemplate ntnNotificationTemplate = dto.get(0);
		NtnNotificationTemplate ntnNotificationTemplate2 = service.insertSelective(requestCtx, ntnNotificationTemplate);
		//下面的操作  没有意义  为了符合  内部定义的js的规范
		List<NtnNotificationTemplate> list = new ArrayList<>();
		list.add(ntnNotificationTemplate2);
		return new ResponseData(list);
	}


    @RequestMapping(value = "/fms/ntn/notification/template/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<NtnNotificationTemplate> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }