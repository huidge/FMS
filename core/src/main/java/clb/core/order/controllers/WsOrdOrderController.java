package clb.core.order.controllers;

import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.dto.CnlProSupRelation;
import clb.core.channel.service.ICnlChannelContractService;
import clb.core.channel.service.ICnlProSupRelationService;
import clb.core.commission.dto.CmnChannelCommission;
import clb.core.common.utils.ListSortUtil;
import clb.core.order.dto.*;
import clb.core.order.mapper.OrdCommissionMapper;
import clb.core.order.service.*;
import clb.core.production.dto.PrdItems;
import clb.core.sys.controllers.ClbBaseController;
import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jun.zhao02@hand-china.com
 * @version 1.0
 * @name WsOrdOrderController
 * @description:订单信息接口Controller
 * @date 2017/4/26
 */
@Controller
public class WsOrdOrderController extends ClbBaseController {

    @Autowired
    private IOrdOrderService ordOrderService;

    @Autowired
    private IOrdPendingService ordPendingService;

    @Autowired
    private IOrdPendingFollowService ordPendingFollowService;

    @Autowired
    private IOrdStatusHisService ordStatusHisService;

    @Autowired
    private IOrdAdditionService ordAdditionService;

    @Autowired
    private IOrdBeneficiaryService ordBeneficiaryService;

    @Autowired
    private IOrdCustomerService ordCustomerService;

    @Autowired
    private IOrdAfterService ordAfterService;

    @Autowired
    private IOrdOrderRenewalService orderRenewalService;

    @Autowired
    private IOrdCommissionService ordCommissionService;

    @Autowired
    private IOrdTeamVisitorService ordTeamVisitorService;

    @Autowired
    private ICnlProSupRelationService cnlProSupRelationService;

    @Autowired
    private ICnlChannelContractService channelContractService;

    @Autowired
    private IOrdCstEducationService ordEducationService;

    @Autowired
    private IOrdCstSkillService ordSkillService;

    @Autowired
    private IOrdCstWorkService ordWorkService;

    @Autowired
    private IOrdFileService ordFileService;

    @Autowired
    private OrdCommissionMapper ordCommissionMapper;


