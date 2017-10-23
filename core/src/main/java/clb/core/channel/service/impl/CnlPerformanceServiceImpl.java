package clb.core.channel.service.impl;

import clb.core.channel.dto.*;
import clb.core.channel.mapper.CnlPerformanceMapper;
import clb.core.channel.service.ICnlPerformanceService;
import clb.core.common.utils.DateUtil;
import clb.core.exchangeRate.dto.FetExchangeRate;
import clb.core.exchangeRate.mapper.FetExchangeRateMapper;
import clb.core.forecast.dto.FetActualPayment;
import clb.core.forecast.dto.FetReceivable;
import clb.core.forecast.dto.FetTimeReceivable;
import clb.core.forecast.service.IFetActualPaymentService;
import clb.core.forecast.service.IFetReceivableService;
import clb.core.forecast.service.IFetTimeReceivableService;
import clb.core.order.dto.OrdCommission;
import clb.core.order.dto.OrdOrder;
import clb.core.order.mapper.OrdCommissionMapper;
import clb.core.order.service.IOrdOrderService;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.mapper.ClbCodeValueMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class CnlPerformanceServiceImpl implements ICnlPerformanceService {

    @Autowired
    private CnlPerformanceMapper cnlPerformanceMapper;

    @Autowired
    private FetExchangeRateMapper fetExchangeRateMapper;

    @Autowired
    private OrdCommissionMapper ordCommissionMapper;

    @Autowired
    private ClbCodeValueMapper clbCodeValueMapper;

    @Autowired
    private IFetActualPaymentService fetActualPaymentService;

    @Autowired
    private IFetReceivableService fetReceivableService;

    @Autowired
    private IOrdOrderService ordOrderService;

    @Autowired
    private IFetTimeReceivableService fetTimeReceivableService;

    /**
     * 查询本月待收款金额、本月收益金额
     *
     * @param fetReceivable
     * @return
     */
    public BigDecimal queryFetReceivableByCondition(FetReceivable fetReceivable) {
        return cnlPerformanceMapper.queryFetReceivableByCondition(fetReceivable);
    }

    /**
     * 查询当前渠道下，起始时间之后创建的客户数量
     *
     * @param performanceParas
     * @return
     */
    public Long queryNewCustomerQty(CnlPerformanceParas performanceParas) {
        return cnlPerformanceMapper.queryNewCustomerQty(performanceParas);
    }

    /**
     * 查询当前渠道下，起始时间之后创建的订单数量
     *
     * @param performanceParas
     * @return
     */
    public Long queryNewOrderQty(CnlPerformanceParas performanceParas) {
        return cnlPerformanceMapper.queryNewOrderQty(performanceParas);
    }

    /**
     * 缴费总额
     * 当前用户的个人订单（保单状态=保单生效；债券状态=发行成功；投资移民状态=购买成功）的缴费总额
     * （保单=年缴费额；债券=金额；投资移民=成交金额）的汇总。
     * @param performanceParas
     * @return
     */
    public BigDecimal queryPayTotalAmount(CnlPerformanceParas performanceParas) {
        BigDecimal totalAmount = new BigDecimal("0");
        List<OrdOrder> orderList = new ArrayList<OrdOrder>();
        performanceParas.setInsuranceStatusArea("('POLICY_EFFECTIVE')");
        performanceParas.setBondStatusArea("('ISSUE_SUCCESS')");
        performanceParas.setImmigrantStatusArea("('BUY_SUCCESS')");
        orderList = cnlPerformanceMapper.queryPersonalOrder(performanceParas);
        if (orderList != null && orderList.size() > 0) {

            for (OrdOrder order : orderList) {

                BigDecimal orderAmount = new BigDecimal("0");
                if ("INSURANCE".equals(order.getOrderType())) {
                    orderAmount = order.getYearPayAmount();
                } else  {
                    orderAmount = order.getPolicyAmount();
                }

                if ("HKD".equals(order.getCurrency())) {
                    totalAmount = totalAmount.add(orderAmount);
                } else {
                    FetExchangeRate rate = new FetExchangeRate();
                    rate.setBaseCurrency("HKD");
                    rate.setForeignCurrency(order.getCurrency());
                    rate.setCurrentTime(order.getCreationDate());
                    List<FetExchangeRate> rateList = new ArrayList<FetExchangeRate>();
                    rateList = fetExchangeRateMapper.selectRateByCurrentTime(rate);
                    if (rateList != null && rateList.size() > 0) {
                        rate = rateList.get(0);

                        totalAmount = totalAmount.add(orderAmount.multiply(new BigDecimal(rate.getRate())));
                    } else {
                        continue;
                    }
                }
            }
        }

        return totalAmount;
    }

    /**
     * 直客收益金额
     * 当前用户的个人订单（保单状态=保单生效；债券状态=发行成功；投资移民状态=购买成功）
     * 的佣金信息中当前渠道的第一年-应派的汇总。
     * @param performanceParas
     * @return
     */
    public BigDecimal queryDirectTotalAmount(CnlPerformanceParas performanceParas) {
        
        BigDecimal totalAmount = new BigDecimal("0");
        List<OrdOrder> orderList = new ArrayList<OrdOrder>();
        performanceParas.setInsuranceStatusArea("('POLICY_EFFECTIVE')");
        performanceParas.setBondStatusArea("('ISSUE_SUCCESS')");
        performanceParas.setImmigrantStatusArea("('BUY_SUCCESS')");
        orderList = cnlPerformanceMapper.queryPersonalOrder(performanceParas);
        if (orderList != null && orderList.size() > 0) {

            for (OrdOrder order : orderList) {

                BigDecimal orderAmount = order.getFirstYearAmount();

                if ("HKD".equals(order.getCurrency())) {
                    totalAmount = totalAmount.add(orderAmount);
                } else {
                    FetExchangeRate rate = new FetExchangeRate();
                    rate.setBaseCurrency("HKD");
                    rate.setForeignCurrency(order.getCurrency());
                    rate.setCurrentTime(order.getCreationDate());
                    List<FetExchangeRate> rateList = new ArrayList<FetExchangeRate>();
                    rateList = fetExchangeRateMapper.selectRateByCurrentTime(rate);
                    if (rateList != null && rateList.size() > 0) {
                        rate = rateList.get(0);
                        totalAmount = totalAmount.add(orderAmount.multiply(new BigDecimal(rate.getRate())));
                    } else {
                        continue;
                    }
                }
            }
        }

        return totalAmount;
    }

    /**
     * 计算预约中收益
     * @param performanceParas
     * @return
     */
    public BigDecimal queryBookingIncome(CnlPerformanceParas performanceParas) {
        BigDecimal totalAmount = new BigDecimal("0");
        String insuranceStatusArea = "(" +
                "'ARRIVAL'," +
                "'DATA_APPROVED'," +
                "'DATA_APPROVING'," +
                "'LEAVE'," +
                "'NEED_REVIEW'," +
                "'NEW'," +
                "'RESERVE_SUCCESS'," +
                "'RESERVING'" +
                ")";
        String bondStatusArea = "(" +
                "'DATA_APPROVED'," +
                "'DATA_APPROVING'," +
                "'NEED_REVIEW'," +
                "'RESERVE_SUCCESS'," +
                "'RESERVING'" +
                ")";
        String immigrantStatusArea = "(" +
                "'NEGOTIATE'," +
                "'RESERVING'" +
                ")";

        // 个人订单数据计算
        List<OrdOrder> personalOrderList = new ArrayList<OrdOrder>();
        performanceParas.setInsuranceStatusArea(insuranceStatusArea);
        performanceParas.setBondStatusArea(bondStatusArea);
        performanceParas.setImmigrantStatusArea(immigrantStatusArea);
        personalOrderList = cnlPerformanceMapper.queryPersonalOrder(performanceParas);
        for (OrdOrder order : personalOrderList) {
            if ("HKD".equals(order.getCurrency())) {
                totalAmount = totalAmount.add(order.getFirstYearAmount());
            } else {
                FetExchangeRate rate = new FetExchangeRate();
                rate.setBaseCurrency("HKD");
                rate.setForeignCurrency(order.getCurrency());
                rate.setCurrentTime(order.getCreationDate());
                List<FetExchangeRate> rateList = new ArrayList<FetExchangeRate>();
                rateList = fetExchangeRateMapper.selectRateByCurrentTime(rate);
                if (rateList != null && rateList.size() > 0) {
                    rate = rateList.get(0);
                    totalAmount = totalAmount.add(order.getFirstYearAmount().multiply(new BigDecimal(rate.getRate())));
                } else {
                    continue;
                }
            }
        }

        // 团队订单数据计算
        if ("team".equals(performanceParas.getPerformanceType())) {
            List<OrdOrder> teamOrderList = new ArrayList<OrdOrder>();
            teamOrderList = cnlPerformanceMapper.queryTeamOrder(performanceParas);
            for (OrdOrder order : teamOrderList) {

                // 查询本级佣金
                OrdCommission ordCommission = new OrdCommission();
                ordCommission.setOrderId(order.getOrderId());
                ordCommission.setCompanyType("CHANNEL");
                ordCommission.setCompanyId(performanceParas.getChannelId());
                List<OrdCommission> ordCommissionList = ordCommissionMapper.queryOrdCommission(ordCommission);

                for (OrdCommission commission : ordCommissionList) {

                    BigDecimal base = commission.getFirstYearAmount();

                    OrdCommission secondCommission = new OrdCommission();
                    secondCommission.setOrderId(order.getOrderId());
                    secondCommission.setCompanyType("CHANNEL");
                    secondCommission.setSeqNum(commission.getSeqNum() - 1);
                    secondCommission.setFirstYearAmount(new BigDecimal("0"));
                    List<OrdCommission> secondCommissionList = ordCommissionMapper.queryOrdCommission(secondCommission);
                    if (secondCommissionList != null && secondCommissionList.size() > 0) {
                        secondCommission = secondCommissionList.get(0);
                    }

                    if ("HKD".equals(order.getCurrency())) {
                        totalAmount = totalAmount.add(commission.getFirstYearAmount().subtract(secondCommission.getFirstYearAmount()));
                    } else {
                        FetExchangeRate rate = new FetExchangeRate();
                        rate.setBaseCurrency("HKD");
                        rate.setForeignCurrency(order.getCurrency());
                        rate.setCurrentTime(order.getCreationDate());
                        List<FetExchangeRate> rateList = new ArrayList<FetExchangeRate>();
                        rateList = fetExchangeRateMapper.selectRateByCurrentTime(rate);
                        if (rateList != null && rateList.size() > 0) {
                            rate = rateList.get(0);
                            totalAmount = totalAmount.add((commission.getFirstYearAmount().subtract(secondCommission.getFirstYearAmount())).multiply(new BigDecimal(rate.getRate())));
                        } else {
                            continue;
                        }
                    }


                }

            }
        }

        return totalAmount;
    }

    /**
     * 计算签单中收益
     * @param performanceParas
     * @return
     */
    public BigDecimal querySignningIncome(CnlPerformanceParas performanceParas) {

        BigDecimal totalAmount = new BigDecimal("0");
        String insuranceStatusArea = "(" +
                "'APPROVING'," +
                "'PENDING'," +
                "'SIGNED'" +
                ")";
        String bondStatusArea = "('WAITING_ISSUE'" +
                "'PENDING'," +
                "'PENDING_HANDLING'," +
                "'PENDING_APPROVING'" +
                ")";

        // 个人订单数据计算
        List<OrdOrder> personalOrderList = new ArrayList<OrdOrder>();
        performanceParas.setInsuranceStatusArea(insuranceStatusArea);
        performanceParas.setBondStatusArea(bondStatusArea);
        personalOrderList = cnlPerformanceMapper.queryPersonalOrder(performanceParas);
        for (OrdOrder order : personalOrderList) {
            if ("HKD".equals(order.getCurrency())) {
                totalAmount = totalAmount.add(order.getFirstYearAmount());
            } else {
                FetExchangeRate rate = new FetExchangeRate();
                rate.setBaseCurrency("HKD");
                rate.setForeignCurrency(order.getCurrency());
                rate.setCurrentTime(order.getCreationDate());
                List<FetExchangeRate> rateList = new ArrayList<FetExchangeRate>();
                rateList = fetExchangeRateMapper.selectRateByCurrentTime(rate);
                if (rateList != null && rateList.size() > 0) {
                    rate = rateList.get(0);
                    totalAmount = totalAmount.add(order.getFirstYearAmount().multiply(new BigDecimal(rate.getRate())));
                } else {
                    continue;
                }
            }
        }

        // 团队订单数据计算
        if ("team".equals(performanceParas.getPerformanceType())) {
            List<OrdOrder> teamOrderList = new ArrayList<OrdOrder>();
            teamOrderList = cnlPerformanceMapper.queryTeamOrder(performanceParas);
            for (OrdOrder order : teamOrderList) {

                // 查询本级佣金
                OrdCommission ordCommission = new OrdCommission();
                ordCommission.setOrderId(order.getOrderId());
                ordCommission.setCompanyType("CHANNEL");
                ordCommission.setCompanyId(performanceParas.getChannelId());
                List<OrdCommission> ordCommissionList = ordCommissionMapper.queryOrdCommission(ordCommission);

                for (OrdCommission commission : ordCommissionList) {

                    BigDecimal base = commission.getFirstYearAmount();

                    OrdCommission secondCommission = new OrdCommission();
                    secondCommission.setOrderId(order.getOrderId());
                    secondCommission.setCompanyType("CHANNEL");
                    secondCommission.setSeqNum(commission.getSeqNum() - 1);
                    secondCommission.setFirstYearAmount(new BigDecimal("0"));
                    List<OrdCommission> secondCommissionList = ordCommissionMapper.queryOrdCommission(secondCommission);
                    if (secondCommissionList != null && secondCommissionList.size() > 0) {
                        secondCommission = secondCommissionList.get(0);
                    }

                    if ("HKD".equals(order.getCurrency())) {
                        totalAmount = totalAmount.add(commission.getFirstYearAmount().subtract(secondCommission.getFirstYearAmount()));
                    } else {
                        FetExchangeRate rate = new FetExchangeRate();
                        rate.setBaseCurrency("HKD");
                        rate.setForeignCurrency(order.getCurrency());
                        rate.setCurrentTime(order.getCreationDate());
                        List<FetExchangeRate> rateList = new ArrayList<FetExchangeRate>();
                        rateList = fetExchangeRateMapper.selectRateByCurrentTime(rate);
                        if (rateList != null && rateList.size() > 0) {
                            rate = rateList.get(0);
                            totalAmount = totalAmount.add((commission.getFirstYearAmount().subtract(secondCommission.getFirstYearAmount())).multiply(new BigDecimal(rate.getRate())));
                        } else {
                            continue;
                        }
                    }
                }

            }
        }

        return totalAmount;
    }


    private Long queryStatusSum(CnlPerformanceParas performanceParas, String statusTye) {
        String insuranceStatusArea = "";

        String bondStatusArea = "";

        String immigrantStatusArea = "";
        if ("审核中".equals(statusTye)) {
            insuranceStatusArea = "('DATA_APPROVED', 'DATA_APPROVING', 'NEED_REVIEW', 'NEW')";
            bondStatusArea = "('DATA_APPROVED', 'DATA_APPROVING', 'NEED_REVIEW')";
            immigrantStatusArea = "";

        } else if ("预约中".equals(statusTye)) {
            insuranceStatusArea = "('RESERVE_SUCCESS', 'RESERVING')";
            bondStatusArea = "('RESERVE_SUCCESS', 'RESERVING')";
            immigrantStatusArea = "('RESERVING')";

        } else if ("签单中".equals(statusTye)) {
            insuranceStatusArea = "('ARRIVAL', 'LEAVE')";
            bondStatusArea = "";
            immigrantStatusArea = "";

        } else if ("批核中".equals(statusTye)) {
            insuranceStatusArea = "('APPROVING', 'SIGNED')";
            bondStatusArea = "";
            immigrantStatusArea = "('NEGOTIATE')";

        } else if ("订单取消".equals(statusTye)) {
            insuranceStatusArea = "('DECLINED', 'SUSPENDED', 'EXPIRED',  'SURRENDERED', 'SURRENDERING')";
            bondStatusArea = "";
            immigrantStatusArea = "";

        } else if ("已取消".equals(statusTye)) {
            insuranceStatusArea = "('RESERVE_FAIL', 'CANCELLED')";
            bondStatusArea = "('RESERVE_FAIL')";
            immigrantStatusArea = "('RESERVE_FAIL')";

        } else if ("已生效".equals(statusTye)) {
            insuranceStatusArea = "('POLICY_EFFECTIVE')";
            bondStatusArea = "('ISSUE_SUCCESS')";
            immigrantStatusArea = "('BUY_SUCCESS')";
        }

        performanceParas.setInsuranceStatusArea(insuranceStatusArea);
        performanceParas.setBondStatusArea(bondStatusArea);
        performanceParas.setImmigrantStatusArea(immigrantStatusArea);

        List<OrdOrder> personalOrderList = new ArrayList<OrdOrder>();
        personalOrderList = cnlPerformanceMapper.queryPersonalOrder(performanceParas);
        List<OrdOrder> teamOrderList = new ArrayList<OrdOrder>();

        if ("team".equals(performanceParas.getPerformanceType())) {
            teamOrderList = cnlPerformanceMapper.queryTeamOrder(performanceParas);
        }

        return Long.valueOf(personalOrderList.size() + teamOrderList.size());
    }

    /**
     * 不同状态订单数量
     *
     * @param performanceParas
     * @return
     */
    public Long queryOrderStatusQty(CnlPerformanceParas performanceParas, String statusTye) {

        String insuranceStatusArea = "";

        String bondStatusArea = "";

        String immigrantStatusArea = "";

        if ("正在跟进".equals(statusTye)) {
            insuranceStatusArea = "('ARRIVAL'," +
                    "'DATA_APPROVED'," +
                    "'DATA_APPROVING'," +
                    "'LEAVE'," +
                    "'NEW'," +
                    "'NEED_REVIEW'," +
                    "'RESERVE_SUCCESS'," +
                    "'RESERVING')";

            bondStatusArea = "('DATA_APPROVED'," +
                    "'DATA_APPROVING'," +
                    "'NEED_REVIEW'," +
                    "'RESERVE_SUCCESS'," +
                    "'RESERVING')";

            immigrantStatusArea = "('RESERVING')";

        } else if ("审核中".equals(statusTye)) {
            insuranceStatusArea = "('DATA_APPROVED', 'DATA_APPROVING', 'NEED_REVIEW', 'NEW')";
            bondStatusArea = "('DATA_APPROVED', 'DATA_APPROVING', 'NEED_REVIEW')";
            immigrantStatusArea = "";

        } else if ("预约中".equals(statusTye)) {
            insuranceStatusArea = "('RESERVE_SUCCESS', 'RESERVING')";
            bondStatusArea = "('RESERVE_SUCCESS', 'RESERVING')";
            immigrantStatusArea = "('RESERVING')";

        } else if ("签单中".equals(statusTye)) {
            insuranceStatusArea = "('ARRIVAL', 'LEAVE')";
            bondStatusArea = "";
            immigrantStatusArea = "";

        } else if ("即将生效".equals(statusTye)) {
            insuranceStatusArea = "('APPROVING'," +
                    "'DECLINED'," +
                    "'EXPIRED'," +
                    "'PENDING'," +
                    "'POLICY_EFFECTIVE'," +
                    "'SIGNED'," +
                    "'SURRENDERED'," +
                    "'SURRENDERING'," +
                    "'SUSPENDED')";
            bondStatusArea = "('ISSUE_SUCCESS'," +
                    "'PENDING'," +
                    "'PENDING_APPROVING'," +
                    "'PENDING_HANDLING'," +
                    "'WAITING_ISSUE')";
            immigrantStatusArea = "('BUY_SUCCESS', 'NEGOTIATE')";


        } else if ("批核中".equals(statusTye)) {
            insuranceStatusArea = "('APPROVING', 'SIGNED')";
            bondStatusArea = "";
            immigrantStatusArea = "('NEGOTIATE')";

        } else if ("pending中".equals(statusTye)) {

            insuranceStatusArea = "('PENDING','APPROVING')";
            bondStatusArea = "('PENDING'," +
                    "'PENDING_APPROVING'," +
                    "'PENDING_HANDLING'," +
                    "'WAITING_ISSUE')";
            immigrantStatusArea = "";
        } else if ("订单取消".equals(statusTye)) {
            insuranceStatusArea = "('DECLINED', 'SUSPENDED', 'EXPIRED',  'SURRENDERED', 'SURRENDERING')";
            bondStatusArea = "";
            immigrantStatusArea = "";

        } else if ("已取消".equals(statusTye)) {
            insuranceStatusArea = "('RESERVE_FAIL', 'CANCELLED')";
            bondStatusArea = "('RESERVE_FAIL')";
            immigrantStatusArea = "('RESERVE_FAIL')";

        } else if ("已生效".equals(statusTye)) {
            insuranceStatusArea = "('POLICY_EFFECTIVE')";
            bondStatusArea = "('ISSUE_SUCCESS')";
            immigrantStatusArea = "('BUY_SUCCESS')";

        }
        performanceParas.setInsuranceStatusArea(insuranceStatusArea);
        performanceParas.setBondStatusArea(bondStatusArea);
        performanceParas.setImmigrantStatusArea(immigrantStatusArea);

        return cnlPerformanceMapper.queryOrderStatusQty(performanceParas);
    }

    /**
     * 查询柱状图数据
     *
     * @param performanceParas
     * @return
     */
    public List<CnlPerformanceStatistics> queryBarData(CnlPerformanceParas performanceParas) {
        return cnlPerformanceMapper.queryBarData(performanceParas);
    }

    /**
     * 查询入口
     *
     * @param dto
     * @return
     */
    public List<CnlPerformanceResults> queryPerformance(CnlPerformanceParas dto,
                                                        int page,
                                                        int pagesize) {
        List<CnlPerformanceResults> resultsList = new ArrayList<CnlPerformanceResults>();

        CnlPerformanceResults resultsRec = new CnlPerformanceResults();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        // 柱状图
        List<CnlPerformanceStatistics> performanceStatisticsList = new ArrayList<CnlPerformanceStatistics>();

        // 业绩统计表 饼图 按照产品分类
        List<CnlPerformanceStatistics> performanceStatisticsPrdList = new ArrayList<CnlPerformanceStatistics>();

        // 业绩统计表 饼图 按照产品公司分类
        List<CnlPerformanceStatistics> performanceStatisticsSpeList = new ArrayList<CnlPerformanceStatistics>();

        // 首页
        if ("-1".equals(dto.getBeginYear().toString())) {
            // 首页“已确认收入”
            CnlPerformanceParas parasa = new CnlPerformanceParas();
            parasa.setChannelId(dto.getChannelId());
            //resultsRec.setPaiedReferralFee(cnlPerformanceMapper.queryDirectTotalAmount(parasa));
            resultsRec.setPaiedReferralFee(cnlPerformanceMapper.queryConfirmedIncome(parasa));

            CnlPerformanceParas paras = new CnlPerformanceParas();
            paras.setChannelId(dto.getChannelId());
            String startDateStr = dto.getBeginYear() + "-" + dto.getBeginMonth();
            try {
                Date startDate = sdf.parse(startDateStr);
                paras.setStartDate(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // 首页“签单量”、明细页新增订单
            //resultsRec.setNewOrderQty(cnlPerformanceMapper.queryNewOrderQty(paras));
            resultsRec.setNewOrderQty(cnlPerformanceMapper.queryEffectiveOrderQty(paras));

            // 计算最近5个月的数据（柱状图）
            Calendar now = Calendar.getInstance();
            int nowYear = now.get(Calendar.YEAR);
            int nowMonth = now.get(Calendar.MONTH) + 1;
            int monthQty = nowMonth;
            int beginYear;
            int beginMonth;
            if (nowMonth - 5 < 0) {
                beginYear = nowYear - 1;
                beginMonth = nowMonth - 5 + 12 + 1;
            } else {
                beginYear = nowYear;
                beginMonth = nowMonth - 5 + 1;
            }

            for (int i = 1; i < 6; i++) {
                CnlPerformanceParas monthPar = new CnlPerformanceParas();
                monthPar.setChannelId(dto.getChannelId());
                monthPar.setDatePeriod(beginYear + "-" + String.format("%02d", beginMonth));

                List<CnlPerformanceStatistics> tmpList = new ArrayList<CnlPerformanceStatistics>();
                tmpList = this.queryBarData(monthPar);
                if (tmpList.size() > 0) {
                    CnlPerformanceStatistics table = tmpList.get(0);
                    table.setChannelId(dto.getChannelId());
                    table.setYear(new Long(beginYear));
                    table.setMonth(new Long(beginMonth));
                    table.setWeek(null);
                    performanceStatisticsList.add(table);
                }

                beginMonth = beginMonth + 1;
                if (beginMonth == 13) {
                    beginYear = beginYear + 1;
                    beginMonth = 1;
                }
            }

            // 签单产品结构（饼图内圈）
            CnlPerformanceParas pieBig = new CnlPerformanceParas();
            pieBig.setChannelId(dto.getChannelId());
            List<CnlPerformanceStatistics> tmpList = new ArrayList<CnlPerformanceStatistics>();
            tmpList = cnlPerformanceMapper.queryPiePrdBig(pieBig);
            performanceStatisticsPrdList.addAll(tmpList);

            // 签单产品结构（饼图外圈）
            CnlPerformanceParas piePara = new CnlPerformanceParas();
            piePara.setChannelId(dto.getChannelId());
            List<CnlPerformanceStatistics> tmpListOut = new ArrayList<CnlPerformanceStatistics>();
            tmpListOut = cnlPerformanceMapper.queryPiePrdData(pieBig);
            performanceStatisticsPrdList.addAll(tmpListOut);

        } else {

            // 本月待收款金额 改成 预约中收益
            /*FetReceivable fetReceivable = new FetReceivable();
            fetReceivable.setReceiveCompanyId(dto.getChannelId());
            String currentMonthStr = sdf.format(new Date());
            fetReceivable.setReceiptPeriod(currentMonthStr);
            resultsRec.setToBeNominatedAmount(cnlPerformanceMapper.queryFetReceivableByCondition(fetReceivable));*/
            resultsRec.setToBeNominatedAmount(this.queryBookingIncome(dto));

            // 本月预计收益 改成 签单中收益
            /*String nextMonthStr = sdf.format(DateUtil.getDate(new Date(), 0, 1, 0));
            fetReceivable.setReceiptPeriod(nextMonthStr);
            resultsRec.setCurrentMonthAmount(cnlPerformanceMapper.queryFetReceivableByCondition(fetReceivable));*/
            resultsRec.setCurrentMonthAmount(this.querySignningIncome(dto));

            // 本月未确认收益  改成  应收款总额
            FetReceivable receivable = new FetReceivable();
            receivable.setReceiveCompanyId(dto.getChannelId());
            //receivable.setBaseDate(new Date());
            resultsRec.setUnconfirmedAmount(this.getUnconfirmedAmount(dto, "应收款总额"));

            // 新增客户
            CnlPerformanceParas paras = new CnlPerformanceParas();
            paras.setChannelId(dto.getChannelId());
            paras.setPerformanceType(dto.getPerformanceType());
            if (!"-1".equals(dto.getBeginMonth().toString()) && dto.getBeginMonth() != null) {
                String startDateStr = dto.getBeginYear() + "-" + dto.getBeginMonth();
                try {
                    Date startDate = sdf.parse(startDateStr);
                    //paras.setStartDate(startDate);
                    paras.setScopeStartDate(startDate);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, dto.getBeginYear().intValue());
                    calendar.set(Calendar.MONTH, dto.getBeginMonth().intValue() - 1);
                    int day = calendar.getActualMaximum(Calendar.DATE);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);

                    paras.setScopeEndDate(calendar.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {

                // 判断是否等于当前年
                Calendar todayCal = Calendar.getInstance();
                int todayYear = todayCal.get(Calendar.YEAR);
                if (todayYear == dto.getBeginYear()) {
                    String startDateStr = dto.getBeginYear() + "-1";
                    try {
                        Date startDate = sdf.parse(startDateStr);
                        paras.setStartDate(startDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    paras.setScopeStartDate(DateUtil.getDateBeginEnd(dto.getBeginYear().intValue(),
                            1,
                            1,
                            "Begin"));
                    paras.setScopeEndDate(DateUtil.getDateBeginEnd(dto.getBeginYear().intValue(),
                            0,
                            0,
                            "End"));
                }

            }
            resultsRec.setNewCustomerQty(this.queryNewCustomerQty(paras));
            // 新增订单  修改为   “生效订单”
            //resultsRec.setNewOrderQty(this.queryNewOrderQty(paras));     // 首页“签单量”、明细页新增订单
            resultsRec.setNewOrderQty(this.queryStatusSum(paras, "已生效"));
            // 缴费总额
            resultsRec.setPredictPaymentAmount(this.getPredictPaymentAmount(dto));
            // 直客收益金额
            resultsRec.setPredictIncomeAmount(this.getUnconfirmedAmount(dto, "直客收益"));

            paras.setStartDate(null);
            paras.setScopeStartDate(null);
            paras.setScopeEndDate(null);
            // 已取消
            resultsRec.setCanceledOrderQty(this.queryOrderStatusQty(paras, "已取消"));
            // 审核中
            resultsRec.setAuditOrderQty(this.queryStatusSum(paras, "审核中"));
            // 预约中
            resultsRec.setAppointmentOrderQty(this.queryStatusSum(paras, "预约中"));
            // 签单中
            resultsRec.setToSignOrderQty(this.queryStatusSum(paras, "签单中"));
            // 正在跟进
            resultsRec.setFollowingUpOrderQty(resultsRec.getAuditOrderQty() + resultsRec.getAppointmentOrderQty() + resultsRec.getToSignOrderQty());
            // 批核中
            resultsRec.setApprovingOrderQty(this.queryStatusSum(paras, "批核中"));
            // pending中
//            resultsRec.setPendingOrderQty(this.queryOrderStatusQty(paras, "pending中"));
            resultsRec.setPendingOrderQty(Long.valueOf(this.getPendingData(dto.getChannelId())));
            // 已生效
            resultsRec.setEffectiveOrderQty(this.queryStatusSum(paras, "已生效"));
            // 订单取消
            resultsRec.setAbrogatedOrderQty(this.queryStatusSum(paras, "订单取消"));
            // 即将生效
            resultsRec.setSignedOrderQty(resultsRec.getPendingOrderQty() + resultsRec.getEffectiveOrderQty() + resultsRec.getAbrogatedOrderQty());

            // 业绩统计表 柱状图
            // 计算月度情况
            if ("-1".equals(dto.getBeginMonth().toString())
                    && dto.getBeginMonth() != null
                    && "month".equals(dto.getSettingType())) {

                Calendar todayCal = Calendar.getInstance();
                int todayYear = todayCal.get(Calendar.YEAR);
                int month = 0;
                if (todayYear == dto.getBeginYear()) {
                    Calendar calendarBar = Calendar.getInstance();
                    month = calendarBar.get(Calendar.MONTH) + 1;
                } else {
                    month = 12;
                }

                // 循环计算每月数据
                Calendar calendarBar = Calendar.getInstance();
                for (int i = 1; i <= month; i++) {
                    CnlPerformanceParas monthPar = new CnlPerformanceParas();
                    monthPar.setChannelId(dto.getChannelId());
                    monthPar.setDatePeriod(dto.getBeginYear() + "-" + String.format("%02d", i));

                    List<CnlPerformanceStatistics> tmpList = new ArrayList<CnlPerformanceStatistics>();
                    tmpList = this.queryBarData(monthPar);
                    if (tmpList.size() > 0) {
                        CnlPerformanceStatistics table = tmpList.get(0);
                        table.setChannelId(dto.getChannelId());
                        table.setYear(dto.getBeginYear());
                        table.setMonth(new Long(i));
                        table.setWeek(null);
                        performanceStatisticsList.add(table);
                    }
                }

            } else if ("-1".equals(dto.getBeginMonth().toString())
                    && dto.getBeginMonth() != null
                    && "quarter".equals(dto.getSettingType())) {
                // 计算季度情况
                Calendar dateCal = Calendar.getInstance();
                dateCal.set(Calendar.YEAR, Integer.valueOf(dto.getBeginYear().toString()));
                dateCal.set(Calendar.MONTH, 1 - 1);
                dateCal.set(Calendar.DAY_OF_MONTH, 1);
                dateCal.set(Calendar.HOUR_OF_DAY, 0);
                dateCal.set(Calendar.MINUTE, 0);
                dateCal.set(Calendar.SECOND, 0);
                dateCal.set(Calendar.MILLISECOND, 0);

                // 1季度
                Date scopeStartDate = dateCal.getTime();
                dateCal.add(Calendar.MONTH, 3);
                dateCal.add(Calendar.MILLISECOND, -1);
                Date scopeEndDate = dateCal.getTime();

                CnlPerformanceParas quaterPar = new CnlPerformanceParas();
                quaterPar.setChannelId(dto.getChannelId());
                quaterPar.setScopeStartDate(scopeStartDate);
                quaterPar.setScopeEndDate(scopeEndDate);

                List<CnlPerformanceStatistics> tmpList1 = new ArrayList<CnlPerformanceStatistics>();
                tmpList1 = this.queryBarData(quaterPar);
                CnlPerformanceStatistics table1 = new CnlPerformanceStatistics();
                if (tmpList1.size() > 0) {
                    table1 = tmpList1.get(0);
                    table1.setChannelId(dto.getChannelId());
                    table1.setYear(dto.getBeginYear());
                    table1.setQuarter("1");
                    table1.setMonth(null);
                    table1.setWeek(null);
                    performanceStatisticsList.add(table1);
                }

                // 2季度
                dateCal.add(Calendar.MILLISECOND, 1);
                scopeStartDate = dateCal.getTime();
                dateCal.add(Calendar.MONTH, 3);
                dateCal.add(Calendar.MILLISECOND, -1);
                scopeEndDate = dateCal.getTime();
                quaterPar.setScopeStartDate(scopeStartDate);
                quaterPar.setScopeEndDate(scopeEndDate);

                List<CnlPerformanceStatistics> tmpList2 = new ArrayList<CnlPerformanceStatistics>();
                tmpList2 = this.queryBarData(quaterPar);
                CnlPerformanceStatistics table2 = new CnlPerformanceStatistics();
                if (tmpList2.size() > 0) {
                    table2 = tmpList2.get(0);
                    table2.setChannelId(dto.getChannelId());
                    table2.setYear(dto.getBeginYear());
                    table2.setQuarter("2");
                    table2.setMonth(null);
                    table2.setWeek(null);
                    performanceStatisticsList.add(table2);
                }

                // 3季度
                dateCal.add(Calendar.MILLISECOND, 1);
                scopeStartDate = dateCal.getTime();
                dateCal.add(Calendar.MONTH, 3);
                dateCal.add(Calendar.MILLISECOND, -1);
                scopeEndDate = dateCal.getTime();
                quaterPar.setScopeStartDate(scopeStartDate);
                quaterPar.setScopeEndDate(scopeEndDate);

                List<CnlPerformanceStatistics> tmpList3 = new ArrayList<CnlPerformanceStatistics>();
                tmpList3 = this.queryBarData(quaterPar);
                CnlPerformanceStatistics table3 = new CnlPerformanceStatistics();
                if (tmpList3.size() > 0) {
                    table3 = tmpList3.get(0);
                    table3.setChannelId(dto.getChannelId());
                    table3.setYear(dto.getBeginYear());
                    table3.setQuarter("3");
                    table3.setMonth(null);
                    table3.setWeek(null);
                    performanceStatisticsList.add(table3);
                }

                // 4季度
                dateCal.add(Calendar.MILLISECOND, 1);
                scopeStartDate = dateCal.getTime();
                dateCal.add(Calendar.MONTH, 3);
                dateCal.add(Calendar.MILLISECOND, -1);
                scopeEndDate = dateCal.getTime();
                quaterPar.setScopeStartDate(scopeStartDate);
                quaterPar.setScopeEndDate(scopeEndDate);

                List<CnlPerformanceStatistics> tmpList4 = new ArrayList<CnlPerformanceStatistics>();
                tmpList4 = this.queryBarData(quaterPar);
                CnlPerformanceStatistics table4 = new CnlPerformanceStatistics();
                if (tmpList4.size() > 0) {
                    table4 = tmpList4.get(0);
                    table4.setChannelId(dto.getChannelId());
                    table4.setYear(dto.getBeginYear());
                    table4.setQuarter("4");
                    table4.setMonth(null);
                    table4.setWeek(null);
                    performanceStatisticsList.add(table4);
                }

            } else if (!"-1".equals(dto.getBeginMonth().toString())) {
                // 计算周情况
                Calendar dateCal = Calendar.getInstance();
                dateCal.set(Calendar.YEAR, Integer.valueOf(dto.getBeginYear().toString()));
                dateCal.set(Calendar.MONTH, Integer.valueOf(dto.getBeginMonth().toString()) - 1);
                dateCal.setFirstDayOfWeek(Calendar.MONDAY);
                int weekQty = dateCal.getActualMaximum(Calendar.WEEK_OF_MONTH);

                // 循环计算每周数据
                for (int i = 1; i <= weekQty; i++) {

                    Map<String, Object> dateScope = DateUtil.getScopeForweeks(dateCal.get(Calendar.YEAR),
                            dateCal.get(Calendar.MONTH) + 1, i);
                    SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                    Date scopeStartDate = new Date();
                    Date scopeEndDate = new Date();
                    try {
                        scopeStartDate = sdformat.parse(dateScope.get("beginDate").toString());
                        scopeEndDate = sdformat.parse(dateScope.get("endDate").toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    CnlPerformanceParas weekPar = new CnlPerformanceParas();
                    weekPar.setChannelId(dto.getChannelId());
                    weekPar.setScopeStartDate(scopeStartDate);
                    weekPar.setScopeEndDate(scopeEndDate);

                    List<CnlPerformanceStatistics> tmpList = new ArrayList<CnlPerformanceStatistics>();
                    tmpList = this.queryBarData(weekPar);
                    if (tmpList.size() > 0) {
                        CnlPerformanceStatistics table = new CnlPerformanceStatistics();
                        table = tmpList.get(0);
                        table.setChannelId(dto.getChannelId());
                        table.setYear(dto.getBeginYear());
                        table.setMonth(dto.getBeginMonth());
                        table.setWeek(new Long(i));
                        performanceStatisticsList.add(table);
                    }
                }
            }

            // 业绩统计表 饼图 按照产品分类
            CnlPerformanceParas piePrdPara = new CnlPerformanceParas();
            piePrdPara.setChannelId(dto.getChannelId());
            if (!"-1".equals(dto.getBeginYear().toString())) {

                if (!"-1".equals(dto.getBeginMonth().toString())) {
                    piePrdPara.setScopeStartDate(DateUtil.getDateBeginEnd(dto.getBeginYear().intValue(),
                                                                          dto.getBeginMonth().intValue(),
                                                                          1,
                                                                          "Begin"));
                    piePrdPara.setScopeEndDate(DateUtil.getDateBeginEnd(dto.getBeginYear().intValue(),
                                                                        dto.getBeginMonth().intValue(),
                                                                        0,
                                                                        "End"));
                } else {
                    piePrdPara.setScopeStartDate(DateUtil.getDateBeginEnd(dto.getBeginYear().intValue(),
                            1,
                            1,
                            "Begin"));
                    piePrdPara.setScopeEndDate(DateUtil.getDateBeginEnd(dto.getBeginYear().intValue(),
                            0,
                            0,
                            "End"));
                }

                // 签单产品结构（饼图内圈）
                List<CnlPerformanceStatistics> tmpList = new ArrayList<CnlPerformanceStatistics>();
                tmpList = cnlPerformanceMapper.queryPiePrdBig(piePrdPara);
                performanceStatisticsPrdList.addAll(tmpList);

                // 签单产品结构（饼图外圈）
                List<CnlPerformanceStatistics> tmpListOut = new ArrayList<CnlPerformanceStatistics>();
                tmpListOut = cnlPerformanceMapper.queryPiePrdData(piePrdPara);
                performanceStatisticsPrdList.addAll(tmpListOut);
            }

            // 业绩统计表  饼图 按照产品公司分类
            CnlPerformanceParas pieSpePara = new CnlPerformanceParas();
            pieSpePara.setChannelId(dto.getChannelId());
            if (!"-1".equals(dto.getBeginYear().toString())) {

                if (!"-1".equals(dto.getBeginMonth().toString())) {
                    pieSpePara.setScopeStartDate(DateUtil.getDateBeginEnd(dto.getBeginYear().intValue(),
                            dto.getBeginMonth().intValue(),
                            1,
                            "Begin"));
                    pieSpePara.setScopeEndDate(DateUtil.getDateBeginEnd(dto.getBeginYear().intValue(),
                            dto.getBeginMonth().intValue(),
                            0,
                            "End"));
                } else {
                    pieSpePara.setScopeStartDate(DateUtil.getDateBeginEnd(dto.getBeginYear().intValue(),
                            1,
                            1,
                            "Begin"));
                    pieSpePara.setScopeEndDate(DateUtil.getDateBeginEnd(dto.getBeginYear().intValue(),
                            0,
                            0,
                            "End"));
                }

                // 签单产品结构（饼图内圈）
                List<CnlPerformanceStatistics> tmpList = new ArrayList<CnlPerformanceStatistics>();
                tmpList = cnlPerformanceMapper.queryPiePrdBig(pieSpePara);
                performanceStatisticsSpeList.addAll(tmpList);

                // 签单产品结构（饼图外圈）
                List<CnlPerformanceStatistics> tmpListOut = new ArrayList<CnlPerformanceStatistics>();
                tmpListOut = cnlPerformanceMapper.queryPieSpeData(pieSpePara);
                performanceStatisticsSpeList.addAll(tmpListOut);
            }

            // 收益结构明细
            List<CnlProfitStructure> profitStructureList = new ArrayList<CnlProfitStructure>();
            profitStructureList = this.calculateProfitStructure(dto);

            // 收益结构汇总
            CnlPerformanceParas totalPara = new CnlPerformanceParas();
            totalPara.setChannelId(dto.getChannelId());
            if (!"-1".equals(dto.getBeginYear().toString())) {

                if (!"-1".equals(dto.getBeginMonth().toString())) {
                    String startDateStr = dto.getBeginYear() + "-" + dto.getBeginMonth();
                    try {
                        Date startDate = sdf.parse(startDateStr);
                        totalPara.setScopeStartDate(startDate);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, dto.getBeginYear().intValue());
                        calendar.set(Calendar.MONTH, dto.getBeginMonth().intValue() - 1);
                        int day = calendar.getActualMaximum(Calendar.DATE);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);

                        totalPara.setScopeEndDate(calendar.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {

                    totalPara.setScopeStartDate(DateUtil.getDateBeginEnd(dto.getBeginYear().intValue(),
                            1,
                            1,
                            "Begin"));
                    totalPara.setScopeEndDate(DateUtil.getDateBeginEnd(dto.getBeginYear().intValue(),
                            0,
                            0,
                            "End"));
                }

                totalPara.setReceiptType("1ZJF");
                resultsRec.setDirectCustomerTotalAmount(cnlPerformanceMapper.queryProfitByCondition(totalPara));
                totalPara.setReceiptType("1GLJT");
                resultsRec.setTeamTotalAmount(cnlPerformanceMapper.queryProfitByCondition(totalPara));
                totalPara.setReceiptType("1JSF");
                resultsRec.setReferralFeeTotalAmount(cnlPerformanceMapper.queryProfitByCondition(totalPara));
            }

            // 业绩明细表
            List<CnlPerformanceDetail> performanceDetailList = new ArrayList<CnlPerformanceDetail>();
            //performanceDetailList = this.calculatePerformanceDetail(dto, page, pagesize);
            //resultsRec.setPerformanceDetailList(performanceDetailList);
            //resultsRec.setPerformanceDetailResponse(new ResponseData(performanceDetailList));
            resultsRec.setPerformanceDetailResponse(this.calculatePerformanceDetail(dto, page, pagesize));

            resultsRec.setProfitStructureList(profitStructureList);
        }

        resultsRec.setPerformanceStatisticsList(performanceStatisticsList);
        resultsRec.setPerformanceStatisticsSpeList(performanceStatisticsSpeList);
        resultsRec.setPerformanceStatisticsPrdList(performanceStatisticsPrdList);

        resultsList.add(resultsRec);
        return resultsList;
    }

    // 计算收益结构
    public List<CnlProfitStructure> calculateProfitStructure(CnlPerformanceParas dto) {
        List<CnlProfitStructure> profitStructureList = new ArrayList<CnlProfitStructure>();

        // 收益结构 柱状图
        // 计算月度情况
        if ("-1".equals(dto.getBeginMonth().toString())
                && dto.getBeginMonth() != null
                && "month".equals(dto.getSettingType())) {

            // 循环计算每月数据
            Calendar calendarBar = Calendar.getInstance();
            for (int i = 1; i <= calendarBar.get(Calendar.MONTH) + 1; i++) {

                CnlPerformanceParas monthPar = new CnlPerformanceParas();
                monthPar.setChannelId(dto.getChannelId());
                monthPar.setDatePeriod(dto.getBeginYear() + "-" + String.format("%02d", i));

                CnlProfitStructure structure = new CnlProfitStructure();
                structure.setChannelId(dto.getChannelId());
                structure.setYear(dto.getBeginYear());
                structure.setMonth(new Long(i));
                monthPar.setReceiptType("1ZJF");
                structure.setDirectCustomerAmount(cnlPerformanceMapper.queryProfitByCondition(monthPar)
                        .setScale(0, BigDecimal.ROUND_HALF_UP));
                monthPar.setReceiptType("1GLJT");
                structure.setTeamAmount(cnlPerformanceMapper.queryProfitByCondition(monthPar)
                        .setScale(0, BigDecimal.ROUND_HALF_UP));
                monthPar.setReceiptType("1JSF");
                structure.setReferralFeeAmount(cnlPerformanceMapper.queryProfitByCondition(monthPar)
                        .setScale(0, BigDecimal.ROUND_HALF_UP));

                profitStructureList.add(structure);
            }

        } else if ("-1".equals(dto.getBeginMonth().toString())
                && dto.getBeginMonth() != null
                && "quarter".equals(dto.getSettingType())) {
            // 计算季度情况
            Calendar dateCal = Calendar.getInstance();
            dateCal.set(Calendar.YEAR, Integer.valueOf(dto.getBeginYear().toString()));
            dateCal.set(Calendar.MONTH, 1 - 1);
            dateCal.set(Calendar.DAY_OF_MONTH, 1);
            dateCal.set(Calendar.HOUR_OF_DAY, 0);
            dateCal.set(Calendar.MINUTE, 0);
            dateCal.set(Calendar.SECOND, 0);
            dateCal.set(Calendar.MILLISECOND, 0);

            // 1季度
            Date scopeStartDate = dateCal.getTime();
            dateCal.add(Calendar.MONTH, 3);
            dateCal.add(Calendar.MILLISECOND, -1);
            Date scopeEndDate = dateCal.getTime();

            CnlPerformanceParas quaterPar = new CnlPerformanceParas();
            quaterPar.setChannelId(dto.getChannelId());
            quaterPar.setScopeStartDate(scopeStartDate);
            quaterPar.setScopeEndDate(scopeEndDate);

            CnlProfitStructure structure1 = new CnlProfitStructure();
            structure1.setChannelId(dto.getChannelId());
            structure1.setYear(dto.getBeginYear());
            structure1.setMonth(null);
            structure1.setQuarter("1");
            quaterPar.setReceiptType("1ZJF");
            structure1.setDirectCustomerAmount(cnlPerformanceMapper.queryProfitByCondition(quaterPar)
                    .setScale(0, BigDecimal.ROUND_HALF_UP));
            quaterPar.setReceiptType("1GLJT");
            structure1.setTeamAmount(cnlPerformanceMapper.queryProfitByCondition(quaterPar)
                    .setScale(0, BigDecimal.ROUND_HALF_UP));
            quaterPar.setReceiptType("1JSF");
            structure1.setReferralFeeAmount(cnlPerformanceMapper.queryProfitByCondition(quaterPar)
                    .setScale(0, BigDecimal.ROUND_HALF_UP));
            profitStructureList.add(structure1);

            // 2季度
            dateCal.add(Calendar.MILLISECOND, 1);
            scopeStartDate = dateCal.getTime();
            dateCal.add(Calendar.MONTH, 3);
            dateCal.add(Calendar.MILLISECOND, -1);
            scopeEndDate = dateCal.getTime();
            quaterPar.setScopeStartDate(scopeStartDate);
            quaterPar.setScopeEndDate(scopeEndDate);

            CnlProfitStructure structure2 = new CnlProfitStructure();
            structure2.setChannelId(dto.getChannelId());
            structure2.setYear(dto.getBeginYear());
            structure2.setMonth(null);
            structure2.setQuarter("2");
            quaterPar.setReceiptType("1ZJF");
            structure2.setDirectCustomerAmount(cnlPerformanceMapper.queryProfitByCondition(quaterPar)
                    .setScale(0, BigDecimal.ROUND_HALF_UP));
            quaterPar.setReceiptType("1GLJT");
            structure2.setTeamAmount(cnlPerformanceMapper.queryProfitByCondition(quaterPar)
                    .setScale(0, BigDecimal.ROUND_HALF_UP));
            quaterPar.setReceiptType("1JSF");
            structure2.setReferralFeeAmount(cnlPerformanceMapper.queryProfitByCondition(quaterPar)
                    .setScale(0, BigDecimal.ROUND_HALF_UP));
            profitStructureList.add(structure2);

            // 3季度
            dateCal.add(Calendar.MILLISECOND, 1);
            scopeStartDate = dateCal.getTime();
            dateCal.add(Calendar.MONTH, 3);
            dateCal.add(Calendar.MILLISECOND, -1);
            scopeEndDate = dateCal.getTime();
            quaterPar.setScopeStartDate(scopeStartDate);
            quaterPar.setScopeEndDate(scopeEndDate);

            CnlProfitStructure structure3 = new CnlProfitStructure();
            structure3.setChannelId(dto.getChannelId());
            structure3.setYear(dto.getBeginYear());
            structure3.setMonth(null);
            structure3.setQuarter("3");
            quaterPar.setReceiptType("1ZJF");
            structure3.setDirectCustomerAmount(cnlPerformanceMapper.queryProfitByCondition(quaterPar)
                    .setScale(0, BigDecimal.ROUND_HALF_UP));
            quaterPar.setReceiptType("1GLJT");
            structure3.setTeamAmount(cnlPerformanceMapper.queryProfitByCondition(quaterPar)
                    .setScale(0, BigDecimal.ROUND_HALF_UP));
            quaterPar.setReceiptType("1JSF");
            structure3.setReferralFeeAmount(cnlPerformanceMapper.queryProfitByCondition(quaterPar)
                    .setScale(0, BigDecimal.ROUND_HALF_UP));
            profitStructureList.add(structure3);

            // 4季度
            dateCal.add(Calendar.MILLISECOND, 1);
            scopeStartDate = dateCal.getTime();
            dateCal.add(Calendar.MONTH, 3);
            dateCal.add(Calendar.MILLISECOND, -1);
            scopeEndDate = dateCal.getTime();
            quaterPar.setScopeStartDate(scopeStartDate);
            quaterPar.setScopeEndDate(scopeEndDate);

            CnlProfitStructure structure4 = new CnlProfitStructure();
            structure4.setChannelId(dto.getChannelId());
            structure4.setYear(dto.getBeginYear());
            structure4.setMonth(null);
            structure4.setQuarter("4");
            quaterPar.setReceiptType("1ZJF");
            structure4.setDirectCustomerAmount(cnlPerformanceMapper.queryProfitByCondition(quaterPar)
                    .setScale(0, BigDecimal.ROUND_HALF_UP));
            quaterPar.setReceiptType("1GLJT");
            structure4.setTeamAmount(cnlPerformanceMapper.queryProfitByCondition(quaterPar)
                    .setScale(0, BigDecimal.ROUND_HALF_UP));
            quaterPar.setReceiptType("1JSF");
            structure4.setReferralFeeAmount(cnlPerformanceMapper.queryProfitByCondition(quaterPar)
                    .setScale(0, BigDecimal.ROUND_HALF_UP));
            profitStructureList.add(structure4);


        } else if (!"-1".equals(dto.getBeginMonth().toString())) {
            // 计算周情况
            Calendar dateCal = Calendar.getInstance();
            dateCal.set(Calendar.YEAR, Integer.valueOf(dto.getBeginYear().toString()));
            dateCal.set(Calendar.MONTH, Integer.valueOf(dto.getBeginMonth().toString()) - 1);
            dateCal.setFirstDayOfWeek(Calendar.MONDAY);
            int weekQty = dateCal.getActualMaximum(Calendar.WEEK_OF_MONTH);

            // 循环计算每周数据
            for (int i = 1; i <= weekQty; i++) {

                Map<String, Object> dateScope = DateUtil.getScopeForweeks(dateCal.get(Calendar.YEAR),
                        dateCal.get(Calendar.MONTH) + 1, i);
                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                Date scopeStartDate = new Date();
                Date scopeEndDate = new Date();
                try {
                    scopeStartDate = sdformat.parse(dateScope.get("beginDate").toString());
                    scopeEndDate = sdformat.parse(dateScope.get("endDate").toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                CnlPerformanceParas weekPar = new CnlPerformanceParas();
                weekPar.setChannelId(dto.getChannelId());
                weekPar.setScopeStartDate(scopeStartDate);
                weekPar.setScopeEndDate(scopeEndDate);

                CnlProfitStructure structure = new CnlProfitStructure();
                structure.setChannelId(dto.getChannelId());
                structure.setYear(dto.getBeginYear());
                structure.setMonth(dto.getBeginMonth());
                structure.setWeek(i + "");

                weekPar.setReceiptType("1ZJF");
                structure.setDirectCustomerAmount(cnlPerformanceMapper.queryProfitByCondition(weekPar)
                        .setScale(0, BigDecimal.ROUND_HALF_UP));
                weekPar.setReceiptType("1GLJT");
                structure.setTeamAmount(cnlPerformanceMapper.queryProfitByCondition(weekPar)
                        .setScale(0, BigDecimal.ROUND_HALF_UP));
                weekPar.setReceiptType("1JSF");
                structure.setReferralFeeAmount(cnlPerformanceMapper.queryProfitByCondition(weekPar)
                        .setScale(0, BigDecimal.ROUND_HALF_UP));
                profitStructureList.add(structure);
            }
        }

        return profitStructureList;
    }

    // 业绩明细表
    public /*List<CnlPerformanceDetail>*/ResponseData calculatePerformanceDetail(CnlPerformanceParas dto, int page, int pagesize) {
        List<CnlPerformanceDetail> performanceDetailList = new ArrayList<CnlPerformanceDetail>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        CnlPerformanceParas detailPara = new CnlPerformanceParas();
        detailPara.setChannelId(dto.getChannelId());
        detailPara.setOrderBy(dto.getOrderBy());

        if (!"-1".equals(dto.getBeginMonth().toString())) {
            String startDateStr = dto.getBeginYear() + "-" + dto.getBeginMonth();
            try {
                Date startDate = sdf.parse(startDateStr);
                detailPara.setScopeStartDate(startDate);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, dto.getBeginYear().intValue());
                calendar.set(Calendar.MONTH, dto.getBeginMonth().intValue() - 1);
                int day = calendar.getActualMaximum(Calendar.DATE);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);

                detailPara.setScopeEndDate(calendar.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            detailPara.setScopeStartDate(DateUtil.getDateBeginEnd(dto.getBeginYear().intValue(),
                    1,
                    1,
                    "Begin"));
            detailPara.setScopeEndDate(DateUtil.getDateBeginEnd(dto.getBeginYear().intValue(),
                    0,
                    0,
                    "End"));
        }

        List<CnlPerformanceDetail> tmpList = new ArrayList<CnlPerformanceDetail>();
        PageHelper.startPage(page, pagesize);
        tmpList = cnlPerformanceMapper.queryPerformanceDetail(detailPara);
        /* 获取"付款类型"代码维护 */
        ClbCodeValue codeValue = new ClbCodeValue();
        codeValue.setCodeId(10190L);
        List<ClbCodeValue> clbCodeValues = clbCodeValueMapper.selectCodeValuesByCodeId(codeValue);
        /* 获取实付统计数据 */
        FetActualPayment fetActualPayment = new FetActualPayment();
        List<FetActualPayment> fetActualPayments = fetActualPaymentService.getData(null, fetActualPayment, 1, 9999999);
        /* 获取应收预测数据 */
        FetReceivable fetReceivable = new FetReceivable();
        List<FetReceivable> fetReceivables = fetReceivableService.getData(null, fetReceivable, 1, 9999999);
        /* 格式化输出 */
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMaximumFractionDigits(2);
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);
        for (CnlPerformanceDetail cnlPerformanceDetail : tmpList) {
            /* 获取收益类型 */
            for (ClbCodeValue clbCodeValue : clbCodeValues) {
                if (cnlPerformanceDetail.getIncomeType().trim().equals(clbCodeValue.getMeaning().trim())) {
                    cnlPerformanceDetail.setPaymentType(clbCodeValue.getValue().trim());
                }
            }
            /* 获取费率和实际收益(HKD) */
            for (FetActualPayment actualPayment : fetActualPayments) {
                if (cnlPerformanceDetail.getOrderNum().equals(actualPayment.getOrderNumber())
                        && cnlPerformanceDetail.getPaymentType().trim().equals(actualPayment.getPaymentType())) {
                    cnlPerformanceDetail.setHkdAmount(df.format(actualPayment.getHkdAmount()));
                }
            }
            for (FetReceivable receivable : fetReceivables) {
                if (cnlPerformanceDetail.getOrderNum().equals(receivable.getOrderNumber())
                        && cnlPerformanceDetail.getPaymentType().trim().equals(receivable.getReceiptType())) {
                    cnlPerformanceDetail.setRate1(nf.format(receivable.getRate()));
                }
            }
            /* 为null则输出"--" */
            if (StringUtils.isBlank(cnlPerformanceDetail.getRate1())) {
                cnlPerformanceDetail.setRate1("--");
            }
            if (StringUtils.isBlank(cnlPerformanceDetail.getHkdAmount())) {
                cnlPerformanceDetail.setHkdAmount("--");
            }
        }
        //performanceDetailList.addAll(tmpList);
        if ("personal".equals(dto.getPerformanceType())) { // 如果是直客业绩则只取转介费
            for (CnlPerformanceDetail performanceDetail : tmpList) {
                if (performanceDetail.getPaymentType().contains("ZJF")) {
                    performanceDetailList.add(performanceDetail);
                }
            }
        } else if ("team".equals(dto.getPerformanceType())) {
            performanceDetailList.addAll(tmpList);
        }

        return new ResponseData(performanceDetailList);
    }

    // 计算收益结构汇总数据
    public void calculateIncomeTotal(CnlPerformanceParas dto) {

        CnlPerformanceParas totalPara = new CnlPerformanceParas();
        totalPara.setChannelId(dto.getChannelId());
        if (!"-1".equals(dto.getBeginYear().toString())) {

            Calendar dateCal = Calendar.getInstance();
            dateCal.set(Calendar.YEAR, Integer.valueOf(dto.getBeginYear().toString()));
            dateCal.set(Calendar.DAY_OF_MONTH, 1);
            dateCal.set(Calendar.HOUR_OF_DAY, 0);
            dateCal.set(Calendar.MINUTE, 0);
            dateCal.set(Calendar.SECOND, 0);
            dateCal.set(Calendar.MILLISECOND, 0);

            if (!"-1".equals(dto.getBeginMonth().toString())) {
                dateCal.set(Calendar.MONTH, Integer.valueOf((dto.getBeginMonth() - 1) + ""));
            } else {
                dateCal.set(Calendar.MONTH, 0);
            }
            totalPara.setStartDate(dateCal.getTime());

        }

    }

    private Integer getPendingData(Long channelId) {
       return ordOrderService.getPendingNum(channelId);
    }

    /**
     * 获取应收款总额和直客收益
     *
     * @param cnlPerformanceParas
     * @return
     */
    private BigDecimal getUnconfirmedAmount(CnlPerformanceParas cnlPerformanceParas, String type){
        BigDecimal totalAmount = new BigDecimal(0);
        CnlPerformanceParas dto = new CnlPerformanceParas();
        dto.setChannelId(cnlPerformanceParas.getChannelId());
        dto.setPerformanceType(cnlPerformanceParas.getPerformanceType());
        if ("直客收益".equals(type)) {
            dto.setBeginYear(cnlPerformanceParas.getBeginYear());
            if (cnlPerformanceParas.getBeginMonth() != -1) {
                dto.setBeginMonth(cnlPerformanceParas.getBeginMonth());
            }
        }
        List<FetTimeReceivable> fetTimeReceivables = fetTimeReceivableService.queryUnconfirmedAmount(dto);
        for (FetTimeReceivable fetTimeReceivable : fetTimeReceivables) {
            totalAmount = totalAmount.add(fetTimeReceivable.getHkdAmount());
        }
        return totalAmount;
    }

    /**
     * 获取缴费总额
     *
     * @param cnlPerformanceParas
     * @return
     */
    private BigDecimal getPredictPaymentAmount(CnlPerformanceParas cnlPerformanceParas) {
        BigDecimal totalAmount = new BigDecimal(0);
        CnlPerformanceParas dto = new CnlPerformanceParas();
        dto.setChannelId(cnlPerformanceParas.getChannelId());
        dto.setPerformanceType(cnlPerformanceParas.getPerformanceType());
        dto.setBeginYear(cnlPerformanceParas.getBeginYear());
        if (cnlPerformanceParas.getBeginMonth() != -1) {
            dto.setBeginMonth(cnlPerformanceParas.getBeginMonth());
        }
        List<FetTimeReceivable> fetTimeReceivables = fetTimeReceivableService.queryUnconfirmedAmount(dto);
        for (FetTimeReceivable fetTimeReceivable : fetTimeReceivables) {
            totalAmount = totalAmount.add(fetTimeReceivable.getAmount()).multiply(fetTimeReceivable.getRate());
        }
        return  totalAmount;
    }

}