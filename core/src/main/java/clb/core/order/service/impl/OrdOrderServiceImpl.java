package clb.core.order.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import clb.core.order.mapper.*;
import clb.core.commission.dto.CmnTradeRouteShow;
import clb.core.commission.mapper.CmnAdtRiskRelationMapper;
import clb.core.commission.mapper.CmnChannelCommissionMapper;
import clb.core.commission.mapper.CmnTradeRouteShowMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.IProfileService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.dto.CnlContractRole;
import clb.core.channel.mapper.CnlChannelContractMapper;
import clb.core.channel.mapper.CnlChannelMapper;
import clb.core.channel.mapper.CnlContractRoleMapper;
import clb.core.commission.dto.CmnAdtRiskRelation;
import clb.core.commission.dto.CmnChannelCommission;
import clb.core.common.service.ISequenceService;
import clb.core.common.utils.AESUtil;
import clb.core.common.utils.Base64Util;
import clb.core.common.utils.CalculateAge;
import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.DateUtil;
import clb.core.common.utils.ImportUtil;
import clb.core.common.utils.StringUtil;
import clb.core.customer.dto.CtmCustomer;
import clb.core.customer.mapper.CtmCustomerMapper;
import clb.core.customer.service.ICtmCustomerService;
import clb.core.notification.service.INtnNotificationService;
import clb.core.order.dto.OrdAddition;
import clb.core.order.dto.OrdBeneficiary;
import clb.core.order.dto.OrdCommission;
import clb.core.order.dto.OrdCstEducation;
import clb.core.order.dto.OrdCstSkill;
import clb.core.order.dto.OrdCstWork;
import clb.core.order.dto.OrdCustomer;
import clb.core.order.dto.OrdFile;
import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdOrderRenewal;
import clb.core.order.dto.OrdPending;
import clb.core.order.dto.OrdStatusHis;
import clb.core.order.dto.OrdTeamVisitor;
import clb.core.order.dto.OrdTemplate;
import clb.core.order.dto.OrdTradeRoute;
import clb.core.order.dto.OrdTradeRouteShow;
import clb.core.order.dto.SysOrderCfgField;
import clb.core.order.dto.SysOrderCfgOperation;
import clb.core.order.service.IOrdAdditionService;
import clb.core.order.service.IOrdBeneficiaryService;
import clb.core.order.service.IOrdCommissionService;
import clb.core.order.service.IOrdCstEducationService;
import clb.core.order.service.IOrdCstSkillService;
import clb.core.order.service.IOrdCstWorkService;
import clb.core.order.service.IOrdCustomerService;
import clb.core.order.service.IOrdFileService;
import clb.core.order.service.IOrdOrderRenewalService;
import clb.core.order.service.IOrdOrderService;
import clb.core.order.service.IOrdPendingService;
import clb.core.order.service.IOrdStatusHisService;
import clb.core.order.service.IOrdTeamVisitorService;
import clb.core.order.service.IOrdTradeRouteService;
import clb.core.order.service.IOrdTradeRouteShowService;
import clb.core.pln.service.IPlnPlanRequestService;
import clb.core.prc.constants.Constants;
import clb.core.production.dto.PrdItems;
import clb.core.production.mapper.PrdItemsMapper;
import clb.core.production.service.IPrdItemsService;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.dto.SpeSupplierLine;
import clb.core.supplier.dto.SpeSupplierSettle;
import clb.core.supplier.mapper.SpeSupplierMapper;
import clb.core.sys.dto.SysFunctionAllocationRule;
import clb.core.sys.mapper.SysFunctionAllocationRuleMapper;
import clb.core.sys.service.ISysFunctionAllocationRuleService;
import clb.core.sys.service.ISysSendSMSService;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;
import clb.core.system.service.IClbCodeService;
import clb.core.system.service.IClbUserService;

@Service
@Transactional
public class OrdOrderServiceImpl extends BaseServiceImpl<OrdOrder>implements IOrdOrderService {

    @Autowired
    private OrdOrderMapper ordOrderMapper;

    @Autowired
    private IOrdAdditionService ordAdditionService;

    @Autowired
    private OrdAdditionMapper ordAdditionMapper;

    @Autowired
    private IOrdBeneficiaryService ordBeneficiaryService;

    @Autowired
    private ISysFileService sysFileService;

    @Autowired
    private OrdStatusHisMapper ordStatusHisMapper;

    @Autowired
    private IOrdStatusHisService ordStatusHisService;

    @Autowired
    private CmnTradeRouteShowMapper cmnTradeRouteShowMapper;

    @Autowired
    private IOrdTradeRouteService ordTradeRouteService;

    @Autowired
    private OrdTradeRouteMapper ordTradeRouteMapper;

    @Autowired
    private IOrdTradeRouteShowService ordTradeRouteShowService;

    @Autowired
    private OrdTradeRouteShowMapper ordTradeRouteShowMapper;

    @Autowired
    private OrdOrderRenewalMapper ordOrderRenewalMapper;

    @Autowired
    private IOrdOrderRenewalService ordOrderRenewalService;

    @Autowired
    private CnlChannelMapper cnlChannelMapper;

    @Autowired
    private SpeSupplierMapper speSupplierMapper;

    @Autowired
    private CnlContractRoleMapper cnlContractRoleMapper;

    @Autowired
    private OrdCommissionMapper ordCommissionMapper;

    @Autowired
    private IOrdCommissionService ordCommissionService;

    @Autowired
    private OrdFileMapper ordFileMapper;

    @Autowired
    private IOrdFileService ordFileService;

    @Autowired
    private ISequenceService sequenceService;

    @Autowired
    private IOrdPendingService ordPendingService;

    @Autowired
    private OrdPendingMapper ordPendingMapper;

    @Autowired
    private OrdTemplateMapper ordTemplateMapper;

    @Autowired
    private IOrdTeamVisitorService ordTeamVisitorService;

    @Autowired
    private IOrdCstEducationService ordCstEducationService;

    @Autowired
    private IOrdCstSkillService ordCstSkillService;

    @Autowired
    private IOrdCstWorkService ordCstWorkService;

    @Autowired
    private PrdItemsMapper prdItemsMapper;

    @Autowired
    private CnlChannelContractMapper cnlChannelContractMapper;

    @Autowired
    private ISysFunctionAllocationRuleService sysFunctionAllocationRuleService;

    @Autowired
    private SysFunctionAllocationRuleMapper sysFunctionAllocationRuleMapper;

    @Autowired
    private ClbUserMapper clbUserMapper;

    @Autowired
    private IClbUserService clbUserService;

    @Autowired
    private INtnNotificationService ntnNotificationService;

    @Autowired
    private IOrdCustomerService ordCustomerService;

    @Autowired
    private CtmCustomerMapper ctmCustomerMapper;

    @Autowired
    private ICtmCustomerService ctmCustomerService;

    @Autowired
    private IPrdItemsService itemsService;

    @Autowired
    private CmnChannelCommissionMapper cmnChannelCommissionMapper;

    @Autowired
    private ISysSendSMSService sendSMSService;

    @Autowired
    private IClbCodeService clbCodeService;

    @Autowired
    private IPlnPlanRequestService plnPlanRequestService;

    @Autowired
    private OrdBeneficiaryMapper ordBeneficiaryMapper;
   
    @Autowired
    private IProfileService profileService;
    
    @Autowired
    private CmnAdtRiskRelationMapper cmnAdtRiskRelationMapper;

    private Long maxSeqNum;
    private Long channelLevel;

    /**
     * 查询
     *
     * @param request
     * @param ordOrder
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<OrdOrder> queryOrder(IRequest request, OrdOrder ordOrder, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<OrdOrder> oList = ordOrderMapper.queryOrder(ordOrder);
        for (OrdOrder k : oList) {
            if (k.getPlanFileId() != null) {
                SysFile sysFile = sysFileService.selectByPrimaryKey(request, k.getPlanFileId());
                if (sysFile != null) {
                    k.setPlanToken(sysFile.get_token());
                } else {
                    k.setPlanToken(null);
                }

            }

            if (k.getReqFileId() != null) {
                SysFile sysFile = sysFileService.selectByPrimaryKey(request, k.getReqFileId());
                if (sysFile != null) {
                    k.setReqToken(sysFile.get_token());
                } else {
                    k.setReqToken(null);
                }

            }

            if (k.getContractFileId() != null) {
                SysFile sysFile = sysFileService.selectByPrimaryKey(request, k.getContractFileId());
                if (sysFile != null) {
                    k.setContractToken(sysFile.get_token());
                } else {
                    k.setContractToken(null);
                }

            }

            OrdTradeRoute preUser = new OrdTradeRoute();
            preUser.setOrderId(k.getOrderId());
            List<OrdTradeRoute> preUserList = ordTradeRouteMapper.queryPreUserId(preUser);
            if (CollectionUtils.isNotEmpty(preUserList)) {
                k.setPreName(preUserList.get(0).getPreName());
            }

            // 所属一级
            if (k.getOrderId() != null) {
                k.setStairSell(ordTradeRouteService.queryStairSellByOrderId(request, k.getOrderId()));
            }
        }
        return oList;
    }

    /**
     * 查询
     *
     * @param request
     * @param clbCodeValue
     * @return
     */
    @Override
    public List<ClbCodeValue> queryOrderStatus(IRequest request, ClbCodeValue clbCodeValue) {

        return ordOrderMapper.queryOrderStatus(clbCodeValue);
    }

    /**
     * 查询
     *
     * @param request
     * @param clbCodeValue
     * @return
     */
    @Override
    public List<ClbCodeValue> queryOrderAppStatus(IRequest request, ClbCodeValue clbCodeValue) {

        return ordOrderMapper.queryOrderAppStatus(clbCodeValue);
    }

    /**
     * 个人订单查询接口
     *
     * @param request
     * @param ordOrder
     * @return
     */
    @Override
    public List<OrdOrder> queryWsPersonalOrder(IRequest request, OrdOrder ordOrder, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);

