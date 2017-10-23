package clb.core.channel.dto;

import com.hand.hap.system.dto.ResponseData;

import java.math.BigDecimal;
import java.util.List;

/**
 * 我的业绩结果
 **/
public class CnlPerformanceResults {

    // 待派收益
    private BigDecimal toBeNominatedAmount;

    // 本月预计派发
    private BigDecimal currentMonthAmount;

    // 尚未确认收益
    private BigDecimal unconfirmedAmount;

    private Long newCustomerQty;

    private Long newOrderQty;

    private BigDecimal predictPaymentAmount;

    private BigDecimal predictIncomeAmount;

    private Long followingUpOrderQty;

    private Long canceledOrderQty;

    private Long auditOrderQty;

    private Long appointmentOrderQty;

    private Long toSignOrderQty;

    private Long signedOrderQty;

    private Long approvingOrderQty;

    private Long pendingOrderQty;

    private Long effectiveOrderQty;

    private Long abrogatedOrderQty;

    private BigDecimal paiedReferralFee;    // 首页“已派转介费”

    // 收益结构汇总数据
    private BigDecimal directCustomerTotalAmount;

    private BigDecimal teamTotalAmount;

    private BigDecimal referralFeeTotalAmount;

    public BigDecimal getDirectCustomerTotalAmount() {
        return directCustomerTotalAmount;
    }

    public void setDirectCustomerTotalAmount(BigDecimal directCustomerTotalAmount) {
        this.directCustomerTotalAmount = directCustomerTotalAmount;
    }

    public BigDecimal getTeamTotalAmount() {
        return teamTotalAmount;
    }

    public void setTeamTotalAmount(BigDecimal teamTotalAmount) {
        this.teamTotalAmount = teamTotalAmount;
    }

    public BigDecimal getReferralFeeTotalAmount() {
        return referralFeeTotalAmount;
    }

    public void setReferralFeeTotalAmount(BigDecimal referralFeeTotalAmount) {
        this.referralFeeTotalAmount = referralFeeTotalAmount;
    }

    public BigDecimal getToBeNominatedAmount() {
        return toBeNominatedAmount;
    }

    public void setToBeNominatedAmount(BigDecimal toBeNominatedAmount) {
        this.toBeNominatedAmount = toBeNominatedAmount;
    }

    public BigDecimal getPaiedReferralFee() {
        return paiedReferralFee;
    }

    public void setPaiedReferralFee(BigDecimal paiedReferralFee) {
        this.paiedReferralFee = paiedReferralFee;
    }

    public BigDecimal getCurrentMonthAmount() {
        return currentMonthAmount;
    }

    public void setCurrentMonthAmount(BigDecimal currentMonthAmount) {
        this.currentMonthAmount = currentMonthAmount;
    }

    public BigDecimal getUnconfirmedAmount() {
        return unconfirmedAmount;
    }

    public void setUnconfirmedAmount(BigDecimal unconfirmedAmount) {
        this.unconfirmedAmount = unconfirmedAmount;
    }

    public Long getNewCustomerQty() {
        return newCustomerQty;
    }

    public void setNewCustomerQty(Long newCustomerQty) {
        this.newCustomerQty = newCustomerQty;
    }

    public Long getNewOrderQty() {
        return newOrderQty;
    }

    public void setNewOrderQty(Long newOrderQty) {
        this.newOrderQty = newOrderQty;
    }

    public BigDecimal getPredictPaymentAmount() {
        return predictPaymentAmount;
    }

    public void setPredictPaymentAmount(BigDecimal predictPaymentAmount) {
        this.predictPaymentAmount = predictPaymentAmount;
    }

    public BigDecimal getPredictIncomeAmount() {
        return predictIncomeAmount;
    }

    public void setPredictIncomeAmount(BigDecimal predictIncomeAmount) {
        this.predictIncomeAmount = predictIncomeAmount;
    }

    public Long getFollowingUpOrderQty() {
        return followingUpOrderQty;
    }

    public void setFollowingUpOrderQty(Long followingUpOrderQty) {
        this.followingUpOrderQty = followingUpOrderQty;
    }

    public Long getCanceledOrderQty() {
        return canceledOrderQty;
    }