    /**
     * 订单文件列表查询
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdOrderListQuery")
    @RequestMapping(value = "/api/ordOrderFileList/query", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData query(@RequestBody OrdFile dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordFileService.queryOrdFile(requestContext,dto));
    }

    /**
     * 订单信息查询
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdOrderQuery")
    @RequestMapping(value = "/api/ordOrder/query", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData query(@RequestBody OrdOrder dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordOrderService.queryWsOrder(requestContext, dto, page, pageSize));
    }

    /**
     * 订单信息个人查询
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdOrderPersonalQuery")
    @RequestMapping(value = "/api/ordOrder/queryPersonal", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData queryPersonal(@RequestBody OrdOrder dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordOrderService.queryWsPersonalOrder(requestContext, dto,page,pageSize));
    }

    /**
     * 订单信息个人查询 角标
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdOrderPersonalQueryTotal")
    @RequestMapping(value = "/api/ordOrder/queryPersonalTotal", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public Long queryPersonalTotal(@RequestBody OrdOrder dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return ordOrderService.queryPersonalTotal(requestContext, dto);
    }

    /**
     * 订单信息团队查询
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdOrderTeamQuery")
    @RequestMapping(value = "/api/ordOrder/queryTeam", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData queryTeam(@RequestBody OrdOrder dto,@RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordOrderService.queryWsTeamOrder(requestContext, dto,page,pageSize));
    }

    /**
     * 转介绍订单数据查询
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdOrderReferralQuery")
    @RequestMapping(value = "/api/ordOrder/queryReferral", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData queryReferral(@RequestBody OrdOrder dto,@RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordOrderService.queryWsReferralOrder(requestContext, dto,page,pageSize));
    }


    /**
     * 订单提交
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdCustomerSubmit")
    @RequestMapping(value = "/api/ordCustomer/submit", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData updateCustomer(HttpServletRequest request, @RequestBody List<OrdCustomer> dto) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordCustomerService.batchSubmit(requestContext, dto));
    }

    /**
     * 订单客户提交
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdOrderSubmit")
    @RequestMapping(value = "/api/ordOrder/submit", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData updateOrder(HttpServletRequest request, @RequestBody List<OrdOrder> dto) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordOrderService.orderBatchUpdate(requestContext, dto));
    }

    /**
     * 订单Pending查询
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdPendingTeamQuery")
    @RequestMapping(value = "/api/ordPending/queryTeam", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData queryTeam(@RequestBody OrdPending dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordPendingService.queryWsTeamOrdPending(requestContext, dto));
    }

    /**
     * 订单Pending查询
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdPendingQuery")
    @RequestMapping(value = "/api/ordPending/query", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData query(@RequestBody OrdPending dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordPendingService.queryWsOrdPending(requestContext, dto));
    }

    /**
     * 订单Pending查询
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdPendingPersonalQuery")
    @RequestMapping(value = "/api/ordPending/queryPersonal", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData queryPersonal(@RequestBody OrdPending dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordPendingService.queryWsPersonalOrdPending(requestContext, dto));
    }

    /**
     * 订单Pending查询 需补资料 条数
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "queryOrdPendingTotle")
    @RequestMapping(value = "/api/ordPending/queryOrdPendingTotle", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public Long queryOrdPendingTotle(@RequestBody OrdPending dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return ordPendingService.queryOrdPendingTotle(requestContext, dto);
    }

    /**
     * 订单Pending查询
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdPendingFollowQuery")
    @RequestMapping(value = "/api/ordPendingFollow/query", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData query(@RequestBody OrdPendingFollow dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordPendingFollowService.queryWsOrdPendingFollow(requestContext, dto));
    }

    /**
     * 订单Pending提交
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdPendingFollowSubmit")
    @RequestMapping(value = "/api/ordPendingFollow/submit", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody List<OrdPendingFollow> dto) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordPendingFollowService.followBatchUpdate(requestContext, dto));
    }

    /**
     * 订单状态跟进查询接口
     * daiqian.shi@hand-china.com
     * @param request
     * @param dto
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdStatusHisQuery")
    @RequestMapping(value = "/api/ordStatusHis/query", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData query(HttpServletRequest request, @RequestBody OrdStatusHis dto) {
        IRequest requestContext = createRequestContext(request);
        List<OrdStatusHis> list = ordStatusHisService.queryWsOrdStatusHis(requestContext,dto);
        if(!CollectionUtils.isEmpty(list)) {
        	for (OrdStatusHis ordStatusHis : list) {
        		//前端显示需要转换
        		if("PRE_APPROVING".equals(ordStatusHis.getStatus()) || "PRE_APPROVED".equals(ordStatusHis.getStatus())) {
        			ordStatusHis.setStatus("DATA_APPROVING");
        			ordStatusHis.setMeaning("资料审核中");
        		}
        	}
        }
        return new ResponseData(list);
    }

    /**
     * 附加险信息查询接口
     * daiqian.shi@hand-china.com
     * @param request
     * @param dto
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdAdditionQuery")
    @RequestMapping(value = "/api/ordAddition/query", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData query(HttpServletRequest request, @RequestBody OrdAddition dto) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordAdditionService.queryWsOrdAddition(requestContext,dto));
    }

    /**
     * 工作經歷接口
     * Rex.Hua@hand-china.com
     * @param request
     * @param dto
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdWorknQuery")
    @RequestMapping(value = "/api/ordWork/query", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData query(@RequestBody OrdCstWork dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordWorkService.select(requestContext,dto,page,pageSize));
    }

    /**
     * 教育程度-技能特长接口
     * Rex.Hua@hand-china.com
     * @param request
     * @param dto
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdSkillnQuery")
    @RequestMapping(value = "/api/ordSkill/query", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData query(@RequestBody OrdCstSkill dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordSkillService.select(requestContext,dto,page,pageSize));
    }
    /**
     * 教育经历接口
     * Rex.Hua@hand-china.com
     * @param request
     * @param dto
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdEducationQuery")
    @RequestMapping(value = "/api/ordEducation/query", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData query(@RequestBody OrdCstEducation dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordEducationService.select(requestContext,dto,page,pageSize));
    }
    /**
     * 受益人查询接口
     * daiqian.shi@hand-china.com
     * @param request
     * @param dto
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdBeneficiaryQuery")
    @RequestMapping(value = "/api/ordBeneficiary/query", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData query(HttpServletRequest request, @RequestBody OrdBeneficiary dto) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordBeneficiaryService.queryWsOrdBeneficiary(requestContext,dto));
    }

    /**
     * 受保人，投保人查询接口，根据客户类型（INSURANT/APPLICANT）来区分受保人，投保人
     * <p>daiqian.shi@hand-china.com</p>
     * @param request
     * @param dto
     * @return
     */
//    @Timed
//    @HapInbound(apiName = "ClbWsOrdCustomerQuery")
//    @RequestMapping(value = "/api/ordCustomer/query", method = RequestMethod.POST, produces = {"application/json"})
//    @ResponseBody
//    public ResponseData query(HttpServletRequest request, @RequestBody OrdCustomer dto) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(ordCustomerService.queryWsOrdCustomer(requestContext,dto));
//    }