        List<OrdOrder> oList = ordOrderMapper.queryWsPersonalOrder(ordOrder);
        for (OrdOrder k : oList) {
            k.setCurDate(new Date());
        }
        return oList;
    }

    /**
     * 个人订单查询接口
     *
     * @param request
     * @param ordOrder
     * @return
     */
    @Override
    public Long queryPersonalTotal(IRequest request, OrdOrder ordOrder) {
        Long oList = ordOrderMapper.queryPersonalTotal(ordOrder);
        return oList;
    }

    /**
     * 团队订单查询接口
     *
     * @param request
     * @param ordOrder
     * @return
     */
    @Override
    public List<OrdOrder> queryWsOrder(IRequest request, OrdOrder ordOrder, int page, int pagesize) {

        PageHelper.startPage(page, pagesize);

        List<OrdOrder> oList = ordOrderMapper.queryWsOrder(ordOrder);
        for (OrdOrder k : oList) {
            k.setCurDate(new Date());
        }
        return oList;
    }

    /**
     * 转介绍订单查询接口
     *
     * @param request
     * @param ordOrder
     * @return
     */
    @Override
    public List<OrdOrder> queryWsReferralOrder(IRequest request, OrdOrder ordOrder, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);

        List<OrdOrder> oList = ordOrderMapper.queryWsReferralOrder(ordOrder);
        for (OrdOrder k : oList) {
            k.setCurDate(new Date());
        }
        return oList;
    }

    @Override
    public List<OrdOrder> queryWsTeamOrder(IRequest request, OrdOrder ordOrder, int page, int pagesize) {
        if (pagesize == 0) {
        } else {
            PageHelper.startPage(page, pagesize);
        }

        List<OrdOrder> oList = ordOrderMapper.queryWsTeamOrder(ordOrder);
        for (OrdOrder k : oList) {
            k.setCurDate(new Date());
        }
        return oList;
    }

    private void generateOrderTradeRoute(IRequest request, List<CmnTradeRouteShow> cmnTradeRouteList, OrdOrder ordOrder,
                                         Long channelCount, Long cmnParentRouteId, Long parentRouteId) {
        Long seqNum1 = 1L;
        Long seqNum2 = 2L;
        Long seqNum3 = 3L;
        Long seqNum4 = 4L;
        Long seqNum5 = 5L;

        Long firstSupplierSeq = channelCount + seqNum1;
        Long secSupplierSeq = channelCount + seqNum2;

        OrdCommission ordCommission = new OrdCommission();
        ordCommission.setOrderId(ordOrder.getOrderId());
        ordCommission.setItemId(ordOrder.getItemId());
        ordCommission.setAdditionId(null);

        if (cmnParentRouteId == null) {
            // 删除交易路线
            OrdTradeRoute oldTradeRoute = new OrdTradeRoute();
            oldTradeRoute.setOrderId(ordOrder.getOrderId());
            List<OrdTradeRoute> oList = ordTradeRouteMapper.select(oldTradeRoute);
            ordTradeRouteService.batchDelete(oList);

            // 生成保险公司数据
            for (CmnTradeRouteShow k : cmnTradeRouteList) {
                if (k.getParentRouteId() == null) {
                    OrdTradeRoute ordTradeRoute = new OrdTradeRoute();
                    ordTradeRoute.setOrderId(ordOrder.getOrderId());
                    ordTradeRoute.setSeqNum(k.getSeqNum());
                    ordTradeRoute.setCompanyId(k.getCompanyId());
                    ordTradeRoute.setCompanyType(k.getCompanyType());
                    ordTradeRoute.setSupplierType("INSURANCE");
                    ordTradeRoute.set__status("ADD");
                    ordTradeRoute.setChannelContractId(k.getChannelContractId());
                    ordTradeRoute = ordTradeRouteService.insertSelective(request, ordTradeRoute);

                    maxSeqNum = k.getSeqNum();
                    channelLevel = 0L;
                    generateOrderTradeRoute(request, cmnTradeRouteList, ordOrder, channelCount, k.getRouteId(),
                            ordTradeRoute.getTradeRouteId());

                }
            }
        } else {
            // 递归生成其他数据
            Long supplierCount = maxSeqNum - channelCount;
            Long lastSeqNum = maxSeqNum - seqNum1;
            for (CmnTradeRouteShow k : cmnTradeRouteList) {
                if (cmnParentRouteId.equals(k.getParentRouteId())) {
                    OrdTradeRoute ordTradeRoute = new OrdTradeRoute();
                    ordTradeRoute.setOrderId(ordOrder.getOrderId());
                    ordTradeRoute.setSeqNum(k.getSeqNum());
                    ordTradeRoute.setCompanyId(k.getCompanyId());
                    ordTradeRoute.setCompanyType(k.getCompanyType());
                    ordTradeRoute.setChannelLevel(null);
                    if ("CHANNEL".equals(k.getCompanyType())) {
                        ordTradeRoute.setSupplierType(null);
                        channelLevel = channelLevel + 1L;
                        ordTradeRoute.setChannelLevel(channelLevel);
                    } else if (k.getSeqNum().longValue() == firstSupplierSeq.longValue()) {
                        ordTradeRoute.setSupplierType("OWN");
                    } else if (supplierCount > seqNum2 && k.getSeqNum().longValue() == secSupplierSeq.longValue()) {
                        ordTradeRoute.setSupplierType("RESERVE");
                    } else if (supplierCount > seqNum1 && k.getSeqNum().longValue() == lastSeqNum.longValue()) {
                        ordTradeRoute.setSupplierType("SIGN");
                    } else
                    if (supplierCount > seqNum4 && k.getSeqNum() > secSupplierSeq && k.getSeqNum() < lastSeqNum) {
                        ordTradeRoute.setSupplierType("MIDDLE");
                    }
                    ordTradeRoute.setParentRouteId(parentRouteId);
                    ordTradeRoute.set__status("ADD");

                    ordTradeRoute.setChannelContractId(k.getChannelContractId());
                    ordTradeRoute = ordTradeRouteService.insertSelective(request, ordTradeRoute);

                    if (k.getSeqNum() > seqNum1) {
                        generateOrderTradeRoute(request, cmnTradeRouteList, ordOrder, channelCount, k.getRouteId(),
                                ordTradeRoute.getTradeRouteId());
                    }
                }
            }
        }
    }

    // 通过交易路线，插入OrdCommission
    private OrdCommission insertOrderCommission(IRequest request, OrdCommission ordCommission, BigDecimal amount,
                                                CmnTradeRouteShow k) {

        ordCommission.setCompanyId(k.getCompanyId());
        ordCommission.setCompanyType(k.getCompanyType());
        ordCommission.setSeqNum(k.getSeqNum());
        ordCommission.setCurrency(k.getCurrency());

        // 佣金率
        ordCommission.setTheFirstYear(k.getTheFirstYear());
        ordCommission.setTheSecondYear(k.getTheSecondYear());
        ordCommission.setTheThirdYear(k.getTheThirdYear());
        ordCommission.setTheFourthYear(k.getTheFourthYear());
        ordCommission.setTheFifthYear(k.getTheFifthYear());
        ordCommission.setTheSixthYear(k.getTheSixthYear());
        ordCommission.setTheSeventhYear(k.getTheSeventhYear());
        ordCommission.setTheEightYear(k.getTheEightYear());
        ordCommission.setTheNinthYear(k.getTheNinthYear());
        ordCommission.setTheTenthYear(k.getTheTenthYear());

        // 应派
        ordCommission.setFirstYearAmount(amount.multiply(k.getTheFirstYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
        ordCommission.setSecondYearAmount(amount.multiply(k.getTheSecondYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
        ordCommission.setThirdYearAmount(amount.multiply(k.getTheThirdYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
        ordCommission.setFourthYearAmount(amount.multiply(k.getTheFourthYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
        ordCommission.setFifthYearAmount(amount.multiply(k.getTheFifthYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
        ordCommission.setSixthYearAmount(amount.multiply(k.getTheSixthYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
        ordCommission
                .setSeventhYearAmount(amount.multiply(k.getTheSeventhYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
        ordCommission.setEightYearAmount(amount.multiply(k.getTheEightYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
        ordCommission.setNinthYearAmount(amount.multiply(k.getTheNinthYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
        ordCommission.setTenthYearAmount(amount.multiply(k.getTheTenthYear()).setScale(2, BigDecimal.ROUND_HALF_UP));

        // 渠道合约
        ordCommission.setChannelContractId(k.getChannelContractId());

        ordCommission = ordCommissionService.insertSelective(request, ordCommission);
        return ordCommission;
    }


    private void generateOrderCommission(IRequest request, List<CmnTradeRouteShow> cmnTradeRouteList, OrdOrder ordOrder,
                                         String manualFlag) {

        // 删除未手工修改的订单佣金
        OrdCommission oldCommission = new OrdCommission();
        oldCommission.setOrderId(ordOrder.getOrderId());
        oldCommission.setManualFlag(manualFlag);
        List<OrdCommission> cList = ordCommissionMapper.queryOrdCommissionAll(oldCommission);
        ordCommissionService.batchDelete(cList);

        OrdCommission ordCommission = new OrdCommission();
        ordCommission.setOrderId(ordOrder.getOrderId());

        OrdCommission queryCom = new OrdCommission();
        queryCom.setOrderId(ordOrder.getOrderId());

        // 获取附加险
        OrdAddition ordAddition = new OrdAddition();
        ordAddition.setOrderId(ordOrder.getOrderId());
        List<OrdAddition> oaList = ordAdditionMapper.queryNotAddition(ordAddition);
        
        String dealPath = "";

        // 循环交易路线
        for (CmnTradeRouteShow k : cmnTradeRouteList) {
            ordCommission.setItemId(ordOrder.getItemId());
            ordCommission.setAdditionId(null);
            dealPath = k.getDealPath();

            // 排除保险公司
            if (k.getCompanyCommissionLineId() != null) {

                // 查询是否已有手工修改的佣金
                queryCom.setManualFlag("Y");
                queryCom.setCompanyId(k.getCompanyId());
                queryCom.setCompanyType(k.getCompanyType());
                queryCom.setSeqNum(k.getSeqNum());
                queryCom.setItemId(ordOrder.getItemId());
                queryCom.setAdditionId(null);
                List<OrdCommission> queryComList = ordCommissionMapper.queryOrdCommissionAll(queryCom);

                // 没有，则新增佣金
                if (CollectionUtils.isEmpty(queryComList)) {

                    BigDecimal amount = new BigDecimal(0);
                    // 保单使用年缴保费，其他使用金额
                    if ("INSURANCE".equals(ordOrder.getOrderType())) {
                        amount = ordOrder.getYearPayAmount();
                    } else {
                        amount = ordOrder.getPolicyAmount();
                    }
                    ordCommission = insertOrderCommission(request, ordCommission, amount, k);
                }

                // 新增附加险交易路线
                for (OrdAddition o : oaList) {
                	o.setDependMainFlag(getDependMainFlag(o,ordOrder));
                    // 附加险跟随主险
                    if ("Y".equals(o.getDependMainFlag())) {
                        ordCommission.setItemId(o.getItemId());
                        ordCommission.setAdditionId(o.getAdditionId());

                        // 查询是否已有手工修改的佣金
                        queryCom.setManualFlag("Y");
                        queryCom.setCompanyId(k.getCompanyId());
                        queryCom.setCompanyType(k.getCompanyType());
                        queryCom.setSeqNum(k.getSeqNum());
                        queryCom.setItemId(o.getItemId());
                        queryCom.setAdditionId(o.getAdditionId());
                        List<OrdCommission> queryComList1 = ordCommissionMapper.queryOrdCommissionAll(queryCom);

                        // 没有，则新增佣金
                        if (CollectionUtils.isEmpty(queryComList1)) {

                            BigDecimal amount = new BigDecimal(0);
                            // 保单使用年缴保费，其他使用金额
                            if ("INSURANCE".equals(ordOrder.getOrderType())) {
                                amount = o.getYearPayAmount();
                            } else {
                                amount = o.getPolicyAmount();
                            }
                            ordCommission = insertOrderCommission(request, ordCommission, amount, k);
                        }
                    }
                }

            }
        }

        // 根据保险公司计算年龄
        int age = 0;
        try {
            age = getAgeByOrder(ordOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (OrdAddition o : oaList) {
        	o.setDependMainFlag(getDependMainFlag(o,ordOrder));
            // 附加险不跟随主险
            if (!"Y".equals(o.getDependMainFlag())) {

                // 查找附加险交易路线
                CmnChannelCommission cmnChannelCommission = new CmnChannelCommission();
                cmnChannelCommission.setItemId(o.getItemId());
                cmnChannelCommission.setChannelId(ordOrder.getChannelId());
                cmnChannelCommission.setPolicyholdersAge(Long.valueOf(age));
                cmnChannelCommission.setDealPath(dealPath);
                cmnChannelCommission.setEffectiveDate(ordOrder.getReserveDate());
                cmnChannelCommission.setCurrency(ordOrder.getCurrency());
                cmnChannelCommission.setPayMethod(ordOrder.getPayMethod());
                cmnChannelCommission.setContributionPeriod(o.getSublineItemName());
                List<CmnChannelCommission> cmnChannelCommissions = cmnChannelCommissionMapper
                        .selectShowAllFields(cmnChannelCommission);

                // 循环佣金
                for (CmnChannelCommission c : cmnChannelCommissions) {
                    CmnTradeRouteShow cmnTradeRoute = new CmnTradeRouteShow();
                    cmnTradeRoute.setChannelCommissionLineId(c.getLineId());
                    List<CmnTradeRouteShow> additionRouteList = cmnTradeRouteShowMapper
                            .queryCmnTradeRoute(cmnTradeRoute);

                    // 循环交易路线
                    for (CmnTradeRouteShow k : additionRouteList) {
                        // 排除保险公司
                        if (k.getCompanyCommissionLineId() != null) {

                            // 查询是否已有手工修改的佣金
                            queryCom.setManualFlag("Y");
                            queryCom.setCompanyId(k.getCompanyId());
                            queryCom.setCompanyType(k.getCompanyType());
                            queryCom.setSeqNum(k.getSeqNum());
                            queryCom.setItemId(o.getItemId());
                            queryCom.setAdditionId(o.getAdditionId());
                            List<OrdCommission> queryComList1 = ordCommissionMapper.queryOrdCommissionAll(queryCom);

                            ordCommission.setItemId(o.getItemId());
                            ordCommission.setAdditionId(o.getAdditionId());

                            // 没有，则新增佣金
                            if (CollectionUtils.isEmpty(queryComList1)) {

                                BigDecimal amount = new BigDecimal(0);
                                // 保单使用年缴保费，其他使用金额
                                if ("INSURANCE".equals(ordOrder.getOrderType())) {
                                    amount = o.getYearPayAmount();
                                } else {
                                    amount = o.getPolicyAmount();
                                }
                                ordCommission = insertOrderCommission(request, ordCommission, amount, k);
                            }
                        }
                    }

                }

            }
        }

    }

    // 根据订单获取年龄
    public int getAgeByOrder(OrdOrder ordOrder) throws Exception {

        SpeSupplier speSupplier = speSupplierMapper.selectByPrimaryKey(ordOrder.getProductSupplierId());

        int age = 0;
        if ("INSURANCE".equals(ordOrder.getOrderType())) {
            try {
                if (ordOrder.getInsurantBirthDate() != null) {
                    age = CalculateAge.accessAge(ordOrder.getInsurantBirthDate(),
                            speSupplier.getAgeCalculateStandard());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return age;
    }

    /****
     * 根据用户类型，以及对应渠道/供应商 去查询用户
     *
     * @param userType
     * @param paramId
     * @return
     */
    public List<ClbUser> queryUserByParam(String userType, Long paramId) {
        ClbUser user = new ClbUser();
        user.setUserType(userType);
        user.setRelatedPartyId(paramId);
        return clbUserMapper.select(user);
    }

    // 分配处理人
    public OrdOrder allocationPerson(IRequest request, OrdOrder ordOrder) {

        // 获取订单信息
        OrdOrder ordOrder1 = new OrdOrder();
        ordOrder1.setOrderId(ordOrder.getOrderId());
        List<OrdOrder> oList = ordOrderMapper.queryWsOrder(ordOrder1);
        ordOrder1 = oList.get(0);
        // 设置通知参数
        Map sendNoticeMap = new HashMap();
        sendNoticeMap.put("item", ordOrder1.getNoticeItem());
        sendNoticeMap.put("insurant", ordOrder1.getInsurant());
        sendNoticeMap.put("applicant", ordOrder1.getApplicant());
        sendNoticeMap.put("policyNumber", ordOrder1.getPolicyNumber());
        sendNoticeMap.put("orderNumber", ordOrder1.getOrderNumber());
        sendNoticeMap.put("reserveDate", DateUtil.getChineseDateString(ordOrder1.getReserveDate()));
        sendNoticeMap.put("productSupplierName", ordOrder1.getProductSupplierName());
        sendNoticeMap.put("issueDate", DateUtil.getChineseDateString(ordOrder1.getIssueDate()));
        sendNoticeMap.put("dividendPeriod", ordOrder1.getDividendPeriod());
        sendNoticeMap.put("signSupplierName", ordOrder1.getSignSupplierName());
        sendNoticeMap.put("signPerson", ordOrder1.getSignPerson());
        sendNoticeMap.put("channelName", ordOrder1.getChannelName());

        if (ordOrder1.getReserveDate() != null) {
            sendNoticeMap.put("reserveDateStr", DateUtil.getDateStr(ordOrder1.getReserveDate(), "yyyy-MM-dd HH:mm"));
        }
        if (ordOrder1.getEffectiveDate() != null) {
            sendNoticeMap.put("effectiveDate", DateUtil.getDateStr(ordOrder1.getEffectiveDate(), "yyyy-MM-dd"));
        }
        if (ordOrder1.getRenewalDueDate() != null) {
            sendNoticeMap.put("renewalDueDate", DateUtil.getDateStr(ordOrder1.getRenewalDueDate(), "yyyy-MM-dd"));
            sendNoticeMap.put("renewalDueDay", DateUtil.getDateDiff(ordOrder1.getRenewalDueDate(), new Date()));
        }
        sendNoticeMap.put("policyAmount", ordOrder1.getPolicyAmount());

        sendNoticeMap.put("curDate", DateUtil.getDateStr(new Date(), "yyyy-MM-dd HH:mm:ss"));

        // 获取创建人
        Long createBy = ordOrder1.getOrderCreatedBy();
        // 保单发送通知
        if ("INSURANCE".equals(ordOrder1.getOrderType()) && "Y".equals(ordOrder.getHisSmsFlag())) {
            sendNoticeMap.put("reserveArrivalDate", DateUtil.getChineseDateString(ordOrder1.getReserveArrivalDate()));
            sendNoticeMap.put("meetAddress", ordOrder1.getMeetAddress());
            sendNoticeMap.put("contactPhone", ordOrder1.getContactPhone());
            sendSMSService.sendSMSByTemplate(request,
                    ordOrder1.getHkContactPhone() == null ? "" : ordOrder1.getHkContactPhone(),
                    ordOrder1.getHkContactPhoneCode() == null ? "" : ordOrder1.getHkContactPhoneCode(), "BD0022",
                    sendNoticeMap);
        }
        // 保单处理
        if ("INSURANCE".equals(ordOrder1.getOrderType())) {
            SysFunctionAllocationRule rule = new SysFunctionAllocationRule();
            rule.setFunctionCode("ORDER");
            Long channelId = null;
            Long supplierId = null;
            String userType = "CLB_SUPPLIER";// 员工所属
            Long userId = null;
            String flag = "N";
            String columnCode = null;
            String columnValue = null;
            // 判断保单所修改的状态，订单状态OR客服状态OR申请书状态
            if (StringUtils.isNotEmpty(ordOrder.getHisStatus())) {
                columnCode = "ORDER_STATUS";
                columnValue = ordOrder.getHisStatus();
            } else if (StringUtils.isNotEmpty(ordOrder.getHisCustomerStatus())) {
                columnCode = "CONTACT_STATUS";
                columnValue = ordOrder.getHisCustomerStatus();
            } else if (StringUtils.isNotEmpty(ordOrder.getHisApplicationStatus())) {
                columnCode = "APPLICATION_STATUS";
                columnValue = ordOrder.getHisApplicationStatus();
            } else {
                return ordOrder;
            }

            // 获取订单分配处理人规则
            rule.setColumnCode(columnCode);
            rule.setColumnValue(columnValue);
            List<SysFunctionAllocationRule> ruleList = sysFunctionAllocationRuleMapper.selectByDto(rule);
            if (org.springframework.util.CollectionUtils.isEmpty(ruleList)) {
                return ordOrder;
            }
            rule = ruleList.get(0);

            // 如没有设置分配处理人规则，则仅传入渠道ID用户发送通知
            if (rule.getRoleId() == null || rule.getRoleId() <= 0) {
                channelId = ordOrder.getChannelId();
                supplierId = null;
            } else {
                // 设置了规则，根据规则的角色，通过保单对应的一级渠道合约签单角色进行对比，得出userType和userId
                if (ordOrder.getChannelContractId() != null) {
                    CnlChannelContract cnlChannelContract = cnlChannelContractMapper
                            .selectByPrimaryKey(ordOrder.getChannelContractId());
                    if ("Business Staff".equals(rule.getRoleCode())) {
                        userType = cnlChannelContract.getBusinessStaff();
                        userId = cnlChannelContract.getBusinessStaffUserId();
                    } else if ("Supplier & Room Booker".equals(rule.getRoleCode())) {
                        userType = cnlChannelContract.getClbClub();
                        userId = cnlChannelContract.getClbClubUserId();
                    } else if ("Customer Service".equals(rule.getRoleCode())) {
                        userType = cnlChannelContract.getReserveSupplier();
                        userId = cnlChannelContract.getReserveSupplierUserId();
                    } else if ("Fill-out".equals(rule.getRoleCode())) {
                        userType = cnlChannelContract.getSignNotice();
                        userId = cnlChannelContract.getSignNoticeUserId();
                    } else if ("Receptionist".equals(rule.getRoleCode())) {
                        userType = cnlChannelContract.getFill();
                        userId = cnlChannelContract.getFillUserId();
                    } else if ("Site Assistant".equals(rule.getRoleCode())) {
                        userType = cnlChannelContract.getContractSign();
                        userId = cnlChannelContract.getContractSignUserId();
                    } else if ("Site Manager".equals(rule.getRoleCode())) {
                        userType = cnlChannelContract.getSiteFollow();
                        userId = cnlChannelContract.getSiteFollowUserId();
                    } else if ("Pending Staff".equals(rule.getRoleCode())) {
                        userType = cnlChannelContract.getPolicyReview();
                        userId = cnlChannelContract.getPolicyReviewUserId();
                    } else if ("Order Follow-up".equals(rule.getRoleCode())) {
                        userType = cnlChannelContract.getPolicySend();
                        userId = cnlChannelContract.getPolicySendUserId();
                    } else if ("After-sales".equals(rule.getRoleCode())) {
                        userType = cnlChannelContract.getPolicyFollow();
                        userId = cnlChannelContract.getPolicyFollowUserId();
                    }

                    // 根据userType设置渠道及供应商ID
                    if ("CLB_SUPPLIER".equals(userType)) {
                        channelId = null;
                        supplierId = null;
                        flag = "Y";
                    } else if ("CHANNEL".equals(userType)) {
                        channelId = cnlChannelContract.getChannelId();
                        supplierId = null;
                        flag = "Y";
                    } else if ("SUPPLIER".equals(userType)) {
                        channelId = null;
                        supplierId = cnlChannelContract.getPartyId();
                        flag = "Y";
                    } else {
                        return ordOrder;
                    }

                }
            }

            // 如果状态为预审中，则获取业务对接行政
            if ("PRE_APPROVING".equals(ordOrder.getHisStatus())) {
                OrdTradeRoute preUser = new OrdTradeRoute();
                preUser.setOrderId(ordOrder.getOrderId());
                List<OrdTradeRoute> preUserList = ordTradeRouteMapper.queryPreUserId(preUser);
                if (CollectionUtils.isNotEmpty(preUserList)) {
                    userId = preUserList.get(0).getPreUserId();
                }
            }

            // todo
//            Long dealUserId = sysFunctionAllocationRuleService.allocationPerson(request, userType, createBy, userId,
//                    supplierId, channelId, ordOrder.getChannelId(), ordOrder.getApplicantCustomerId(),
//                    ordOrder.getOrderId(), "ORDER", columnCode, columnValue, sendNoticeMap);
            Long dealUserId = sysFunctionAllocationRuleService.allocationPerson2(request, userType, createBy, userId,
                    supplierId, channelId, ordOrder.getChannelId(), ordOrder.getApplicantCustomerId(),
                    ordOrder.getOrderId(), "ORDER", columnCode, columnValue, sendNoticeMap,ordOrder.getWhetherSendMes());
            if (dealUserId != null && dealUserId > 0) {
                if ("ORDER_STATUS".equals(columnCode)) {
                    ordOrder.setOrderDealUserId(dealUserId);
                } else if ("CONTACT_STATUS".equals(columnCode)) {
                    ordOrder.setCustomerDealUserId(dealUserId);
                } else if ("APPLICATION_STATUS".equals(columnCode)) {
                    ordOrder.setApplicationDealUserId(dealUserId);
                }
            }

            // 债券处理
        } else if ("BOND".equals(ordOrder1.getOrderType())) {

            Long userId = null;
            List<ClbUser> userList = queryUserByParam("CHANNEL", ordOrder1.getChannelId());
            if (CollectionUtils.isEmpty(userList)) {
                throw new RuntimeException("查询不到渠道用户,channelId:" + ordOrder1.getChannelId());
            } else {
                userId = userList.get(0).getUserId();
            }

            if ("DATA_APPROVING".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "ZQ", sendNoticeMap);
            } else if ("NEED_REVIEW".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "ZQ0002", sendNoticeMap);
            } else if ("DATA_APPROVED".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "ZQ0003", sendNoticeMap);
            } else if ("RESERVING".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "ZQ0004", sendNoticeMap);
            } else if ("RESERVE_SUCCESS".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "ZQ0005", sendNoticeMap);
            } else if ("PENDING".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "ZQ0006", sendNoticeMap);
            } else if ("WAITING_ISSUE".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "ZQ0007", sendNoticeMap);
            } else if ("ISSUE_SUCCESS".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "ZQ0008", sendNoticeMap);
            } else if ("RESERVE_CANCELLED".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "ZQ0009", sendNoticeMap);
            }

            String columnCode = null;
            String columnValue = null;

            if (StringUtils.isNotEmpty(ordOrder.getHisStatus())) {
                columnCode = "ORD.BOND_STATUS";
                columnValue = ordOrder.getHisStatus();
            } else {
                return ordOrder;
            }

            Long dealUserId = sysFunctionAllocationRuleService.allocationPerson(request, "CLB_SUPPLIER", createBy, null,
                    null, null, ordOrder.getChannelId(), ordOrder.getApplicantCustomerId(), ordOrder.getOrderId(),
                    "BOND", columnCode, columnValue, sendNoticeMap);
            if (dealUserId != null && dealUserId > 0) {
                if ("ORD.BOND_STATUS".equals(columnCode)) {
                    ordOrder.setOrderDealUserId(dealUserId);
                }
            }

            // 增值服务处理
        } else if ("VALUEADD".equals(ordOrder1.getOrderType())) {

            Long userId = null;
            List<ClbUser> userList = queryUserByParam("CHANNEL", ordOrder1.getChannelId());
            if (CollectionUtils.isEmpty(userList)) {
                throw new RuntimeException("查询不到渠道用户,channelId:" + ordOrder1.getChannelId());
            } else {
                userId = userList.get(0).getUserId();
            }

            if ("DATA_APPROVING".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "ZZFW0001", sendNoticeMap);
            } else if ("NEED_REVIEW".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "ZZFW0002", sendNoticeMap);
            } else if ("RESERVE_CANCELLED".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "ZZFW0003", sendNoticeMap);
            } else if ("WAIT_PAY".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "ZZFW0004", sendNoticeMap);
            } else if ("RESERVE_SUCCESS".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "ZZFW0005", sendNoticeMap);
            }

            // 投资移民处理
        } else if ("IMMIGRANT".equals(ordOrder1.getOrderType())) {

            Long userId = null;
            List<ClbUser> userList = queryUserByParam("CHANNEL", ordOrder1.getChannelId());
            if (CollectionUtils.isEmpty(userList)) {
                throw new RuntimeException("查询不到渠道用户,channelId:" + ordOrder1.getChannelId());
            } else {
                userId = userList.get(0).getUserId();
            }

            if ("RESERVING".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "TZYM0001", sendNoticeMap);
            } else if ("RESERVE_CANCELLED".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "TZYM0002", sendNoticeMap);
            } else if ("BUY_SUCCESS".equals(ordOrder.getHisStatus())) {
                ntnNotificationService.sendNotification(request, userId, "TZYM0003", sendNoticeMap);
            }

            String columnCode = null;
            String columnValue = null;

            if (StringUtils.isNotEmpty(ordOrder.getHisStatus())) {
                columnCode = "ORD.IMMIGRANT_STATUS";
                columnValue = ordOrder.getHisStatus();
            } else {
                return ordOrder;
            }

            Long dealUserId = sysFunctionAllocationRuleService.allocationPerson(request, "CLB_SUPPLIER", createBy, null,
                    null, null, ordOrder.getChannelId(), ordOrder.getApplicantCustomerId(), ordOrder.getOrderId(),
                    "INVEST", columnCode, columnValue, sendNoticeMap);
            if (dealUserId != null && dealUserId > 0) {
                if ("ORD.IMMIGRANT_STATUS".equals(columnCode)) {
                    ordOrder.setOrderDealUserId(dealUserId);
                }
            }

        }

        return ordOrder;
    }

    // 增加状态日志
    public OrdOrder addStatusHis(IRequest request, OrdOrder ordOrder) {

        // 判断状态日志字段有值
        if (ordOrder.getHisStatus() != null) {
            // 保险签单时，奖励渠道申请书额度20
            if ("INSURANCE".equals(ordOrder.getOrderType()) && "SIGNED".equals(ordOrder.getHisStatus())) {

                OrdStatusHis ordStatusHis1 = new OrdStatusHis();
                ordStatusHis1.setOrderId(ordOrder.getOrderId());
                ordStatusHis1.setStatus("SIGNED");
                List<OrdStatusHis> ordStatusHiss = ordStatusHisMapper.queryOrdStatusHis(ordStatusHis1);
                if (CollectionUtils.isEmpty(ordStatusHiss)) {
                    ClbUser clbUser = new ClbUser();
                    clbUser.setUserType("CHANNEL");
                    clbUser.setRelatedPartyId(ordOrder.getChannelId());
                    List<ClbUser> clbUsers = clbUserMapper.selectAllFields(clbUser);
                    for (ClbUser c : clbUsers) {
                        Long planQuota = c.getPlanQuota();
                        if (planQuota == null) {
                            planQuota = 0L;
                        }
                        //新增配置文件  增加的计划书额度做成配置文件
                        String addPlanQuota = profileService.getProfileValue(request, "ADD_PLAN_QUOTA");
                        if (null != addPlanQuota && !"".equals(addPlanQuota.trim())) {

                            c.setPlanQuota(planQuota + Long.valueOf(addPlanQuota));
                        }else {
                            //若配置文件为空  则数值增加0
                            c.setPlanQuota(planQuota + 0L);
                        }
                        c.set__status("update");
                    }
                    clbUserService.batchUpdate(request, clbUsers);
                }
            }

            // 增加状态日志
            OrdStatusHis ordStatusHis = new OrdStatusHis();
            ordStatusHis.setStatus(ordOrder.getHisStatus());
            ordStatusHis.setOperatorUserId(request.getUserId());
            ordStatusHis.setDescription(ordOrder.getHisDesc());
            ordStatusHis.setOrderId(ordOrder.getOrderId());
            if ("ARRIVAL".equals(ordOrder.getHisStatus())) {
                ordStatusHis.setStatusDate(ordOrder.getArrivalDate());
            } else if ("LEAVE".equals(ordOrder.getHisStatus())) {
                ordStatusHis.setStatusDate(ordOrder.getLeaveDate());
            } else {
                ordStatusHis.setStatusDate(new Date());
            }
            ordStatusHisService.insertSelective(request, ordStatusHis);

            // 债券更新状态时，如更新为Pending，则创建Pending头
            if ("BOND".equals(ordOrder.getOrderType()) && "PENDING".equals(ordOrder.getHisStatus())) {
                OrdPending ordPending = new OrdPending();
                ordPending.setOrderId(ordOrder.getOrderId());
                List<OrdPending> ordPendingList = ordPendingMapper.select(ordPending);
                if (CollectionUtils.isEmpty(ordPendingList)) {
                    ordPending.setPendingNumber(sequenceService.getSequence("PENDING"));
                    ordPending.setStatus("PENDING");
                    ordPending.setDealPerson(request.getUserId().toString());

                    OrdTemplate ordTemplate = new OrdTemplate();
                    ordTemplate.setApplyClassCode("bond");
                    ordTemplate.setApplyItem("预约跟进");
                    List<OrdTemplate> ordTemplateList = ordTemplateMapper.queryOrdTemplate(ordTemplate);
                    for (OrdTemplate o : ordTemplateList) {
                        ordPending.setTemplateId(o.getTemplateId());
                    }
                    ordPendingService.insertSelective(request, ordPending);
                    ordOrder.setCountPending(1L);
                } else {
                    for (OrdPending p : ordPendingList) {
                        p.setStatus("PENDING");
                        p.set__status("update");
                    }
                    ordPendingService.batchUpdate(request, ordPendingList);
                }
            }
        }
        return ordOrder;
    }

    // 增加续保
    public OrdOrder addRenewal(IRequest request, OrdOrder ordOrder) {

        // 续保记录生成
        // 查询续保数据
        OrdOrderRenewal ordOrderRenewal1 = new OrdOrderRenewal();
        ordOrderRenewal1.setOrderId(ordOrder.getOrderId());
        List<OrdOrderRenewal> ordOrderRenewals1 = ordOrderRenewalMapper.select(ordOrderRenewal1);

        // 根据主险年期获取续保年限
        int renewalYear;
        int year = 0;
        int age = 0;
        if ("终身".equals(ordOrder.getSublineItemName())) {
            year = 100;
        } else if ("整付".equals(ordOrder.getSublineItemName())) {
            year = 1;
        } else if (ordOrder.getSublineItemName().indexOf("至被保人") >= 0) {
            String str = StringUtils.replace(ordOrder.getSublineItemName(), "至被保人", "");
            str = StringUtils.replace(str, "岁", "");
            try {
                age = getAgeByOrder(ordOrder);
                year = Integer.parseInt(str) - age;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                year = Integer.valueOf(ordOrder.getSublineItemName());
            } catch (NumberFormatException e) {
                year = 100;
            }
        }

        // 大于10年则只计算10年
        if (year > 10) {
            year = 10;
        }
        // 根据缴费方式，确定续保期数
        // 1.月缴：期数=年限*12
        // 2.季缴：期数=年限*4
        // 3.半年缴：期数=年限*2
        // 4.其他：期数=年限
        int period = year;
        if ("MP".equals(ordOrder.getPayMethod())) {
            period = year * 12;
        } else if ("QP".equals(ordOrder.getPayMethod())) {
            period = year * 4;
        } else if ("SAP".equals(ordOrder.getPayMethod())) {
            period = year * 2;
        }
        renewalYear = period;

        // 判断是否已经存在续保
        if (CollectionUtils.isEmpty(ordOrderRenewals1)) {

            OrdOrderRenewal ordOrderRenewal = new OrdOrderRenewal();
            Class renewalCla = (Class) ordOrderRenewal.getClass();
            Field[] fs = renewalCla.getDeclaredFields();

            OrdOrderRenewal ordOrderRenewalTotal = new OrdOrderRenewal();

            // 循环期数，使用反射set数据
            for (int i = 2; i <= period; i++) {

                // 生成主险的续保记录
                Date reserveDate;
                if ("MP".equals(ordOrder.getPayMethod())) {
                    reserveDate = DateUtils.addMonths(ordOrder.getFirstPayDate(), i - 1);
                } else if ("QP".equals(ordOrder.getPayMethod())) {
                    reserveDate = DateUtils.addMonths(ordOrder.getFirstPayDate(), (i - 1) * 3);
                } else if ("SAP".equals(ordOrder.getPayMethod())) {
                    reserveDate = DateUtils.addMonths(ordOrder.getFirstPayDate(), (i - 1) * 6);
                } else {
                    reserveDate = DateUtils.addYears(ordOrder.getFirstPayDate(), i - 1);
                }

                Method method = ImportUtil.getMethod(renewalCla, "renewalDate" + i, "set", Date.class);
                if (method != null) {
                    try {
                        method.invoke(ordOrderRenewal, reserveDate);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                Method method2 = ImportUtil.getMethod(renewalCla, "currency" + i, "set", String.class);
                if (method2 != null) {
                    try {
                        method2.invoke(ordOrderRenewal, ordOrder.getCurrency());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                Method method3 = ImportUtil.getMethod(renewalCla, "renewalFlag" + i, "set", String.class);
                if (method3 != null) {
                    try {
                        method3.invoke(ordOrderRenewal, "Y");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                Method method4 = ImportUtil.getMethod(renewalCla, "renewalStatus" + i, "set", String.class);
                if (method4 != null) {
                    try {
                        method4.invoke(ordOrderRenewal, "TORENEWAL");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                if (i == 2) {
                    Method method5 = ImportUtil.getMethod(renewalCla, "totalAmount" + i, "set", BigDecimal.class);
                    if (method5 != null) {
                        try {
                            method5.invoke(ordOrderRenewalTotal, ordOrder.getNextPolicyAmount());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            ordOrderRenewal.setOrderId(ordOrder.getOrderId());
            ordOrderRenewal.setType("ITEM");
            ordOrderRenewal.setItemId(ordOrder.getItemId());

            ordOrderRenewalService.insertSelective(request, ordOrderRenewal);

            // 查询附加险
            OrdAddition ordAddition = new OrdAddition();
            ordAddition.setOrderId(ordOrder.getOrderId());
            List<OrdAddition> ordAdditionList = ordAdditionMapper.queryOrdAddition(ordAddition);

            // 循环附加险
            for (OrdAddition addition : ordAdditionList) {

                // 根据附加险年期获取续保年限
                int addYear = 0;
                if ("终身".equals(addition.getSublineItemName())) {
                    addYear = 100;
                } else if ("整付".equals(addition.getSublineItemName())) {
                    addYear = 1;
                } else if (addition.getSublineItemName().indexOf("至被保人") >= 0) {
                    String str = StringUtils.replace(addition.getSublineItemName(), "至被保人", "");
                    str = StringUtils.replace(str, "岁", "");
                    try {
                        age = getAgeByOrder(ordOrder);
                        addYear = Integer.parseInt(str) - age;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if ("与主险一致".equals(addition.getSublineItemName())) {
                    addYear = year;
                } else {
                    try {
                        addYear = Integer.valueOf(addition.getSublineItemName());
                    } catch (NumberFormatException e) {
                        addYear = 100;
                    }
                }

                // 大于10年则计算10年
                if (addYear > 10) {
                    addYear = 10;
                }

                // 根据缴费方式，确定续保期数
                // 1.月缴：期数=年限*12
                // 2.季缴：期数=年限*4
                // 3.半年缴：期数=年限*2
                // 4.其他：期数=年限
                if ("MP".equals(ordOrder.getPayMethod())) {
                    addYear = addYear * 12;
                } else if ("QP".equals(ordOrder.getPayMethod())) {
                    addYear = addYear * 4;
                } else if ("SAP".equals(ordOrder.getPayMethod())) {
                    addYear = addYear * 2;
                }

                if (addYear > renewalYear) {
                    renewalYear = addYear;
                }

                OrdOrderRenewal ordOrderRenewalAdd = new OrdOrderRenewal();

                // 循环期数，使用反射set数据
                for (int i = 2; i <= addYear; i++) {

                    // 生成附加险的续保记录
                    Date reserveDate;
                    if ("MP".equals(ordOrder.getPayMethod())) {
                        reserveDate = DateUtils.addMonths(ordOrder.getFirstPayDate(), i - 1);
                    } else if ("QP".equals(ordOrder.getPayMethod())) {
                        reserveDate = DateUtils.addMonths(ordOrder.getFirstPayDate(), (i - 1) * 3);
                    } else if ("SAP".equals(ordOrder.getPayMethod())) {
                        reserveDate = DateUtils.addMonths(ordOrder.getFirstPayDate(), (i - 1) * 6);
                    } else {
                        reserveDate = DateUtils.addYears(ordOrder.getFirstPayDate(), i - 1);
                    }

                    Method method = ImportUtil.getMethod(renewalCla, "renewalDate" + i, "set", Date.class);
                    if (method != null) {
                        try {
                            method.invoke(ordOrderRenewalAdd, reserveDate);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    Method method2 = ImportUtil.getMethod(renewalCla, "currency" + i, "set", String.class);
                    if (method2 != null) {
                        try {
                            method2.invoke(ordOrderRenewalAdd, ordOrder.getCurrency());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    Method method3 = ImportUtil.getMethod(renewalCla, "renewalFlag" + i, "set", String.class);
                    if (method3 != null) {
                        try {
                            method3.invoke(ordOrderRenewalAdd, "Y");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    Method method4 = ImportUtil.getMethod(renewalCla, "renewalStatus" + i, "set", String.class);
                    if (method4 != null) {
                        try {
                            method4.invoke(ordOrderRenewalAdd, "TORENEWAL");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                ordOrderRenewalAdd.setOrderId(ordOrder.getOrderId());
                ordOrderRenewalAdd.setType("ITEM");
                ordOrderRenewalAdd.setItemId(addition.getItemId());

                ordOrderRenewalService.insertSelective(request, ordOrderRenewalAdd);
            }

            ordOrderRenewalTotal.setOrderId(ordOrder.getOrderId());
            ordOrderRenewalTotal.setType("TOTAL");
            ordOrderRenewalTotal.setItemId(null);
            ordOrderRenewalService.insertSelective(request, ordOrderRenewalTotal);

        } else {
            // 已存在续保记录，则需要刷新续保总额
            for (OrdOrderRenewal ordOrderRenewal : ordOrderRenewals1) {
                if ("TOTAL".equals(ordOrderRenewal.getType())) {
                    ordOrderRenewal.setTotalAmount2(ordOrder.getNextPolicyAmount());
                }
                ordOrderRenewal.set__status("update");
            }
            ordOrderRenewalService.batchUpdate(request, ordOrderRenewals1);
        }
        ordOrder.setRenewalYear(Long.valueOf(renewalYear));
        return ordOrder;
    }

    /**
     * 订单头行提交
     *
     * @param request
     * @param ordOrders
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<OrdOrder> orderBatchUpdate(IRequest request, List<OrdOrder> ordOrders) {

        //当订单状态修改为已退保/搁置受保/保险公司拒保时，该保单对应的所有Pending的状态自动变更为“已关闭”
        for (OrdOrder op : ordOrders){
            if(op.getStatus().equals("SURRENDERED")||op.getStatus().equals("SUSPENDED")||op.getStatus().equals("DECLINED")){
                OrdPending ordPending = new OrdPending();
                ordPending.setOrderId(op.getOrderId());
                List<OrdPending> ordPendingList = ordPendingMapper.select(ordPending);
                for(OrdPending opl : ordPendingList){
                    opl.setStatus("PENDING_CLOSED");
                    opl.set__status("update");
                    opl.setDealPerson(op.getLastUpdatedBy()+"");
                    opl.setDealEndDate(op.getLastUpdateDate());
                }
                ordPendingService.batchUpdate(request, ordPendingList);
            }
        }

        // 处理订单主体数据
        for (OrdOrder k : ordOrders) {
            if (k.getOrderId() == null || k.getOrderId() == -1) {
                k.setOrderId(null);
                k.setPayPeriods(1L);
                k.setOrderNumber(sequenceService.getSequence("ORDER"));
                if ("INSURANCE".equals(k.getOrderType())) {
                    k.setApplicationStatus("NOT_TRANSCRIBED");
                }
                k.setHisStatus(k.getStatus());
            }

            //业务需求  为空的时候  不显示为0
			/*if (k.getPolicyAmount() == null) {
				k.setPolicyAmount(BigDecimal.valueOf(0));
			}*/


            // Modified by jun.zhao@20170718
            // 需要判断是更新还是插入
            if (k.getOrderId() == null) {
                self().insertSelective(request, k);
            } else {
                //修改订单需要把申请书状态改为'CHANGED'
                OrdOrder ord = self().selectOrdOrder(k.getOrderId());
                if (k.getApplicationStatus().equals(ord.getApplicationStatus())) {
                    k.setApplicationStatus("CHANGED");
                }
                // Modified by Rex.Hua@20170718
                // Bug修复:不能更新为空
                self().updateByPrimaryKey(request, k);
            }
        }
        // this.batchUpdate(request, ordOrders);
        // End@20170718

        for (OrdOrder k : ordOrders) {

            // 提交客户
            List<OrdCustomer> ordCustomers = k.getOrdCustomer();
            if (ordCustomers != null && ordCustomers.size() != 0) {
                for (OrdCustomer ordCustomer : ordCustomers) {
                    ordCustomer.setOrderId(k.getOrderId());
                }
                ordCustomerService.batchSubmit(request, ordCustomers);
            }

            // 提交附加险
            List<OrdAddition> ordAdditions = k.getOrdAddition();
            if (ordAdditions != null && ordAdditions.size() != 0) {
                for (OrdAddition ordAddition : ordAdditions) {
                    ordAddition.setOrderId(k.getOrderId());
                }
                ordAdditionService.batchUpdate(request, ordAdditions);
            }

            // 更新附加险状态
            OrdAddition ordAddition1 = new OrdAddition();
            ordAddition1.setOrderId(k.getOrderId());
            ordAddition1.setStatus(k.getStatus());
            List<OrdAddition> oaList = ordAdditionMapper.queryNotAddition(ordAddition1);
            for (OrdAddition o : oaList) {
                o.setStatus(k.getStatus());
                o.set__status("update");
            }
            ordAdditionService.batchUpdate(request, oaList);

            // 提交佣金
            List<OrdCommission> ordCommissions = k.getOrdCommission();
            if (ordCommissions != null && ordCommissions.size() != 0) {
                // 循环页面佣金数据
                for (OrdCommission ordCommission : ordCommissions) {
                    ordCommission.setOrderId(k.getOrderId());
                    if (ordCommission.getCommissionId() != null) {
                        // 通过主键查找数据库佣金数据
                        OrdCommission ordCommission1 = ordCommissionMapper
                                .selectByPrimaryKey(ordCommission.getCommissionId());
                        // 对比佣金
                        if (!ordCommission1.getTheFirstYear().equals(ordCommission.getTheFirstYear())
                                || !ordCommission1.getTheSecondYear().equals(ordCommission.getTheSecondYear())
                                || !ordCommission1.getTheThirdYear().equals(ordCommission.getTheThirdYear())
                                || !ordCommission1.getTheFourthYear().equals(ordCommission.getTheFourthYear())
                                || !ordCommission1.getTheFifthYear().equals(ordCommission.getTheFifthYear())
                                || !ordCommission1.getTheSixthYear().equals(ordCommission.getTheSixthYear())
                                || !ordCommission1.getTheSeventhYear().equals(ordCommission.getTheSeventhYear())
                                || !ordCommission1.getTheEightYear().equals(ordCommission.getTheEightYear())
                                || !ordCommission1.getTheNinthYear().equals(ordCommission.getTheNinthYear())
                                || !ordCommission1.getTheTenthYear().equals(ordCommission.getTheTenthYear())) {
                            // 打上  是否手动修改的标志
                            ordCommission.setManualFlag("Y");
                        }
                    }
                }
                // 保存佣金
                ordCommissionService.batchUpdate(request, ordCommissions);
            }

            List<CmnTradeRouteShow> cmnTradeRouteList = new ArrayList<>();
            // 新增附加险时，创建佣金信息
            // 判断已存在交易路线
            if (StringUtils.isNotEmpty(k.getDealPath())) {
                int age = 0;
                try {
                    age = getAgeByOrder(k);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 获取主险交易路线
                CmnChannelCommission channelCommission = new CmnChannelCommission();
                channelCommission.setChannelId(k.getChannelId());
                channelCommission.setCurrency(k.getCurrency());
                channelCommission.setContributionPeriod(k.getSublineItemName());
                channelCommission.setPayMethod(k.getPayMethod());
                channelCommission.setItemId(k.getItemId());
                channelCommission.setDealPath(k.getDealPath());
                if ("INSURANCE".equals(k.getOrderType())) {
                    channelCommission.setPolicyholdersAge(Long.valueOf(age));
                    channelCommission.setEffectiveDate(k.getReserveDate());
                } else {
                    channelCommission.setPolicyholdersAge(null);
                    channelCommission.setEffectiveDate(k.getIssueDate());
                }

                List<CmnChannelCommission> cmnList = cmnChannelCommissionMapper.selectShowAllFields(channelCommission);

                // 获取主险佣金
                if (CollectionUtils.isNotEmpty(cmnList)) {
                    CmnTradeRouteShow cmnTradeRoute = new CmnTradeRouteShow();
                    cmnTradeRoute.setChannelCommissionLineId(cmnList.get(0).getLineId());
                    cmnTradeRouteList = cmnTradeRouteShowMapper.queryCmnTradeRoute(cmnTradeRoute);
                }

                OrdCommission ordCommission = new OrdCommission();
                ordCommission.setOrderId(k.getOrderId());

                // 循环附加险
                for (OrdAddition o : oaList) {
                    // 判断附加险佣金是否存在
                    if (o.getComCount() == 0) {
                        ordCommission.setItemId(o.getItemId());
                        ordCommission.setAdditionId(o.getAdditionId());
                        // 判断是否跟随主险
                        o.setDependMainFlag(getDependMainFlag(o,k));
                        if ("Y".equals(o.getDependMainFlag())) {
                            for (CmnTradeRouteShow c : cmnTradeRouteList) {
                                if (c.getCompanyCommissionLineId() != null) {

                                    BigDecimal amount = new BigDecimal(0);
                                    if ("INSURANCE".equals(k.getOrderType())) {
                                        amount = o.getYearPayAmount();
                                    } else {
                                        amount = o.getPolicyAmount();
                                    }
                                    ordCommission = insertOrderCommission(request, ordCommission, amount, c);

                                }

                            }
                        } else {
                            // 查询附加险交易路线
                            CmnChannelCommission cmnChannelCommission = new CmnChannelCommission();
                            cmnChannelCommission.setItemId(o.getItemId());
                            cmnChannelCommission.setChannelId(k.getChannelId());
                            cmnChannelCommission.setPolicyholdersAge(Long.valueOf(age));
                            cmnChannelCommission.setDealPath(k.getDealPath());
                            cmnChannelCommission.setEffectiveDate(k.getReserveDate());
                            cmnChannelCommission.setCurrency(k.getCurrency());
                            List<CmnChannelCommission> cmnChannelCommissions = cmnChannelCommissionMapper
                                    .selectShowAllFields(cmnChannelCommission);

                            for (CmnChannelCommission cc : cmnChannelCommissions) {
                                // 查询附加险佣金
                                CmnTradeRouteShow cmnTradeRoute = new CmnTradeRouteShow();
                                cmnTradeRoute.setChannelCommissionLineId(cc.getLineId());
                                List<CmnTradeRouteShow> additionRouteList = cmnTradeRouteShowMapper
                                        .queryCmnTradeRoute(cmnTradeRoute);

                                // 循环附加险佣金
                                for (CmnTradeRouteShow c : additionRouteList) {
                                    if (c.getCompanyCommissionLineId() != null) {

                                        BigDecimal amount = new BigDecimal(0);
                                        if ("INSURANCE".equals(k.getOrderType())) {
                                            amount = o.getYearPayAmount();
                                        } else {
                                            amount = o.getPolicyAmount();
                                        }
                                        ordCommission = insertOrderCommission(request, ordCommission, amount, c);

                                    }
                                }

                            }
                        }
                    }
                }

            }

            // 提交受益人,家庭状况
            List<OrdBeneficiary> ordBeneficiaries = k.getOrdBeneficiary();
            if (ordBeneficiaries != null && ordBeneficiaries.size() != 0) {
                for (OrdBeneficiary ordBeneficiary : ordBeneficiaries) {
                    ordBeneficiary.setOrderId(k.getOrderId());
                }
                ordBeneficiaryService.batchUpdate(request, ordBeneficiaries);
            }
            // 提交客户教育经历
            List<OrdCstEducation> ordCstEducations = k.getOrdCstEducation();
            if (null != ordCstEducations && !ordCstEducations.isEmpty()) {
                for (OrdCstEducation education : ordCstEducations) {
                    education.setOrderId(k.getOrderId());
                }
                ordCstEducationService.batchUpdate(request, ordCstEducations);
            }
            // 提交客户技能特长
            List<OrdCstSkill> ordCstSkills = k.getOrdCstSkill();
            if (null != ordCstSkills && !ordCstSkills.isEmpty()) {
                for (OrdCstSkill skill : ordCstSkills) {
                    skill.setOrderId(k.getOrderId());
                }
                ordCstSkillService.batchUpdate(request, ordCstSkills);
            }
            // 提交客户工作经历
            List<OrdCstWork> ordCstWorks = k.getOrdCstWork();
            if (null != ordCstWorks && !ordCstWorks.isEmpty()) {
                for (OrdCstWork work : ordCstWorks) {
                    work.setOrderId(k.getOrderId());
                }
                ordCstWorkService.batchUpdate(request, ordCstWorks);
            }
            // 删除附件
            if (k.getOldPlanFileId() != null) {
                Long fileId = k.getOldPlanFileId();
                SysFile file = sysFileService.selectByPrimaryKey(request, fileId);
                if (file != null) {
                    sysFileService.delete(request, file);
                }
            }
            // 删除附件
            if (k.getOldReqFileId() != null) {
                Long fileId = k.getOldReqFileId();
                SysFile file = sysFileService.selectByPrimaryKey(request, fileId);
                if (file != null) {
                    sysFileService.delete(request, file);
                }
            }
            // 删除附件
            if (k.getOldContractFileId() != null) {
                Long fileId = k.getOldContractFileId();
                SysFile file = sysFileService.selectByPrimaryKey(request, fileId);
                if (file != null) {
                    sysFileService.delete(request, file);
                }
            }

            // 提交订单附件
            List<Long> fileIdList = k.getFileIds();
            if (fileIdList != null && fileIdList.size() != 0) {
                OrdFile ordFile = new OrdFile();
                ordFile.setOrderId(k.getOrderId());
                List<OrdFile> ofList = ordFileMapper.select(ordFile);
                List<OrdFile> ordFiles = new ArrayList<OrdFile>();

                for (int i = 1; i < fileIdList.size(); i++) {
                    if (fileIdList.get(i) != 0L) {
                        String flag = "N";
                        for (OrdFile of : ofList) {
                            if (Long.valueOf(i).equals(of.getFileSeq())) {
                                if (!fileIdList.get(i).equals(of.getFileId())) {
                                    of.setFileId(fileIdList.get(i));
                                    ordFiles.add(of);
                                }

                                flag = "Y";
                            }
                        }
                        if ("N".equals(flag)) {
                            ordFile = new OrdFile();
                            ordFile.setOrderId(k.getOrderId());
                            ordFile.setFileId(fileIdList.get(i));
                            ordFile.setFileSeq(Long.valueOf(i));
                            ordFiles.add(ordFile);
                        }
                    }
                }

                for (OrdFile of1 : ordFiles) {
                    if (of1.getOrdFileId() != null) {
                        ordFileService.updateByPrimaryKey(request, of1);
                    } else {
                        ordFileService.insertSelective(request, of1);
                    }
                }
            }

            // 刷新佣金金额字段
            OrdCommission ordCommission = new OrdCommission();
            ordCommission.setOrderId(k.getOrderId());
            List<OrdCommission> ordComs = ordCommissionMapper.queryOrdCommissionAll(ordCommission);
            for (OrdCommission c : ordComs) {
                c.setFirstYearAmount(
                        c.getYearPayAmount().multiply(c.getTheFirstYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
                c.setSecondYearAmount(
                        c.getYearPayAmount().multiply(c.getTheSecondYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
                c.setThirdYearAmount(
                        c.getYearPayAmount().multiply(c.getTheThirdYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
                c.setFourthYearAmount(
                        c.getYearPayAmount().multiply(c.getTheFourthYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
                c.setFifthYearAmount(
                        c.getYearPayAmount().multiply(c.getTheFifthYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
                c.setSixthYearAmount(
                        c.getYearPayAmount().multiply(c.getTheSixthYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
                c.setSeventhYearAmount(
                        c.getYearPayAmount().multiply(c.getTheSeventhYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
                c.setEightYearAmount(
                        c.getYearPayAmount().multiply(c.getTheEightYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
                c.setNinthYearAmount(
                        c.getYearPayAmount().multiply(c.getTheNinthYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
                c.setTenthYearAmount(
                        c.getYearPayAmount().multiply(c.getTheTenthYear()).setScale(2, BigDecimal.ROUND_HALF_UP));
                c.set__status("update");
            }
            ordCommissionService.batchUpdate(request, ordComs);

            Long roleUserId = null;

            // 计算交易路线
            if (k.getChannelCommissionLineId() != null) {

                // 预约供应商、签单供应商、一级渠道合约、交易路线dealPath、所属供应商
                if (k.getReserveSupplierId() == null) {
                    CmnChannelCommission cmnChannelCommission = new CmnChannelCommission();
                    cmnChannelCommission.setLineId(k.getChannelCommissionLineId());
                    List<CmnChannelCommission> cmnChannelCommissions = ordOrderMapper
                            .queryTradeRoute(cmnChannelCommission);
                    for (CmnChannelCommission c : cmnChannelCommissions) {
                        k.setReserveSupplierId(c.getReserveSupplierId());
                        k.setSignSupplierId(c.getSignSupplierId());
                        k.setChannelContractId(c.getChannelContractId());
                        k.setDealPath(c.getDealPath());
                        k.setOwnSupplierId(c.getOwnSupplierId());
                    }
                }

                // 介绍人、介绍费
                CnlContractRole cnlContractRole = new CnlContractRole();
                cnlContractRole.setChannelCommissionLineId(k.getChannelCommissionLineId());
                List<CnlContractRole> roleList = cnlContractRoleMapper.queryIntroducer(cnlContractRole);
                for (CnlContractRole r : roleList) {
                    k.setIntroducer(r.getRoleUserId().toString());
                    k.setIntroduceBenefit(r.getBenefit());
                }

                // 签单费、刷卡费
                if (k.getSignSupplierId() != null) {
                    SpeSupplierSettle speSupplierSettle = new SpeSupplierSettle();
                    speSupplierSettle.setSupplierId(k.getSignSupplierId());
                    speSupplierSettle.setCostName("签单费");
                    List<SpeSupplierSettle> sList = ordOrderMapper.querySpeSupplierSettle(speSupplierSettle);

                    for (SpeSupplierSettle s : sList) {
                        k.setSignFee(s.getAmount());
                    }
                }

                // 业务行政
                if (k.getChannelContractId() != null) {
                    CnlChannelContract cnlChannelContract = cnlChannelContractMapper
                            .selectByPrimaryKey(k.getChannelContractId());
                    k.setContractRoleId(cnlChannelContract.getBusinessStaffUserId());
                }

                // 获取佣金交易路线
                CmnTradeRouteShow cmnTradeRoute = new CmnTradeRouteShow();
                cmnTradeRoute.setChannelCommissionLineId(k.getChannelCommissionLineId());
                List<CmnTradeRouteShow> cmnTradeRouteList1 = cmnTradeRouteShowMapper.queryCmnTradeRoute(cmnTradeRoute);

                Long channelCount = 0L;
                Long supplierCount = 0L;

                OrdTradeRouteShow ordTradeRouteShow = new OrdTradeRouteShow();
                Class routeCla = (Class) ordTradeRouteShow.getClass();
                Field[] fs = routeCla.getDeclaredFields();

                int channelSeq = 0;
                int supplierSeq = 0;
                String companyFlag = "N";

                // 刷新OrdTradeRouteShow
                for (CmnTradeRouteShow c : cmnTradeRouteList1) {
                    if ("CHANNEL".equals(c.getCompanyType())) {
                        if ("COMPANY".equals(c.getChannelTypeCode()) && "N".equals(companyFlag)) {
                            companyFlag = "Y";
                            k.setCompanyChannelId(c.getCompanyId());
                        }
                        channelCount = channelCount + 1L;
                        channelSeq = channelSeq + 1;
                        CnlChannel cnlChannel = cnlChannelMapper.selectByPrimaryKey(c.getCompanyId());
                        Method method = ImportUtil.getMethod(routeCla, "channelId" + channelSeq, "set", Long.class);
                        if (method != null) {
                            try {
                                method.invoke(ordTradeRouteShow, c.getCompanyId());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        method = ImportUtil.getMethod(routeCla, "channelName" + channelSeq, "set", String.class);
                        if (method != null) {
                            try {
                                method.invoke(ordTradeRouteShow, cnlChannel.getChannelName());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        supplierCount = supplierCount + 1L;
                        supplierSeq = supplierSeq + 1;
                        SpeSupplier speSupplier = speSupplierMapper.selectByPrimaryKey(c.getCompanyId());
                        Method method = ImportUtil.getMethod(routeCla, "supplierId" + supplierSeq, "set", Long.class);
                        if (method != null) {
                            try {
                                method.invoke(ordTradeRouteShow, c.getCompanyId());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        method = ImportUtil.getMethod(routeCla, "supplierName" + supplierSeq, "set", String.class);
                        if (method != null) {
                            try {
                                method.invoke(ordTradeRouteShow, speSupplier.getName());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
                ordTradeRouteShow.setOrderId(k.getOrderId());
                ordTradeRouteShow.setChannelCount(channelCount);
                ordTradeRouteShow.setSupplierCount(supplierCount);

                OrdTradeRouteShow oldTradeRouteShow = new OrdTradeRouteShow();
                oldTradeRouteShow.setOrderId(k.getOrderId());
                List<OrdTradeRouteShow> osList = ordTradeRouteShowMapper.select(oldTradeRouteShow);
                ordTradeRouteShowService.batchDelete(osList);
                ordTradeRouteShowService.insertSelective(request, ordTradeRouteShow);

                // 生成OrdTradeRoute
                generateOrderTradeRoute(request, cmnTradeRouteList1, k, channelCount, null, null);

                // 生成
                generateOrderCommission(request, cmnTradeRouteList1, k, null);

            }

            OrdTradeRoute preUser = new OrdTradeRoute();
            preUser.setOrderId(k.getOrderId());
            List<OrdTradeRoute> preUserList = ordTradeRouteMapper.queryPreUserId(preUser);
            if (CollectionUtils.isNotEmpty(preUserList)) {
                roleUserId = preUserList.get(0).getPreUserId();
            }
            // 预审
            if ("DATA_APPROVING".equals(k.getHisStatus()) && roleUserId != null
                    && "INSURANCE".equals(k.getOrderType())) {
                k.setStatus("PRE_APPROVING");
                k.setHisStatus("PRE_APPROVING");
                k.setOrderDealUserId(roleUserId);
            }

            // 当备注信息不为空时，需要添加状态历史
            if (StringUtils.isNotEmpty(k.getDescription())) {
                k.setHisStatus(k.getStatus());
                k.setHisDesc(k.getDescription());
            }
            // 新增状态历史
            if (k.getHisStatus() != null) {
                // 增加历史记录
                k = addStatusHis(request, k);
            }

            List<OrdOrderRenewal> ordOrderRenewals = k.getOrdOrderRenewal();
            if (ordOrderRenewals != null && ordOrderRenewals.size() != 0) {
                for (OrdOrderRenewal ordOrderRenewal : ordOrderRenewals) {
                    ordOrderRenewal.setOrderId(k.getOrderId());
                }
                ordOrderRenewalService.batchUpdate(request, ordOrderRenewals);
            }

            if ("POLICY_EFFECTIVE".equals(k.getHisStatus()) && !"FJ".equals(k.getPayMethod())
                    && !"WP".equals(k.getPayMethod()) && k.getNextPolicyAmount() != null
                    && k.getFirstPayDate() != null) {

                // 生成续保信息
                addRenewal(request, k);

                OrdPending ordPending = new OrdPending();
                ordPending.setOrderId(k.getOrderId());
                List<OrdPending> ordPendings = ordPendingMapper.queryNotClosed(ordPending);
                for (OrdPending op : ordPendings) {
                    op.setStatus("PENDING_CLOSED");
                    op.set__status("update");
                }
                ordPendingService.batchUpdate(request, ordPendings);
                // 重新刷新佣金
                //generateOrderCommission(request, cmnTradeRouteList, k, "N");
            }
            if("SIGNED".equals(k.getHisStatus()) && !"FJ".equals(k.getPayMethod())
                    && !"WP".equals(k.getPayMethod()) && k.getNextPolicyAmount() != null
                    && k.getFirstPayDate() != null){
                // 重新刷新佣金
                generateOrderCommission(request, cmnTradeRouteList, k, "N");
            }
            
            List<OrdTeamVisitor> ordTeamVisitors = k.getOrdTeamVisitor();
            if (ordTeamVisitors != null && ordTeamVisitors.size() != 0) {
                for (OrdTeamVisitor ordTeamVisitor : ordTeamVisitors) {
                    ordTeamVisitor.setOrderId(k.getOrderId());
                }
                ordTeamVisitorService.batchUpdate(request, ordTeamVisitors);
            }

            // 调用分配处理人接口
            // --------查询通知模板的信息
            k = allocationPerson(request, k);

            k.setHisStatus(null);
            k.setHisDesc(null);
            k.setHisCustomerStatus(null);
            k.setHisApplicationStatus(null);
            k.setHisSmsFlag(null);

            self().updateByPrimaryKey(request, k);
        }
        return ordOrders;
    }

    /**
     * 用户查询
     *
     * @param request
     * @param clbUser
     * @return
     */
    @Override
    public List<ClbUser> queryUser(IRequest request, ClbUser clbUser) {
        return ordOrderMapper.queryUser(clbUser);
    }

    /**
     * 字段查询
     *
     * @param request
     * @param sysOrderCfgField
     * @return
     */
    @Override
    public List<SysOrderCfgField> queryField(IRequest request, SysOrderCfgField sysOrderCfgField) {
        return ordOrderMapper.queryField(sysOrderCfgField);
    }

    /**
     * 操作查询
     *
     * @param request
     * @param sysOrderCfgOperation
     * @return
     */
    @Override
    public List<SysOrderCfgOperation> queryOpera(IRequest request, SysOrderCfgOperation sysOrderCfgOperation) {
        return ordOrderMapper.queryOpera(sysOrderCfgOperation);
    }

    /**
     * 操作查询
     *
     * @param request
     * @param cmnSupplierRelation
     * @return
     */
    // @Override
    // public List<CmnSupplierRelation> querySupplierRelation(IRequest request,
    // CmnSupplierRelation cmnSupplierRelation) {
    // return ordOrderMapper.querySupplierRelation(cmnSupplierRelation);
    // }

    /**
     * 交易路线查询
     *
     * @param request
     * @param cmnChannelCommission
     * @return
     */
    @Override
    public List<CmnChannelCommission> queryTradeRoute(IRequest request, CmnChannelCommission cmnChannelCommission) {
        return ordOrderMapper.queryTradeRoute(cmnChannelCommission);
    }

    /**
     * 订单交易路线查询
     *
     * @param request
     * @param ordOrder
     * @return
     */
    @Override
    public List<OrdOrder> queryOrdTradeRoute(IRequest request, OrdOrder ordOrder) {
        return ordOrderMapper.queryOrdTradeRoute(ordOrder);
    }

    /**
     * 投资移民订单查询 daiqian.shi@hand-china.com
     *
     * @param request
     * @param ordOrder
     * @return
     */
    @Override
    public List<OrdOrder> queryImmigrantOrder(IRequest request, OrdOrder ordOrder, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return ordOrderMapper.queryImmigrantOrder(ordOrder);
    }

    /**
     * 投资移民订单查询 jun.zhao@hand-china.com
     *
     * @param request
     * @param ordOrder
     * @return
     */
    @Override
    public List<OrdOrder> queryBondOrder(IRequest request, OrdOrder ordOrder, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<OrdOrder> oList = ordOrderMapper.queryBondOrder(ordOrder);
        for (OrdOrder k : oList) {
            if (k.getContractFileId() != null) {
                SysFile sysFile = sysFileService.selectByPrimaryKey(request, k.getContractFileId());
                if (sysFile != null) {
                    k.setContractToken(sysFile.get_token());
                } else {
                    k.setContractToken(null);
                }

            }

            // 查询订单下面的pending数量
            if (null != k.getOrderId()) {
                Long countPending = ordPendingService.countPendingByOrderId(k.getOrderId());
                k.setCountPending(countPending);
            }
        }
        return oList;
    }

    /**
     * 各角色用户查询
     *
     * @param request
     * @param clbUser
     * @return
     */
    @Override
    public List<ClbUser> queryRole(IRequest request, ClbUser clbUser) {
        return ordOrderMapper.queryRole(clbUser);
    }

    /**
     * 查询用户及角色
     *
     * @param request
     * @param clbUser
     * @return
     */
    @Override
    public List<ClbUser> queryUserRole(IRequest request, ClbUser clbUser) {
        return ordOrderMapper.queryUserRole(clbUser);
    }

    /**
     * 地点查询
     *
     * @param request
     * @param speSupplierLine
     * @return
     */
    @Override
    public List<SpeSupplierLine> queryAddress(IRequest request, SpeSupplierLine speSupplierLine) {
        return ordOrderMapper.queryAddress(speSupplierLine);
    }

    /**
     * 联络人查询
     *
     * @param request
     * @param speSupplierLine
     * @return
     */
    @Override
    public List<SpeSupplierLine> queryContactPerson(IRequest request, SpeSupplierLine speSupplierLine) {
        return ordOrderMapper.queryContactPerson(speSupplierLine);
    }

    /**
     * 电话查询
     *
     * @param request
     * @param speSupplierLine
     * @return
     */
    @Override
    public List<SpeSupplierLine> queryContactPhone(IRequest request, SpeSupplierLine speSupplierLine) {
        return ordOrderMapper.queryContactPhone(speSupplierLine);
    }

    /**
     * 订单状态更新
     *
     * @param request
     * @param ordOrders
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<OrdOrder> orderStatusUpdate(IRequest request, List<OrdOrder> ordOrders) {
        for (OrdOrder k : ordOrders) {
            OrdOrder ordOrder = ordOrderMapper.selectByPrimaryKey(k.getOrderId());
            ordOrder.setStatus(k.getStatus());
            ordOrder.set__status("update");
            self().updateByPrimaryKey(request, ordOrder);
        }

        for (OrdOrder k : ordOrders) {
            k.setHisStatus(k.getStatus());

            // 增加历史记录
            k = addStatusHis(request, k);

            // 调用分配处理人接口
            // --------查询通知模板的信息
            k = allocationPerson(request, k);

            k.setHisStatus(null);
            k.setHisDesc(null);
            k.setHisCustomerStatus(null);
            k.setHisApplicationStatus(null);

        }
        return ordOrders;
    }

    /**
     * 订单状态更新
     *
     * @param request
     * @param ordOrder
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public OrdOrder updateStatus(IRequest request, OrdOrder ordOrder) {
        self().updateByPrimaryKeySelective(request, ordOrder);

        ordOrder.setHisStatus(ordOrder.getStatus());

        // 增加历史记录
        ordOrder = addStatusHis(request, ordOrder);

        // 调用分配处理人接口
        // --------查询通知模板的信息
        ordOrder = allocationPerson(request, ordOrder);

        ordOrder.setHisStatus(null);
        ordOrder.setHisDesc(null);
        ordOrder.setHisCustomerStatus(null);
        ordOrder.setHisApplicationStatus(null);

        return ordOrder;
    }

    /**
     * 交易路线查询
     *
     * @param request
     * @param cmnChannelCommission
     * @return
     */
    @Override
    public List<CmnChannelCommission> validateTradeRoute(IRequest request, CmnChannelCommission cmnChannelCommission) {
        return ordOrderMapper.validateTradeRoute(cmnChannelCommission);
    }

    @Override
    public List<PrdItems> queryItemByClass(PrdItems prdItems) {
        List<PrdItems> prdItemsList = new ArrayList<PrdItems>();
        prdItemsList = prdItemsMapper.selectByParam(prdItems);
        return prdItemsList;
    }

    /**
     * 导出
     */
    @Override
    public void exportPDF(HttpServletRequest request, HttpServletResponse response, String orderId,
                          IRequest requestContext) throws Exception {
        response.reset();
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        try {
            // 获取模板文件路径
            String pdfTemplate = request.getSession().getServletContext().getRealPath("/");
            pdfTemplate = pdfTemplate + "/template/大都会合同.pdf";// Excel模板所在的路径。
            // 解析pdf
            PdfReader reader = new PdfReader(pdfTemplate);
            PdfStamper stamper = new PdfStamper(reader, ba);
            BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            AcroFields form = stamper.getAcroFields();
            form.addSubstitutionFont(bf);
            // 所有订单信息
            Map<String, String> formMap = null;
            // 将所有的订单数据通过反射存入map中
            OrdOrder ordOrder = new OrdOrder();
            ordOrder = self().selectOrdOrder(Long.valueOf(orderId));
            // ordOrder.setApplicantPhone(AESUtil.decrypt("CLB",
            // ordOrder.getApplicantPhone()));
            // ordOrder.setInsurantPhone(AESUtil.decrypt("CLB",
            // ordOrder.getInsurantPhone()));
            // 查找投保人和受保人
            OrdCustomer ordCustomer = new OrdCustomer();
            ordCustomer.setOrderId(Long.valueOf(orderId));
            ordCustomer.setCustomerType("APPLICANT"); // 投保人
            OrdCustomer applicantCustomer = ordCustomerService.queryOrdCustomer(requestContext, ordCustomer, 1, 10)
                    .get(0);
            ordCustomer.setCustomerType("INSURANT"); // 受保人
            OrdCustomer insuredCustomer = ordCustomerService.queryOrdCustomer(requestContext, ordCustomer, 1, 10)
                    .get(0);

            formMap = this.Obj2Map(ordOrder);
            // 将map数据遍历到PDF_form表单中
            for (Map.Entry<String, String> entry : formMap.entrySet()) {
                form.setField(entry.getKey(), entry.getValue());
                // 投保人性别
                if ("applicantSex".equals(entry.getKey())) {
                    if (entry.getValue().equals("F")) {
                        form.setField("applicantSexM", Constants.ERROR);
                    } else {
                        form.setField("applicantSexF", Constants.ERROR);
                    }
                }
                // 投保人英文名全称转换为suname和givenname
                if ("applicantEnglishName".equals(entry.getKey())) {
                    form.setField("applicantSurName",
                            entry.getValue().substring(0, entry.getValue().indexOf(Constants.BLANK)));
                    form.setField("applicantGivenName", entry.getValue()
                            .substring(entry.getValue().indexOf(Constants.BLANK), entry.getValue().length()));
                }
                // 投保人出生日期
                if ("applicantbirthDate".equals(entry.getKey())) {
                    String[] dateArray = entry.getValue().split("-");
                    form.setField("applicantbirthYear", dateArray[0]);
                    form.setField("applicantbirthMonth", dateArray[1]);
                    form.setField("applicantbirthDay", dateArray[2]);
                    // 投保人年龄
                    form.setField("applicantAge",
                            String.valueOf(DateUtil.getYear(new Date()) - Integer.parseInt(dateArray[0])));
                }
                // 投保人身份证号码和出生地
                if ("applicantIdentityNation".equals(entry.getKey())) {
                    if ("China".equals(entry.getValue())) {
                        form.setField("applicantHK", Constants.ERROR);
                        form.setField("applicantUSA", Constants.ERROR);
                        form.setField("applicantChinaIdentityNumber", formMap.get("applicantIdentityNumber"));
                        form.setField("applicantNationalityUS", Constants.ERROR);
                    }
                    if ("Hong Kong".equals(entry.getValue())) {
                        form.setField("applicantChina", Constants.ERROR);
                        form.setField("applicantUSA", Constants.ERROR);
                        form.setField("applicantHKIdentityNumber", formMap.get("applicantIdentityNumber"));
                        form.setField("applicantNationalityUS", Constants.ERROR);
                    }
                    if ("US".equals(entry.getValue())) {
                        form.setField("applicantHK", Constants.ERROR);
                        form.setField("applicantChina", Constants.ERROR);
                        form.setField("applicantNationalityChina", Constants.ERROR);
                    }
                }
                // 投保人是否美国公民
                if ("applicantAmericanCitizenFlag".equals(entry.getKey())) {
                    if ("Y".equals(entry.getValue())) {
                        form.setField("applicantAmericanCitizenFlagN", Constants.ERROR);
                    }
                    if ("N".equals(entry.getValue())) {
                        form.setField("applicantAmericanCitizenFlagY", Constants.ERROR);
                    }
                }
                // 投保人婚姻状况
                if ("applicantMarriageStatus".equals(entry.getKey())) {
                    if (entry.getValue().equals("BEREFT")) {
                        form.setField("applicantDivorce", Constants.ERROR);
                        form.setField("applicantMarried", Constants.ERROR);
                        form.setField("applicantSingle", Constants.ERROR);
                        form.setField("applicantMarryOther", Constants.ERROR);
                    } else if (entry.getValue().equals("DIVORCE")) {
                        form.setField("applicantBereft", Constants.ERROR);
                        form.setField("applicantMarried", Constants.ERROR);
                        form.setField("applicantSingle", Constants.ERROR);
                        form.setField("applicantMarryOther", Constants.ERROR);
                    } else if (entry.getValue().equals("MARRIED")) {
                        form.setField("applicantBereft", Constants.ERROR);
                        form.setField("applicantDivorce", Constants.ERROR);
                        form.setField("applicantSingle", Constants.ERROR);
                        form.setField("applicantMarryOther", Constants.ERROR);
                    } else if (entry.getValue().equals("SINGLE")) {
                        form.setField("applicantBereft", Constants.ERROR);
                        form.setField("applicantDivorce", Constants.ERROR);
                        form.setField("applicantMarried", Constants.ERROR);
                        form.setField("applicantMarryOther", Constants.ERROR);
                    } else {
                        form.setField("applicantDivorce", Constants.ERROR);
                        form.setField("applicantBereft", Constants.ERROR);
                        form.setField("applicantMarried", Constants.ERROR);
                        form.setField("applicantSingle", Constants.ERROR);
                        form.setField("applicantMarryOther", Constants.ERROR);
                    }
                }
                // 投保人是否吸烟
                if ("applicantSmokeFlag".equals(entry.getKey())) {
                    if (entry.getValue().equals("Y")) {
                        form.setField("applicantSmokeFlagN", Constants.ERROR);
                    }
                    if (entry.getValue().equals("N")) {
                        form.setField("applicantSmokeFlagY", Constants.ERROR);
                    }
                }
                // 投保人是否喝酒
                if ("applicantDrinkFlag".equals(entry.getKey())) {
                    if (entry.getValue().equals("Y")) {
                        form.setField("applicantDrinkFlagN", Constants.ERROR);
                    }
                    if (entry.getValue().equals("N")) {
                        form.setField("applicantDrinkFlagY", Constants.ERROR);
                    }
                }
                // 投保人是否从事危险活动
                if ("applicantDangerousFlag".equals(entry.getKey())) {
                    if (entry.getValue().equals("Y")) {
                        form.setField("applicantDangerousFlagN", Constants.ERROR);
                    }
                    if (entry.getValue().equals("N")) {
                        form.setField("applicantDangerousFlagY", Constants.ERROR);
                    }
                }
                // 投保人是否经常出国
                if ("applicantAbroadFlag".equals(entry.getKey())) {
                    if (entry.getValue().equals("Y")) {
                        form.setField("applicantAbroadFlagN", Constants.ERROR);
                    }
                    if (entry.getValue().equals("N")) {
                        form.setField("applicantAbroadFlagY", Constants.ERROR);
                    }
                }

                // 投保人学历信息
                if ("applicantEducation".equals(entry.getKey())) {
                    // 小学及以下
                    if (entry.getValue().equals("BPS") || entry.getValue().equals("PS")) {
                        form.setField("applicantMiddle", Constants.ERROR);
                        form.setField("applicantCollege", Constants.ERROR);
                        form.setField("applicantEduOther", Constants.ERROR);
                        form.setField("applicantU", Constants.ERROR);// 本科
                        form.setField("applicantO", Constants.ERROR);// 研究生及以上
                    }
                    // 中学
                    else if (entry.getValue().equals("JMS") || entry.getValue().equals("HS")) {
                        form.setField("applicantGrade", Constants.ERROR);
                        form.setField("applicantCollege", Constants.ERROR);
                        form.setField("applicantEduOther", Constants.ERROR);
                        form.setField("applicantU", Constants.ERROR);// 本科
                        form.setField("applicantO", Constants.ERROR);// 研究生及以上
                    }
                    // 大专
                    else if (entry.getValue().equals("JC")) {
                        form.setField("applicantGrade", Constants.ERROR);
                        form.setField("applicantMiddle", Constants.ERROR);
                        form.setField("applicantU", Constants.ERROR);// 本科
                        form.setField("applicantO", Constants.ERROR);// 研究生及以上
                    }
                    // 本科
                    else if (entry.getValue().equals("U")) {
                        form.setField("applicantGrade", Constants.ERROR);
                        form.setField("applicantMiddle", Constants.ERROR);
                        form.setField("applicantCollege", Constants.ERROR);
                        form.setField("applicantO", Constants.ERROR);// 研究生及以上
                    }
                    // 研究生以上
                    else {
                        form.setField("applicantGrade", Constants.ERROR);
                        form.setField("applicantMiddle", Constants.ERROR);
                        form.setField("applicantCollege", Constants.ERROR);
                        form.setField("applicantU", Constants.ERROR);// 本科
                    }
                }

                // 每月收入去掉小数
                if ("applicantIncomePerMonth".equals(entry.getKey())) {
                    form.setField("applicantIncomePerMonth",
                            entry.getValue().substring(0, entry.getValue().indexOf(".") - 1));
                }
                // 年供款额去掉小数yearPayAmount
                if ("yearPayAmount".equals(entry.getKey())) {
                    String yearPayAmount = entry.getValue();
                    if(yearPayAmount.indexOf(".") > 0){
                        yearPayAmount = yearPayAmount.replaceAll("0+?$", "");//去掉多余的0
                        yearPayAmount = yearPayAmount.replaceAll("[.]$", "");//如最后一位是.则去掉
                    }
                    form.setField("yearPayAmount", yearPayAmount);
                }

                // 总负债

                if("applicantLiabilities".equals(entry.getKey())){
                    form.setField("applicantLiabilities",
                            entry.getValue().substring(0,
                                    entry.getValue().indexOf("."))); }



                if("applicantCurrentAssets".equals(entry.getKey())){
                    form.setField("applicantCurrentAssets",entry.getValue().
                            substring(0, entry.getValue().indexOf("."))); }


                if ("applicantAmountPerMonth".equals(entry.getKey())) {
                    form.setField("applicantAmountPerMonth",
                            entry.getValue().substring(0, entry.getValue().indexOf(".")));
                }

                if ("applicantFixedAssets".equals(entry.getKey())) {
                    form.setField("applicantFixedAssets", entry.getValue().substring(0, entry.getValue().indexOf(".")));
                }

                // 判断投保人和受保人身高以及体重
                if ("applicantHeight".equals(entry.getKey())) {
                    form.setField("applicantHeight", entry.getValue().substring(0, entry.getValue().indexOf(".")));
                }
                if ("applicantWeight".equals(entry.getKey())) {
                    form.setField("applicantWeight", entry.getValue().substring(0, entry.getValue().indexOf(".")));
                }

                // 保单货币
                if ("currency".equals(entry.getKey())) {
                    if (entry.getValue().equals("USD")) {
                        form.setField("currencyHKD", Constants.ERROR);
                        form.setField("currencyCNY", Constants.ERROR);
                        // 斜杠p45页
                        //form.setField("currencyHKD1", "");
                        form.setField("currencyUSD1", "");
                    }
                    if (entry.getValue().equals("HKD")) {
                        form.setField("currencyUSD", Constants.ERROR);
                        form.setField("currencyCNY", Constants.ERROR);
                        // 斜杠
                        //form.setField("currencyUSD1", "");
                        form.setField("currencyHKD1", "");
                    }
                    if (entry.getValue().equals("CNY")) {
                        form.setField("currencyUSD", Constants.ERROR);
                        form.setField("currencyHKD", Constants.ERROR);
                        form.setField("currencyOther", "人民币");
                        // 斜杠
                        form.setField("currencyHKD1", "");
                        form.setField("currencyUSD1", "");
                    }
                }

                // 缴费方式
                if ("payMethod".equals(entry.getKey())) {
                    // 月缴
                    if (entry.getValue().equals("MP")) {
                        form.setField("payMethodNJ", Constants.ERROR);
                        form.setField("payMethodYJ", Constants.ERROR);
                        form.setField("payMethodZF", Constants.ERROR);
                        form.setField("payMethodJJ", Constants.ERROR);
                        form.setField("payMethodBNJ", Constants.ERROR);

                        // 斜杠
                        form.setField("payMethodZDYJ1", "");
                    }
                    // 季缴
                    if (entry.getValue().equals("QP")) {
                        form.setField("payMethodNJ", Constants.ERROR);
                        form.setField("payMethodYJ", Constants.ERROR);
                        form.setField("payMethodZDYJ", Constants.ERROR);
                        form.setField("payMethodZF", Constants.ERROR);
                        form.setField("payMethodBNJ", Constants.ERROR);
                        // 斜杠
                        form.setField("payMethodJJ", "");
                    }
                    // 年缴
                    if (entry.getValue().equals("AP")) {
                        form.setField("payMethodYJ", Constants.ERROR);
                        form.setField("payMethodZDYJ", Constants.ERROR);
                        form.setField("payMethodZF", Constants.ERROR);
                        form.setField("payMethodJJ", Constants.ERROR);
                        form.setField("payMethodBNJ", Constants.ERROR);
                        // 斜杠
                        form.setField("payMethodNJ1", "");
                    }
                    // 预缴
                    if (entry.getValue().equals("FJ")) {
                        form.setField("payMethodNJ", Constants.ERROR);
                        form.setField("payMethodZDYJ", Constants.ERROR);
                        form.setField("payMethodZF", Constants.ERROR);
                        form.setField("payMethodJJ", Constants.ERROR);
                        form.setField("payMethodBNJ", Constants.ERROR);
                    }
                    // 半年缴
                    if (entry.getValue().equals("SAP")) {
                        form.setField("payMethodNJ", Constants.ERROR);
                        form.setField("payMethodYJ", Constants.ERROR);
                        form.setField("payMethodZDYJ", Constants.ERROR);
                        form.setField("payMethodZF", Constants.ERROR);
                        form.setField("payMethodJJ", Constants.ERROR);
                        // 斜杠
                        form.setField("payMethodBNJ1", "");
                    }
                    // 趸缴
                    if (entry.getValue().equals("WP")) {
                        form.setField("payMethodNJ", Constants.ERROR);
                        form.setField("payMethodYJ", Constants.ERROR);
                        form.setField("payMethodZDYJ", Constants.ERROR);
                        form.setField("payMethodZF", Constants.ERROR);
                        form.setField("payMethodJJ", Constants.ERROR);
                        form.setField("payMethodBNJ", Constants.ERROR);
                    }
                    // 整付
                    if (entry.getValue().equals("WP")) {
                        form.setField("payMethodNJ", Constants.ERROR);
                        form.setField("payMethodYJ", Constants.ERROR);
                        form.setField("payMethodZDYJ", Constants.ERROR);
                        form.setField("payMethodJJ", Constants.ERROR);
                        form.setField("payMethodBNJ", Constants.ERROR);
                        // 斜杠
                        form.setField("payMethodZF", "");
                    }
                }

                // 产品名称判断勾选框
                if ("itemName".equals(entry.getKey())) {
                    if (entry.getValue().equals("「简选易」定期寿险")) {
                        form.setField("itemBigTypeB", Constants.ERROR);
                        form.setField("itemBigTypeC", Constants.ERROR);
                        form.setField("itemBigTypeD", Constants.ERROR);
                        form.setField("itemTypeB", Constants.ERROR);
                        form.setField("itemTypeC", Constants.ERROR);
                        form.setField("itemTypeD", Constants.ERROR);
                        form.setField("itemTypeE", Constants.ERROR);
                    } else if (entry.getValue().equals("「柏御」终身保障计划")) {
                        form.setField("itemTypeB", Constants.ERROR);
                        form.setField("itemTypeC", Constants.ERROR);
                        form.setField("itemTypeE", Constants.ERROR);
                        form.setField("itemBigTypeB", Constants.ERROR);
                        form.setField("itemBigTypeA", Constants.ERROR);
                        form.setField("itemBigTypeD", Constants.ERROR);
                    } else if (entry.getValue().equals("耀光·永恒储蓄计划")) {
                        form.setField("itemTypeA", Constants.ERROR);
                        form.setField("itemTypeB", Constants.ERROR);
                        form.setField("itemTypeC", Constants.ERROR);
                        form.setField("itemTypeE", Constants.ERROR);
                        form.setField("itemBigTypeB", Constants.ERROR);
                        form.setField("itemBigTypeA", Constants.ERROR);
                        form.setField("itemBigTypeD", Constants.ERROR);
                    } else if (entry.getValue().equals("康晴危疾保障计划")) {
                        form.setField("itemTypeC", Constants.ERROR);
                        form.setField("itemTypeE", Constants.ERROR);
                        form.setField("itemBigTypeB", Constants.ERROR);
                        form.setField("itemBigTypeA", Constants.ERROR);
                        form.setField("itemBigTypeD", Constants.ERROR);
                    } else if (entry.getValue().equals("康挚选危疾保障计划")) {
                        form.setField("itemTypeA", Constants.ERROR);
                        form.setField("itemTypeD", Constants.ERROR);
                        form.setField("itemTypeC", Constants.ERROR);
                        form.setField("itemTypeE", Constants.ERROR);
                        form.setField("itemBigTypeB", Constants.ERROR);
                        form.setField("itemBigTypeC", Constants.ERROR);
                        form.setField("itemBigTypeD", Constants.ERROR);
                    } else if (entry.getValue().equals("更关怀癌症保障终身计划")) {
                        form.setField("itemTypeC", Constants.ERROR);
                        form.setField("itemTypeE", Constants.ERROR);
                        form.setField("itemBigTypeB", Constants.ERROR);
                        form.setField("itemBigTypeA", Constants.ERROR);
                        form.setField("itemBigTypeD", Constants.ERROR);
                    } else {
                        form.setField("itemTypeA", Constants.ERROR);
                        form.setField("itemTypeB", Constants.ERROR);
                        form.setField("itemTypeD", Constants.ERROR);
                        form.setField("itemTypeC", Constants.ERROR);
                        form.setField("itemTypeE", Constants.ERROR);
                        form.setField("itemBigTypeA", Constants.ERROR);
                        form.setField("itemBigTypeB", Constants.ERROR);
                        form.setField("itemBigTypeC", Constants.ERROR);
                        form.setField("itemBigTypeD", Constants.ERROR);
                    }
                }

                // 产品名称判断勾选框
                if ("midClass".equals(entry.getKey())) {
                    if (entry.getValue() != "ZJX" && entry.getValue() != "RSX" && entry.getValue() != "CXX"
                            && entry.getValue() != "WYSX") {
                        form.setField("itemSublineCheck", Constants.ERROR);
                    }
                }

                // 判断月收入
                if ("applicantIncome".equals(entry.getKey())) {
                    if (Double.parseDouble(entry.getValue()) > 0) {
                        form.setField("applicantIncomeN", Constants.ERROR);
                        form.setField("applicantIncome", entry.getValue().substring(0, entry.getValue().indexOf(".")));
                        form.setField("applicantIncomePerMonth",
                                new BigDecimal(entry.getValue()).divide(new BigDecimal(12), 2).toString().substring(0,
                                        entry.getValue().indexOf(".") - 1));
                    } else {
                        form.setField("applicantIncomeY", Constants.ERROR);
                    }
                }

                // 判断年期
                if ("sublineItemName".equals(entry.getKey())) {
                    // 如果是数字组成sublineItemName6-10
                    //sublineItemName11-20
                    if (StringUtil.isNumeric(entry.getValue())) {
                        if (Long.valueOf(entry.getValue()) <= 1) {
                            form.setField("sublineItemName1-5", Constants.ERROR);
                            form.setField("sublineItemName6-10", Constants.ERROR);
                            form.setField("sublineItemName11-20", Constants.ERROR);
                            form.setField("sublineItemName20", Constants.ERROR);
                            form.setField("sublineItemNameZS", Constants.ERROR);
                            form.setField("imageSublineItemName5-10", Constants.ERROR);
                            form.setField("imageSublineItemName10", Constants.ERROR);
                        } else if (Long.valueOf(entry.getValue()) <= 5) {
                            form.setField("sublineItemName1", Constants.ERROR);
                            form.setField("sublineItemName6-10", Constants.ERROR);
                            form.setField("sublineItemName11-20", Constants.ERROR);
                            form.setField("sublineItemName20", Constants.ERROR);
                            form.setField("sublineItemNameZS", Constants.ERROR);
                            //逻辑变更 pdf36页的年期如果等于5勾选5-10
                            if (Long.valueOf(entry.getValue()) == 5) {
                                form.setField("imageSublineItemName1-5", Constants.ERROR);
                                form.setField("imageSublineItemName10", Constants.ERROR);
                            }else {
                                form.setField("imageSublineItemName5-10", Constants.ERROR);
                                form.setField("imageSublineItemName10", Constants.ERROR);
                            }
                        } else if (Long.valueOf(entry.getValue()) <= 10) {
                            form.setField("sublineItemName1", Constants.ERROR);
                            form.setField("sublineItemName1-5", Constants.ERROR);
                            form.setField("sublineItemName11-20", Constants.ERROR);
                            form.setField("sublineItemName20", Constants.ERROR);
                            form.setField("sublineItemNameZS", Constants.ERROR);
                            form.setField("imageSublineItemName1-5", Constants.ERROR);
                            form.setField("imageSublineItemName10", Constants.ERROR);
                        } else if (Long.valueOf(entry.getValue()) <= 20) {
                            form.setField("sublineItemName1", Constants.ERROR);
                            form.setField("sublineItemName1-5", Constants.ERROR);
                            form.setField("sublineItemName6-10", Constants.ERROR);
                            form.setField("sublineItemName20", Constants.ERROR);
                            form.setField("sublineItemNameZS", Constants.ERROR);
                            form.setField("imageSublineItemName1-5", Constants.ERROR);
                            form.setField("imageSublineItemName5-10", Constants.ERROR);
                        } else {
                            form.setField("sublineItemName1", Constants.ERROR);
                            form.setField("sublineItemName1-5", Constants.ERROR);
                            form.setField("sublineItemName6-10", Constants.ERROR);
                            form.setField("sublineItemName11-20", Constants.ERROR);
                            form.setField("sublineItemNameZS", Constants.ERROR);
                            form.setField("imageSublineItemName1-5", Constants.ERROR);
                            form.setField("imageSublineItemName5-10", Constants.ERROR);
                        }
                    } else {
                        if (entry.getValue().equals("终身")) {
                            form.setField("sublineItemName1", Constants.ERROR);
                            form.setField("sublineItemName1-5", Constants.ERROR);
                            form.setField("sublineItemName6-10", Constants.ERROR);
                            form.setField("sublineItemName11-20", Constants.ERROR);
                            form.setField("sublineItemName20", Constants.ERROR);
                            form.setField("imageSublineItemName1-5", Constants.ERROR);
                            form.setField("imageSublineItemName5-10", Constants.ERROR);
                            form.setField("imageSublineItemName1-5", Constants.ERROR);
                            form.setField("imageSublineItemName5-10", Constants.ERROR);
                        } else {
                            form.setField("sublineItemName1", Constants.ERROR);
                            form.setField("sublineItemName1-5", Constants.ERROR);
                            form.setField("sublineItemName6-10", Constants.ERROR);
                            form.setField("sublineItemName11-20", Constants.ERROR);
                            form.setField("sublineItemName20", Constants.ERROR);
                            form.setField("sublineItemNameZS", Constants.ERROR);
                            form.setField("imageSublineItemName1-5", Constants.ERROR);
                            form.setField("imageSublineItemName5-10", Constants.ERROR);
                            form.setField("imageSublineItemName10", Constants.ERROR);
                        }
                    }
                }

                // 投保人和受保人国籍
				/*form.setField("applicantNationalityUS", Constants.ERROR);
				form.setField("applicantNationalityOthers", Constants.ERROR);
				form.setField("applicantNationalityChina", Constants.ERROR);*/
                if ("applicantNationality".equals(entry.getKey())) {
                    if ("China".equals(entry.getValue())) {
                        form.setField("applicantNationality", "中国");
                        form.setField("applicantNationalityUS", Constants.ERROR);
                        form.setField("applicantNationalityOthers", Constants.ERROR);
                    } else if ("Hong Kong".equals(entry.getValue())) {
                        form.setField("applicantNationality", "香港");
                        form.setField("applicantNationalityUS", Constants.ERROR);
                        form.setField("applicantNationalityChina", Constants.ERROR);
                        form.setField("applicantNationalityValue", "香港");
                    } else if ("Taiwan".equals(entry.getValue())) {
                        form.setField("applicantNationality", "台湾");
                        form.setField("applicantNationalityUS", Constants.ERROR);
                        form.setField("applicantNationalityChina", Constants.ERROR);
                        form.setField("applicantNationalityValue", "台湾");
                    } else if ("MO".equals(entry.getValue())) {
                        form.setField("applicantNationality", "澳门");
                        form.setField("applicantNationalityUS", Constants.ERROR);
                        form.setField("applicantNationalityChina", Constants.ERROR);
                        form.setField("applicantNationalityValue", "澳门");
                    } else if ("US".equals(entry.getValue())) {
                        form.setField("applicantNationality", "美国");
                        form.setField("applicantNationalityOthers", Constants.ERROR);
                        form.setField("applicantNationalityChina", Constants.ERROR);
                    } else {
                        form.setField("applicantNationality",
                                plnPlanRequestService.selectCodeMeaning("PUB.NATION", entry.getValue()));
                        form.setField("applicantNationalityUS", Constants.ERROR);
                        form.setField("applicantNationalityChina", Constants.ERROR);
                        form.setField("applicantNationalityValue",
                                plnPlanRequestService.selectCodeMeaning("PUB.NATION", entry.getValue()));
                    }
                }

                // 投保人 居住地址(身份证地址)
                if ("applicantIdentityNation".equals(entry.getKey())) {
                    if ("China".equals(entry.getValue())) {
                        form.setField("applicantIdentityCountry", "中国");
                    } else if ("Hong Kong".equals(entry.getValue())) {
                        form.setField("applicantIdentityDistrict", "香港");
                    } else if ("Taiwan".equals(entry.getValue())) {
                        form.setField("applicantIdentityDistrict", "台湾");
                    } else {
                        form.setField("applicantIdentityCountry",
                                plnPlanRequestService.selectCodeMeaning("PUB.NATION", entry.getValue()));
                    }
                }

                // 电话区号
                if ("applicantPhoneCode".equals(entry.getKey())) {
                    if ("86".equals(entry.getValue())) {
                        form.setField("applicantPhoneCode", "+86");
                    } else if ("00852".equals(entry.getValue())) {
                        form.setField("applicantPhoneCode", "+852");
                    } else if ("00853".equals(entry.getValue())) {
                        form.setField("applicantPhoneCode", "+853");
                    } else if ("00886".equals(entry.getValue())) {
                        form.setField("applicantPhoneCode", "+886");
                    }
                }
                // 受保人和投保人的关系insurantChildrent,insurantSpouse
                // 其他insurantSameFlag,值insurantSameFlagValue
                if ("sameFlag".equals(entry.getKey())) {
                    if ("GPARENT-GCHILD".equals(entry.getValue())) {
                        form.setField("sameFlag", "祖孙");
                        form.setField("insurantChildren", Constants.ERROR);
                        form.setField("insurantSpouse", Constants.ERROR);
                        form.setField("insurantSameFlagValue", "祖孙");
                    } else if ("COUPLE".equals(entry.getValue())) {
                        form.setField("sameFlag", "夫妻");
                        form.setField("insurantChildren", Constants.ERROR);
                        form.setField("insurantSameFlag", Constants.ERROR);
                    } else if ("MOTHER-DAUGHTER".equals(entry.getValue())) {
                        form.setField("sameFlag", "母女");
                        form.setField("insurantSpouse", Constants.ERROR);
                        form.setField("insurantSameFlag", Constants.ERROR);
                    } else if ("MOTHER-SON".equals(entry.getValue())) {
                        form.setField("sameFlag", "母子");
                        form.setField("insurantSpouse", Constants.ERROR);
                        form.setField("insurantSameFlag", Constants.ERROR);
                    } else if ("FATHER-DAUGHTER".equals(entry.getValue())) {
                        form.setField("sameFlag", "父女");
                        form.setField("insurantSpouse", Constants.ERROR);
                        form.setField("insurantSameFlag", Constants.ERROR);
                    } else if ("FATHER-SON".equals(entry.getValue())) {
                        form.setField("sameFlag", "父子");
                        form.setField("insurantSpouse", Constants.ERROR);
                        form.setField("insurantSameFlag", Constants.ERROR);
                    } else if ("SELF".equals(entry.getValue())) {
                        form.setField("sameFlag", "本人");
                        form.setField("insurantChildren", Constants.ERROR);
                        form.setField("insurantSpouse", Constants.ERROR);
                        // form.setField("insurantSameFlagValue", "本人");
                    } else if ("UNSELF".equals(entry.getValue())) {
                        form.setField("sameFlag", "非本人");
                        form.setField("insurantChildren", Constants.ERROR);
                        form.setField("insurantSpouse", Constants.ERROR);
                        form.setField("insurantSameFlagValue", "非本人");
                    } else {
                        form.setField("sameFlag", "");
                    }
                }

                // 通讯地址(邮寄地址)
                if ("applicantPostNation".equals(entry.getKey())) {
                    if ("China".equals(entry.getValue())) {
                        form.setField("applicantPostCountry", "中国");
                    } else if ("Hong Kong".equals(entry.getValue())) {
                        form.setField("applicantPostDistrict", "香港");
                    } else if ("Taiwan".equals(entry.getValue())) {
                        form.setField("applicantPostDistrict", "台湾");
                    } else {
                        form.setField("applicantPostCountry",
                                plnPlanRequestService.selectCodeMeaning("PUB.NATION", entry.getValue()));
                    }
                }

                // 工作地址(公司地址)
                if ("applicantCompanyNation".equals(entry.getKey())) {
                    if ("China".equals(entry.getValue())) {
                        form.setField("applicantCompanyCountry", "中国");
                    } else if ("Hong Kong".equals(entry.getValue())) {
                        form.setField("applicantCompanyDistrict", "香港");
                    } else if ("Taiwan".equals(entry.getValue())) {
                        form.setField("applicantCompanyDistrict", "台湾");
                    } else {
                        form.setField("applicantCompanyCountry",
                                plnPlanRequestService.selectCodeMeaning("PUB.NATION", entry.getValue()));
                    }
                }

                // #############

                // 判断投保人和受保人是否是同一个人 根据名字判断
                if (!applicantCustomer.getChineseName().equals(insuredCustomer.getChineseName())) {
                    // 受保人性别
                    if ("insurantSex".equals(entry.getKey())) {
                        if (entry.getValue().equals("F")) {
                            form.setField("insurantSexM", Constants.ERROR);
                        } else {
                            form.setField("insurantSexF", Constants.ERROR);
                        }
                    }

                    // 受保人英文名全称转换为suname和givenname  同时给部分受保人英文赋值
                    if ("insurantEnglishName".equals(entry.getKey())) {
                        form.setField("insurantSurName",
                                entry.getValue().substring(0, entry.getValue().indexOf(Constants.BLANK)));
                        form.setField("insurantGivenName", entry.getValue()
                                .substring(entry.getValue().indexOf(Constants.BLANK), entry.getValue().length()));
                        form.setField("insurantSameEnglishName", entry.getValue());
                    }
                    // 受保人出生日期
                    if ("insurantbirthDate".equals(entry.getKey())) {
                        String[] dateArray = entry.getValue().split("-");
                        form.setField("insurantbirthYear", dateArray[0]);
                        form.setField("insurantbirthMonth", dateArray[1]);
                        form.setField("insurantbirthDay", dateArray[2]);
                        // 受保人年龄
                        form.setField("insurantAge",
                                String.valueOf(DateUtil.getYear(new Date()) - Integer.parseInt(dateArray[0])));
                    }
                    // 受保人身份证号码和出生地还有国籍
                    if ("insurantIdentityNation".equals(entry.getKey())) {
                        String nation = entry.getValue();
                        if ("China".equals(entry.getValue())) {
                            form.setField("insurantHK", Constants.ERROR);
                            form.setField("insurantUSA", Constants.ERROR);
                            form.setField("insurantChinaIdentityNumber", formMap.get("insurantIdentityNumber"));
                            form.setField("insurantNationalityUS", Constants.ERROR);
                            form.setField("insurantNationalityOthers", Constants.ERROR);
                        }
                        if ("Hong Kong".equals(entry.getValue())) {
                            form.setField("insurantChina", Constants.ERROR);
                            form.setField("insurantUSA", Constants.ERROR);
                            form.setField("insurantHKIdentityNumber", formMap.get("insurantIdentityNumber"));
                            form.setField("insurantNationalityUS", Constants.ERROR);
                            form.setField("insurantNationalityChina", Constants.ERROR);
                            form.setField("insurantNationalityValue", "香港");
                        }
                        if ("US".equals(entry.getValue())) {
                            form.setField("insurantHK", Constants.ERROR);
                            form.setField("insurantChina", Constants.ERROR);
                            form.setField("insurantNationalityChina", Constants.ERROR);
                            form.setField("insurantNationalityOthers", Constants.ERROR);
                        }
                    }
                    // 受保人是否美国公民
                    if ("insurantAmericanCitizenFlag".equals(entry.getKey())) {
                        if ("Y".equals(entry.getValue())) {
                            form.setField("insurantAmericanCitizenFlagN", Constants.ERROR);
                        }
                        if ("N".equals(entry.getValue())) {
                            form.setField("insurantAmericanCitizenFlagY", Constants.ERROR);
                        }
                    }
                    // 受保人婚姻状况
                    if ("insurantMarriageStatus".equals(entry.getKey())) {
                        if (entry.getValue().equals("BEREFT")) {
                            form.setField("insurantDivorce", Constants.ERROR);
                            form.setField("insurantMarried", Constants.ERROR);
                            form.setField("insurantSingle", Constants.ERROR);
                        }
                        if (entry.getValue().equals("DIVORCE")) {
                            form.setField("insurantBereft", Constants.ERROR);
                            form.setField("insurantMarried", Constants.ERROR);
                            form.setField("insurantSingle", Constants.ERROR);
                        }
                        if (entry.getValue().equals("MARRIED")) {
                            form.setField("insurantBereft", Constants.ERROR);
                            form.setField("insurantDivorce", Constants.ERROR);
                            form.setField("insurantSingle", Constants.ERROR);
                        }
                        if (entry.getValue().equals("SINGLE")) {
                            form.setField("insurantBereft", Constants.ERROR);
                            form.setField("insurantDivorce", Constants.ERROR);
                            form.setField("insurantMarried", Constants.ERROR);
                        }
                    }
                    // 受保人收入
                    if ("insurantIncome".equals(entry.getKey())) {
                        form.setField("insurantIncome", entry.getValue().substring(0, entry.getValue().indexOf(".")));
                    }

                    // 受保人是否吸烟
                    if ("insurantSmokeFlag".equals(entry.getKey())) {
                        if (entry.getValue().equals("Y")) {
                            form.setField("insurantSmokeFlagN", Constants.ERROR);
                        }
                        if (entry.getValue().equals("N")) {
                            form.setField("insurantSmokeFlagY", Constants.ERROR);
                        }
                    }
                    // 受保人是否喝酒
                    if ("insurantDrinkFlag".equals(entry.getKey())) {
                        if (entry.getValue().equals("Y")) {
                            form.setField("insurantDrinkFlagN", Constants.ERROR);
                        }
                        if (entry.getValue().equals("N")) {
                            form.setField("insurantDrinkFlagY", Constants.ERROR);
                        }
                    }

                    // 受保人是否从事危险活动
                    if ("insurantDangerousFlag".equals(entry.getKey())) {
                        if (entry.getValue().equals("Y")) {
                            form.setField("insurantDangerousFlagN", Constants.ERROR);
                        }
                        if (entry.getValue().equals("N")) {
                            form.setField("insurantDangerousFlagY", Constants.ERROR);
                        }
                    }
                    // 受保人是否经常出国
                    if ("insurantAbroadFlag".equals(entry.getKey())) {
                        if (entry.getValue().equals("Y")) {
                            form.setField("insurantAbroadFlagN", Constants.ERROR);
                        }
                        if (entry.getValue().equals("N")) {
                            form.setField("insurantAbroadFlagY", Constants.ERROR);
                        }
                    }

                    // 判断投保人和受保人身高以及体重
                    if ("insurantHeight".equals(entry.getKey())) {
                        form.setField("insurantHeight", entry.getValue().substring(0, entry.getValue().indexOf(".")));
                    }
                    if ("insurantWeight".equals(entry.getKey())) {
                        form.setField("insurantWeight", entry.getValue().substring(0, entry.getValue().indexOf(".")));
                    }

                    // 受保人 居住地址(身份证地址)
                    if ("insurantIdentityNation".equals(entry.getKey())) {
                        if ("China".equals(entry.getValue())) {
                            form.setField("insurantIdentityCountry", "中国");
                        } else if ("Hong Kong".equals(entry.getValue())) {
                            form.setField("insurantIdentityDistrict", "香港");
                        } else if ("Taiwan".equals(entry.getValue())) {
                            form.setField("insurantIdentityDistrict", "台湾");
                        } else {
                            form.setField("insurantIdentityCountry",
                                    plnPlanRequestService.selectCodeMeaning("PUB.NATION", entry.getValue()));
                        }
                    }

                    // 通讯地址(邮寄地址)
                    if ("insurantPostNation".equals(entry.getKey())) {
                        if ("China".equals(entry.getValue())) {
                            form.setField("insurantPostCountry", "中国");
                        } else if ("Hong Kong".equals(entry.getValue())) {
                            form.setField("insurantPostDistrict", "香港");
                        } else if ("Taiwan".equals(entry.getValue())) {
                            form.setField("insurantPostDistrict", "台湾");
                        } else {
                            form.setField("insurantPostCountry",
                                    plnPlanRequestService.selectCodeMeaning("PUB.NATION", entry.getValue()));
                        }
                    }

                    // 工作地址(公司地址)
                    if ("insurantCompanyNation".equals(entry.getKey())) {
                        if ("China".equals(entry.getValue())) {
                            form.setField("insurantCompanyCountry", "中国");
                        } else if ("Hong Kong".equals(entry.getValue())) {
                            form.setField("insurantCompanyDistrict", "香港");
                        } else if ("Taiwan".equals(entry.getValue())) {
                            form.setField("insurantCompanyDistrict", "台湾");
                        } else {
                            form.setField("insurantCompanyCountry",
                                    plnPlanRequestService.selectCodeMeaning("PUB.NATION", entry.getValue()));
                        }
                    }

                    // 投保人和受保人国籍
                    if ("insurantNationality".equals(entry.getKey())) {
                        if ("China".equals(entry.getValue())) {
                            form.setField("insurantNationality", "中国");
                        } else if ("Hong Kong".equals(entry.getValue())) {
                            form.setField("insurantNationality", "香港");
                        } else if ("Taiwan".equals(entry.getValue())) {
                            form.setField("insurantNationality", "台湾");
                        } else {
                            form.setField("insurantNationality",
                                    plnPlanRequestService.selectCodeMeaning("PUB.NATION", entry.getValue()));
                        }
                    }

                    // 电话区号
                    if ("insurantPhoneCode".equals(entry.getKey())) {
                        if ("86".equals(entry.getValue())) {
                            form.setField("insurantPhoneCode", "+86");
                        } else if ("00852".equals(entry.getValue())) {
                            form.setField("insurantPhoneCode", "+852");
                        } else if ("00853".equals(entry.getValue())) {
                            form.setField("insurantPhoneCode", "+853");
                        } else if ("00886".equals(entry.getValue())) {
                            form.setField("insurantPhoneCode", "+886");
                        }
                    }
                    // 投保人不是受保人
                    form.setField("applicantIsInsurant", Constants.ERROR);

                } else {// ***********
                    // 相同的时候 所有受保人信息都不填写
                    // 性别
                    form.setField("insurantSexM", Constants.ERROR);
                    form.setField("insurantSexF", Constants.ERROR);
                    // 是否经常出国
                    form.setField("insurantAbroadFlagN", Constants.ERROR);
                    form.setField("insurantAbroadFlagY", Constants.ERROR);
                    // 受保人身份证号码和出生地
                    form.setField("insurantHK", Constants.ERROR);
                    form.setField("insurantChina", Constants.ERROR);
                    form.setField("insurantUSA", Constants.ERROR);
                    form.setField("insurantNationalityChina", Constants.ERROR);
                    form.setField("insurantNationalityUS", Constants.ERROR);
                    // 是否是美国公民
                    form.setField("insurantAmericanCitizenFlagN", Constants.ERROR);
                    form.setField("insurantAmericanCitizenFlagY", Constants.ERROR);
                    // 婚姻状况
                    form.setField("insurantBereft", Constants.ERROR);
                    form.setField("insurantDivorce", Constants.ERROR);
                    form.setField("insurantMarried", Constants.ERROR);
                    form.setField("insurantSingle", Constants.ERROR);

                    // 受保人是否吸烟
                    form.setField("insurantSmokeFlagN", Constants.ERROR);
                    form.setField("insurantSmokeFlagY", Constants.ERROR);
                    // 受保人是否喝酒
                    form.setField("insurantDrinkFlagN", Constants.ERROR);
                    form.setField("insurantDrinkFlagY", Constants.ERROR);

                    // 受保人是否从事危险活动
                    form.setField("insurantDangerousFlagN", Constants.ERROR);
                    form.setField("insurantDangerousFlagY", Constants.ERROR);
                    // 受保人是否经常出国
                    form.setField("insurantAbroadFlagN", Constants.ERROR);
                    form.setField("insurantAbroadFlagY", Constants.ERROR);
                    // 与申请人之关系
                    form.setField("insurantSameFlag", Constants.ERROR);
                    // 受保人国籍
                    form.setField("insurantNationalityChina", Constants.ERROR);
                    form.setField("insurantNationalityUS", Constants.ERROR);
                    form.setField("insurantNationalityOthers", Constants.ERROR);
                    // 受保人非拼接字段设为空
                    // 受保人中文名
                    form.setField("insurantChineseName", "");
                    // 受保人身高
                    form.setField("insurantHeight", "");
                    // 受保人体重
                    form.setField("insurantWeight", "");
                    // 受保人邮箱
                    form.setField("insurantEmail", "");
                    // 受保人公司名
                    form.setField("insurantCompanyName", "");
                    // 受保人行业
                    form.setField("insurantIndustry", "");
                    // 受保人城市
                    form.setField("insurantCity", "");
                    // 受保人收入
                    form.setField("insurantIncome", "");
                    // 受保人工作
                    form.setField("insurantJob", "");
                    // 受保人英文名姓名拼音
                    //form.setField("insurantEnglishName", "");
                    // 受保人身份证号码
                    form.setField("insurantIdentityNumber", "");
                    // 受保人电话手机
                    form.setField("insurantPhone", "");
                    // 受保人电话区号
                    form.setField("insurantPhoneCode", "");
                    // 受保人职位 字段单词被拼错将错就错
                    form.setField("insurantPostion", "");
                    // 大都会pdf
                    form.setField("insurantCountryRisk", "");
                    form.setField("insurantCustomerRisk", "");
                    form.setField("insurantProduct", "");
                    form.setField("insurantDelivery", "");
                    form.setField("insurantTotalScore", "");
                    //相同时部分的受保人英文置为空
                    form.setField("insurantSameEnglishName", "");

                }

                // 受益人
                List<OrdBeneficiary> ordBeneficiaryList = new ArrayList<OrdBeneficiary>();
                ordBeneficiaryList = ordBeneficiaryService.selectAllBeneficiary(Long.valueOf(orderId));
                if (ordBeneficiaryList != null && ordBeneficiaryList.size() >= 1) {
                    StringBuffer beneficiaryName = new StringBuffer("");// 受益人姓名
                    // 默认申请人和受益人不包含
                    String applicantBenifit = "N";
                    for (int i = 1; i < ordBeneficiaryList.size() + 1; i++) {
                        form.setField("englishName" + i, ordBeneficiaryList.get(i - 1).getEnglishName());
                        form.setField("chineseName" + i, ordBeneficiaryList.get(i - 1).getChineseName());
                        form.setField("identityNumber" + i, ordBeneficiaryList.get(i - 1).getIdentityNumber());
                        form.setField("relationship" + i, ordBeneficiaryList.get(i - 1).getRelationship());
                        String rate = ordBeneficiaryList.get(i - 1).getRate().multiply(new BigDecimal(100)).toString();
                        form.setField("rate" + i, rate.substring(0, rate.indexOf(".")) + "%");
                        // form.setField("rate"+i,
                        // rate.substring(0,rate.indexOf(".")+3)+"%");
                        beneficiaryName.append(ordBeneficiaryList.get(i - 1).getEnglishName()).append(",");
                        // 投保人不是受益人(申请人不是受益人)applicantIsBenifit不判断受益人了
                        /*if (applicantCustomer.getChineseName().equals(ordBeneficiaryList.get(i - 1).getChineseName())) {
                            applicantBenifit = "Y";
                        }*/
                    }
                    // 如果不包含置为未选中  不判断受益人了
                    /*if (applicantBenifit == "N") {
                        form.setField("applicantIsBenifit", Constants.ERROR);
                    }*/
                    form.setField("beneficiaryName", beneficiaryName.toString().substring(0, beneficiaryName.length()));
                    if(beneficiaryName.toString().endsWith(",")){
                        form.setField("beneficiaryName", beneficiaryName.toString().substring(0, beneficiaryName.length()-1));

                    }
                    // 受益人存在
                    form.setField("beneficiary1", "1");
                    form.setField("beneficiary2", "1");
                    form.setField("beneficiary3", "1");
                    form.setField("beneficiary4", "1");
                    form.setField("beneficiary5", "4");
                }
                //受益人选项置为不勾选  业务说不作判断
                form.setField("applicantIsBenifit", Constants.ERROR);

            }

            Double d = Double.parseDouble(formMap.get("applicantIncome")) / 12;
            d = d - Double.parseDouble(formMap.get("applicantAmountPerMonth"));
            form.setField("applicantRemainPerMonth", d.toString().substring(0, d.toString().indexOf(".")));
            Double applicantNetWorth = Double.parseDouble(formMap.get("applicantCurrentAssets"))
                    + Double.parseDouble(formMap.get("applicantFixedAssets"))
                    - Double.parseDouble(formMap.get("applicantLiabilities"));
            form.setField("applicantNetWorth",
                    applicantNetWorth.toString().substring(0, applicantNetWorth.toString().indexOf(".")));

            // 通讯地址 不加国家 + 详细地址 ----> 邮寄地址
            form.setField("applicantPostInfo2",
                    self().queryIdentityInfo2(formMap.get("applicantPostProvince"), formMap.get("applicantPostCity"))
                            + formMap.get("applicantPostAddress"));
            // 居住地址 ---->身份证地址
            String provinceName = self().queryIdentityInfo2(formMap.get("applicantIdentityProvince"),
                    formMap.get("applicantIdentityCity"));
		/*	String addressName = formMap.get("applicantIdentityAddress");
			//adobe bug 写不上 特别的别字  所以将所有的特别行政区替换成空
			provinceName=provinceName.replace("特别行政区", "");
			form.setField("applicantIdentityInfo2",provinceName+addressName);*/
            form.setField("applicantIdentityInfo2", self().queryIdentityInfo2(formMap.get("applicantIdentityProvince"),
                    formMap.get("applicantIdentityCity")) + formMap.get("applicantIdentityAddress"));
            // 工作地址 --->公司地址
            form.setField("applicantCompanyInfo2", self().queryIdentityInfo2(formMap.get("applicantCompanyProvince"),
                    formMap.get("applicantCompanyCity")) + formMap.get("applicantCompanyAddress"));
            // 投保人和受保人 --->区号 +电话号码显示
            form.setField("applicantPhoneNumber",
                    formMap.get("applicantPhoneCode") + " " + formMap.get("applicantPhone"));

            // 判断投保人和受保人是否是同一个人 根据名字判断
            if (!applicantCustomer.getChineseName().equals(insuredCustomer.getChineseName())) {
                // 通讯地址 不加国家 + 详细地址 ----> 邮寄地址
                form.setField("insurantPostInfo2",
                        self().queryIdentityInfo2(formMap.get("insurantPostProvince"), formMap.get("insurantPostCity"))
                                + formMap.get("insurantPostAddress"));
				/*
				 * //居住地址 ---->身份证地址 form.setField("insurantIdentityInfo2",
				 * self().queryIdentityInfo2(formMap.get(
				 * "insurantIdentityProvince"),
				 * formMap.get("insurantIdentityCity")) +
				 * formMap.get("insurantIdentityAddress"));
				 */
                // 受保人年龄大于等于18岁才写受保人地址 否则为投保人地址
                //居住地址的国家也不显示
                int insAge = Integer.parseInt(form.getField("insurantAge"));
                if (insAge < 18) {
                    // 居住地址 ---->身份证地址
                    form.setField("insurantIdentityInfo2","同左");
                    form.setField("insurantIdentityCountry","");
                } else {
                    form.setField("insurantIdentityInfo2",
                            self().queryIdentityInfo2(formMap.get("insurantIdentityProvince"),
                                    formMap.get("insurantIdentityCity")) + formMap.get("insurantIdentityAddress"));

                }
                // 工作地址 --->公司地址   如果为null  则置为""
                String insurantCompanyInfo2 = self().queryIdentityInfo2(formMap.get("insurantCompanyProvince"),
                        formMap.get("insurantCompanyCity")) + formMap.get("insurantCompanyAddress");
                if (null == self().queryIdentityInfo2(formMap.get("insurantCompanyProvince"),
                        formMap.get("insurantCompanyCity")).trim() || "".equals(self().queryIdentityInfo2(formMap.get("insurantCompanyProvince"),
                        formMap.get("insurantCompanyCity")).trim()) ) {
                    form.setField("insurantCompanyInfo2","" );
                }else if (null ==formMap.get("insurantCompanyAddress")||"".equals(formMap.get("insurantCompanyAddress").trim())) {
                    form.setField("insurantCompanyInfo2","" );
                }
                else {
                    form.setField("insurantCompanyInfo2",insurantCompanyInfo2 );
                }

                // 投保人和受保人 --->区号 +电话号码显示
                form.setField("insurantPhoneNumber",
                        formMap.get("insurantPhoneCode") + " " + formMap.get("insurantPhone"));
                // 判断申请人是受保人还是受益人选择对应勾选框
            }

            stamper.setFormFlattening(true);
            stamper.close();
            response.setContentType("application/pdf");
            response.setContentLength(ba.size());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            try {
                ServletOutputStream out = response.getOutputStream();
                ba.writeTo(out);
                out.flush();
                out.close();
                ba.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * aia 合同报表 add by 谈晟
     *
     * @param request
     * @param response
     * @param orderId
     */
    @Override
    public void aiaContractPDF(HttpServletRequest request, HttpServletResponse response, String orderId,
                               IRequest requestContext) {
        response.reset();
        ByteArrayOutputStream ba = new ByteArrayOutputStream();

		/* 获取模板文件路径 */
        String pdfTemplate = request.getSession().getServletContext().getRealPath("/");
        pdfTemplate = pdfTemplate + "/template/aia合同.pdf"; // aia合同pdf模板所在的路径。

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		/* 获取对应保单 */
        OrdOrder ordOrder = ordOrderMapper.selectOrdOrder(Long.valueOf(orderId));
		/* 获取投保人和受保人 */
        OrdCustomer ordCustomer = new OrdCustomer();
        ordCustomer.setOrderId(Long.valueOf(orderId));
        ordCustomer.setCustomerType("APPLICANT"); // 投保人
        OrdCustomer applicantCustomer = ordCustomerService.queryOrdCustomer(requestContext, ordCustomer, 1, 10).get(0);
        ordCustomer.setCustomerType("INSURANT"); // 受保人
        OrdCustomer insuredCustomer = ordCustomerService.queryOrdCustomer(requestContext, ordCustomer, 1, 10).get(0);
		/* 获取受益人List */
        List<OrdBeneficiary> beneficiaries = ordBeneficiaryMapper.selectAllBeneficiary(Long.valueOf(orderId));

        ClbCodeValue clbCodeValue = new ClbCodeValue();
        clbCodeValue.setCodeId(10022L);
        List<ClbCodeValue> provinceList = clbCodeService.selectCodeValues(requestContext, clbCodeValue);
        clbCodeValue.setCodeId(10047L);
        List<ClbCodeValue> cityList = clbCodeService.selectCodeValues(requestContext, clbCodeValue);

		/* 解析pdf */
        try {
            PdfReader reader = new PdfReader(pdfTemplate);
            PdfStamper stamper = new PdfStamper(reader, ba);
            BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            AcroFields form = stamper.getAcroFields();
            form.addSubstitutionFont(bf);

			/* 根据表单数据往PDF中写入数据 */

			/* 投保人和受保人不相同时才需要填写的字段 */
            if (applicantCustomer != null && insuredCustomer != null
                    && !applicantCustomer.getEnglishName().equals(insuredCustomer.getEnglishName())) {
				/* 投保人英文姓名 */
                if (!StringUtils.isBlank(applicantCustomer.getEnglishName())) {
                    String name[] = applicantCustomer.getEnglishName().trim().split(Constants.BLANK);
                    form.setField("applicantLastName", name[0]);
                    form.setField("applicantFirstName", name[1]);
                }
				/* 投保人中文姓名 */
                if (!StringUtils.isBlank(applicantCustomer.getEnglishName())) {
                    form.setField("reApplicantChineseName", applicantCustomer.getChineseName());
                }
				/* 投保人性别 */
                if (!StringUtils.isBlank(applicantCustomer.getSex())) {
                    if ("M".equals(applicantCustomer.getSex())) {
                        form.setField("applicantSexF", Constants.onNoCheck);
                    } else {
                        form.setField("applicantSexM", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantSexM", Constants.onNoCheck);
                    form.setField("applicantSexF", Constants.onNoCheck);
                }
				/* 投保人身份证号 */
                if (!StringUtils.isBlank(applicantCustomer.getIdentityNumber())) {
                    form.setField("applicantIDCard", applicantCustomer.getIdentityNumber().trim());
                    if (StringUtils.isNotBlank(applicantCustomer.getNationality())) {
                        String applicantNational = applicantCustomer.getNationality().trim();
                        if ("Hong Kong".equals(applicantNational)) { // 国籍为香港
                            form.setField("applicantNOHKCheck", Constants.onNoCheck);
                            form.setField("applicantIDCheck", Constants.onNoCheck);
                            form.setField("applicationPassportCheck", Constants.onNoCheck);
                            form.setField("applicantHKIDCard", applicantCustomer.getIdentityNumber());
                        } else if ("China".equals(applicantNational) || "Taiwan".equals(applicantNational)
                                || "MO".equals(applicantNational)) { // 国籍为大陆、台湾、澳门
                            form.setField("applicantHKCheck", Constants.onNoCheck);
                            form.setField("applicationPassportCheck", Constants.onNoCheck);
                            form.setField("applicantNOHKIDCard", applicantCustomer.getIdentityNumber());
                        } else { // 国籍为外国
                            form.setField("applicantHKCheck", Constants.onNoCheck);
                            form.setField("applicantNOHKCheck", Constants.onNoCheck);
                            form.setField("applicantIDCheck", Constants.onNoCheck);
                            form.setField("applicantPassportNum", applicantCustomer.getIdentityNumber());
                        }
                    } else {
                        form.setField("applicantHKCheck", Constants.onNoCheck);
                        form.setField("applicantNOHKCheck", Constants.onNoCheck);
                        form.setField("applicantIDCheck", Constants.onNoCheck);
                        form.setField("applicationPassportCheck", Constants.onNoCheck);
                    }
                }
				/* 投保人生日 */
                if (applicantCustomer.getBirthDate() != null
                        && !StringUtils.isBlank(String.valueOf(applicantCustomer.getBirthDate()))) {
                    // 将出生日期拆分为年月日
                    String[] dateArray = sdf.format(applicantCustomer.getBirthDate()).split("-");
                    form.setField("reApplicantBirthYear", dateArray[0]);
                    form.setField("reApplicantBirthMonth", dateArray[1]);
                    form.setField("reApplicantBirthDay", dateArray[2]);
                }
                List<String> marriages = new ArrayList<>(Arrays.asList("Single", "Married", "Bereft", "Divorce"));
                if (!StringUtils.isBlank(applicantCustomer.getMarriageStatus())) {
                    marriages.remove(marriages.indexOf(captureName(applicantCustomer.getMarriageStatus().trim())));
                }
                for (String marriage : marriages) {
                    form.setField("reApplicantMarriage" + marriage, Constants.onNoCheck);
                }
				/* 投保人身份国籍 */
                if (!StringUtils.isBlank(applicantCustomer.getIdentityNation())) {
                    form.setField("applicantIdentityNation", applicantCustomer.getIdentityNation().trim());
                }
				/* 投保人国籍 */
                if (!StringUtils.isBlank(applicantCustomer.getNationality())) {
                    form.setField("reApplicantNationality", applicantCustomer.getNationality().trim());
                    if (StringUtils.isNotBlank(applicantCustomer.getIdentityNation())
                            && "Hong Kong".equals(applicantCustomer.getIdentityNation().trim())) {
                        form.setField("applicantIdentityNation", "");
                    }
                }
				/* 投保人身份地址 */
                if (!StringUtils.isBlank(applicantCustomer.getIdentityAddress())) {
                    String baseAddress = "";
                    if (StringUtils.isNotBlank(applicantCustomer.getIdentityProvince())) {
                        for (ClbCodeValue codeValue : provinceList) {
                            if (codeValue.getValue().equals(applicantCustomer.getIdentityProvince())) {
                                baseAddress += codeValue.getDescription();
                            }
                        }
                    }
                    if (StringUtils.isNotBlank(applicantCustomer.getIdentityCity())) {
                        for (ClbCodeValue codeValue : cityList) {
                            if (codeValue.getValue().equals(applicantCustomer.getIdentityCity())) {
                                baseAddress += codeValue.getDescription();
                            }
                        }
                    }
                    baseAddress += applicantCustomer.getIdentityAddress();
                    form.setField("reApplicantAddress", baseAddress);
                }
				/* 投保人公司名称 */
                if (!StringUtils.isBlank(applicantCustomer.getCompanyName())) {
                    form.setField("applicantCompanyName", applicantCustomer.getCompanyName().trim());
                }
				/* 投保人公司地址 */
                if (!StringUtils.isBlank(applicantCustomer.getCompanyAddress())) {
                    String baseAddress = "";
                    if (StringUtils.isNotBlank(applicantCustomer.getCompanyProvince())) {
                        for (ClbCodeValue codeValue : provinceList) {
                            if (codeValue.getValue().equals(applicantCustomer.getCompanyProvince())) {
                                baseAddress += codeValue.getDescription();
                            }
                        }
                    }
                    if (StringUtils.isNotBlank(applicantCustomer.getCompanyCity())) {
                        for (ClbCodeValue codeValue : cityList) {
                            if (codeValue.getValue().equals(applicantCustomer.getCompanyCity())) {
                                baseAddress += codeValue.getDescription();
                            }
                        }
                    }
                    baseAddress += applicantCustomer.getCompanyAddress();
                    form.setField("applicantCompanyAddress", baseAddress);
                }
				/* 投保人职位 */
                if (!StringUtils.isBlank(applicantCustomer.getPosition())) {
                    form.setField("reApplicantPosition", applicantCustomer.getPosition().trim());
                }
				/* 投保人公司性质 */
                if (!StringUtils.isBlank(applicantCustomer.getIndustry())) {
                    form.setField("reApplicantIndustry", applicantCustomer.getIndustry().trim());
                }
				/* 投保人收入 */
                if (!StringUtils.isBlank(applicantCustomer.getIncome().toString())) {
                    form.setField("reApplicantIncome", String.valueOf(Math.round(applicantCustomer.getIncome())));
                }
				/* 您是否曾被保险公司拒保、推迟承保、增加保费或更改首保条件 */
				/* 投保人选择 */
                if (StringUtils.isNotBlank(applicantCustomer.getBadFlag())) {
                    if ("N".equals(applicantCustomer.getBadFlag().trim())) {
                        form.setField("applicantBadFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("applicantBadFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantBadFlag_y", Constants.onNoCheck);
                    form.setField("applicantBadFlag_n", Constants.onNoCheck);
                }
				/* 您是否出于因工作或娱乐目的，参与或计划参与任何危险性活动？例如潜水、赛车、飞行 */
				/* 投保人选择 */
                if (StringUtils.isNotBlank(applicantCustomer.getDangerousFlag())) {
                    if ("N".equals(applicantCustomer.getDangerousFlag())) {
                        form.setField("applicantDangerousFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("applicantDangerousFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantDangerousFlag_y", Constants.onNoCheck);
                    form.setField("applicantDangerousFlag_n", Constants.onNoCheck);
                }
				/* 是否吸烟 */
				/* 投保人选择 */
                if (StringUtils.isNotBlank(applicantCustomer.getSmokeFlag())) {
                    if ("N".equals(applicantCustomer.getSmokeFlag())) {
                        form.setField("applicantSmokeFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("applicantSmokeFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantSmokeFlag_y", Constants.onNoCheck);
                    form.setField("applicantSmokeFlag_n", Constants.onNoCheck);
                }
				/* 投保人身高 */
                if (applicantCustomer.getHeight() != null && applicantCustomer.getHeight() != 0L
                        && StringUtils.isNotBlank(String.valueOf(applicantCustomer.getHeight()))) {
                    form.setField("applicantHeight", applicantCustomer.getHeight().toString());
                } else {
                    form.setField("applicantHeightBox", Constants.onNoCheck);
                }
				/* 投保人体重 */
                if (applicantCustomer.getWeight() != null && applicantCustomer.getWeight() != 0L
                        && StringUtils.isNotBlank(String.valueOf(applicantCustomer.getWeight()))) {
                    form.setField("applicantWeight", applicantCustomer.getWeight().toString());
                } else {
                    form.setField("applicantWeightBox", Constants.onNoCheck);
                }
				/* 您的父母、兄弟姐妹或子女曾否被诊断患... */
				/* 投保人选择 */
                if (StringUtils.isNotBlank(applicantCustomer.getHereditaryFlag())) {
                    if ("N".equals(applicantCustomer.getHereditaryFlag())) {
                        form.setField("applicantFatherHealth_y", Constants.onNoCheck);
                    } else {
                        form.setField("applicantFatherHealth_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantFatherHealth_y", Constants.onNoCheck);
                    form.setField("applicantFatherHealth_n", Constants.onNoCheck);
                }
				/* 是否饮酒 */
				/* 投保人选择 */
                if (StringUtils.isNotBlank(applicantCustomer.getDrinkFlag())) {
                    if ("N".equals(applicantCustomer.getDrinkFlag())) {
                        form.setField("applicantDrinkFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("applicantDrinkFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantDrinkFlag_y", Constants.onNoCheck);
                    form.setField("applicantDrinkFlag_n", Constants.onNoCheck);
                }
				/* 是否呼吸系统疾病 */
				/* 投保人选择 */
                if (StringUtils.isNotBlank(applicantCustomer.getRespirationFlag())) {
                    if ("N".equals(applicantCustomer.getRespirationFlag())) {
                        form.setField("applicantRespirationFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("applicantRespirationFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantRespirationFlag_y", Constants.onNoCheck);
                    form.setField("applicantRespirationFlag_n", Constants.onNoCheck);
                }
				/* 是否循环系统疾病 */
				/* 投保人选择 */
                if (StringUtils.isNotBlank(applicantCustomer.getCycleFlag())) {
                    if ("N".equals(applicantCustomer.getCycleFlag())) {
                        form.setField("applicantCycleFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("applicantCycleFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantCycleFlag_y", Constants.onNoCheck);
                    form.setField("applicantCycleFlag_n", Constants.onNoCheck);
                }
				/* 是否生殖系统疾病 */
				/* 投保人选择 */
                if (StringUtils.isNotBlank(applicantCustomer.getReproductionFlag())) {
                    if ("N".equals(applicantCustomer.getReproductionFlag())) {
                        form.setField("applicantReproductionFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("applicantReproductionFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantReproductionFlag_y", Constants.onNoCheck);
                    form.setField("applicantReproductionFlag_n", Constants.onNoCheck);
                }
				/* 是否精神失常 */
				/* 投保人选择 */
                if (StringUtils.isNotBlank(applicantCustomer.getSpiritFlag())) {
                    if ("N".equals(applicantCustomer.getSpiritFlag())) {
                        form.setField("applicantSpiritFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("applicantSpiritFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantSpiritFlag_y", Constants.onNoCheck);
                    form.setField("applicantSpiritFlag_n", Constants.onNoCheck);
                }
				/* 是否肿瘤癌症 */
				/* 投保人选择 */
                if (StringUtils.isNotBlank(applicantCustomer.getTumorFlag())) {
                    if ("N".equals(applicantCustomer.getTumorFlag())) {
                        form.setField("applicantTumorFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("applicantTumorFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantTumorFlag_y", Constants.onNoCheck);
                    form.setField("applicantTumorFlag_n", Constants.onNoCheck);
                }
				/* 是否骨骼疾病 */
				/* 投保人选择 */
                if (StringUtils.isNotBlank(applicantCustomer.getJointFlag())) {
                    if ("N".equals(applicantCustomer.getJointFlag())) {
                        form.setField("applicantJointFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("applicantJointFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantJointFlag_y", Constants.onNoCheck);
                    form.setField("applicantJointFlag_n", Constants.onNoCheck);
                }
				/* 心电图、血液或小便检查 */
				/* 投保人选择 */
                if (StringUtils.isNotBlank(applicantCustomer.getExaminationFlag())) {
                    if ("N".equals(applicantCustomer.getExaminationFlag())) {
                        form.setField("applicantExaminationFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("applicantExaminationFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantExaminationFlag_y", Constants.onNoCheck);
                    form.setField("applicantExaminationFlag_n", Constants.onNoCheck);
                }
				/* 心理疾病 */
				/* 投保人选择 */
                if (StringUtils.isNotBlank(applicantCustomer.getOtherFlag())) {
                    if ("N".equals(applicantCustomer.getOtherFlag())) {
                        form.setField("applicantOtherFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("applicantOtherFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantOtherFlag_y", Constants.onNoCheck);
                    form.setField("applicantOtherFlag_n", Constants.onNoCheck);
                }
				/* 艾滋 */
				/* 投保人选择 */
                if (StringUtils.isNotBlank(applicantCustomer.getAidsFlag())) {
                    if ("N".equals(applicantCustomer.getAidsFlag())) {
                        form.setField("applicantAidsFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("applicantAidsFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantAidsFlag_y", Constants.onNoCheck);
                    form.setField("applicantAidsFlag_n", Constants.onNoCheck);
                }
				/* 成瘾药品或毒品 */
				/* 投保人选择 */
                if (StringUtils.isNotBlank(applicantCustomer.getDrugFlag())) {
                    if ("N".equals(applicantCustomer.getDrugFlag())) {
                        form.setField("applicantDrugFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("applicantDrugFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantDrugFlag_y", Constants.onNoCheck);
                    form.setField("applicantDrugFlag_n", Constants.onNoCheck);
                }
                if (StringUtils.isNotBlank(applicantCustomer.getSex()) && "F".equals(applicantCustomer.getSex())) {
					/* 你现在是否怀孕 */
					/* 投保人选择 */
                    if (StringUtils.isNotBlank(applicantCustomer.getPregnancyFlag())) {
                        if ("N".equals(applicantCustomer.getPregnancyFlag())) {
                            form.setField("applicantPregnancyFlag_y", Constants.onNoCheck);
                        } else {
                            form.setField("applicantPregnancyFlag_n", Constants.onNoCheck);
                        }
                    } else {
                        form.setField("applicantPregnancyFlag_y", Constants.onNoCheck);
                        form.setField("applicantPregnancyFlag_n", Constants.onNoCheck);
                    }
					/* 您曾否因妇科问题... */
					/* 投保人选择 */
                    if (StringUtils.isNotBlank(applicantCustomer.getGynecologyFlag())) {
                        if ("N".equals(applicantCustomer.getGynecologyFlag())) {
                            form.setField("applicantGynecologyFlag_y", Constants.onNoCheck);
                        } else {
                            form.setField("applicantGynecologyFlag_n", Constants.onNoCheck);
                        }
                    } else {
                        form.setField("applicantGynecologyFlag_y", Constants.onNoCheck);
                        form.setField("applicantGynecologyFlag_n", Constants.onNoCheck);
                    }
					/* 您是否做过妇科检查... */
					/* 投保人选择 */
                    if (StringUtils.isNotBlank(applicantCustomer.getGynecologyOthFlag())) {
                        if ("N".equals(applicantCustomer.getGynecologyOthFlag())) {
                            form.setField("applicantGynecologyOthFlag_y", Constants.onNoCheck);
                        } else {
                            form.setField("applicantGynecologyOthFlag_n", Constants.onNoCheck);
                        }
                    } else {
                        form.setField("applicantGynecologyOthFlag_y", Constants.onNoCheck);
                        form.setField("applicantGynecologyOthFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("applicantPregnancyFlag_y", Constants.onNoCheck);
                    form.setField("applicantPregnancyFlag_n", Constants.onNoCheck);
                    form.setField("applicantGynecologyFlag_y", Constants.onNoCheck);
                    form.setField("applicantGynecologyFlag_n", Constants.onNoCheck);
                    form.setField("applicantGynecologyOthFlag_y", Constants.onNoCheck);
                    form.setField("applicantGynecologyOthFlag_n", Constants.onNoCheck);
                }
				/* 新生意表格 */
				/* 保单持有人 */
                if (!StringUtils.isBlank(applicantCustomer.getEnglishName())) {
                    form.setField("reApplicantName", applicantCustomer.getEnglishName());
                    form.setField("appName", applicantCustomer.getEnglishName()); // pdf
                    // 50
                }
				/* 投保人生日 */
                if (applicantCustomer.getBirthDate() != null
                        && !StringUtils.isBlank(String.valueOf(applicantCustomer.getBirthDate()))) {
                    // 将出生日期拆分为年月日
                    String[] dateArray = sdf.format(applicantCustomer.getBirthDate()).split("-");
                    form.setField("applicantBirthYear", dateArray[0]);
                    form.setField("applicantBirthMonth", dateArray[1]);
                    form.setField("applicantBirthDay", dateArray[2]);
                }
				/* 投保人年龄 */
                if (applicantCustomer.getBirthDate() != null
                        && StringUtils.isNotBlank(String.valueOf(applicantCustomer.getBirthDate()))) {
                    form.setField("applicantAge",
                            String.valueOf(DateUtil.getAgeByDate(applicantCustomer.getBirthDate())));
                }
				/* 投保人电话号码 需要区号+电话号码 */
                if (!StringUtils.isBlank(applicantCustomer.getPhone())) {
                    String basePhoneCode = "+86 "; // 默认区号为+86
                    if (!StringUtils.isBlank(applicantCustomer.getPhoneCode())) { // 如果有区号则使用数据库里的区号
                        basePhoneCode = "+" + applicantCustomer.getPhoneCode() + " ";
                    }
                    form.setField("reApplicantPhone", basePhoneCode + applicantCustomer.getPhone());
                }
                form.setField("appDefault1", "1");
                form.setField("appDefault4", "4");
                // todo
            } else {
                form.setField("applicantSexM", Constants.onNoCheck);
                form.setField("applicantSexF", Constants.onNoCheck);
                form.setField("applicantHKCheck", Constants.onNoCheck);
                form.setField("applicantNOHKCheck", Constants.onNoCheck);
                form.setField("applicantIDCheck", Constants.onNoCheck);
                form.setField("applicationPassportCheck", Constants.onNoCheck);

                List<String> marriages = new ArrayList<>(Arrays.asList("Single", "Married", "Bereft", "Divorce"));
                for (String marriage : marriages) {
                    form.setField("reApplicantMarriage" + marriage, Constants.onNoCheck);
                }

                form.setField("applicantBadFlag_y", Constants.onNoCheck);
                form.setField("applicantBadFlag_n", Constants.onNoCheck);
                form.setField("applicantDangerousFlag_y", Constants.onNoCheck);
                form.setField("applicantDangerousFlag_n", Constants.onNoCheck);
                form.setField("applicantSmokeFlag_y", Constants.onNoCheck);
                form.setField("applicantSmokeFlag_n", Constants.onNoCheck);
                form.setField("applicantHeightBox", Constants.onNoCheck);
                form.setField("applicantWeightBox", Constants.onNoCheck);
                form.setField("applicantFatherHealth_y", Constants.onNoCheck);
                form.setField("applicantFatherHealth_n", Constants.onNoCheck);
                form.setField("applicantDrinkFlag_y", Constants.onNoCheck);
                form.setField("applicantDrinkFlag_n", Constants.onNoCheck);
                form.setField("applicantRespirationFlag_y", Constants.onNoCheck);
                form.setField("applicantRespirationFlag_n", Constants.onNoCheck);
                form.setField("applicantCycleFlag_y", Constants.onNoCheck);
                form.setField("applicantCycleFlag_n", Constants.onNoCheck);
                form.setField("applicantReproductionFlag_y", Constants.onNoCheck);
                form.setField("applicantReproductionFlag_n", Constants.onNoCheck);
                form.setField("applicantSpiritFlag_y", Constants.onNoCheck);
                form.setField("applicantSpiritFlag_n", Constants.onNoCheck);
                form.setField("applicantTumorFlag_y", Constants.onNoCheck);
                form.setField("applicantTumorFlag_n", Constants.onNoCheck);
                form.setField("applicantJointFlag_y", Constants.onNoCheck);
                form.setField("applicantJointFlag_n", Constants.onNoCheck);
                form.setField("applicantExaminationFlag_y", Constants.onNoCheck);
                form.setField("applicantExaminationFlag_n", Constants.onNoCheck);
                form.setField("applicantOtherFlag_y", Constants.onNoCheck);
                form.setField("applicantOtherFlag_n", Constants.onNoCheck);
                form.setField("applicantAidsFlag_y", Constants.onNoCheck);
                form.setField("applicantAidsFlag_n", Constants.onNoCheck);
                form.setField("applicantDrugFlag_y", Constants.onNoCheck);
                form.setField("applicantDrugFlag_n", Constants.onNoCheck);
                form.setField("applicantPregnancyFlag_y", Constants.onNoCheck);
                form.setField("applicantPregnancyFlag_n", Constants.onNoCheck);
                form.setField("applicantGynecologyFlag_y", Constants.onNoCheck);
                form.setField("applicantGynecologyFlag_n", Constants.onNoCheck);
                form.setField("applicantGynecologyOthFlag_y", Constants.onNoCheck);
                form.setField("applicantGynecologyOthFlag_n", Constants.onNoCheck);

                form.setField("defaultNoCheck", Constants.onNoCheck);
            }
			/* 投险申请书 */
			/* 保单号码 */
            if (!StringUtils.isBlank(ordOrder.getPolicyNumber())) {
                form.setField("policyNum", ordOrder.getPolicyNumber());
                char[] pn = ordOrder.getPolicyNumber().toCharArray();
                for (int i = 0; i < pn.length; i++) {
                    form.setField("policyNum" + i, String.valueOf(pn[i]));
                }
            }
			/* 受保人英文姓名 */
            if (!StringUtils.isBlank(insuredCustomer.getEnglishName())) {
                String name[] = insuredCustomer.getEnglishName().trim().split(Constants.BLANK);
                form.setField("insuredLastName", name[0]);
                form.setField("insuredFirstName", name[1]);
            }
			/* 受保人中文姓名 */
            if (!StringUtils.isBlank(insuredCustomer.getChineseName())) {
                form.setField("insuredChineseName", insuredCustomer.getChineseName().trim());
            }
			/* 受保人性别 */
            if (!StringUtils.isBlank(insuredCustomer.getSex())) {
                if ("M".equals(insuredCustomer.getSex())) {
                    form.setField("insuredSexF", Constants.onNoCheck);
                } else {
                    form.setField("insuredSexM", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredSexM", Constants.onNoCheck);
                form.setField("insuredSexF", Constants.onNoCheck);
            }
			/* 受保人身份证号 */
            if (StringUtils.isNotBlank(insuredCustomer.getIdentityNumber())) {
                form.setField("insuredIDCard", insuredCustomer.getIdentityNumber().trim());
                if (StringUtils.isNotBlank(insuredCustomer.getNationality())) {
                    String insuredNational = insuredCustomer.getNationality().trim();
                    if ("Hong Kong".equals(insuredNational)) { // 国籍为香港
                        form.setField("insuredNOHKCheck", Constants.onNoCheck);
                        form.setField("insuredIDCheck", Constants.onNoCheck);
                        form.setField("insuredPassportCheck", Constants.onNoCheck);
                        form.setField("insuredHKIDCard", insuredCustomer.getIdentityNumber());
                    } else if ("China".equals(insuredNational) || "Taiwan".equals(insuredNational)
                            || "MO".equals(insuredNational)) { // 国籍为大陆、台湾、澳门
                        form.setField("insuredHKCheck", Constants.onNoCheck);
                        form.setField("insuredPassportCheck", Constants.onNoCheck);
                        form.setField("insuredNOHKIDCard", insuredCustomer.getIdentityNumber());
                    } else { // 国籍为外国
                        form.setField("insuredHKCheck", Constants.onNoCheck);
                        form.setField("insuredNOHKCheck", Constants.onNoCheck);
                        form.setField("insuredIDCheck", Constants.onNoCheck);
                        form.setField("insuredPassportNum", insuredCustomer.getIdentityNumber());
                    }
                } else {
                    form.setField("insuredHKCheck", Constants.onNoCheck);
                    form.setField("insuredNOHKCheck", Constants.onNoCheck);
                    form.setField("insuredIDCheck", Constants.onNoCheck);
                    form.setField("insuredPassportCheck", Constants.onNoCheck);
                }
            }
			/* 受保人生日 */
            if (insuredCustomer.getBirthDate() != null
                    && !StringUtils.isBlank(String.valueOf(insuredCustomer.getBirthDate()))) {
                // 将出生日期拆分为年月日
                String[] dateArray = sdf.format(insuredCustomer.getBirthDate()).split("-");
                form.setField("insuredBirthYear", dateArray[0]);
                form.setField("insuredBirthMonth", dateArray[1]);
                form.setField("insuredBirthDay", dateArray[2]);
            }
			/* 受保人婚姻状况 */
            List<String> marriages = new ArrayList<>(Arrays.asList("Single", "Married", "Bereft", "Divorce")); // 所有婚姻情况
            if (!StringUtils.isBlank(insuredCustomer.getMarriageStatus())) {
                marriages.remove(marriages.indexOf(captureName(insuredCustomer.getMarriageStatus().trim())));
            }
            for (String marriage : marriages) {
                form.setField("insuredMarriage" + marriage, Constants.onNoCheck);
            }
			/* 投保人婚姻状况 */
            marriages = new ArrayList<>(Arrays.asList("Single", "Married", "Bereft", "Divorce"));
            if (!StringUtils.isBlank(applicantCustomer.getMarriageStatus())) {
                marriages.remove(marriages.indexOf(captureName(applicantCustomer.getMarriageStatus().trim())));
            }
            for (String marriage : marriages) {
                form.setField("applicantMarriage" + marriage, Constants.onNoCheck);
            }
			/* 受保人身份国籍 */
            if (!StringUtils.isBlank(insuredCustomer.getIdentityNation())) {
                form.setField("insurantIdentityNation", insuredCustomer.getIdentityNation().trim());
            }
			/* 投保人身份国籍 */
            if (!StringUtils.isBlank(applicantCustomer.getIdentityNation())) {
                form.setField("reApplicantIdentityNation", applicantCustomer.getIdentityNation().trim());
            }
			/* 受保人国籍 */
            if (!StringUtils.isBlank(insuredCustomer.getNationality())) {
                form.setField("insurantNationality", insuredCustomer.getNationality().trim());
                if (StringUtils.isNotBlank(insuredCustomer.getNationality())
                        && "Hong Kong".equals(insuredCustomer.getNationality().trim())) {
                    form.setField("insurantIdentityNation", "");
                }
            }
			/* 投保人国籍 */
            if (!StringUtils.isBlank(applicantCustomer.getNationality())) {
                form.setField("applicantNationality", applicantCustomer.getNationality().trim());
                if (StringUtils.isNotBlank(applicantCustomer.getIdentityNation())
                        && "Hong Kong".equals(applicantCustomer.getIdentityNation().trim())) {
                    form.setField("applicantIdentityNation", "");
                }
                if ("Hong Kong".equals(applicantCustomer.getNationality())) {
                    form.setField("nationalNoHK", Constants.onNoCheck);
                } else {
                    form.setField("nationalHK", Constants.onNoCheck);
                }
            } else {
                form.setField("nationalHK", Constants.onNoCheck);
                form.setField("nationalNoHK", Constants.onNoCheck);
            }
			/* 受保人住宅地址 */
            if (!StringUtils.isBlank(insuredCustomer.getIdentityAddress())) {
                String baseAddress = "";
                if (StringUtils.isNotBlank(insuredCustomer.getIdentityProvince())) {
                    for (ClbCodeValue codeValue : provinceList) {
                        if (codeValue.getValue().equals(insuredCustomer.getIdentityProvince())) {
                            baseAddress += codeValue.getDescription();
                        }
                    }
                }
                if (StringUtils.isNotBlank(insuredCustomer.getIdentityCity())) {
                    for (ClbCodeValue codeValue : cityList) {
                        if (codeValue.getValue().equals(insuredCustomer.getIdentityCity())) {
                            baseAddress += codeValue.getDescription();
                        }
                    }
                }
                baseAddress += insuredCustomer.getIdentityAddress();
                form.setField("insurantAddress", baseAddress);
            }
			/* 投保人身份地址 */
            if (!StringUtils.isBlank(applicantCustomer.getIdentityAddress())) {
                String baseAddress = "";
                if (StringUtils.isNotBlank(applicantCustomer.getIdentityProvince())) {
                    for (ClbCodeValue codeValue : provinceList) {
                        if (codeValue.getValue().equals(applicantCustomer.getIdentityProvince())) {
                            baseAddress += codeValue.getDescription();
                        }
                    }
                }
                if (StringUtils.isNotBlank(applicantCustomer.getIdentityCity())) {
                    for (ClbCodeValue codeValue : cityList) {
                        if (codeValue.getValue().equals(applicantCustomer.getIdentityCity())) {
                            baseAddress += codeValue.getDescription();
                        }
                    }
                }
                baseAddress += applicantCustomer.getIdentityAddress();
                form.setField("applicantAddress", baseAddress);
            }
			/* 投保人邮件地址 */
            if (!StringUtils.isBlank(applicantCustomer.getPostAddress())) {
                String baseAddress = "";
                if (StringUtils.isNotBlank(applicantCustomer.getPostProvince())) {
                    for (ClbCodeValue codeValue : provinceList) {
                        if (codeValue.getValue().equals(applicantCustomer.getPostProvince())) {
                            baseAddress += codeValue.getDescription();
                        }
                    }
                }
                if (StringUtils.isNotBlank(applicantCustomer.getPostCity())) {
                    for (ClbCodeValue codeValue : cityList) {
                        if (codeValue.getValue().equals(applicantCustomer.getPostCity())) {
                            baseAddress += codeValue.getDescription();
                        }
                    }
                }
                baseAddress += applicantCustomer.getPostAddress();
                form.setField("mailingAddress", baseAddress);
            }
			/* 受保人邮件地址 */
            if (!StringUtils.isBlank(insuredCustomer.getPostAddress())) {
                String baseAddress = "";
                if (StringUtils.isNotBlank(insuredCustomer.getPostProvince())) {
                    for (ClbCodeValue codeValue : provinceList) {
                        if (codeValue.getValue().equals(insuredCustomer.getPostProvince())) {
                            baseAddress += codeValue.getDescription();
                        }
                    }
                }
                if (StringUtils.isNotBlank(insuredCustomer.getPostCity())) {
                    for (ClbCodeValue codeValue : cityList) {
                        if (codeValue.getValue().equals(insuredCustomer.getPostCity())) {
                            baseAddress += codeValue.getDescription();
                        }
                    }
                }
                baseAddress += insuredCustomer.getPostAddress();
                form.setField("insuredMailingAddress", baseAddress);
            }
			/* 投保人手机号区号 */
            if (StringUtils.isNotBlank(applicantCustomer.getPhoneCode())) {
                form.setField("phoneCode", applicantCustomer.getPhoneCode());
            }
			/* 投保人手机号 */
            if (StringUtils.isNotBlank(applicantCustomer.getPhone())) {
                form.setField("phone", applicantCustomer.getPhone());
            }
			/* 投保人邮箱 */
            if (!StringUtils.isBlank(applicantCustomer.getEmail())) {
                form.setField("email", applicantCustomer.getEmail().trim());
            }
			/* 受保人公司名称 */
            if (!StringUtils.isBlank(insuredCustomer.getCompanyName())) {
                form.setField("insuredCompanyName", insuredCustomer.getCompanyName().trim());
            }
			/* 受保人公司地址 */
            if (!StringUtils.isBlank(insuredCustomer.getCompanyAddress())) {
                String baseAddress = "";
                if (StringUtils.isNotBlank(insuredCustomer.getCompanyProvince())) {
                    for (ClbCodeValue codeValue : provinceList) {
                        if (codeValue.getValue().equals(insuredCustomer.getCompanyProvince())) {
                            baseAddress += codeValue.getDescription();
                        }
                    }
                }
                if (StringUtils.isNotBlank(insuredCustomer.getCompanyCity())) {
                    for (ClbCodeValue codeValue : cityList) {
                        if (codeValue.getValue().equals(insuredCustomer.getCompanyCity())) {
                            baseAddress += codeValue.getDescription();
                        }
                    }
                }
                baseAddress += insuredCustomer.getCompanyAddress();
                form.setField("insuredCompanyAddress", baseAddress);
            }
			/* 受保人职位 */
            if (!StringUtils.isBlank(insuredCustomer.getPosition())) {
                form.setField("insuredPosition", insuredCustomer.getPosition().trim());
            }
			/* 投保人职位 */
            if (!StringUtils.isBlank(applicantCustomer.getPosition())) {
                form.setField("applicantPosition", applicantCustomer.getPosition().trim());
            }
			/* 受保人公司性质 */
            if (!StringUtils.isBlank(insuredCustomer.getIndustry())) {
                form.setField("insuredIndustry", insuredCustomer.getIndustry().trim());
            }
			/* 投保人公司性质 */
            if (!StringUtils.isBlank(applicantCustomer.getIndustry())) {
                form.setField("applicantIndustry", applicantCustomer.getIndustry().trim());
            }
			/* 受保人收入 */
            if (insuredCustomer.getIncome() != null) {
                form.setField("insuredIncome", String.valueOf(Math.round(insuredCustomer.getIncome())));
            }
			/* 投保人每月支出 */
            if (!StringUtils.isBlank(String.valueOf(applicantCustomer.getAmountPerMonth()))) {
                form.setField("applicantExpenditure",
                        String.valueOf(Math.round(applicantCustomer.getAmountPerMonth())));
            }
			/* 保单是否回溯 */
            if (StringUtils.isNotBlank(ordOrder.getBackFlag()) && "Y".equals(ordOrder.getBackFlag().trim())) {
                if (ordOrder.getBackDate() != null) {
                    String[] dateArray = sdf.format(ordOrder.getBackDate()).split("-");
                    form.setField("backYear", dateArray[0]);
                    form.setField("backMonth", dateArray[1]);
                    form.setField("backDay", dateArray[2]);
                }
            } else {
                form.setField("backFlag", Constants.onNoCheck);
            }
			/* 投保人学历 */
            if (!StringUtils.isBlank(applicantCustomer.getEducation())) {
                String appEducation = applicantCustomer.getEducation();
                if ("BPS".equals(appEducation) || "PS".equals(appEducation)) { // 小学或以下
                    form.setField("middle", Constants.onNoCheck);
                    form.setField("college", Constants.onNoCheck);

                    form.setField("appSecondary", Constants.onNoCheck);
                    form.setField("appTertiary", Constants.onNoCheck);
                } else if ("JMS".equals(appEducation) || "HS".equals(appEducation)) { // 中学
                    form.setField("grade", Constants.onNoCheck);
                    form.setField("college", Constants.onNoCheck);

                    form.setField("appPrimary", Constants.onNoCheck);
                    form.setField("appTertiary", Constants.onNoCheck);
                } else { // 大专或以上
                    form.setField("grade", Constants.onNoCheck);
                    form.setField("middle", Constants.onNoCheck);

                    form.setField("appPrimary", Constants.onNoCheck);
                    form.setField("appSecondary", Constants.onNoCheck);
                }
            } else {
                form.setField("grade", Constants.onNoCheck);
                form.setField("middle", Constants.onNoCheck);
                form.setField("college", Constants.onNoCheck);

                form.setField("appPrimary", Constants.onNoCheck);
                form.setField("appSecondary", Constants.onNoCheck);
                form.setField("appTertiary", Constants.onNoCheck);
            }
			/* 投保人流动资产 */
            if (!StringUtils.isBlank(String.valueOf(applicantCustomer.getCurrentAssets()))) {
                form.setField("assets", String.valueOf(Math.round(applicantCustomer.getCurrentAssets())));
            }
			/* 币种 */
            if (!StringUtils.isBlank(ordOrder.getCurrency())) {
                String currency = ordOrder.getCurrency().trim();
                if ("USD".equals(currency)) {
                    form.setField("currencyHK", Constants.onNoCheck);
                    form.setField("currencyOther", Constants.onNoCheck);
                    form.setField("USD", "");
                } else if ("HKD".equals(currency)) {
                    form.setField("currencyUS", Constants.onNoCheck);
                    form.setField("currencyOther", Constants.onNoCheck);
                    form.setField("HKD", "");
                } else {
                    form.setField("currencyUS", Constants.onNoCheck);
                    form.setField("currencyHK", Constants.onNoCheck);
                    form.setField("currencyTxt", ordOrder.getCurrency().trim());
                    form.setField("currencyTxt2", ordOrder.getCurrency().trim());
                    form.setField("USD", "");
                    form.setField("HKD", "");
                }
            } else {
                form.setField("currencyUS", Constants.onNoCheck);
                form.setField("currencyHK", Constants.onNoCheck);
                form.setField("currencyOther", Constants.onNoCheck);
            }
			/* 缴费方式 */
            List<String> payMethod = new ArrayList<>(Arrays.asList("MP", "QP", "SAP", "AP", "WP", "FJ"));
            if (!StringUtils.isBlank(ordOrder.getPayMethod())) {
                String basePayMethod = ordOrder.getPayMethod().trim();
                payMethod.remove(payMethod.indexOf(basePayMethod));
                form.setField(basePayMethod, "");
                if (!"AP".equals(basePayMethod) && !"SAP".equals(basePayMethod) && !"QP".equals(basePayMethod)
                        && !"MP".equals(basePayMethod)) {
                    form.setField("AP", "");
                    form.setField("SAP", "");
                    form.setField("QP", "");
                    form.setField("MP", "");
                }
                String payMethodMonth = "";
                if ("WP".equals(basePayMethod) || "AP".equals(basePayMethod) || "FJ".equals(basePayMethod)) {
                    payMethodMonth = "12";
                } else if ("MP".equals(basePayMethod)) {
                    payMethodMonth = "1";
                } else if ("QP".equals(basePayMethod)) {
                    payMethodMonth = "3";
                } else if ("SAP".equals(basePayMethod)) {
                    payMethodMonth = "6";
                }
                form.setField("payMethodMonth", payMethodMonth);
            }
            for (String s : payMethod) {
                form.setField("payMethod" + s, Constants.onNoCheck);
            }
			/* 受益人,如果有多个，只取前四个 */
            if (beneficiaries.size() > 4) {
                beneficiaries = beneficiaries.subList(0, 4);
            }
            int i = 1;
            for (OrdBeneficiary beneficiary : beneficiaries) {
                form.setField("engName" + i, beneficiary.getEnglishName());
                form.setField("chineseName" + i, beneficiary.getChineseName());
				/* 建立百分比格式化引用 */
                NumberFormat percent = NumberFormat.getPercentInstance();
                percent.setMaximumFractionDigits(2);
                form.setField("share" + i, percent.format(beneficiary.getRate()));
                form.setField("relationship" + i, beneficiary.getRelationship());
                form.setField("beneficiaryAge" + i, beneficiary.getAge().toString());
                form.setField("IDCard" + i, beneficiary.getIdentityNumber());
                i = i + 1;
            }
			/* 您是否曾被保险公司拒保、推迟承保、增加保费或更改首保条件 */
			/* 受保人选择 */
            if (StringUtils.isNotBlank(insuredCustomer.getBadFlag())) {
                if ("N".equals(insuredCustomer.getBadFlag().trim())) {
                    form.setField("insuredBadFlag_y", Constants.onNoCheck);
                } else {
                    form.setField("insuredBadFlag_n", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredBadFlag_y", Constants.onNoCheck);
                form.setField("insuredBadFlag_n", Constants.onNoCheck);
            }
			/* 您是否出于因工作或娱乐目的，参与或计划参与任何危险性活动？例如潜水、赛车、飞行 */
			/* 受保人选择 */
            if (StringUtils.isNotBlank(insuredCustomer.getDangerousFlag())) {
                if ("N".equals(insuredCustomer.getDangerousFlag())) {
                    form.setField("insuredDangerousFlag_y", Constants.onNoCheck);
                } else {
                    form.setField("insuredDangerousFlag_n", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredDangerousFlag_y", Constants.onNoCheck);
                form.setField("insuredDangerousFlag_n", Constants.onNoCheck);
            }
			/* 是否吸烟 */
			/* 受保人选择 */
            if (StringUtils.isNotBlank(insuredCustomer.getSmokeFlag())) {
                if ("N".equals(insuredCustomer.getSmokeFlag())) {
                    form.setField("insuredSmokeFlag_y", Constants.onNoCheck);
                } else {
                    form.setField("insuredSmokeFlag_n", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredSmokeFlag_y", Constants.onNoCheck);
                form.setField("insuredSmokeFlag_n", Constants.onNoCheck);
            }
			/* 受保人身高 */
            if (insuredCustomer.getHeight() != null && insuredCustomer.getHeight() != 0L
                    && StringUtils.isNotBlank(String.valueOf(insuredCustomer.getHeight()))) {
                form.setField("insuredHeight", insuredCustomer.getHeight().toString());
            } else {
                form.setField("insuredHeightBox", Constants.onNoCheck);
            }
			/* 受保人体重 */
            if (insuredCustomer.getWeight() != null && insuredCustomer.getWeight() != 0L
                    && StringUtils.isNotBlank(String.valueOf(insuredCustomer.getWeight()))) {
                form.setField("insuredWeight", insuredCustomer.getWeight().toString());
            } else {
                form.setField("insuredWeightBox", Constants.onNoCheck);
            }
			/* 您的父母、兄弟姐妹或子女曾否被诊断患... */
			/* 受保人选择 */
            if (StringUtils.isNotBlank(insuredCustomer.getHereditaryFlag())) {
                if ("N".equals(insuredCustomer.getHereditaryFlag())) {
                    form.setField("insuredFatherHealth_y", Constants.onNoCheck);
                } else {
                    form.setField("insuredFatherHealth_n", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredFatherHealth_y", Constants.onNoCheck);
                form.setField("insuredFatherHealth_n", Constants.onNoCheck);
            }
			/* 是否饮酒 */
			/* 受保人选择 */
            if (StringUtils.isNotBlank(insuredCustomer.getDrinkFlag())) {
                if ("N".equals(insuredCustomer.getDrinkFlag())) {
                    form.setField("insuredDrinkFlag_y", Constants.onNoCheck);
                } else {
                    form.setField("insuredDrinkFlag_n", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredDrinkFlag_y", Constants.onNoCheck);
                form.setField("insuredDrinkFlag_n", Constants.onNoCheck);
            }
			/* 是否呼吸系统疾病 */
			/* 受保人选择 */
            if (StringUtils.isNotBlank(insuredCustomer.getRespirationFlag())) {
                if ("N".equals(insuredCustomer.getRespirationFlag())) {
                    form.setField("insuredRespirationFlag_y", Constants.onNoCheck);
                } else {
                    form.setField("insuredRespirationFlag_n", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredRespirationFlag_y", Constants.onNoCheck);
                form.setField("insuredRespirationFlag_n", Constants.onNoCheck);
            }
			/* 是否循环系统疾病 */
			/* 受保人选择 */
            if (StringUtils.isNotBlank(insuredCustomer.getCycleFlag())) {
                if ("N".equals(insuredCustomer.getCycleFlag())) {
                    form.setField("insuredCycleFlag_y", Constants.onNoCheck);
                } else {
                    form.setField("insuredCycleFlag_n", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredCycleFlag_y", Constants.onNoCheck);
                form.setField("insuredCycleFlag_n", Constants.onNoCheck);
            }
			/* 是否生殖系统疾病 */
			/* 受保人选择 */
            if (StringUtils.isNotBlank(insuredCustomer.getReproductionFlag())) {
                if ("N".equals(insuredCustomer.getReproductionFlag())) {
                    form.setField("insuredReproductionFlag_y", Constants.onNoCheck);
                } else {
                    form.setField("insuredReproductionFlag_n", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredReproductionFlag_y", Constants.onNoCheck);
                form.setField("insuredReproductionFlag_n", Constants.onNoCheck);
            }
			/* 是否精神失常 */
			/* 受保人选择 */
            if (StringUtils.isNotBlank(insuredCustomer.getSpiritFlag())) {
                if ("N".equals(insuredCustomer.getSpiritFlag())) {
                    form.setField("insuredSpiritFlag_y", Constants.onNoCheck);
                } else {
                    form.setField("insuredSpiritFlag_n", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredSpiritFlag_y", Constants.onNoCheck);
                form.setField("insuredSpiritFlag_n", Constants.onNoCheck);
            }
			/* 是否肿瘤癌症 */
			/* 受保人选择 */
            if (StringUtils.isNotBlank(insuredCustomer.getTumorFlag())) {
                if ("N".equals(insuredCustomer.getTumorFlag())) {
                    form.setField("insuredTumorFlag_y", Constants.onNoCheck);
                } else {
                    form.setField("insuredTumorFlag_n", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredTumorFlag_y", Constants.onNoCheck);
                form.setField("insuredTumorFlag_n", Constants.onNoCheck);
            }
			/* 是否骨骼疾病 */
			/* 受保人选择 */
            if (StringUtils.isNotBlank(insuredCustomer.getJointFlag())) {
                if ("N".equals(insuredCustomer.getJointFlag())) {
                    form.setField("insuredJointFlag_y", Constants.onNoCheck);
                } else {
                    form.setField("insuredJointFlag_n", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredJointFlag_y", Constants.onNoCheck);
                form.setField("insuredJointFlag_n", Constants.onNoCheck);
            }
			/* 心电图、血液或小便检查 */
			/* 受保人选择 */
            if (StringUtils.isNotBlank(insuredCustomer.getExaminationFlag())) {
                if ("N".equals(insuredCustomer.getExaminationFlag())) {
                    form.setField("insuredExaminationFlag_y", Constants.onNoCheck);
                } else {
                    form.setField("insuredExaminationFlag_n", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredExaminationFlag_y", Constants.onNoCheck);
                form.setField("insuredExaminationFlag_n", Constants.onNoCheck);
            }
			/* 心理疾病 */
			/* 受保人选择 */
            if (StringUtils.isNotBlank(insuredCustomer.getOtherFlag())) {
                if ("N".equals(insuredCustomer.getOtherFlag())) {
                    form.setField("insuredOtherFlag_y", Constants.onNoCheck);
                } else {
                    form.setField("insuredOtherFlag_n", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredOtherFlag_y", Constants.onNoCheck);
                form.setField("insuredOtherFlag_n", Constants.onNoCheck);
            }
			/* 艾滋 */
			/* 受保人选择 */
            if (StringUtils.isNotBlank(insuredCustomer.getAidsFlag())) {
                if ("N".equals(insuredCustomer.getAidsFlag())) {
                    form.setField("insuredAidsFlag_y", Constants.onNoCheck);
                } else {
                    form.setField("insuredAidsFlag_n", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredAidsFlag_y", Constants.onNoCheck);
                form.setField("insuredAidsFlag_n", Constants.onNoCheck);
            }
			/* 成瘾药品或毒品 */
			/* 受保人选择 */
            if (StringUtils.isNotBlank(insuredCustomer.getDrugFlag())) {
                if ("N".equals(insuredCustomer.getDrugFlag())) {
                    form.setField("insuredDrugFlag_y", Constants.onNoCheck);
                } else {
                    form.setField("insuredDrugFlag_n", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredDrugFlag_y", Constants.onNoCheck);
                form.setField("insuredDrugFlag_n", Constants.onNoCheck);
            }
            if (StringUtils.isNotBlank(insuredCustomer.getSex()) && "F".equals(insuredCustomer.getSex())) {
				/* 你现在是否怀孕 */
				/* 受保人选择 */
                if (StringUtils.isNotBlank(insuredCustomer.getPregnancyFlag())) {
                    if ("N".equals(insuredCustomer.getPregnancyFlag())) {
                        form.setField("insuredPregnancyFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("insuredPregnancyFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("insuredPregnancyFlag_y", Constants.onNoCheck);
                    form.setField("insuredPregnancyFlag_n", Constants.onNoCheck);
                }
				/* 您曾否因妇科问题... */
				/* 受保人选择 */
                if (StringUtils.isNotBlank(insuredCustomer.getGynecologyFlag())) {
                    if ("N".equals(insuredCustomer.getGynecologyFlag())) {
                        form.setField("insuredGynecologyFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("insuredGynecologyFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("insuredGynecologyFlag_y", Constants.onNoCheck);
                    form.setField("insuredGynecologyFlag_n", Constants.onNoCheck);
                }
				/* 您是否做过妇科检查... */
				/* 受保人选择 */
                if (StringUtils.isNotBlank(insuredCustomer.getGynecologyOthFlag())) {
                    if ("N".equals(insuredCustomer.getGynecologyOthFlag())) {
                        form.setField("insuredGynecologyOthFlag_y", Constants.onNoCheck);
                    } else {
                        form.setField("insuredGynecologyOthFlag_n", Constants.onNoCheck);
                    }
                } else {
                    form.setField("insuredGynecologyOthFlag_y", Constants.onNoCheck);
                    form.setField("insuredGynecologyOthFlag_n", Constants.onNoCheck);
                }
            } else {
                form.setField("insuredPregnancyFlag_y", Constants.onNoCheck);
                form.setField("insuredPregnancyFlag_n", Constants.onNoCheck);
                form.setField("insuredGynecologyFlag_y", Constants.onNoCheck);
                form.setField("insuredGynecologyFlag_n", Constants.onNoCheck);
                form.setField("insuredGynecologyOthFlag_y", Constants.onNoCheck);
                form.setField("insuredGynecologyOthFlag_n", Constants.onNoCheck);
            }

			/* 财务分析表格 */
			/* 受保人姓名拼音 */
            if (!StringUtils.isBlank(insuredCustomer.getEnglishName())) {
                form.setField("insuredEnglishName", insuredCustomer.getEnglishName().trim());
            }
			/* 受益人身份证 */
            if (CollectionUtils.isNotEmpty(beneficiaries)) {
                form.setField("beneficiaryIdCard", beneficiaries.get(0).getIdentityNumber());
            }

			/* 申请人个人资料 */
			/* 姓名 */
            if (!StringUtils.isBlank(applicantCustomer.getEnglishName())) {
                form.setField("applicantEnglishName", applicantCustomer.getEnglishName().trim());
            }
			/* 出生日期 */
            if (!StringUtils.isBlank(applicantCustomer.getBirthDate().toString())) {
                form.setField("applicantBirthDate",
                        DateUtil.getDateStr(applicantCustomer.getBirthDate(), "yyyy-MM-dd"));
            }
			/* 职业 */
            if (!StringUtils.isBlank(applicantCustomer.getPosition())) {
                form.setField("applicantOccupation", applicantCustomer.getPosition());
            }
			/* 投保人收入 */
            if (!StringUtils.isBlank(applicantCustomer.getIncome().toString())) {
                form.setField("applicantIncome", String.valueOf(Math.round(applicantCustomer.getIncome())));
            }
			/* 投保人资产 */
            if (!StringUtils.isBlank(applicantCustomer.getFixedAssets().toString())) {
                form.setField("applicantAssets", String.valueOf(Math.round(applicantCustomer.getFixedAssets())));
            }

			/* 适合性评估 */
            // 判断产品中分类
            if (!StringUtils.isBlank(ordOrder.getMidClass())) {
                if (!("ZJX".equals(ordOrder.getMidClass()) || "RSX".equals(ordOrder.getMidClass())
                        || "CXX".equals(ordOrder.getMidClass()) || "WYSX".equals(ordOrder.getMidClass()))) {
                    form.setField("applicantAllLife", Constants.onNoCheck);
                    form.setField("applicantWholeLife", Constants.onNoCheck); // pdf
                    // 44.3
                }
            } else {
                form.setField("applicantAllLife", Constants.onNoCheck);
                form.setField("applicantWholeLife", Constants.onNoCheck);
            }
			/* 供款年期 */
            List<String> orderYears = new ArrayList<>(Arrays.asList("<1", "1-5", "6-10", "11-20", ">20", "Whole"));
            if (!StringUtils.isBlank(ordOrder.getSublineItemName())) {
                form.setField("contributionYears", ordOrder.getSublineItemName());
                if ("整付".equals(ordOrder.getSublineItemName().trim())) {
                    orderYears.remove(1);
                } else if ("终身".equals(ordOrder.getSublineItemName().trim())) {
                    orderYears.remove(5);
                } else {
                    try {
                        int orderYear = Integer.parseInt(ordOrder.getSublineItemName());
                        if (orderYear < 1) {
                            orderYears.remove(0);
                        } else if (orderYear >= 1 && orderYear <= 5) {
                            orderYears.remove(1);
                        } else if (orderYear >= 6 && orderYear <= 10) {
                            orderYears.remove(2);
                        } else if (orderYear >= 11 && orderYear <= 20) {
                            orderYears.remove(3);
                        } else if (orderYear > 20) {
                            orderYears.remove(4);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
            for (String orderYear : orderYears) {
                form.setField("order" + orderYear, Constants.onNoCheck);
            }

			/* 投保跟进确认书 投保申请修正书 非香港身份证持有人之投保声明 */
            if (!StringUtils.isBlank(applicantCustomer.getEnglishName())) {
				/* 保单持有人 */
                form.setField("sureApplicantEnglishName", applicantCustomer.getEnglishName());
				/* 投保人 */
                form.setField("amendmentApplicantName", applicantCustomer.getEnglishName());
            }
			/* 准受保人 */
            if (!StringUtils.isBlank(insuredCustomer.getEnglishName())) {
                form.setField("sureInsuredEnglishName", insuredCustomer.getEnglishName());
                form.setField("amendmentInsuredName", insuredCustomer.getEnglishName());
            }

			/* 重要资料声明书 */
            if (!StringUtils.isBlank(applicantCustomer.getEnglishName())) {
                form.setField("applicantName", applicantCustomer.getEnglishName());
            }

			/* 新生意表格 */
			/* 受保人年龄 */
            if (insuredCustomer.getBirthDate() != null
                    && !StringUtils.isBlank(String.valueOf(insuredCustomer.getBirthDate()))) {
                form.setField("age", String.valueOf(DateUtil.getAgeByDate(insuredCustomer.getBirthDate())));
            }
			/* 保险公司 */
            if (!StringUtils.isBlank(ordOrder.getName())) {
                form.setField("company", ordOrder.getName());
            }

			/* 财务需要分析表格 */
			/* 投保人中文姓名 */
            if (!StringUtils.isBlank(applicantCustomer.getEnglishName())) {
                form.setField("applicantChineseName", applicantCustomer.getChineseName());
            }
			/* 投保人出生日期 格式:yyyy-MM-dd */
            if (applicantCustomer.getBirthDate() != null
                    && !StringUtils.isBlank(String.valueOf(applicantCustomer.getBirthDate()))) {
                form.setField("applicantBirthDateFormat",
                        DateUtil.getDateStr(applicantCustomer.getBirthDate(), "yyyy-MM-dd"));
            }
			/* 投保人是否吸烟 */
            if (!StringUtils.isBlank(applicantCustomer.getSmokeFlag())) {
                if ("N".equals(applicantCustomer.getSmokeFlag().trim())) {
                    form.setField("smoker_y", Constants.onNoCheck);
                } else {
                    form.setField("smoker_n", Constants.onNoCheck);
                }
            } else {
                form.setField("smoker_y", Constants.onNoCheck);
                form.setField("smoker_n", Constants.onNoCheck);
            }
			/* 投保人电话号码 需要区号+电话号码 */
            if (!StringUtils.isBlank(applicantCustomer.getPhone())) {
                String basePhoneCode = "+86 "; // 默认区号为+86
                if (!StringUtils.isBlank(applicantCustomer.getPhoneCode())) { // 如果有区号则使用数据库里的区号
                    basePhoneCode = "+" + applicantCustomer.getPhoneCode() + " ";
                }
                form.setField("applicantPhone", basePhoneCode + applicantCustomer.getPhone());
            }
			/* 受保人电话号码 需要区号+电话号码 */
            if (!StringUtils.isBlank(insuredCustomer.getPhone())) {
                String basePhoneCode = "+86 "; // 默认区号为+86
                if (!StringUtils.isBlank(insuredCustomer.getPhoneCode())) { // 如果有区号则使用数据库里的区号
                    basePhoneCode = "+" + insuredCustomer.getPhoneCode() + " ";
                }
                form.setField("insuredPhone", basePhoneCode + insuredCustomer.getPhone());
            }
			/* 投保人教育程度 */
            List<String> appEdus = new ArrayList<>(Arrays.asList("Primary", "Secondary", "JC", "U", "M", "Other"));
            if (!StringUtils.isBlank(applicantCustomer.getEducation())) {
                String applicantEducation = applicantCustomer.getEducation().trim();
                if ("PS".equals(applicantEducation) || "BPS".equals(applicantEducation)) { // 小学及以下
                    appEdus.remove(0);
                } else if ("JMS".equals(applicantEducation) || "HS".equals(applicantEducation)) { // 中学
                    appEdus.remove(1);
                } else if ("D".equals(applicantEducation)) { // 其他
                    appEdus.remove(5);
                    form.setField("applicantEducationOtherTxt", "博士");
                } else { // 大专 大学 硕士
                    appEdus.remove(appEdus.indexOf(applicantEducation));
                }
            }
            for (String edus : appEdus) {
                form.setField("applicantEducation" + edus, Constants.onNoCheck);
            }
			/* 投保人每月剩余或超支 */
            if (applicantCustomer.getIncome() != null && applicantCustomer.getAmountPerMonth() != null
                    && !StringUtils.isBlank(String.valueOf(applicantCustomer.getIncome()))
                    && !StringUtils.isBlank(String.valueOf(applicantCustomer.getAmountPerMonth()))) {
                String baseSurplus = String
                        .valueOf(applicantCustomer.getIncome() - applicantCustomer.getAmountPerMonth());
                if (baseSurplus.contains(".")) {
                    baseSurplus = baseSurplus.substring(0, baseSurplus.indexOf("."));
                }
                form.setField("applicantSurplus", baseSurplus);
            }
			/* 投保人总负债额 */
            if (!StringUtils.isBlank(applicantCustomer.getLiabilities().toString())) {
                form.setField("applicantLiabilities", String.valueOf(Math.round(applicantCustomer.getLiabilities())));
            }
			/* 投保人估计净资产 */
            if (applicantCustomer.getCurrentAssets() != null && applicantCustomer.getFixedAssets() != null
                    && applicantCustomer.getLiabilities() != null
                    && !StringUtils.isBlank(String.valueOf(applicantCustomer.getCurrentAssets()))
                    && !StringUtils.isBlank(String.valueOf(applicantCustomer.getFixedAssets()))
                    && !StringUtils.isBlank(String.valueOf(applicantCustomer.getLiabilities()))) {
                form.setField("applicantNetAssets", String.valueOf(Math.round(applicantCustomer.getCurrentAssets()
                        + applicantCustomer.getFixedAssets() - applicantCustomer.getLiabilities())));
            }
            List<String> selectProducts = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F"));
            List<String> typeProducts = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E"));
            if (StringUtils.isNotBlank(ordOrder.getMidClass()) && StringUtils.isNotBlank(ordOrder.getItemName())) {
                String midClass = ordOrder.getMidClass().trim();
                String item = ordOrder.getItemName().trim();
                if ("ZJX".equals(midClass) && ("加裕倍安保".equals(item) || "加裕倍安保（加強版）".equals(item))
                        || "进泰安心保2".equals(item)) {
                    selectProducts.remove(1);
                    typeProducts.remove(2);
                } else if ("CXX".equals(midClass) && ("充裕未来2".equals(item) || "充裕未来2（特級保障)".equals(item))) {
                    selectProducts.remove(4);
                    typeProducts.remove(2);
                } else if ("RSX".equals(midClass)) {
                    if ("爱无忧长享计划3".equals(item) || "简爱·延续保障计划2".equals(item)) {
                        selectProducts.remove(2);
                        selectProducts.remove(3);
                        typeProducts.remove(2);
                    } else if ("爱无忧长享计划3（特級保障)".equals(item) || "易达终身保".equals(item) || "裕满人生保障计划2".equals(item)) {
                        selectProducts.remove(0);
                        selectProducts.remove(2);
                        selectProducts.remove(3);
                        typeProducts.remove(2);
                    }
                }
            }
            for (String selectProduct : selectProducts) {
                form.setField("product" + selectProduct, Constants.onNoCheck);
            }
            for (String typeProduct : typeProducts) {
                form.setField("typeProduct" + typeProduct, Constants.onNoCheck);
            }
			/* 投保人流动资产 */
            if (applicantCustomer.getCurrentAssets() != null
                    && !StringUtils.isBlank(String.valueOf(applicantCustomer.getCurrentAssets()))) {
                form.setField("totalAmount", String.valueOf(Math.round(applicantCustomer.getCurrentAssets())).trim());
            }
			/* 判断订单年期是不是整付 */
            if (StringUtils.isBlank(ordOrder.getSublineItemName())
                    || !"整付".equals(ordOrder.getSublineItemName().trim())) {
                form.setField("checkPeriod1-5", Constants.onNoCheck);
            }

			/* AML/CTF Self-Assessment Checklist（internal document） */
			/* 受保人与投保人之关系 */
            if (StringUtils.isNotBlank(ordOrder.getSameFlag())) {
                clbCodeValue.setCodeId(10234L);
                List<ClbCodeValue> relationList = clbCodeService.selectCodeValues(requestContext, clbCodeValue);
                for (ClbCodeValue codeValue : relationList) {
                    if (codeValue.getValue().equals(ordOrder.getSameFlag())) {
                        form.setField("relationship", codeValue.getDescription());
                    }
                }
            }
			/* 投保人身份证号 */
            if (StringUtils.isNotBlank(applicantCustomer.getIdentityNumber())) {
                form.setField("HKID", applicantCustomer.getIdentityNumber());
            }
			/* 受保人英文姓名 */
            if (StringUtils.isNotBlank(insuredCustomer.getEnglishName())) {
                form.setField("insuredName", insuredCustomer.getEnglishName());
            }
			/* 保险公司 */
            if (!StringUtils.isBlank(ordOrder.getProductSupplierName())) {
                form.setField("production", ordOrder.getProductSupplierName().trim() + "保险公司");
            }
			/* 订单年期 */
            if (!StringUtils.isBlank(ordOrder.getSublineItemName())) {
                if ("整付".equals(ordOrder.getSublineItemName())) {
                    form.setField("periodWhole", "");
                } else {
                    form.setField("contributionPeriod", ordOrder.getSublineItemName());
                    form.setField("periodYear", "");
                }
            }
			/* 每年保费 */
            if (ordOrder.getYearPayAmount() != null
                    && StringUtils.isNotBlank(String.valueOf(ordOrder.getYearPayAmount()))) {
                String baseYearPayAmount = String.valueOf(ordOrder.getYearPayAmount());
                if (baseYearPayAmount.contains(".")) {
                    baseYearPayAmount = baseYearPayAmount.substring(0, baseYearPayAmount.indexOf("."));
                }
                form.setField("premium", baseYearPayAmount);
                form.setField("contributionAmount", baseYearPayAmount);
                form.setField("policyAmount", baseYearPayAmount);
                if (StringUtils.isNotBlank(ordOrder.getCurrency())) {
                    baseYearPayAmount = ordOrder.getCurrency() + " " + baseYearPayAmount;
                }
                form.setField("currencyPremium", baseYearPayAmount);
            }

			/* Assessment method for aml risk Factors */
			/* 受益人姓名 */
            if (CollectionUtils.isNotEmpty(beneficiaries)) {
                StringBuilder baseBeneficiariesName = new StringBuilder();
                for (OrdBeneficiary beneficiary : beneficiaries) {
                    baseBeneficiariesName.append(beneficiary.getEnglishName()).append("、");
                }
                String names = baseBeneficiariesName.substring(0, baseBeneficiariesName.length() - 2);
                form.setField("beneficiariesName", names);
                form.setField("default1b", "1");
                form.setField("default4b", "4");
            }

            stamper.setFormFlattening(true);
            stamper.close();
            response.setContentType("application/pdf");
            response.setContentLength(ba.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ServletOutputStream outputStream = response.getOutputStream();
                ba.writeTo(outputStream);
                outputStream.flush();
                outputStream.close();
                ba.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将字符串转换为首字母大写的格式 add by 谈晟
     *
     * @param str
     * @return
     */
    private String captureName(String str) {
        char[] cs = str.toLowerCase().toCharArray();
        cs[0] -= (cs[0] > 96 && cs[0] < 123) ? 32 : 0; // 判断首字母是否为小写字母,是则转换为大写字母
        return String.valueOf(cs);
    }

    /**
     * 通过反射对象转换为map
     *
     * @param obj
     * @return
     * @throws Exception
     */
    protected Map<String, String> Obj2Map(Object obj) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            // 如果是long类型
            if ("java.lang.Long".equals(field.getType().getName())) {
                if (field.get(obj) != null) {
                    map.put(field.getName(), field.get(obj).toString());
                }
            }
            // 如果是BigDecimal类型
            if ("java.math.BigDecimal".equals(field.getType().getName())) {
                if (field.get(obj) != null) {
                    map.put(field.getName(), field.get(obj).toString());
                }
            }
            // 如果字符串且不为空
            if ("java.lang.String".equals(field.getType().getName())) {
                if (field.get(obj) != null && field.get(obj) != "") {
                    if (field.getName().equals("insurantIdentityAddress")) {
                        System.out.println(field.get(obj).toString());
                    }
                    map.put(field.getName(), field.get(obj).toString());
                }
            }
            // 如果是日期
            if ("java.util.Date".equals(field.getType().getName())) {
                if (field.get(obj) != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String str = sdf.format(field.get(obj));
                    map.put(field.getName(), str);
                }
            }
        }
        return map;
    }

    @Override
    public List<PrdItems> queryClassAmount(IRequest request, PrdItems prdItems) {
        List<PrdItems> prdItemsList = ordOrderMapper.queryClassAmount(prdItems);
        return prdItemsList;
    }

    /**
     * 查询续保的年数
     */
    @Override
    public List<OrdOrder> queryRenewalYear(OrdOrder dto) {
        List<OrdOrder> list = ordOrderMapper.queryRenewalYear(dto);
        return list;
    }

    /**
     * 订单数据导入
     */
    @Override
    public void importData(IRequest request, List<OrdOrder> ordOrders, List<OrdCustomer> ordCustomers,
                           List<OrdBeneficiary> ordBeneficiaries, List<OrdTradeRoute> ordTradeRoutes,
                           List<OrdTradeRouteShow> ordTradeRouteShows, List<OrdAddition> ordAdditions,
                           List<OrdCommission> ordCommissions) {
        // 客户
        for (OrdCustomer ordCustomer : ordCustomers) {
            CtmCustomer ctmCustomer = new CtmCustomer();
            ctmCustomer.setIdentityNumber(ordCustomer.getIdentityNumber());
            List<CtmCustomer> ctmCustomerList = ctmCustomerMapper.select(ctmCustomer);
            if (CollectionUtils.isEmpty(ctmCustomerList)) {
                try {
                    BeanUtils.copyProperties(ctmCustomer, ordCustomer);
                    ctmCustomer.set__status("add");
                    ctmCustomer.setCustomerId(null);
                    if (StringUtils.isEmpty(ctmCustomer.getCustomerCode())) {
                        ctmCustomer.setCustomerCode(sequenceService.getSequence("CUSTOMER"));
                    }
                    ctmCustomerService.insertSelective(request, ctmCustomer);
                    // ctmCustomers.add(ctmCustomer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // if (CollectionUtils.isNotEmpty(ctmCustomers)) {
        // ctmCustomerService.batchUpdate(request, ctmCustomers);
        // }

        // 订单
        for (OrdOrder ordOrder : ordOrders) {
            if (ordOrder.getApplicantCustomerId() == null
                    && StringUtils.isNotEmpty(ordOrder.getApplicantIdentityNumber())) {
                CtmCustomer ctmCustomer = new CtmCustomer();
                ctmCustomer.setIdentityNumber(ordOrder.getApplicantIdentityNumber());
                List<CtmCustomer> ctmCustomerList = ctmCustomerMapper.select(ctmCustomer);
                if (CollectionUtils.isNotEmpty(ctmCustomerList)) {
                    ordOrder.setApplicantCustomerId(ctmCustomerList.get(0).getCustomerId());
                }
            }

            if (ordOrder.getInsurantCustomerId() == null
                    && StringUtils.isNotEmpty(ordOrder.getInsurantIdentityNumber())) {
                CtmCustomer ctmCustomer = new CtmCustomer();
                ctmCustomer.setIdentityNumber(ordOrder.getInsurantIdentityNumber());
                List<CtmCustomer> ctmCustomerList = ctmCustomerMapper.select(ctmCustomer);
                if (CollectionUtils.isNotEmpty(ctmCustomerList)) {
                    ordOrder.setInsurantCustomerId(ctmCustomerList.get(0).getCustomerId());
                }
            }
        }
        ordOrders = self().batchUpdate(request, ordOrders);

        // 客户
        for (OrdCustomer ordCustomer : ordCustomers) {
            for (OrdOrder ordOrder : ordOrders) {
                if (ordOrder.getOrderNumber().equals(ordCustomer.getOrderNumber())) {
                    ordCustomer.setOrderId(ordOrder.getOrderId());
                }
            }
        }
        ordCustomerService.batchUpdate(request, ordCustomers);

        // 受益人
        for (OrdBeneficiary ordBeneficiary : ordBeneficiaries) {
            for (OrdOrder ordOrder : ordOrders) {
                if (ordOrder.getOrderNumber().equals(ordBeneficiary.getOrderNumber())) {
                    ordBeneficiary.setOrderId(ordOrder.getOrderId());
                }
            }
        }
        ordBeneficiaryService.batchUpdate(request, ordBeneficiaries);

        // 交易路线
        for (OrdTradeRoute ordTradeRoute : ordTradeRoutes) {
            for (OrdOrder ordOrder : ordOrders) {
                if (ordOrder.getOrderNumber().equals(ordTradeRoute.getOrderNumber())) {
                    ordTradeRoute.setOrderId(ordOrder.getOrderId());
                }
            }
        }
        ordTradeRouteService.batchUpdate(request, ordTradeRoutes);

        // 交易路线展示
        for (OrdTradeRouteShow ordTradeRouteShow : ordTradeRouteShows) {
            for (OrdOrder ordOrder : ordOrders) {
                if (ordOrder.getOrderNumber().equals(ordTradeRouteShow.getOrderNumber())) {
                    ordTradeRouteShow.setOrderId(ordOrder.getOrderId());
                }
            }
        }
        ordTradeRouteShowService.batchUpdate(request, ordTradeRouteShows);

        // 附加险
        for (OrdAddition ordAddition : ordAdditions) {
            for (OrdOrder ordOrder : ordOrders) {
                if (ordOrder.getOrderNumber().equals(ordAddition.getOrderNumber())) {
                    ordAddition.setOrderId(ordOrder.getOrderId());
                }
            }
        }
        ordAdditionService.batchUpdate(request, ordAdditions);

        // 佣金
        for (OrdCommission ordCommission : ordCommissions) {
            for (OrdOrder ordOrder : ordOrders) {
                if (ordOrder.getOrderNumber().equals(ordCommission.getOrderNumber())) {
                    ordCommission.setOrderId(ordOrder.getOrderId());
                }
            }

            for (OrdAddition ordAddition : ordAdditions) {
                if (ordAddition.getOrderNumber().equals(ordCommission.getOrderNumber())
                        && ordAddition.getItemId().equals(ordCommission.getItemId())) {
                    ordCommission.setAdditionId(ordAddition.getAdditionId());
                }
            }
        }
        ordCommissionService.batchUpdate(request, ordCommissions);

        List<OrdOrder> reOrders = new ArrayList<>();
        for (OrdOrder ordOrder : ordOrders) {
            OrdOrder k = new OrdOrder();
            k.setOrderId(ordOrder.getOrderId());
            List<OrdOrder> orders = ordOrderMapper.queryWsOrder(k);
            if (CollectionUtils.isNotEmpty(orders)) {
                if ("POLICY_EFFECTIVE".equals(orders.get(0).getStatus()) && !"FJ".equals(k.getPayMethod())
                        && !"WP".equals(k.getPayMethod())) {
                    this.addRenewal(request, orders.get(0));
                    orders.get(0).set__status("update");
                    reOrders.add(orders.get(0));
                }
            }
        }
        self().batchUpdate(request, reOrders);
    }

    @Override
    public OrdOrder selectOrdOrder(Long orderId) {
        return ordOrderMapper.selectOrdOrder(orderId);
    }

    @Override
    public String queryIdentityInfo3(String nationValueCode, String provinceValueCode, String cityValueCode) {
        return plnPlanRequestService.selectCodeMeaning("PUB.NATION", nationValueCode)
                + plnPlanRequestService.selectCodeMeaning("PUB.PROVICE", provinceValueCode)
                + plnPlanRequestService.selectCodeMeaning("PUB.CITY", cityValueCode);
    }

    @Override
    public String queryIdentityInfo2(String provinceValueCode, String cityValueCode) {
        return plnPlanRequestService.selectCodeMeaning("PUB.PROVICE", provinceValueCode)
                + plnPlanRequestService.selectCodeMeaning("PUB.CITY", cityValueCode);
    }

    @Override
    public String queryIdentityInfo1(String nationValueCode) {
        return plnPlanRequestService.selectCodeMeaning("PUB.PROVICE", nationValueCode);
    }

    @Override
    public void addHisStatus2(IRequest requestContext, OrdStatusHis ordStatusHis) {
        // 设置时间和处理人
        ordStatusHis.setStatusDate(new Date());
        ordStatusHis.setOperatorUserId(requestContext.getUserId());
        // 会所确认
        if ("CLUB_CONFIRM".equals(ordStatusHis.getStatus())) {
            if ("CLUB_CONFIRMED".equals(ordStatusHis.getStatusConfirm())) {
                ordStatusHis.setDescription("会所已确认!时间为:" + ordStatusHis.getDescription());
            } else {
                ordStatusHis.setDescription("会所未确认!");
            }
        }
        // 客服确认
        if ("CUSTOMER_CONFIRM".equals(ordStatusHis.getStatus())) {
            if ("Y".equals(ordStatusHis.getStatusConfirm())) {
                ordStatusHis.setDescription("客服已与赴港联系人电话沟通签单时间和见面地址。");
            } else {
                ordStatusHis.setDescription("赴港联系人电话无法接通，请联系对接业务行政告知新的联系方式。");
            }
        }
        // 更新申请书状态
        if ("UPDATE_APP_STATUS".equals(ordStatusHis.getStatus())) {
            ordStatusHis.setDescription(plnPlanRequestService.selectCodeMeaning("ORD.ORDER_APPLICATION_STATUS",
                    ordStatusHis.getStatusConfirm()));
        }

        // 已电话
        if ("PHONE_FLAG".equals(ordStatusHis.getStatus())) {
            if ("Y".equals(ordStatusHis.getStatusConfirm())) {
                ordStatusHis.setDescription("客户已在前往签单现场路途中，会所前台已与赴港联系人电话沟通到达时间和见面地址。");
            } else {
                ordStatusHis.setDescription("赴港联系人电话无法接通，请联系对接业务行政告知新的联系方式。");
            }
        }
        // 复核1
        if ("REVIEW1".equals(ordStatusHis.getStatus())) {
            if(ordStatusHis.getStatusConfirm() != null) {
                ordStatusHis.setDescription(ordStatusHis.getStatusConfirm() + "第一次复核");
            }else {
                ordStatusHis.setDescription("第一次复核");
            }
        }
        // 复核2
        if ("REVIEW2".equals(ordStatusHis.getStatus())) {
            if(ordStatusHis.getStatusConfirm() != null) {
                ordStatusHis.setDescription(ordStatusHis.getStatusConfirm() + "第二次复核");
            }else {
                ordStatusHis.setDescription("第二次复核");
            }
        }
        // 递交状态
        if ("SEND_STATUS".equals(ordStatusHis.getStatus())) {
            ordStatusHis.setDescription("已递交!");
        }

        // 添加数据
        ordStatusHisService.insertSelective(requestContext, ordStatusHis);
    }

    @Override
    public Integer getPendingNum(Long channelId) {
        Set<String> numSet = new HashSet<>();
        List<OrdOrder> ordOrders = ordOrderMapper.getPendingNum(channelId);
        for (OrdOrder ordOrder : ordOrders) {
            numSet.add(ordOrder.getOrderNumber());
        }
        return numSet.size();
    }
    /**
     * 查询当前附加险的佣金是否跟随主险 Y跟随  N不跟随
     * @param ordAddition
     * @param ordOrder
     * @return
     */
    public String getDependMainFlag(OrdAddition ordAddition,OrdOrder ordOrder) {
    	String dependMainFlag = "N";
    	
    	CmnAdtRiskRelation dto = new CmnAdtRiskRelation();
    	dto.setSubItemId(ordAddition.getItemId());
    	List<CmnAdtRiskRelation> list = cmnAdtRiskRelationMapper.queryAll(dto);
    	if(CollectionUtils.isNotEmpty(list)) {
    		//附加险id在表中是唯一的
    		if(list.get(0).getItemId() == null) {
    			dependMainFlag = list.get(0).getDependMainFlag();
    		}else if(list.get(0).getItemId() != null && ordOrder.getItemId().equals(list.get(0).getItemId())) {
    			dependMainFlag = list.get(0).getDependMainFlag();
    		}
    	}
    	return dependMainFlag;
    }

}