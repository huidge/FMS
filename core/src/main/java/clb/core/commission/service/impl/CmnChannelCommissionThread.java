package clb.core.commission.service.impl;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.dto.CnlContractRate;
import clb.core.channel.dto.CnlLevel;
import clb.core.channel.service.ICnlLevelService;
import clb.core.commission.dto.CmnChannelCommission;
import clb.core.commission.dto.CmnChannelRatioDetail;
import clb.core.commission.dto.CmnSupplierCommission;
import clb.core.commission.service.ICmnChannelCommissionService;
import clb.core.commission.service.ICmnSupplierCommissionService;
import clb.core.common.utils.CommonUtil;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @jiaolong.li@hand-china.com
 * @create 2017-08-13 21:37
 **/
public class CmnChannelCommissionThread implements Runnable, Serializable {
    private static final long serialVersionUID = 4742696517887241792L;

    private ICmnChannelCommissionService channelCommissionService;

    private ICmnSupplierCommissionService supplierCommissionService;

    private ICnlLevelService levelService;

    List<CmnSupplierCommission> speCmnList = new ArrayList<CmnSupplierCommission>();

    public CmnChannelCommissionThread(List<CmnSupplierCommission> speCmnList,
                               ICmnChannelCommissionService channelCommissionService,
                               ICmnSupplierCommissionService supplierCommissionService,
                               ICnlLevelService levelService){
        this.speCmnList = speCmnList;
        this.channelCommissionService = channelCommissionService;
        this.supplierCommissionService = supplierCommissionService;
        this.levelService = levelService;
    }

    @Override
    public void run() {

        if (speCmnList.size() > 0) {
            for (CmnSupplierCommission speCommission : speCmnList) {

                if (!"YT".equals(speCommission.getChannelTypeCode() )
                        &&  !"GYS".equals(speCommission.getChannelTypeCode()) ) {
                    // 处理当前信息
                    processContract(speCommission, null, 1L);
                }
            }
        }

    }


