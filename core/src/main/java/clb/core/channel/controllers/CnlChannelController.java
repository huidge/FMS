package clb.core.channel.controllers;

import clb.core.system.dto.ClbUser;
import clb.core.system.service.IClbUserService;
import com.hand.hap.system.service.IProfileService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.channel.dto.CnlChannel;
import clb.core.channel.service.ICnlChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author jun.zhao02@hand-china.com
 * @version 1.0
 * @name CnlChannelController
 * @description:渠道信息controller
 * @date 2017/4/25
 */
@Controller
@RequestMapping({"/fms/cnl/channel"})
public class CnlChannelController extends BaseController{

    @Autowired
    private ICnlChannelService service;

    @Autowired
    private IProfileService profileService;

    @Autowired
    private IClbUserService clbUserService;
    /**
     * 渠道信息查询
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(CnlChannel dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<CnlChannel> cnlChannelList = service.queryChannelSummaryPriv(requestContext,dto,page,pageSize);
        if(cnlChannelList != null && cnlChannelList.size() > 0) {
        	for(CnlChannel cnlChannel :cnlChannelList){
        		cnlChannel.setContactPhoneComb(cnlChannel.getContactPhone()+" "+cnlChannel.getContactPhone());
        	}
        }
        return new ResponseData(cnlChannelList);
    }

    /**
     * 渠道所属地区查询
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/queryArea")
    @ResponseBody
    public ResponseData queryArea(CnlChannel dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryArea(requestContext,dto));
    }


    /**
     * 渠道信息提交
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<CnlChannel> dto){
        IRequest requestCtx = createRequestContext(request);
        return service.channelBatchUpdate(requestCtx, dto);
    }


    /**
     * 渠道信息删除
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<CnlChannel> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * 配置维护查询
     * @param profileName
     * @param request
     * @return
     */
    @RequestMapping(value = "/getProfileValue")
    @ResponseBody
    public ResponseData getProfileValue(String profileName, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        ResponseData responseData = new ResponseData();
        responseData.setCode(profileService.getProfileValue(requestContext,profileName));
        return responseData;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/user/submit")
    @ResponseBody
    public ResponseData userSubmit(HttpServletRequest request, @RequestBody List<ClbUser> dto) {
        IRequest requestCtx = createRequestContext(request);
        /***
         * modify by tiansheng.ye@hand-china.com
         * @Desc 增加事务
         */
        return clbUserService.channelUserSubmit(requestCtx, dto);
    }
}