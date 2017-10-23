package clb.core.channel.mapper;

import clb.core.channel.dto.CnlPerformanceDetail;
import clb.core.channel.dto.CnlPerformanceParas;
import clb.core.channel.dto.CnlPerformanceStatistics;
import clb.core.forecast.dto.FetReceivable;
import clb.core.order.dto.OrdOrder;

import java.math.BigDecimal;
import java.util.List;

public interface CnlPerformanceMapper {

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
    public Long queryOrderStatusQty(CnlPerformanceParas performanceParas);

    /**
     * 查询柱状图数据
     * @param performanceParas
     * @return
     */
    public List<CnlPerformanceStatistics> queryBarData(CnlPerformanceParas performanceParas);

    /**
     * 查询饼图数据 内圈 大类
     * @param performanceParas
     * @return
     */
    public List<CnlPerformanceStatistics> queryPiePrdBig(CnlPerformanceParas performanceParas);

    /**
     * 查询饼图数据(按照产品分类)
     * @param performanceParas
     * @return
     */
    public List<CnlPerformanceStatistics> queryPiePrdData(CnlPerformanceParas performanceParas);

    /**
     * 查询饼图数据(按照产品公司)
     * @param performanceParas
     * @return
     */
    public List<CnlPerformanceStatistics> queryPieSpeData(CnlPerformanceParas performanceParas);

    /**
     * 查询业绩明细表
     * @param performanceParas
     * @return
     */
    public List<CnlPerformanceDetail> queryPerformanceDetail(CnlPerformanceParas performanceParas);

    /**
     * 根据条件查询收益结构
     * @param performanceParas
     * @return
     */
    public BigDecimal queryProfitByCondition(CnlPerformanceParas performanceParas);

    /**
     * 查询首页“已确认收入”
     * @param performanceParas
     * @return
     */
    public BigDecimal queryConfirmedIncome(CnlPerformanceParas performanceParas);

    /**
     * 查询个人订单
     * @param performanceParas
     * @return
     */
    public List<OrdOrder> queryPersonalOrder(CnlPerformanceParas performanceParas);

    /**
     * 查询团队订单
     * @param performanceParas
     * @return
     */
    public List<OrdOrder> queryTeamOrder(CnlPerformanceParas performanceParas);

    /**
     * 查询生效订单数量
     * @param performanceParas
     * @return
     */
    public Long queryEffectiveOrderQty(CnlPerformanceParas performanceParas);
}