    /**
     * 处理当前信息
     * @param speCommission
     * @param cnlChannel 当前渠道
     */
    public void processContract(CmnSupplierCommission speCommission,
                                CnlChannel cnlChannel,
                                Long levelNumber) {

        IRequest iRequest = RequestHelper.newEmptyRequest();

        // 上级为渠道
        if (cnlChannel != null) {

            // 查询当前渠道是否在渠道合约信息中为合约主体
            CnlChannelContract cnlContract = new CnlChannelContract();
            cnlContract.setPartyType("CHANNEL");
            cnlContract.setPartyId(cnlChannel.getChannelId());
            //cnlContract.setProductDivision(speCommission.getBigClass());
            List<CnlChannelContract> cnlContractList = new ArrayList<CnlChannelContract>();
            cnlContractList = channelCommissionService.queryContractList(cnlContract);


            if (cnlContractList.size() == 0) {
                return;  // 退出循环
            } else {

                // 查找该产品与当前渠道对应的渠道佣金表数据
                List<CmnChannelCommission> cnlCommissionList = new ArrayList<CmnChannelCommission>();
                CmnChannelCommission channelCommission = new CmnChannelCommission();
                channelCommission.setChannelId(cnlChannel.getChannelId());
                channelCommission.setItemId(speCommission.getItemId());
                channelCommission.setChannelTypeCode(speCommission.getChannelTypeCode());
                channelCommission.setSupplierId(speCommission.getSupplierId());
                channelCommission.setContributionPeriod(speCommission.getContributionPeriod());
                channelCommission.setCurrency(speCommission.getCurrency());
                channelCommission.setPayMethod(speCommission.getPayMethod());
                channelCommission.setPolicyholdersMinAge(speCommission.getPolicyholdersMinAge());
                channelCommission.setPolicyholdersMaxAge(speCommission.getPolicyholdersMaxAge());
                channelCommission.setRelationLevelNumber(levelNumber);
                channelCommission.setCalculateFlag("N");
                cnlCommissionList = channelCommissionService.selectAllFields(channelCommission);

                for (CnlChannelContract contract : cnlContractList) {

                    // 已存在的渠道佣金与（合约与费率匹配的结果）做比较
                    for (CmnChannelCommission existCmn : cnlCommissionList) {

                        List<CmnChannelCommission> timeCommissionList = new ArrayList<CmnChannelCommission>();

                        // 自定义费率级别
                        if ("Y".equals(contract.getDefineRateFlag())) {
                            // 合约下的费率
                            List<CnlContractRate> rateList = new ArrayList<CnlContractRate>();
                            CnlContractRate rate = new CnlContractRate();
                            rate.setChannelContractId(contract.getChannelContractId());
                            // 特殊条件
                            rate.setBigClass(existCmn.getBigClass());
                            rate.setMidClass(existCmn.getMidClass());
                            rate.setMinClass(existCmn.getMinClass());
                            rate.setItemId(existCmn.getItemId());
                            rate.setSublineItemName(existCmn.getContributionPeriod());
                            if (channelCommissionService.queryTopRateList(rate) != null) {
                                rateList = channelCommissionService.queryTopRateList(rate);
                            }

                            for (CnlContractRate contractRate : rateList) {
                                // 匹配有效时间
                                CmnChannelCommission timeCommission = new CmnChannelCommission();
                                //timeCommission.setSupplierId(channelContract.getPartyId());
                                timeCommission.setChannelId(contract.getChannelId());
                                timeCommission.setChannelContractId(contract.getChannelContractId());
                                timeCommission.setRelationLevelNumber(levelNumber);
                                timeCommission.setChannelRateId(contractRate.getChannelRateId());
                                timeCommission.setChannelProportion1(contractRate.getRate1());
                                timeCommission.setChannelProportion2(contractRate.getRate2());
                                timeCommission.setChannelProportion3(contractRate.getRate3());
                                timeCommission.setChannelProportion4(contractRate.getRate4());
                                timeCommission.setChannelProportion5(contractRate.getRate5());
                                timeCommission.setChannelProportion6(contractRate.getRate6());
                                timeCommission.setChannelProportion7(contractRate.getRate7());
                                timeCommission.setChannelProportion8(contractRate.getRate8());
                                timeCommission.setChannelProportion9(contractRate.getRate9());
                                timeCommission.setChannelProportion10(contractRate.getRate10());
                                timeCommission.setBigClass(contract.getProductDivision());

                                timeCommission.setEffectiveStartDate(contract.getStartDate());
                                timeCommission.setEffectiveEndDate(contract.getEndDate());
                                timeCommissionList.add(timeCommission);
                            }

                        } else {

                            List<CmnChannelRatioDetail> ratioDetailList = new ArrayList<CmnChannelRatioDetail>();
                            // 渠道佣金分成级别
                            CmnChannelRatioDetail paraRatioDetail = new CmnChannelRatioDetail();
                            paraRatioDetail.setRatioId(contract.getRateLevelId());
                            paraRatioDetail.setBigClass(existCmn.getBigClass());
                            paraRatioDetail.setMidClass(existCmn.getMidClass());
                            paraRatioDetail.setMinClass(existCmn.getMinClass());
                            paraRatioDetail.setItemId(existCmn.getItemId());
                            paraRatioDetail.setSublineItemName(existCmn.getContributionPeriod());

                            ratioDetailList = channelCommissionService.queryTopRatioDetailList(paraRatioDetail);
                            if (ratioDetailList != null && ratioDetailList.size() > 0) {
                                for (CmnChannelRatioDetail ratioDetail : ratioDetailList) {

                                    CmnChannelCommission timeCommission = new CmnChannelCommission();
                                    //timeCommission.setSupplierId(channelContract.getPartyId());
                                    timeCommission.setChannelId(contract.getChannelId());
                                    timeCommission.setChannelContractId(contract.getChannelContractId());
                                    timeCommission.setRelationLevelNumber(levelNumber);
                                    //timeCommission.setChannelRateId(ratioDetail.getRatioLineId());
                                    timeCommission.setChannelProportion1(ratioDetail.getTheFirstYear());
                                    timeCommission.setChannelProportion2(ratioDetail.getTheSecondYear());
                                    timeCommission.setChannelProportion3(ratioDetail.getTheThirdYear());
                                    timeCommission.setChannelProportion4(ratioDetail.getTheFourthYear());
                                    timeCommission.setChannelProportion5(ratioDetail.getTheFifthYear());
                                    timeCommission.setChannelProportion6(ratioDetail.getTheSixthYear());
                                    timeCommission.setChannelProportion7(ratioDetail.getTheSeventhYear());
                                    timeCommission.setChannelProportion8(ratioDetail.getTheEightYear());
                                    timeCommission.setChannelProportion9(ratioDetail.getTheNinthYear());
                                    timeCommission.setChannelProportion10(ratioDetail.getTheTenthYear());
                                    timeCommission.setBigClass(contract.getProductDivision());

                                    timeCommission.setEffectiveStartDate(contract.getStartDate());
                                    timeCommission.setEffectiveEndDate(contract.getEndDate());
                                    timeCommissionList.add(timeCommission);
                                }

                            }
                        }

                        for (CmnChannelCommission timeCommission : timeCommissionList) {

                            // 匹配有效时间
                            CmnChannelCommission resultCommission = new CmnChannelCommission();
                            resultCommission.setChannelId(timeCommission.getChannelId());
                            resultCommission.setChannelContractId(timeCommission.getChannelContractId());
                            resultCommission.setChannelRateId(timeCommission.getChannelRateId());
                            resultCommission.setChannelTypeCode(existCmn.getChannelTypeCode());
                            resultCommission.setItemId(existCmn.getItemId());
                            resultCommission.setContributionPeriod(existCmn.getContributionPeriod());
                            resultCommission.setCurrency(existCmn.getCurrency());
                            resultCommission.setPayMethod(existCmn.getPayMethod());
                            resultCommission.setPolicyholdersMinAge(existCmn.getPolicyholdersMinAge());
                            resultCommission.setPolicyholdersMaxAge(existCmn.getPolicyholdersMaxAge());
                            resultCommission.setPriceType(existCmn.getPriceType());
                            resultCommission.setRelationLevelNumber(levelNumber);
                            resultCommission.setSupplierId(existCmn.getSupplierId());
                            resultCommission.setParentLineId(existCmn.getLineId());

                            long timeBegin = timeCommission.getEffectiveStartDate().getTime();
                            long timeEnd = timeCommission.getEffectiveEndDate().getTime();
                            long existBegin = existCmn.getEffectiveStartDate().getTime();
                            long existEnd = existCmn.getEffectiveEndDate().getTime();

                            if ((existBegin >= timeBegin && existBegin <= timeEnd)
                                    || (existEnd >= timeBegin && existEnd <= timeEnd)) {
                                // 一定有重叠部分
                                long resultBegin = existBegin >= timeBegin ? existBegin : timeBegin;
                                long resultEnd = timeEnd >= existEnd ? existEnd : timeEnd;

                                Date resultBeginDate = new Date(resultBegin);
                                Date resultEndDate = new Date(resultEnd);

                                resultCommission.setEffectiveStartDate(resultBeginDate);
                                resultCommission.setEffectiveEndDate(resultEndDate);
                            } else if (existBegin < timeBegin && existEnd > timeEnd) {
                                Date resultBeginDate = new Date(timeBegin);
                                Date resultEndDate = new Date(timeEnd);

                                resultCommission.setEffectiveStartDate(resultBeginDate);
                                resultCommission.setEffectiveEndDate(resultEndDate);
                            }

                            if (resultCommission.getEffectiveStartDate() != null
                                    && resultCommission.getEffectiveEndDate() != null) {
                                BigDecimal finalData;

                                finalData = existCmn.getTheFirstYear().multiply(CommonUtil.getZeroIfNull(
                                        timeCommission.getChannelProportion1()));
                                resultCommission.setTheFirstYear(finalData);

                                finalData = existCmn.getTheSecondYear().multiply(CommonUtil.getZeroIfNull(
                                        timeCommission.getChannelProportion2()));
                                resultCommission.setTheSecondYear(finalData);

                                finalData = existCmn.getTheThirdYear().multiply(CommonUtil.getZeroIfNull(
                                        timeCommission.getChannelProportion3()));
                                resultCommission.setTheThirdYear(finalData);

                                finalData = existCmn.getTheFourthYear().multiply(CommonUtil.getZeroIfNull(
                                        timeCommission.getChannelProportion4()));
                                resultCommission.setTheFourthYear(finalData);

                                finalData = existCmn.getTheFifthYear().multiply(CommonUtil.getZeroIfNull(
                                        timeCommission.getChannelProportion5()));
                                resultCommission.setTheFifthYear(finalData);

                                finalData = existCmn.getTheSixthYear().multiply(CommonUtil.getZeroIfNull(
                                        timeCommission.getChannelProportion6()));
                                resultCommission.setTheSixthYear(finalData);

                                finalData = existCmn.getTheSeventhYear().multiply(CommonUtil.getZeroIfNull(
                                        timeCommission.getChannelProportion7()));
                                resultCommission.setTheSeventhYear(finalData);

                                finalData = existCmn.getTheEightYear().multiply(CommonUtil.getZeroIfNull(
                                        timeCommission.getChannelProportion8()));
                                resultCommission.setTheEightYear(finalData);

                                finalData = existCmn.getTheNinthYear().multiply(CommonUtil.getZeroIfNull(
                                        timeCommission.getChannelProportion9()));
                                resultCommission.setTheNinthYear(finalData);

                                finalData = existCmn.getTheTenthYear().multiply(CommonUtil.getZeroIfNull(
                                        timeCommission.getChannelProportion10()));
                                resultCommission.setTheTenthYear(finalData);

                                // who字段
                                resultCommission.setCalculateFlag("N");
                                resultCommission.setObjectVersionNumber(1L);
                                resultCommission.setVersion("1");

                                channelCommissionService.insertSelective(iRequest, resultCommission);
                            }
                        }

                        // 计算过的数据打上标记
                        existCmn.setCalculateFlag("Y");
                        channelCommissionService.updateByPrimaryKeySelective(iRequest, existCmn);

                    }

                    // 处理下一级
                    //levelNumber = levelNumber + 1;
                    CnlChannel nextChannel = new CnlChannel();
                    nextChannel.setChannelId(contract.getChannelId());
                    processContract(speCommission, nextChannel, levelNumber + 1);
                }
            }

        } else {

            // 上级渠道为空，代表是第一层, 即跟供应商签约
            CnlChannelContract cnlContract = new CnlChannelContract();
            cnlContract.setPartyType("SUPPLIER");
            cnlContract.setPartyId(speCommission.getSupplierId());
            cnlContract.setProductDivision(speCommission.getBigClass());
            cnlContract.setChannelTypeCode(speCommission.getChannelTypeCode());
            List<CnlChannelContract> cnlContractList = new ArrayList<CnlChannelContract>();
            cnlContractList = channelCommissionService.queryContractList(cnlContract);

            // 循环合约信息
            for (CnlChannelContract contract : cnlContractList) {

                levelNumber = 1L;     // 初始化

                // 1、合约与供应商佣金表时间做比较
                CmnChannelCommission channelCommission = new CmnChannelCommission();
                channelCommission.setSupplierId(contract.getPartyId());
                channelCommission.setChannelId(contract.getChannelId());
                //channelCommission.setChannelLevelId(contract.getRateLevelId());
                channelCommission.setChannelLevelName(contract.getRateLevelName());
                channelCommission.setSupplierCommissionId(speCommission.getLineId());
                channelCommission.setChannelContractId(contract.getChannelContractId());
                channelCommission.setRelationLevelNumber(levelNumber);

                long contractBegin = contract.getStartDate().getTime();
                long contractEnd = contract.getEndDate().getTime();
                long sCommissionBegin = speCommission.getEffectiveStartDate().getTime();
                long sCommissionEnd = speCommission.getEffectiveEndDate().getTime();

                if ((sCommissionBegin >= contractBegin && sCommissionBegin <= contractEnd)
                        || (sCommissionEnd >= contractBegin && sCommissionEnd <= contractEnd)) {
                    // 一定有重叠部分
                    long resultBegin = sCommissionBegin >= contractBegin ? sCommissionBegin : contractBegin;
                    long resultEnd = contractEnd >= sCommissionEnd ? sCommissionEnd : contractEnd;

                    Date resultBeginDate = new Date(resultBegin);
                    Date resultEndDate = new Date(resultEnd);

                    channelCommission.setEffectiveStartDate(resultBeginDate);
                    channelCommission.setEffectiveEndDate(resultEndDate);
                } else if (sCommissionBegin < contractBegin && sCommissionEnd > contractEnd) {
                    Date resultBeginDate = new Date(contractBegin);
                    Date resultEndDate = new Date(contractEnd);

                    channelCommission.setEffectiveStartDate(resultBeginDate);
                    channelCommission.setEffectiveEndDate(resultEndDate);
                }

                // 2、对比结果跟费率级别做对比
                /*List<CnlContractRate> rateList = new ArrayList<CnlContractRate>();
                CnlContractRate rate = new CnlContractRate();
                rate.setChannelContractId(channelCommission.getChannelContractId());
                if (channelCommissionService.queryContractRateList(rate) != null) {
                    rateList = channelCommissionService.queryContractRateList(rate);
                }*/
                List<CmnChannelCommission> resultCommissionList = new ArrayList<CmnChannelCommission>();
                List<CmnChannelCommission> resultEndCommissionList = new ArrayList<CmnChannelCommission>();
                if (channelCommission.getEffectiveStartDate() != null
                        && channelCommission.getEffectiveEndDate() != null) {
                    //for (CnlContractRate contractRate : rateList) {
                        CmnChannelCommission commission = new CmnChannelCommission();
                        commission.setAtt(channelCommission);
                        //commission.setChannelLevelName(contractRate.getLevelName());
                        //commission.setChannelLevelId(channelCommission.getChannelLevelId());
                        commission.setChannelLevelName(contract.getRateLevelName());
                        commission.setRelationLevelNumber(channelCommission.getRelationLevelNumber());
                        commission.setEffectiveStartDate(channelCommission.getEffectiveStartDate());
                        commission.setEffectiveEndDate(channelCommission.getEffectiveEndDate());
                        resultCommissionList.add(commission);
                    //}

                    // 3、对比结果跟等级表做对比
                    if (resultCommissionList.size() > 0) {
                        for (CmnChannelCommission lCommission: resultCommissionList) {
                            List<CnlLevel> levelList = new ArrayList<CnlLevel>();
                            CnlLevel level = new CnlLevel();
                            //level.setId(lCommission.getChannelLevelId());
                            level.setLevelName(contract.getRateLevelName());    //  直接从渠道合约上取渠道等级名称
                            level.setSupplierId(channelCommission.getSupplierId());
                            level.setChannelClassCode(contract.getChannelTypeCode());
                            if (channelCommissionService.queryLevelList(level) != null) {
                                levelList = channelCommissionService.queryLevelList(level);
                            }

                            for (CnlLevel cnlLevel : levelList) {
                                CmnChannelCommission levelCommission = new CmnChannelCommission();
                                levelCommission.setAtt(lCommission);
                                levelCommission.setChannelLevelId(cnlLevel.getId());

                                long lCommissionBegin = levelCommission.getEffectiveStartDate().getTime();
                                long lCommissionEnd = levelCommission.getEffectiveEndDate().getTime();
                                long leveBegin = cnlLevel.getEffectiveStartDate().getTime();
                                long leveEnd = cnlLevel.getEffectiveEndDate().getTime();

                                if ((leveBegin >= lCommissionBegin && leveBegin <= lCommissionEnd)
                                        || (leveEnd >= lCommissionBegin && leveEnd <= lCommissionEnd)) {
                                    // 一定有重叠部分
                                    long resultBegin = leveBegin >= lCommissionBegin ? leveBegin : lCommissionBegin;
                                    long resultEnd = lCommissionEnd >= leveEnd ? leveEnd : lCommissionEnd;

                                    Date resultBeginDate = new Date(resultBegin);
                                    Date resultEndDate = new Date(resultEnd);

                                    levelCommission.setEffectiveStartDate(resultBeginDate);
                                    levelCommission.setEffectiveEndDate(resultEndDate);
                                    resultEndCommissionList.add(levelCommission);
                                } else if (leveBegin < lCommissionBegin && leveEnd > lCommissionEnd) {

                                    Date resultBeginDate = new Date(lCommissionBegin);
                                    Date resultEndDate = new Date(lCommissionEnd);

                                    levelCommission.setEffectiveStartDate(resultBeginDate);
                                    levelCommission.setEffectiveEndDate(resultEndDate);
                                    resultEndCommissionList.add(levelCommission);
                                }

                            }
                        }
                    }

                }

                // 计算佣金值,存入数据库中
                calculateTopCommission(resultEndCommissionList);

                // 处理下一级
                if (resultCommissionList.size() > 0) {
                    //levelNumber = levelNumber + 1;
                    CnlChannel nextChannel = new CnlChannel();
                    nextChannel.setChannelId(contract.getChannelId());
                    processContract(speCommission, nextChannel, levelNumber + 1);
                }
            }

        }


    }