    public void setCanceledOrderQty(Long canceledOrderQty) {
        this.canceledOrderQty = canceledOrderQty;
    }

    public Long getAuditOrderQty() {
        return auditOrderQty;
    }

    public void setAuditOrderQty(Long auditOrderQty) {
        this.auditOrderQty = auditOrderQty;
    }

    public Long getAppointmentOrderQty() {
        return appointmentOrderQty;
    }

    public void setAppointmentOrderQty(Long appointmentOrderQty) {
        this.appointmentOrderQty = appointmentOrderQty;
    }

    public Long getToSignOrderQty() {
        return toSignOrderQty;
    }

    public void setToSignOrderQty(Long toSignOrderQty) {
        this.toSignOrderQty = toSignOrderQty;
    }

    public Long getSignedOrderQty() {
        return signedOrderQty;
    }

    public void setSignedOrderQty(Long signedOrderQty) {
        this.signedOrderQty = signedOrderQty;
    }

    public Long getApprovingOrderQty() {
        return approvingOrderQty;
    }

    public void setApprovingOrderQty(Long approvingOrderQty) {
        this.approvingOrderQty = approvingOrderQty;
    }

    public Long getPendingOrderQty() {
        return pendingOrderQty;
    }

    public void setPendingOrderQty(Long pendingOrderQty) {
        this.pendingOrderQty = pendingOrderQty;
    }

    public Long getEffectiveOrderQty() {
        return effectiveOrderQty;
    }

    public void setEffectiveOrderQty(Long effectiveOrderQty) {
        this.effectiveOrderQty = effectiveOrderQty;
    }

    public Long getAbrogatedOrderQty() {
        return abrogatedOrderQty;
    }

    public void setAbrogatedOrderQty(Long abrogatedOrderQty) {
        this.abrogatedOrderQty = abrogatedOrderQty;
    }

    public List<CnlPerformanceStatistics> getPerformanceStatisticsList() {
        return performanceStatisticsList;
    }

    public void setPerformanceStatisticsList(List<CnlPerformanceStatistics> cnlPerformanceStatisticsList) {
        this.performanceStatisticsList = cnlPerformanceStatisticsList;
    }

    public List<CnlProfitStructure> getProfitStructureList() {
        return profitStructureList;
    }

    public void setProfitStructureList(List<CnlProfitStructure> profitStructureList) {
        this.profitStructureList = profitStructureList;
    }

    public List<CnlPerformanceDetail> getPerformanceDetailList() {
        return performanceDetailList;
    }

    public void setPerformanceDetailList(List<CnlPerformanceDetail> performanceDetailList) {
        this.performanceDetailList = performanceDetailList;
    }

    private List<CnlProfitStructure> profitStructureList;

    private List<CnlPerformanceDetail> performanceDetailList;

    public ResponseData getPerformanceDetailResponse() {
        return performanceDetailResponse;
    }

    public void setPerformanceDetailResponse(ResponseData performanceDetailResponse) {
        this.performanceDetailResponse = performanceDetailResponse;
    }

    private ResponseData performanceDetailResponse;

    public List<CnlPerformanceStatistics> getPerformanceStatisticsSpeList() {
        return performanceStatisticsSpeList;
    }

    public void setPerformanceStatisticsSpeList(List<CnlPerformanceStatistics> performanceStatisticsSpeList) {
        this.performanceStatisticsSpeList = performanceStatisticsSpeList;
    }

    public List<CnlPerformanceStatistics> getPerformanceStatisticsPrdList() {
        return performanceStatisticsPrdList;
    }

    public void setPerformanceStatisticsPrdList(List<CnlPerformanceStatistics> performanceStatisticsPrdList) {
        this.performanceStatisticsPrdList = performanceStatisticsPrdList;
    }

    // 业绩统计图 按照产品公司分类
    private List<CnlPerformanceStatistics> performanceStatisticsSpeList;

    // 业绩统计图 按照产品分类
    private List<CnlPerformanceStatistics> performanceStatisticsPrdList;

    // 业绩统计表 按照产品分类
    private List<CnlPerformanceStatistics> performanceStatisticsList;

}
