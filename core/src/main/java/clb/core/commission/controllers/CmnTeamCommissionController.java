package clb.core.commission.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.commission.dto.CmnTeamCommission;
import clb.core.commission.service.ICmnTeamCommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

    @Controller
    public class CmnTeamCommissionController extends BaseController{

    @Autowired
    private ICmnTeamCommissionService service;


    @RequestMapping(value = "/fms/cmn/team/commission/query")
    @ResponseBody
    public ResponseData query(CmnTeamCommission dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/cmn/team/commission/queryBasic")
    @ResponseBody
    public ResponseData queryBasic(CmnTeamCommission dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryBasic(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/cmn/team/commission/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<CmnTeamCommission> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/cmn/team/commission/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<CmnTeamCommission> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }

        /**
         * 查询是否有最大版本
         * @param dto 团队佣金dto
         * @param request 请求
         * @return version(不存在返回0)
         */
        @RequestMapping(value = "/fms/cmn/team/commission/queryMaxVersion")
        @ResponseBody
        public Long queryMaxVersion(@RequestBody CmnTeamCommission dto, HttpServletRequest request) {
            return service.queryMaxVersion(dto);
        }

        /**
         * 增加新版本
         * @param dto 团队佣金dto
         * @param oldVersion 最大版本号
         * @param request 请求
         * @return 旧版本&新版本团队佣金dto list
         */
        @RequestMapping(value = "/fms/cmn/team/commission/addVersion/{oldVersion}")
        @ResponseBody
        public ResponseData addVersion(@RequestBody CmnTeamCommission dto, @PathVariable("oldVersion") Long oldVersion, HttpServletRequest request) {
            IRequest requestContext = createRequestContext(request);
            return new ResponseData(service.addVersion(requestContext,dto,oldVersion));
        }

    }