    /**
     *  计算佣金值
     * @param resultCommissionList
     */
    public void calculateTopCommission(List<CmnChannelCommission> resultCommissionList) {

        IRequest iRequest = RequestHelper.newEmptyRequest();

        for (CmnChannelCommission channelCommission : resultCommissionList) {
            BigDecimal finalData;

            CmnSupplierCommission supplierCommission = new CmnSupplierCommission();
            supplierCommission.setLineId(channelCommission.getSupplierCommissionId());
            supplierCommission = supplierCommissionService.selectByPrimaryKey(iRequest, supplierCommission);

            CnlLevel level = new CnlLevel();
            level.setId(channelCommission.getChannelLevelId());
            level = levelService.selectByPrimaryKey(iRequest, level);

            channelCommission.setLineId(null);
            channelCommission.setChannelTypeCode(supplierCommission.getChannelTypeCode());
            channelCommission.setItemId(supplierCommission.getItemId());
            channelCommission.setContributionPeriod(supplierCommission.getContributionPeriod());
            channelCommission.setCurrency(supplierCommission.getCurrency());
            channelCommission.setPayMethod(supplierCommission.getPayMethod());
            channelCommission.setPolicyholdersMinAge(supplierCommission.getPolicyholdersMinAge());
            channelCommission.setPolicyholdersMaxAge(supplierCommission.getPolicyholdersMaxAge());
            channelCommission.setPriceType(supplierCommission.getPriceType());

            finalData = supplierCommission.getTheFirstYear().multiply(level.getTheFirstYear());
            channelCommission.setTheFirstYear(finalData);

            finalData = supplierCommission.getTheSecondYear().multiply(level.getTheSecondYear());
            channelCommission.setTheSecondYear(finalData);

            finalData = supplierCommission.getTheThirdYear().multiply(level.getTheThirdTear());
            channelCommission.setTheThirdYear(finalData);

            finalData = supplierCommission.getTheFourthYear().multiply(level.getTheForthYear());
            channelCommission.setTheFourthYear(finalData);

            finalData = supplierCommission.getTheFifthYear().multiply(level.getTheFifthYear());
            channelCommission.setTheFifthYear(finalData);

            finalData = supplierCommission.getTheSixthYear().multiply(level.getTheSixthYear());
            channelCommission.setTheSixthYear(finalData);

            finalData = supplierCommission.getTheSeventhYear().multiply(level.getTheSeventhYear());
            channelCommission.setTheSeventhYear(finalData);

            finalData = supplierCommission.getTheEightYear().multiply(level.getTheEightYear());
            channelCommission.setTheEightYear(finalData);

            finalData = supplierCommission.getTheNinthYear().multiply(level.getTheNinthYear());
            channelCommission.setTheNinthYear(finalData);

            finalData = supplierCommission.getTheTenthYear().multiply(level.getTheTenthYear());
            channelCommission.setTheTenthYear(finalData);

            // who字段
            channelCommission.setCalculateFlag("N");
            channelCommission.setObjectVersionNumber(1L);
            channelCommission.setVersion("1");

            channelCommissionService.insertSelective(iRequest, channelCommission);
        }

    }

}
