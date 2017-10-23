package clb.core.system.controllers;

import clb.core.system.dto.ClbCodeValue;
import clb.core.system.dto.ClbUser;
import clb.core.system.dto.SysUserCustomFuncs;
import clb.core.system.dto.SysUserCustomFunction;
import clb.core.system.service.IClbCodeService;
import clb.core.system.service.ISysUserCustomFunctionService;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户自定义功能相关接口.
 *
 * @author jiaolong.li@hand-china.com
 */
@RestController
@RequestMapping("/api/userCustom")
public class WsUserCustomFunctionController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(WsUserCustomFunctionController.class);

    @Autowired
    private ISysUserCustomFunctionService functionService;

    /**
     * 查询用户可用的自定义功能
     * @param request
     * @param sysUserCustomFunction
     * @return
     * @throws JsonProcessingException
     */
    @Timed
    @HapInbound(apiName = "ClbAvailableFuncs")
    @RequestMapping(value = "/getAvailableFuncs", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public ResponseData getAvailableFuncs(HttpServletRequest request , @RequestBody SysUserCustomFunction sysUserCustomFunction) throws JsonProcessingException {

        return new ResponseData(functionService.queryAvailableFuncs(sysUserCustomFunction));
    }

    /**
     * 查询用户已拥有的自定义功能
     * @param request
     * @param sysUserCustomFunction
     * @return
     * @throws JsonProcessingException
     */
    @Timed
    @HapInbound(apiName = "ClbOwnFuncs")
    @RequestMapping(value = "/getOwnFuncs", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public ResponseData getOwnFuncs(HttpServletRequest request , @RequestBody SysUserCustomFunction sysUserCustomFunction) throws JsonProcessingException {

        return new ResponseData(functionService.queryOwnFuncs(sysUserCustomFunction));
    }

    /**
     * 保存数据
     * @param request
     * @param funcs
     * @return
     * @throws JsonProcessingException
     */
    @Timed
    @HapInbound(apiName = "ClbSaveUserFunction")
    @RequestMapping(value = "/saveUserFunction", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public ResponseData saveUserFunction(HttpServletRequest request , @RequestBody SysUserCustomFuncs funcs) throws JsonProcessingException {
        ResponseData response = new ResponseData(true);

        // 清空所有
        SysUserCustomFunction function = new SysUserCustomFunction();
        function.setUserId(funcs.getClbUser().getUserId());
        functionService.deleteFuncs(function);

        for (SysUserCustomFunction func : funcs.getFunctionList()) {
            func.setObjectVersionNumber(1L);
            func.setCreationDate(new Date());
            func.setCreatedBy(-1L);
            func.setLastUpdatedBy(-1L);
            func.setLastUpdateDate(new Date());
            func.setUserId(funcs.getClbUser().getUserId());
            functionService.insert(null, func);
        }

        return response;
    }

}