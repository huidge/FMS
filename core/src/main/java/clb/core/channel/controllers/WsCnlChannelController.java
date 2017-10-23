package clb.core.channel.controllers;

import clb.core.channel.dto.*;
import clb.core.channel.service.*;
import clb.core.channel.service.impl.CnlChannelServiceImpl.ReturnData;
import clb.core.common.exceptions.CommonException;
import clb.core.supplier.dto.SpeCollectionTerms;
import clb.core.supplier.service.ISpeCollectionTermsService;
import clb.core.sys.controllers.ClbBaseController;
import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jun.zhao02@hand-china.com
 * @version 1.0
 * @name WsCnlChannelController
 * @description:渠道信息接口Controller
 * @date 2017/4/26
 */
@Controller
public class WsCnlChannelController extends ClbBaseController{

    @Autowired
    private ICnlChannelService service;

    @Autowired
    private ICnlChannelContractService contractService;

    @Autowired
    private ICnlContractRateService rateService;

    @Autowired
    private ICnlContractRateHistoryService cnlContractRateHistoryServiceImpl;

    @Autowired
    private ICnlContractRoleService roleService;

    @Autowired
    private ICnlChannelArchiveService archiveService;
    @Autowired
    private ICnlContractArchiveService contractArchiveService;

    @Autowired
    private ISpeCollectionTermsService speCollectionTermsService;


    /**
     * 我的团队 汇总查询
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelTeam")
    @RequestMapping(value = "/api/channel/team", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData query(@RequestBody CnlChannel dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<ReturnData> data = null;
		try {
			data = service.queryWsChannel(requestContext,dto);
		} catch (CommonException e) {
			ResponseData responseData = new ResponseData(false);
			responseData.setMessage(e.getDescriptionKey());
		    return responseData;
		}
        return new ResponseData(data);
    }

    /**
     * 我的团队 团队成员佣金分成
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelTeam")
    @RequestMapping(value = "/api/channel/teamMemberCommission", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryTeamMemberCommission(@RequestBody CnlChannelContract dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(rateService.queryWsChannelCommission(requestContext, dto));
    }


    /**
     * 渠道注册之后的详细信息填写提交
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelSubmit")
    @RequestMapping(value = "/api/cnlChannel/submit", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<CnlChannel> dto){
        IRequest requestCtx = createRequestContext(request);
        return service.channelBatchUpdate(requestCtx, dto);
    }


    /**
     * 渠道合约信息提交
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelContractSubmit")
    @RequestMapping(value = "/api/cnlChannel/contract/submit", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData contractSubmit(HttpServletRequest request,@RequestBody List<CnlChannelContract> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(contractService.contractBatchUpdate(requestCtx, dto));
    }

    /**
     * 合约 费率 提交
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelContractRateSubmit")
    @RequestMapping(value = "/api/cnlChannel/contract/rate/submit", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData rateSubmit(HttpServletRequest request,@RequestBody List<CnlContractRate> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(rateService.batchUpdate(requestCtx, dto));
    }


    /**
     * 个人中心 首页查询
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelPersonalQuery")
    @RequestMapping(value = "/api/channel/personal/query", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryPersonal(@RequestBody CnlChannel dto,@RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        CnlChannelArchive archive = new CnlChannelArchive();
        List<CnlChannel> channelList = service.queryChannelSimple(requestContext, dto);
        for(CnlChannel channel : channelList){
            archive.setChannelId(channel.getChannelId());
            channel.setCnlChannelArchive(archiveService.queryArchive(requestContext,archive,page,pageSize));
        }
        return new ResponseData(channelList);
    }


    /**
     * 个人中心 合约/(带条件查询就是 合约详情）
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelPersonalContract")
    @RequestMapping(value = "/api/channel/personal/contract", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryPersonalContract(@RequestBody CnlChannelContract dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(contractService.queryContract(requestContext,dto,page,pageSize));
    }


    /**
     *查询合约详情中的 付款条件
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelPersonalContractPayment")
    @RequestMapping(value = "/api/channel/personal/contract/payment", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryAll(SpeCollectionTerms dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        List<SpeCollectionTerms> data = speCollectionTermsService.selectAllData(dto);
        return new ResponseData(data);
    }


    /**
     * 个人中心 合约 费率
     *
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelPersonalContractRate")
    @RequestMapping(value = "/api/channel/personal/contract/rate", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryRate(@RequestBody CnlContractRate dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        if (dto.getDefineRateFlag().equals("Y")) {
            return new ResponseData(rateService.queryRate(requestContext,dto,page,pageSize));
        } else {
            if (dto.getPartyType().equals("CHANNEL") && dto.getRateLevelId() != null) {
                return new ResponseData(rateService.queryChannelRate(requestContext,dto,page,pageSize));
            } else if (dto.getPartyType().equals("SUPPLIER") && dto.getRateLevelId() != null) {
                return new ResponseData(rateService.querySupplierRate(requestContext,dto,page,pageSize));
            }
        }
        return new ResponseData();
    }
    
    /**
     * 渠道合约费率历史记录查询
     *
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelPersonalContractRateHistory")
    @RequestMapping(value = "/api/channel/personal/contract/rate/history", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryRateHistory(@RequestBody CnlContractRateHistory dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(cnlContractRateHistoryServiceImpl.queryRateHis(requestContext, dto, page, pageSize));
    }
    
    /**
     * 渠道合约费率历史记录提交
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelPersonalContractRateHisSubmit")
    @RequestMapping(value = "/api/channel/personal/contract/rate/his/submit", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData updateRateHis(HttpServletRequest request,@RequestBody List<CnlContractRateHistory> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(cnlContractRateHistoryServiceImpl.batchUpdate(requestCtx, dto));
    }

    /**
     * 个人中心 合约 利益分配（角色）
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelPersonalContractRole")
    @RequestMapping(value = "/api/channel/personal/contract/role", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryRole(@RequestBody CnlContractRole dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(roleService.queryCnlContractRole(requestContext,dto,page,pageSize));
    }


    /**
     * 个人中心 合约表中的文件
     *
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelPersonalContractArchive")
    @RequestMapping(value = "/api/channel/personal/contract/archive", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData queryArchive(@RequestBody CnlContractArchive dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                     @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(contractArchiveService.queryArchive(requestContext,dto,page,pageSize));
    }


    /**
     * 个人中心 文件删除 主要删除的就是 渠道附件表中的记录(渠道的文件)
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelPersonalArchiveDelete")
    @RequestMapping(value = "/api/channel/personal/archiveDelete", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData archiveDelete(@RequestBody List<CnlChannelArchive> dto, HttpServletRequest request) {
        archiveService.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * 个人中心合约附件 删除 主要删除的是合约附件表中的记录
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelPersonalContractArchiveDelete")
    @RequestMapping(value = "/api/channel/personal/contract/archiveDelete", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData contractArchiveDelete(@RequestBody List<CnlContractArchive> dto, HttpServletRequest request) {
        contractArchiveService.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * 个人中心 合约 费率 删除
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelPersonalContractRateDelete")
    @RequestMapping(value = "/api/channel/personal/contract/rate/delete", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData deleteRate(@RequestBody List<CnlContractRate> dto, HttpServletRequest request) {
        rateService.batchDelete(dto);
        return new ResponseData();
    }


    /**
     * 个人中心 合约 利益分配（角色）
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCnlChannelPersonalContractRoleDelete")
    @RequestMapping(value = "/api/channel/personal/contract/role/delete", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    public ResponseData deleteRole(@RequestBody List<CnlContractRole> dto, HttpServletRequest request) {
        roleService.batchDelete(dto);
        return new ResponseData();
    }

}