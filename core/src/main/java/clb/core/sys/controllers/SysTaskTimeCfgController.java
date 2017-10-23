package clb.core.sys.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.sys.dto.SysTaskTimeCfg;
import clb.core.sys.mapper.SysTaskTimeCfgMapper;
import clb.core.sys.service.ISysTaskTimeCfgService;

@Controller
@RequestMapping({"/sys/taskTimeCfg"})
public class SysTaskTimeCfgController extends BaseController {
    
    @Autowired
    private ISysTaskTimeCfgService sysTaskTimeCfgService;
    @Autowired
    private SysTaskTimeCfgMapper sysTaskTimeCfgMapper;
    /**
     * 查询
     * @param request
     * @return
     */
    @RequestMapping(value = {"/query"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData query(SysTaskTimeCfg dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(sysTaskTimeCfgService.queryAllField(iRequest, page, pagesize, dto));
    }

    /**
     * 保存
     * @param request
     * @return
     */
    @RequestMapping(value = {"/submit"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData submit(HttpServletRequest request, @RequestBody List<SysTaskTimeCfg> dtoList, BindingResult result)  {
        getValidator().validate(dtoList, result);
        if (result.hasErrors()) {
          ResponseData rd = new ResponseData(false);
          rd.setMessage(getErrorMessage(result, request));
          return rd;
        }
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(sysTaskTimeCfgService.batchUpdate(iRequest, dtoList));
    }

    @RequestMapping(value = "/remove")
    public ResponseData delete(HttpServletRequest request, @RequestBody List<SysTaskTimeCfg> dtoList){
    	sysTaskTimeCfgService.batchDelete(dtoList);
        return new ResponseData();
    }

}