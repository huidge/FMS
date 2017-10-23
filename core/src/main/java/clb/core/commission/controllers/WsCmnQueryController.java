package clb.core.commission.controllers;

import clb.core.commission.dto.CmnCalc;
import clb.core.commission.dto.CmnChannelCommission;
import clb.core.commission.dto.CmnQueryCommission;
import clb.core.commission.service.ICmnCalcService;
import clb.core.commission.service.ICmnChannelCommissionService;
import clb.core.common.utils.DateUtil;
import clb.core.common.utils.ListSortUtil;
import clb.core.order.dto.OrdOrder;
import clb.core.order.service.IOrdOrderService;
import clb.core.pln.service.IPlnPlanRequestService;
import clb.core.production.dto.PrdItems;
import clb.core.production.service.IPrdItemsService;
import clb.core.sys.controllers.ClbBaseController;
import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IProfileService;

import net.sf.json.JSONObject;
import org.apache.ibatis.jdbc.Null;
import org.opensaml.xml.encryption.P;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class WsCmnQueryController extends ClbBaseController {

    @Autowired
    private ICmnChannelCommissionService channelCommissionService;

    @Autowired
    private IOrdOrderService orderService;
    
    @Autowired
   	private IProfileService profileService;
    
    @Autowired
    private IPlnPlanRequestService plnPlanRequestService;
    
    @Timed
    @HapInbound(apiName = "ClbWsCmnQueryReferralFee")
    @RequestMapping(value = "/api/cmn/queryReferralFee")
    @ResponseBody
    public ResponseData queryReferralFee(@RequestBody CmnChannelCommission dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        //return new ResponseData(service.select(requestContext, dto, page, pageSize));

        // 如果缴费方式为“预缴”，则默认为“年缴”
        if ("FJ".equals(dto.getPayMethod())) {
            dto.setPayMethod("AP");
        }
        
        //如果配置文件中配置的"是"   佣金只显示第一年佣金最高的一条
        String flag = profileService.getProfileValue(requestContext, "COMMISSION_ONLY_ONE");//读取配置文件
        if("是".equals(flag)) {
        	if ("".equals(dto.getOrderBy())
                    || dto.getOrderBy() == null) {
                dto.setOrderBy("supplierId," +
                        "        channelId," +
                        "        itemId," +
                        "        contributionPeriod," +
                        "        policyholdersMinAge," +
                        "        policyholdersMaxAge," +
                        "        effectiveStartDate," +
                        "        effectiveEndDate");
            }
        	List<CmnChannelCommission> referralFeeList = channelCommissionService.queryReferralFee(requestContext, dto, page, pageSize);
        	for (CmnChannelCommission cmnChannelCommission : referralFeeList) {
        		//币种转换
        		StringBuffer tempCurrency = new StringBuffer();
        		String[] currencys = cmnChannelCommission.getCurrency().split("/");
        		for (String currency : currencys) {
        			tempCurrency.append(plnPlanRequestService.selectCodeMeaning("PUB.CURRENCY", currency)).append("/");
				}
        		cmnChannelCommission.setCurrency(tempCurrency.substring(0, tempCurrency.length()-1).toString());
        		//支付方式转换
        		String[] payMethods = cmnChannelCommission.getPayMethod().split("/");
        		StringBuffer tempPayMethod = new StringBuffer();
        		for (String payMethod : payMethods) {
        			tempPayMethod.append(plnPlanRequestService.selectCodeMeaning("CMN.PAY_METHOD", payMethod)).append("/");
				}
        		cmnChannelCommission.setPayMethodName(tempPayMethod.substring(0, tempPayMethod.length()-1).toString());
			}
        	return new ResponseData(referralFeeList);
        }
        
        if ("".equals(dto.getOrderBy())
                || dto.getOrderBy() == null) {
            dto.setOrderBy("supplierId," +
                    "        channelTypeCode," +
                    "        channelLevelId," +
                    "        channelId," +
                    "        itemId," +
                    "        contributionPeriod," +
                    "        currency," +
                    "        payMethod," +
                    "        policyholdersMinAge," +
                    "        policyholdersMaxAge," +
                    "        effectiveStartDate," +
                    "        effectiveEndDate");
        }
        List<CmnChannelCommission> list = channelCommissionService.selectShowAllFields(requestContext, dto, page, pageSize);
        for (CmnChannelCommission cmnChannelCommission : list) {
        	cmnChannelCommission.setCurrency(plnPlanRequestService.selectCodeMeaning("PUB.CURRENCY", cmnChannelCommission.getCurrency()));
        	cmnChannelCommission.setPayMethodName(plnPlanRequestService.selectCodeMeaning("CMN.PAY_METHOD", cmnChannelCommission.getPayMethod()));
		}
        return new ResponseData(list);
    }

    @Timed
    @HapInbound(apiName = "ClbWsCmnValidateUpdateOrder")
    @RequestMapping(value = "/api/cmn/validateUpdateOrder")
    @ResponseBody
    public ResponseData validateUpdateOrder(@RequestBody OrdOrder dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);

        ResponseData response=new ResponseData(true);

        // 根据orderId找已选择佣金数据
        OrdOrder order = new OrdOrder();
        order.setOrderId(dto.getOrderId());
        List<OrdOrder> ordOrderList = new ArrayList<OrdOrder>();
        ordOrderList = orderService.queryOrder(requestContext, order, 1, 100);
        if (ordOrderList != null && ordOrderList.size() > 0) {
            order = ordOrderList.get(0);
        }

        if (order != null) {

            // 如果缴费方式为“预缴”，则默认为“年缴”
            if ("FJ".equals(order.getPayMethod())) {
                order.setPayMethod("AP");
            }

            CmnChannelCommission channelCommission = new CmnChannelCommission();
            channelCommission.setItemId(order.getItemId());
            channelCommission.setChannelId(order.getChannelId());
            channelCommission.setSupplierId(order.getOwnSupplierId());
            channelCommission.setPayMethod(order.getPayMethod());
            channelCommission.setContributionPeriod(order.getSublineItemName());
            channelCommission.setCurrency(order.getCurrency());
            channelCommission.setEffectiveDate(order.getReserveDate());
            channelCommission.setPolicyholdersAge(new Long(DateUtil.getAgeByDate(order.getInsurantBirthDate())));
            List<CmnChannelCommission> commissionList = new ArrayList<CmnChannelCommission>();
            commissionList = channelCommissionService.queryChannelCmnList(channelCommission);
            if (commissionList != null && commissionList.size() > 0) {
                channelCommission = commissionList.get(0);

                // 预约时间 验证
                if (dto.getReserveDate() != null) {
                    if (dto.getReserveDate().compareTo(channelCommission.getEffectiveStartDate()) < 0
                            || dto.getReserveDate().compareTo(channelCommission.getEffectiveEndDate()) > 0) {
                        response.setSuccess(false);
                        response.setMessage("预约时间应该在交易路线时间范围内：！" +
                                DateUtil.getDateStr(channelCommission.getEffectiveStartDate(), "yyyy-MM-dd HH:mm:ss")
                                + "--"
                                + DateUtil.getDateStr(channelCommission.getEffectiveEndDate(), "yyyy-MM-dd HH:mm:ss")
                        );
                        return response;
                    }
                }

                // 出生年月 验证
                if (dto.getInsurantBirthDate() != null) {
                    Long insurantAge = new Long(DateUtil.getAgeByDate(dto.getInsurantBirthDate()));
                    if (insurantAge < channelCommission.getPolicyholdersMinAge()
                            || insurantAge > channelCommission.getPolicyholdersMaxAge()) {
                        response.setSuccess(false);
                        response.setMessage("出生年月对应的年龄" + insurantAge +
                                "应该在已选佣金的数据范围内：" + channelCommission.getPolicyholdersMinAge()
                                + "--" + channelCommission.getPolicyholdersMaxAge());
                        return response;
                    }
                }

                // 添加的附加险或者高端医疗 验证
                if (dto.getItemId() != null) {

                    CmnChannelCommission addChannelCommission = new CmnChannelCommission();
                    addChannelCommission.setItemId(dto.getItemId());
                    addChannelCommission.setChannelId(order.getChannelId());
                    addChannelCommission.setSupplierId(order.getOwnSupplierId());
                    addChannelCommission.setPayMethod(order.getPayMethod());
                    addChannelCommission.setContributionPeriod(order.getSublineItemName());
                    addChannelCommission.setCurrency(order.getCurrency());
                    addChannelCommission.setEffectiveDate(order.getReserveDate());
                    addChannelCommission.setPolicyholdersAge(new Long(DateUtil.getAgeByDate(order.getInsurantBirthDate())));
                    addChannelCommission.setDealPath(order.getDealPath());
                    List<CmnChannelCommission> addCommissionList = new ArrayList<CmnChannelCommission>();
                    addCommissionList = channelCommissionService.selectShowAllFields(requestContext,
                            addChannelCommission, 0, 0);
                    if (addCommissionList.size() == 0
                            || addCommissionList == null) {
                        response.setSuccess(false);
                        response.setMessage("添加的附加险与高端医疗交易路线与已选主险产品的交易路线不同，或者没有相应佣金比例！");
                        return response;
                    }
                }
            }

        }

        return response;
    }


    @Timed
    @HapInbound(apiName = "ClbWsCmnChooseCommission")
    @RequestMapping(value = "/api/cmn/chooseCommission")
    @ResponseBody
    public ResponseData query(@RequestBody CmnQueryCommission dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        ResponseData response = new ResponseData(true);

        if (dto.getChannelId() == null) {
            response.setSuccess(false);
            response.setMessage("渠道ID不能为空!");
            return response;
        }

        if ("".equals(dto.getCurrency())) {
            response.setSuccess(false);
            response.setMessage("币种不能为空!");
            return response;
        }

        if ("".equals(dto.getPayMethod())) {
            response.setSuccess(false);
            response.setMessage("缴费方式不能为空!");
            return response;
        }

        if ("".equals(dto.getContributionPeriod())) {
            response.setSuccess(false);
            response.setMessage("供款期不能为空!");
            return response;
        }

        if (dto.getItemId() == null) {
            response.setSuccess(false);
            response.setMessage("主险产品不能为空!");
            return response;
        }

        List<CmnChannelCommission> resultList = channelCommissionService.chooseCommission(requestContext,dto);
        //前端要求接口调用返回时屏蔽掉dealPathName和parentPartyNamebug11450
        for (CmnChannelCommission cmnChannelCommission : resultList) {
			cmnChannelCommission.setDealPathName(null);
			cmnChannelCommission.setParentPartyName(null);
			cmnChannelCommission.setSupplierName(null);
		}

        return new ResponseData(resultList);
    }

    /**
     * 查询债券列表
     * @param dto
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsCmnQueryBondList")
    @RequestMapping(value = "/api/cmn/queryBondList")
    @ResponseBody
    public ResponseData queryBondList(@RequestBody CmnChannelCommission dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        //return new ResponseData(service.select(requestContext, dto, page, pageSize));
        return new ResponseData(channelCommissionService.queryBondList(dto));
    }
}