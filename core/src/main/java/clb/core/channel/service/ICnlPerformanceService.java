package clb.core.channel.service;

import clb.core.channel.dto.CnlPerformanceParas;
import clb.core.channel.dto.CnlPerformanceResults;
import clb.core.channel.dto.CnlPerformanceStatistics;
import clb.core.forecast.dto.FetReceivable;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;

import java.math.BigDecimal;
import java.util.List;

public interface ICnlPerformanceService extends ProxySelf<ICnlPerformanceService>{
    /**
     * 查询本月待收款金额、本月收益金额
     * @param fetReceivable
     * @return
     */
    public BigDecimal queryFetReceivableByCondition(FetReceivable fetReceivable);

    /**
     * 查询当前渠道下，起始时间之后创建的客户数量
     * @param performanceParas
     * @return
     */
    public Long queryNewCustomerQty(CnlPerformanceParas performanceParas);

    /**
     * 查询当前渠道下，起始时间之后创建的订单数量
     * @param performanceParas
     * @return
     */
    public Long queryNewOrderQty(CnlPerformanceParas performanceParas);

    /**
     * 缴费总额
     * @param performanceParas
     * @return
     */
    public BigDecimal queryPayTotalAmount(CnlPerformanceParas performanceParas);

    /**
     * 直客收益金额
     * @param performanceParas
     * @return
     */
    public BigDecimal queryDirectTotalAmount(CnlPerformanceParas performanceParas);

    /**
     * 不同状态订单数量
     * @param performanceParas
     * @return
     */
    public Long queryOrderStatusQty(CnlPerformanceParas performanceParas, String statusTye);

    /**
     * 查询柱状图数据
     * @param performanceParas
     * @return
     */
    public List<CnlPerformanceStatistics> queryBarData(CnlPerformanceParas performanceParas);

    /**
     * 查询入口
     * @param dto
     * @return
     */
    public List<CnlPerformanceResults> queryPerformance(CnlPerformanceParas dto,
                                                        int page,
                                                        int pagesize);

    /**
     * 业绩明细表
     * @param dto
     * @return
     */
    public ResponseData calculatePerformanceDetail(CnlPerformanceParas dto, int page, int pagesize);
}