    /**
     * 查询售后单接口
     * daiqian.shi@hand-china.com
     * @param request
     * @param dto
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdAfterQuery")
    @RequestMapping(value = "/api/ordAfter/query", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData query(HttpServletRequest request, @RequestBody OrdAfter dto) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordAfterService.queryWsOrdAfter(requestContext,dto));
    }

    /**
     * 续保信息查询接口
     * daiqian.shi@hand-china.com
     * @param request
     * @param dto
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdOrderRenewalQuery")
    @RequestMapping(value = "/api/ordOrderRenewal/query", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData query(HttpServletRequest request, @RequestBody OrdOrderRenewal dto) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(orderRenewalService.queryWsRenewal(requestContext,dto));
    }

    /**
     * 佣金查询接口
     * @param request
     * @param dto
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdCommissionQuery")
    @RequestMapping(value = "/api/ordCommission/query", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData query(HttpServletRequest request, @RequestBody OrdCommission dto) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordCommissionService.queryWsOrdCommission(requestContext,dto));
    }

    /**
     * 取消预约
     * @param request
     * @param dto
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCancelReservation")
    @RequestMapping(value = "/api/cancel/reservation", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData update(HttpServletRequest request, @RequestBody OrdOrder dto) {
        if (null == dto.getOrderId()){
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage("订单主键不能为空");
            return responseData;
        }
        IRequest requestContext = createRequestContext(request);

        dto.setStatus("RESERVE_CANCELLED");
        dto.setHisStatus("RESERVE_CANCELLED");
        List<OrdOrder> ordOrders = new ArrayList<OrdOrder>();
        ordOrders.add(dto);
        ordOrderService.orderStatusUpdate(requestContext,ordOrders);
        ResponseData responseData = new ResponseData(true);
        responseData.setMessage("取消预约成功");
        return responseData;
    }

    /**
     * L签访客信息接口
     * @param request
     * @param dto
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdTeamVisitorQuery")
    @RequestMapping(value = "/api/ordTeamVisitor/query", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData query(HttpServletRequest request, @RequestBody OrdTeamVisitor dto) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordTeamVisitorService.queryWsOrdTeamVisitor(requestContext,dto));
    }

    /**
     * 订单状态更新
     *
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdOrderStatusUpdate")
    @RequestMapping(value = "/api/ordOrder/updateStatus", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData updateStatus(HttpServletRequest request, @RequestBody List<OrdOrder> dto) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordOrderService.orderStatusUpdate(requestContext, dto));
    }

    // 验证交易路线
    @Timed
    @HapInbound(apiName = "ClbWsOrdOrderValidateTradeRoute")
    @RequestMapping(value = "/api/ordOrder/validateTradeRoute", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData validateTradeRoute(HttpServletRequest request, @RequestBody CmnChannelCommission dto) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordOrderService.validateTradeRoute(requestContext, dto));
    }

    // 查询订单数据汇总
    @Timed
    @HapInbound(apiName = "ClbWsOrdOrderQueryClassAmount")
    @RequestMapping(value = "/api/ordOrder/queryClassAmount", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData queryClassAmount(HttpServletRequest request, @RequestBody PrdItems dto) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(ordOrderService.queryClassAmount(requestContext, dto));
    }

    // 删除附加险
    @Timed
    @HapInbound(apiName = "ClbWsOrdAdditionRemove")
    @RequestMapping(value = "/api/ordAddition/remove", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData ordAdditionRemove(HttpServletRequest request, @RequestBody List<OrdAddition> dto) {

        List<OrdCommission> ordCommissions = new ArrayList<>();
        for (OrdAddition o:dto) {
            OrdCommission ordCommission = new OrdCommission();
            ordCommission.setAdditionId(o.getAdditionId());
            List<OrdCommission> ordCommissionList = ordCommissionMapper.queryOrdCommissionAll(ordCommission);
            ordCommissions.addAll(ordCommissionList);
        }
        ordAdditionService.batchDelete(dto);
        ordCommissionService.batchDelete(ordCommissions);
        return new ResponseData();
    }

    // 删除受益人
    @Timed
    @HapInbound(apiName = "ClbWsOrdBeneficiaryRemove")
    @RequestMapping(value = "/api/ordBeneficiary/remove", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData ordBeneficiaryRemove(HttpServletRequest request, @RequestBody List<OrdBeneficiary> dto) {
        ordBeneficiaryService.batchDelete(dto);
        return new ResponseData();
    }

    // 删除受益人
    @Timed
    @HapInbound(apiName = "ClbWsOrdFileRemove")
    @RequestMapping(value = "/api/ordFile/remove", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData ordFileRemove(HttpServletRequest request, @RequestBody List<OrdFile> dto) {
        ordFileService.batchDelete(dto);
        return new ResponseData();
    }
    /**
     * 查询续保的年数
     * @param request
     * @param dto
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrderQueryRenewalYear")
    @RequestMapping(value = "/api/order/queryRenewalYear", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData queryRenewalYear(HttpServletRequest request, @RequestBody OrdOrder dto) {
    		return new ResponseData(ordOrderService.queryRenewalYear(dto));
    }

    

}