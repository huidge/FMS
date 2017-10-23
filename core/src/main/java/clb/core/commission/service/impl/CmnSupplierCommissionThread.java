package clb.core.commission.service.impl;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.dto.CnlContractRate;
import clb.core.channel.dto.CnlLevel;
import clb.core.channel.service.ICnlLevelService;
import clb.core.commission.dto.*;
import clb.core.commission.service.*;
import clb.core.common.service.ISequenceService;
import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.DateUtil;
import clb.core.common.utils.StringUtil;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @jiaolong.li@hand-china.com
 * @create 2017-09-01 21:37
 **/
public class CmnSupplierCommissionThread implements Runnable, Serializable {

    private ICmnBasicService basicService;

    private ICmnOverrideService overrideService;

    private ICmnExtrasService extrasService;

    private ICmnSupplierCommissionService supplierCommissionService;

    private ICmnSupplierRouteService supplierRouteService;

    private ISequenceService sequenceService;

    private List<CmnBasic> basicList = new ArrayList<CmnBasic>();

    private IRequest iRequest;

    public CmnSupplierCommissionThread(List<CmnBasic> basicList,
                                       ICmnBasicService basicService,
                                       ICmnOverrideService overrideService,
                                       ICmnExtrasService extrasService,
                                       ICmnSupplierCommissionService supplierCommissionService,
                                       ICmnSupplierRouteService supplierRouteService,
                                       ISequenceService sequenceService){
        this.basicList = basicList;
        this.basicService = basicService;
        this.overrideService = overrideService;
        this.extrasService = extrasService;
        this.supplierCommissionService = supplierCommissionService;
        this.supplierRouteService = supplierRouteService;
        this.sequenceService = sequenceService;
    }

    @Override
    public void run() {
        iRequest = RequestHelper.newEmptyRequest();

        // 循环Basic记录
        for (CmnBasic cmnBasic : this.basicList) {

            // 设置Key关键字
            CmnSpeCmnKeyField basicKey = new CmnSpeCmnKeyField();
            basicKey.setItemId(cmnBasic.getItemId());
            basicKey.setPriceType(cmnBasic.getPriceType());
            basicKey.setPayMethod(cmnBasic.getPayMethod());
            basicKey.setCurrency(cmnBasic.getCurrency());
            basicKey.setContributionPeriod(cmnBasic.getContributionPeriod());
            basicKey.setSupplierId(cmnBasic.getSupplierId());

            // 计算佣金值
            processSupplierCommission(1L, basicKey, cmnBasic, "");

            // 交易路线计算
            processRoute(cmnBasic.getBasicId());
        }

    }

    // 计算佣金最终值
    public void calculateCommission(List<CmnSpeCmnCalRec> recList,
                                    Long levelNumber,
                                    String commissionNum,
                                    String parentCommissionNum) {

        for (CmnSpeCmnCalRec cmnSpeCmnCalRec : recList) {
            CmnBasic basicData = new CmnBasic();
            CmnOverride overrideData = new CmnOverride();
            CmnExtras extraData = new CmnExtras();
            CmnSupplierCommission csc = new CmnSupplierCommission();
            BigDecimal finalData;

            basicData.setBasicId(cmnSpeCmnCalRec.getBasicId());
            basicData = basicService.queryBasic(basicData).get(0);

            overrideData.setOverrideId(cmnSpeCmnCalRec.getOverrideId());
            overrideData = overrideService.selectByPrimaryKey(iRequest, overrideData);

            if (cmnSpeCmnCalRec.getExtraId() != null) {
                extraData.setExtraId(cmnSpeCmnCalRec.getExtraId());
                extraData = extrasService.selectByPrimaryKey(iRequest, extraData);
            }

            csc.setEffectiveStartDate(cmnSpeCmnCalRec.getEffectiveStartDate());
            csc.setEffectiveEndDate(cmnSpeCmnCalRec.getEffectiveEndDate());

            // 佣金数据
            if ("保诚".equals(basicData.getProductCompany())) {

                if ("YT".equals(overrideData.getChannelTypeCode())) {

                    finalData = basicData.getTheFirstYear().add(CommonUtil.getZeroIfNull(extraData.getExtra1()))
                            .multiply(overrideData.getSuperiorOverride1());
                    csc.setTheFirstYear(finalData);

                    finalData = basicData.getTheSecondYear().add(CommonUtil.getZeroIfNull(extraData.getExtra2()))
                            .multiply(overrideData.getSuperiorOverride2());
                    csc.setTheSecondYear(finalData);

                    finalData = basicData.getTheThirdYear().add(CommonUtil.getZeroIfNull(extraData.getExtra3()))
                            .multiply(overrideData.getSuperiorOverride3());
                    csc.setTheThirdYear(finalData);

                    finalData = basicData.getTheFourthYear().add(CommonUtil.getZeroIfNull(extraData.getExtra4()))
                            .multiply(overrideData.getSuperiorOverride4());
                    csc.setTheFourthYear(finalData);

                    finalData = basicData.getTheFifthYear().add(CommonUtil.getZeroIfNull(extraData.getExtra5()))
                            .multiply(overrideData.getSuperiorOverride5());
                    csc.setTheFifthYear(finalData);

                    finalData = basicData.getTheSixthYear().add(CommonUtil.getZeroIfNull(extraData.getExtra6()))
                            .multiply(overrideData.getSuperiorOverride6());
                    csc.setTheSixthYear(finalData);

                    finalData = basicData.getTheSeventhYear().add(CommonUtil.getZeroIfNull(extraData.getExtra7()))
                            .multiply(overrideData.getSuperiorOverride7());
                    csc.setTheSeventhYear(finalData);

                    finalData = basicData.getTheEightYear().add(CommonUtil.getZeroIfNull(extraData.getExtra8()))
                            .multiply(overrideData.getSuperiorOverride8());
                    csc.setTheEightYear(finalData);

                    finalData = basicData.getTheNinthYear().add(CommonUtil.getZeroIfNull(extraData.getExtra9()))
                            .multiply(overrideData.getSuperiorOverride9());
                    csc.setTheNinthYear(finalData);

                    finalData = basicData.getTheTenthYear().add(CommonUtil.getZeroIfNull(extraData.getExtra10()))
                            .multiply(overrideData.getSuperiorOverride10());
                    csc.setTheTenthYear(finalData);

                } else {

                    finalData = basicData.getTheFirstYear().add(CommonUtil.getZeroIfNull(extraData.getExtra1()))
                            .multiply(overrideData.getOverride1()).multiply(overrideData.getAdjustment1());
                    csc.setTheFirstYear(finalData);

                    finalData = basicData.getTheSecondYear().add(CommonUtil.getZeroIfNull(extraData.getExtra2()))
                            .multiply(overrideData.getOverride2()).multiply(overrideData.getAdjustment2());
                    csc.setTheSecondYear(finalData);

                    finalData = basicData.getTheThirdYear().add(CommonUtil.getZeroIfNull(extraData.getExtra3()))
                            .multiply(overrideData.getOverride3()).multiply(overrideData.getAdjustment3());
                    csc.setTheThirdYear(finalData);

                    finalData = basicData.getTheFourthYear().add(CommonUtil.getZeroIfNull(extraData.getExtra4()))
                            .multiply(overrideData.getOverride4()).multiply(overrideData.getAdjustment4());
                    csc.setTheFourthYear(finalData);

                    finalData = basicData.getTheFifthYear().add(CommonUtil.getZeroIfNull(extraData.getExtra5()))
                            .multiply(overrideData.getOverride5()).multiply(overrideData.getAdjustment5());
                    csc.setTheFifthYear(finalData);

                    finalData = basicData.getTheSixthYear().add(CommonUtil.getZeroIfNull(extraData.getExtra6()))
                            .multiply(overrideData.getOverride6()).multiply(overrideData.getAdjustment6());
                    csc.setTheSixthYear(finalData);

                    finalData = basicData.getTheSeventhYear().add(CommonUtil.getZeroIfNull(extraData.getExtra7()))
                            .multiply(overrideData.getOverride7()).multiply(overrideData.getAdjustment7());
                    csc.setTheSeventhYear(finalData);

                    finalData = basicData.getTheEightYear().add(CommonUtil.getZeroIfNull(extraData.getExtra8()))
                            .multiply(overrideData.getOverride8()).multiply(overrideData.getAdjustment8());
                    csc.setTheEightYear(finalData);

                    finalData = basicData.getTheNinthYear().add(CommonUtil.getZeroIfNull(extraData.getExtra9()))
                            .multiply(overrideData.getOverride9()).multiply(overrideData.getAdjustment9());
                    csc.setTheNinthYear(finalData);

                    finalData = basicData.getTheTenthYear().add(CommonUtil.getZeroIfNull(extraData.getExtra10()))
                            .multiply(overrideData.getOverride10()).multiply(overrideData.getAdjustment10());
                    csc.setTheTenthYear(finalData);
                }
            } else {

                if ("YT".equals(overrideData.getChannelTypeCode())) {
                    finalData = basicData.getTheFirstYear().multiply(overrideData.getSuperiorOverride1()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra1()))));
                    csc.setTheFirstYear(finalData);

                    finalData = basicData.getTheSecondYear().multiply(overrideData.getSuperiorOverride2()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra2()))));
                    csc.setTheSecondYear(finalData);

                    finalData = basicData.getTheThirdYear().multiply(overrideData.getSuperiorOverride3()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra3()))));
                    csc.setTheThirdYear(finalData);

                    finalData = basicData.getTheFourthYear().multiply(overrideData.getSuperiorOverride4()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra4()))));
                    csc.setTheFourthYear(finalData);

                    finalData = basicData.getTheFifthYear().multiply(overrideData.getSuperiorOverride5()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra5()))));
                    csc.setTheFifthYear(finalData);

                    finalData = basicData.getTheSixthYear().multiply(overrideData.getSuperiorOverride6()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra6()))));
                    csc.setTheSixthYear(finalData);

                    finalData = basicData.getTheSeventhYear().multiply(overrideData.getSuperiorOverride7()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra7()))));
                    csc.setTheSeventhYear(finalData);

                    finalData = basicData.getTheEightYear().multiply(overrideData.getSuperiorOverride8()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra8()))));
                    csc.setTheEightYear(finalData);

                    finalData = basicData.getTheNinthYear().multiply(overrideData.getSuperiorOverride9()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra9()))));
                    csc.setTheNinthYear(finalData);

                    finalData = basicData.getTheTenthYear().multiply(overrideData.getSuperiorOverride10()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra10()))));
                    csc.setTheTenthYear(finalData);


                } else {
                    finalData = basicData.getTheFirstYear().multiply(overrideData.getOverride1()).multiply(overrideData.getAdjustment1()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra1()))));
                    csc.setTheFirstYear(finalData);

                    finalData = basicData.getTheSecondYear().multiply(overrideData.getOverride2()).multiply(overrideData.getAdjustment2()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra2()))));
                    csc.setTheSecondYear(finalData);

                    finalData = basicData.getTheThirdYear().multiply(overrideData.getOverride3()).multiply(overrideData.getAdjustment3()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra3()))));
                    csc.setTheThirdYear(finalData);

                    finalData = basicData.getTheFourthYear().multiply(overrideData.getOverride4()).multiply(overrideData.getAdjustment4()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra4()))));
                    csc.setTheFourthYear(finalData);

                    finalData = basicData.getTheFifthYear().multiply(overrideData.getOverride5()).multiply(overrideData.getAdjustment5()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra5()))));
                    csc.setTheFifthYear(finalData);

                    finalData = basicData.getTheSixthYear().multiply(overrideData.getOverride6()).multiply(overrideData.getAdjustment6()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra6()))));
                    csc.setTheSixthYear(finalData);

                    finalData = basicData.getTheSeventhYear().multiply(overrideData.getOverride7()).multiply(overrideData.getAdjustment7()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra7()))));
                    csc.setTheSeventhYear(finalData);

                    finalData = basicData.getTheEightYear().multiply(overrideData.getOverride8()).multiply(overrideData.getAdjustment8()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra8()))));
                    csc.setTheEightYear(finalData);

                    finalData = basicData.getTheNinthYear().multiply(overrideData.getOverride9()).multiply(overrideData.getAdjustment9()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra9()))));
                    csc.setTheNinthYear(finalData);

                    finalData = basicData.getTheTenthYear().multiply(overrideData.getOverride10()).multiply(overrideData.getAdjustment10()).multiply(
                            (new BigDecimal("1").add(CommonUtil.getZeroIfNull(extraData.getExtra10()))));
                    csc.setTheTenthYear(finalData);
                }
            }
            csc.setVersion("1");

            csc.setPolicyholdersMinAge(cmnSpeCmnCalRec.getPolicyholdersMinAge());
            csc.setPolicyholdersMaxAge(cmnSpeCmnCalRec.getPolicyholdersMaxAge());
            csc.setEffectiveStartDate(cmnSpeCmnCalRec.getEffectiveStartDate());
            csc.setEffectiveEndDate(cmnSpeCmnCalRec.getEffectiveEndDate());

            // who字段
            csc.setObjectVersionNumber(1L);

            csc.setBasicId(cmnSpeCmnCalRec.getBasicId());
            csc.setOverrideId(cmnSpeCmnCalRec.getOverrideId());
            csc.setExtraId(cmnSpeCmnCalRec.getExtraId());

            // 必输字段
            csc.setPriceType(basicData.getPriceType());
            csc.setSupplierId(overrideData.getSupplierId());
            csc.setSuperiorSupplierId(overrideData.getSuperiorSupplierId());
            csc.setCommissionNum(commissionNum);
            csc.setParentCommissionNum(parentCommissionNum);

            csc.setChannelTypeCode(overrideData.getChannelTypeCode());
            csc.setItemId(basicData.getItemId());
            csc.setContributionPeriod(basicData.getContributionPeriod());
            csc.setCurrency(basicData.getCurrency());
            csc.setPayMethod(basicData.getPayMethod());
            csc.setLevelNumber(levelNumber);

            // 插入记录表
            supplierCommissionService.insertSelective(iRequest, csc);
        }
    }

    // 结果集与Extra匹配年龄
    public List<CmnSpeCmnCalRec> matchAge(List<CmnSpeCmnCalRec> recList, CmnExtras cmnExtras) {

        List<CmnSpeCmnCalRec> resultRecList = new ArrayList<CmnSpeCmnCalRec>();

        if (recList.size() > 0) {
            for (CmnSpeCmnCalRec cmnSpeCmnCalRec : recList) {

                CmnSpeCmnCalRec rec = new CmnSpeCmnCalRec();
                rec.setBasicId(cmnSpeCmnCalRec.getBasicId());
                rec.setOverrideId(cmnSpeCmnCalRec.getOverrideId());
                rec.setEffectiveStartDate(cmnSpeCmnCalRec.getEffectiveStartDate());
                rec.setEffectiveEndDate(cmnSpeCmnCalRec.getEffectiveEndDate());

                // 年龄情况1
                //   rec           -------------
                //   Extra  -------------
                if (cmnExtras.getPolicyholdersMaxAge() > cmnSpeCmnCalRec.getPolicyholdersMinAge()
                        && cmnExtras.getPolicyholdersMinAge() < cmnSpeCmnCalRec.getPolicyholdersMinAge()
                        && cmnExtras.getPolicyholdersMaxAge() < cmnSpeCmnCalRec.getPolicyholdersMaxAge()
                        ) {

                    CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                    rec1.setAtt(rec);
                    rec1.setPolicyholdersMinAge(cmnSpeCmnCalRec.getPolicyholdersMinAge());
                    rec1.setPolicyholdersMaxAge(cmnExtras.getPolicyholdersMaxAge());
                    rec1.setExtraId(cmnExtras.getExtraId());
                    resultRecList.add(rec1);

                    CmnSpeCmnCalRec rec3 = new CmnSpeCmnCalRec();
                    rec3.setAtt(rec);
                    rec3.setPolicyholdersMinAge(cmnExtras.getPolicyholdersMaxAge() + 1);
                    rec3.setPolicyholdersMaxAge(cmnSpeCmnCalRec.getPolicyholdersMaxAge());
                    rec3.setExtraId(null);
                    resultRecList.add(rec3);
                }

                // 年龄情况2
                //   rec           -------------
                //   Extra  --------------------
                if (cmnExtras.getPolicyholdersMaxAge() > cmnSpeCmnCalRec.getPolicyholdersMinAge()
                        && cmnExtras.getPolicyholdersMinAge() < cmnSpeCmnCalRec.getPolicyholdersMinAge()
                        && cmnExtras.getPolicyholdersMaxAge() == cmnSpeCmnCalRec.getPolicyholdersMaxAge()
                        ) {

                    CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                    rec1.setAtt(rec);
                    rec1.setPolicyholdersMinAge(cmnSpeCmnCalRec.getPolicyholdersMinAge());
                    rec1.setPolicyholdersMaxAge(cmnSpeCmnCalRec.getPolicyholdersMaxAge());
                    rec1.setExtraId(cmnExtras.getExtraId());
                    resultRecList.add(rec1);

                }

                // 年龄情况3
                //   Rec           -------------
                //   Extra               -------------
                if (cmnExtras.getPolicyholdersMinAge() > cmnSpeCmnCalRec.getPolicyholdersMinAge()
                        && cmnExtras.getPolicyholdersMinAge() < cmnSpeCmnCalRec.getPolicyholdersMaxAge()
                        && cmnExtras.getPolicyholdersMaxAge() > cmnSpeCmnCalRec.getPolicyholdersMaxAge()) {

                    CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                    rec1.setAtt(rec);
                    rec1.setPolicyholdersMinAge(cmnExtras.getPolicyholdersMinAge());
                    rec1.setPolicyholdersMaxAge(cmnSpeCmnCalRec.getPolicyholdersMaxAge());
                    rec1.setExtraId(cmnExtras.getExtraId());
                    resultRecList.add(rec1);

                    CmnSpeCmnCalRec rec2 = new CmnSpeCmnCalRec();
                    rec2.setAtt(rec);
                    rec2.setPolicyholdersMinAge(cmnSpeCmnCalRec.getPolicyholdersMinAge());
                    rec2.setPolicyholdersMaxAge(cmnExtras.getPolicyholdersMinAge() - 1);
                    rec2.setExtraId(null);
                    resultRecList.add(rec2);
                }

                // 年龄情况4
                //   Rec           -------------
                //   Extra         ------------------------
                if (cmnExtras.getPolicyholdersMinAge() == cmnSpeCmnCalRec.getPolicyholdersMinAge()
                        && cmnExtras.getPolicyholdersMinAge() < cmnSpeCmnCalRec.getPolicyholdersMaxAge()
                        && cmnExtras.getPolicyholdersMaxAge() > cmnSpeCmnCalRec.getPolicyholdersMaxAge()) {

                    CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                    rec1.setAtt(rec);
                    rec1.setPolicyholdersMinAge(cmnSpeCmnCalRec.getPolicyholdersMinAge());
                    rec1.setPolicyholdersMaxAge(cmnSpeCmnCalRec.getPolicyholdersMaxAge());
                    rec1.setExtraId(cmnExtras.getExtraId());
                    resultRecList.add(rec1);
                }

                // 年龄情况5
                //   Rec           -------------
                //   Extra         ------
                if (cmnExtras.getPolicyholdersMaxAge() < cmnSpeCmnCalRec.getPolicyholdersMaxAge()
                        && cmnExtras.getPolicyholdersMinAge() == cmnSpeCmnCalRec.getPolicyholdersMinAge()) {

                    CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                    rec1.setAtt(rec);
                    rec1.setPolicyholdersMinAge(cmnExtras.getPolicyholdersMinAge());
                    rec1.setPolicyholdersMaxAge(cmnExtras.getPolicyholdersMaxAge());
                    rec1.setExtraId(cmnExtras.getExtraId());
                    resultRecList.add(rec1);

                    CmnSpeCmnCalRec rec2 = new CmnSpeCmnCalRec();
                    rec2.setAtt(rec);
                    rec2.setPolicyholdersMinAge(cmnExtras.getPolicyholdersMaxAge() + 1);
                    rec2.setPolicyholdersMaxAge(cmnSpeCmnCalRec.getPolicyholdersMaxAge());
                    rec2.setExtraId(null);
                    resultRecList.add(rec2);
                }

                // 年龄情况6
                //   Rec         -------------------
                //   Extra             ------
                if (cmnExtras.getPolicyholdersMaxAge() < cmnSpeCmnCalRec.getPolicyholdersMaxAge()
                        && cmnExtras.getPolicyholdersMinAge() > cmnSpeCmnCalRec.getPolicyholdersMinAge()) {

                    CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                    rec1.setAtt(rec);
                    rec1.setPolicyholdersMinAge(cmnExtras.getPolicyholdersMinAge() + 1);
                    rec1.setPolicyholdersMaxAge(cmnExtras.getPolicyholdersMaxAge());
                    rec1.setExtraId(cmnExtras.getExtraId());
                    resultRecList.add(rec1);

                    CmnSpeCmnCalRec rec2 = new CmnSpeCmnCalRec();
                    rec2.setAtt(rec);
                    rec2.setPolicyholdersMinAge(cmnSpeCmnCalRec.getPolicyholdersMinAge());
                    rec2.setPolicyholdersMaxAge(cmnExtras.getPolicyholdersMinAge());
                    rec2.setExtraId(null);
                    resultRecList.add(rec2);

                    CmnSpeCmnCalRec rec3 = new CmnSpeCmnCalRec();
                    rec3.setAtt(rec);
                    rec3.setPolicyholdersMinAge(cmnExtras.getPolicyholdersMaxAge() + 1);
                    rec3.setPolicyholdersMaxAge(cmnSpeCmnCalRec.getPolicyholdersMinAge());
                    rec3.setExtraId(null);
                    resultRecList.add(rec3);
                }

                // 年龄情况7
                //   Rec           -------------
                //   Extra                ------
                if (cmnExtras.getPolicyholdersMaxAge() == cmnSpeCmnCalRec.getPolicyholdersMaxAge()
                        && cmnExtras.getPolicyholdersMinAge() > cmnSpeCmnCalRec.getPolicyholdersMinAge()) {

                    CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                    rec1.setAtt(rec);
                    rec1.setPolicyholdersMinAge(cmnExtras.getPolicyholdersMinAge());
                    rec1.setPolicyholdersMaxAge(cmnExtras.getPolicyholdersMaxAge());
                    rec1.setExtraId(cmnExtras.getExtraId());
                    resultRecList.add(rec1);

                    CmnSpeCmnCalRec rec2 = new CmnSpeCmnCalRec();
                    rec2.setAtt(rec);
                    rec2.setPolicyholdersMinAge(cmnSpeCmnCalRec.getPolicyholdersMinAge());
                    rec2.setPolicyholdersMaxAge(cmnExtras.getPolicyholdersMinAge() - 1);
                    rec2.setExtraId(null);
                    resultRecList.add(rec2);
                }

                // 年龄情况8
                //   Rec           -------------
                //   Extra         -------------
                if (cmnExtras.getPolicyholdersMaxAge() == cmnSpeCmnCalRec.getPolicyholdersMaxAge()
                        && cmnExtras.getPolicyholdersMinAge() == cmnSpeCmnCalRec.getPolicyholdersMinAge()) {

                    CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                    rec1.setAtt(rec);
                    rec1.setPolicyholdersMinAge(cmnExtras.getPolicyholdersMinAge());
                    rec1.setPolicyholdersMaxAge(cmnExtras.getPolicyholdersMaxAge());
                    rec1.setExtraId(cmnExtras.getExtraId());
                    resultRecList.add(rec1);
                }


                // 年龄情况9
                //   Rec           -------------
                //   Extra    ------------------------
                if (cmnExtras.getPolicyholdersMaxAge() > cmnSpeCmnCalRec.getPolicyholdersMaxAge()
                        && cmnExtras.getPolicyholdersMinAge() < cmnSpeCmnCalRec.getPolicyholdersMinAge()) {

                    CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                    rec1.setAtt(rec);
                    rec1.setPolicyholdersMinAge(cmnSpeCmnCalRec.getPolicyholdersMinAge());
                    rec1.setPolicyholdersMaxAge(cmnSpeCmnCalRec.getPolicyholdersMaxAge());
                    rec1.setExtraId(cmnExtras.getExtraId());
                    resultRecList.add(rec1);

                }

                // 年龄情况10
                //   Rec                     -------------
                //   Extra    ---------                           -------------
                if (cmnExtras.getPolicyholdersMaxAge() < cmnSpeCmnCalRec.getPolicyholdersMinAge()
                        || cmnExtras.getPolicyholdersMinAge() > cmnSpeCmnCalRec.getPolicyholdersMaxAge()) {

                    CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                    rec1.setAtt(rec);
                    rec1.setPolicyholdersMinAge(cmnSpeCmnCalRec.getPolicyholdersMinAge());
                    rec1.setPolicyholdersMaxAge(cmnSpeCmnCalRec.getPolicyholdersMaxAge());
                    rec1.setExtraId(cmnSpeCmnCalRec.getExtraId());
                    resultRecList.add(rec1);
                }

            }
        }

        return resultRecList;
    }

    // 结果集与Extra匹配时间
    public List<CmnSpeCmnCalRec> matchDate(List<CmnSpeCmnCalRec> recList, CmnExtras cmnExtras) {

        List<CmnSpeCmnCalRec> resultDateRecList = new ArrayList<CmnSpeCmnCalRec>();

        for (CmnSpeCmnCalRec cmnRec : recList) {

            CmnSpeCmnCalRec rec = new CmnSpeCmnCalRec();
            rec.setAtt(cmnRec);

            // 有效期情况1
            //   rec           -------------
            //   Extra  -------------
            if (cmnExtras.getEffectiveEndDate().compareTo(cmnRec.getEffectiveStartDate()) > 0
                    && cmnExtras.getEffectiveStartDate().compareTo(cmnRec.getEffectiveStartDate()) < 0
                    && cmnExtras.getEffectiveEndDate().compareTo(cmnRec.getEffectiveEndDate()) < 0
                    ) {

                CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                rec1.setAtt(rec);
                rec1.setEffectiveStartDate(cmnRec.getEffectiveStartDate());
                rec1.setEffectiveEndDate(cmnExtras.getEffectiveEndDate());
                //if (rec1.getExtraId() != null) {
                rec1.setExtraId(cmnExtras.getExtraId());
                //}
                resultDateRecList.add(rec1);

                CmnSpeCmnCalRec rec3 = new CmnSpeCmnCalRec();
                rec3.setAtt(rec);
                rec3.setEffectiveStartDate(DateUtil.addSecond(cmnExtras.getEffectiveEndDate(), 1));
                rec3.setEffectiveEndDate(cmnRec.getEffectiveEndDate());
                rec3.setExtraId(null);
                resultDateRecList.add(rec3);
            }

            // 有效期情况2
            //   rec           -------------
            //   Extra  --------------------
            if (cmnExtras.getEffectiveEndDate().compareTo(cmnRec.getEffectiveStartDate()) > 0
                    && cmnExtras.getEffectiveStartDate().compareTo(cmnRec.getEffectiveStartDate()) < 0
                    && cmnExtras.getEffectiveEndDate().compareTo(cmnRec.getEffectiveEndDate()) == 0
                    ) {

                CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                rec1.setAtt(rec);
                rec1.setEffectiveStartDate(cmnRec.getEffectiveStartDate());
                rec1.setEffectiveEndDate(cmnRec.getEffectiveEndDate());
                //if (rec1.getExtraId() != null) {
                rec1.setExtraId(cmnExtras.getExtraId());
                //}
                resultDateRecList.add(rec1);

            }

            // 有效期情况3
            //   Rec           -------------
            //   Extra               -------------
            if (cmnExtras.getEffectiveStartDate().compareTo(cmnRec.getEffectiveStartDate()) > 0
                    && cmnExtras.getEffectiveStartDate().compareTo(cmnRec.getEffectiveEndDate()) < 0
                    && cmnExtras.getEffectiveEndDate().compareTo(cmnRec.getEffectiveEndDate()) > 0
                    ) {

                CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                rec1.setAtt(rec);
                rec1.setEffectiveStartDate(cmnExtras.getEffectiveStartDate());
                rec1.setEffectiveEndDate(cmnRec.getEffectiveEndDate());
                //if (rec1.getExtraId() != null) {
                rec1.setExtraId(cmnExtras.getExtraId());
                //}
                resultDateRecList.add(rec1);

                CmnSpeCmnCalRec rec2 = new CmnSpeCmnCalRec();
                rec2.setAtt(rec);
                rec2.setEffectiveStartDate(cmnRec.getEffectiveStartDate());
                rec2.setEffectiveEndDate(DateUtil.addSecond(cmnExtras.getEffectiveStartDate(), -1));
                rec2.setExtraId(null);
                resultDateRecList.add(rec2);
            }

            // 有效期情况4
            //   Rec           -------------
            //   Extra         ------------------------
            if (cmnExtras.getEffectiveStartDate().compareTo(cmnRec.getEffectiveStartDate()) == 0
                    && cmnExtras.getEffectiveStartDate().compareTo(cmnRec.getEffectiveEndDate()) < 0
                    && cmnExtras.getEffectiveEndDate().compareTo(cmnRec.getEffectiveEndDate()) > 0
                    ) {

                CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                rec1.setAtt(rec);
                rec1.setEffectiveStartDate(cmnRec.getEffectiveStartDate());
                rec1.setEffectiveEndDate(cmnRec.getEffectiveEndDate());
                //if (rec1.getExtraId() != null) {
                rec1.setExtraId(cmnExtras.getExtraId());
                //}
                resultDateRecList.add(rec1);

            }

            // 有效期情况5
            //   Rec           -------------
            //   Extra         ------
            if (cmnExtras.getEffectiveEndDate().compareTo(cmnRec.getEffectiveEndDate()) < 0
                    && cmnExtras.getEffectiveStartDate().compareTo(cmnRec.getEffectiveStartDate()) == 0
                    ) {

                CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                rec1.setAtt(rec);
                rec1.setEffectiveStartDate(cmnExtras.getEffectiveStartDate());
                rec1.setEffectiveEndDate(cmnExtras.getEffectiveEndDate());
                //if (rec1.getExtraId() != null) {
                rec1.setExtraId(cmnExtras.getExtraId());
                //}
                resultDateRecList.add(rec1);

                CmnSpeCmnCalRec rec2 = new CmnSpeCmnCalRec();
                rec2.setAtt(rec);
                rec2.setEffectiveStartDate(DateUtil.addSecond(cmnExtras.getEffectiveEndDate(), 1));
                rec2.setEffectiveEndDate(cmnRec.getEffectiveEndDate());
                rec2.setExtraId(null);
                resultDateRecList.add(rec2);
            }

            // 有效期情况6
            //   Rec         -------------------
            //   Extra             ------
            if (cmnExtras.getEffectiveEndDate().compareTo(cmnRec.getEffectiveEndDate()) < 0
                    && cmnExtras.getEffectiveStartDate().compareTo(cmnRec.getEffectiveStartDate()) > 0
                    ) {

                CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                rec1.setAtt(rec);
                rec1.setEffectiveStartDate(cmnExtras.getEffectiveStartDate());
                rec1.setEffectiveEndDate(cmnExtras.getEffectiveEndDate());
                //if (rec1.getExtraId() != null) {
                rec1.setExtraId(cmnExtras.getExtraId());
                //}
                resultDateRecList.add(rec1);

                CmnSpeCmnCalRec rec2 = new CmnSpeCmnCalRec();
                rec2.setAtt(rec);
                rec2.setEffectiveStartDate(cmnRec.getEffectiveStartDate());
                rec2.setEffectiveEndDate(DateUtil.addSecond(cmnExtras.getEffectiveStartDate(), -1));
                rec2.setExtraId(null);
                resultDateRecList.add(rec2);

                CmnSpeCmnCalRec rec3 = new CmnSpeCmnCalRec();
                rec3.setAtt(rec);
                rec3.setEffectiveStartDate(DateUtil.addSecond(cmnExtras.getEffectiveEndDate(), 1));
                rec3.setEffectiveEndDate(cmnRec.getEffectiveEndDate());
                rec3.setExtraId(null);
                resultDateRecList.add(rec3);
            }

            // 有效期情况7
            //   Rec           -------------
            //   Extra                ------
            if (cmnExtras.getEffectiveEndDate().compareTo(cmnRec.getEffectiveEndDate()) == 0
                    && cmnExtras.getEffectiveStartDate().compareTo(cmnRec.getEffectiveStartDate()) > 0
                    ) {

                CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                rec1.setAtt(rec);
                rec1.setEffectiveStartDate(cmnExtras.getEffectiveStartDate());
                rec1.setEffectiveEndDate(cmnExtras.getEffectiveEndDate());
                rec1.setExtraId(cmnExtras.getExtraId());
                resultDateRecList.add(rec1);

                CmnSpeCmnCalRec rec2 = new CmnSpeCmnCalRec();
                rec2.setAtt(rec);
                rec2.setEffectiveStartDate(cmnRec.getEffectiveStartDate());
                rec2.setEffectiveEndDate(DateUtil.addSecond(cmnExtras.getEffectiveStartDate(), -1));
                rec2.setExtraId(null);
                resultDateRecList.add(rec2);
            }

            // 有效期情况8
            //   Rec           -------------
            //   Extra         -------------
            if (cmnExtras.getEffectiveEndDate().compareTo(cmnRec.getEffectiveEndDate()) == 0
                    && cmnExtras.getEffectiveStartDate().compareTo(cmnRec.getEffectiveStartDate()) == 0
                    ) {
                CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                rec1.setAtt(rec);
                rec1.setEffectiveStartDate(cmnExtras.getEffectiveStartDate());
                rec1.setEffectiveEndDate(cmnExtras.getEffectiveEndDate());
                //if (rec1.getExtraId() != null) {
                rec1.setExtraId(cmnExtras.getExtraId());
                //}
                resultDateRecList.add(rec1);
            }


            // 有效期情况9
            //   Rec           -------------
            //   Extra    ------------------------
            if (cmnExtras.getEffectiveEndDate().compareTo(cmnRec.getEffectiveEndDate()) > 0
                    && cmnExtras.getEffectiveStartDate().compareTo(cmnRec.getEffectiveStartDate()) < 0
                    ) {
                CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                rec1.setAtt(rec);
                rec1.setEffectiveStartDate(cmnRec.getEffectiveStartDate());
                rec1.setEffectiveEndDate(cmnRec.getEffectiveEndDate());
                //if (rec1.getExtraId() != null) {
                rec1.setExtraId(cmnExtras.getExtraId());
                //}
                resultDateRecList.add(rec1);
            }

            // 有效期情况10
            //   Rec                     -------------
            //   Extra    ---------                           -------------
            if (cmnExtras.getEffectiveEndDate().compareTo(cmnRec.getEffectiveStartDate()) < 0
                    || cmnExtras.getEffectiveStartDate().compareTo(cmnRec.getEffectiveEndDate()) > 0
                    ) {
                CmnSpeCmnCalRec rec1 = new CmnSpeCmnCalRec();
                rec1.setAtt(rec);
                rec1.setEffectiveStartDate(cmnRec.getEffectiveStartDate());
                rec1.setEffectiveEndDate(cmnRec.getEffectiveEndDate());
                rec1.setExtraId(cmnRec.getExtraId());
                resultDateRecList.add(rec1);
            }

        }

        return resultDateRecList;
    }

    // 处理供应商佣金数据
    public void processSupplierCommission(Long levelNumber,
                                          CmnSpeCmnKeyField keyField,
                                          CmnBasic cmnBasic,
                                          String parentCommissionNum) {

        // 用superiorSupplierId与key字段到Override中查询是否存在
        List<CmnOverride> overrideList = new ArrayList<CmnOverride>();
        if (supplierCommissionService.queryOverrideList(keyField) != null) {
            overrideList = supplierCommissionService.queryOverrideList(keyField);
        }

        if (overrideList.size() == 0) {
            // 找不到退出循环
            return;
        }

        if (overrideList.size() != 0) {

            levelNumber = levelNumber + 1;

            for (CmnOverride cmnOverride : overrideList) {

                List<CmnSpeCmnCalRec> recList = new ArrayList<CmnSpeCmnCalRec>();

                CmnSpeCmnCalRec calRec = new CmnSpeCmnCalRec();
                // 匹配年龄
                if ((cmnOverride.getPolicyholdersMinAge() >= cmnBasic.getPolicyholdersMinAge()
                        && cmnOverride.getPolicyholdersMinAge() <= cmnBasic.getPolicyholdersMaxAge())
                        || (cmnOverride.getPolicyholdersMaxAge() >= cmnBasic.getPolicyholdersMinAge()
                        && cmnOverride.getPolicyholdersMaxAge() <= cmnBasic.getPolicyholdersMaxAge())) {
                    // 一定有重叠部分
                    long resultBegin = cmnOverride.getPolicyholdersMinAge() >= cmnBasic.getPolicyholdersMinAge()
                            ? cmnOverride.getPolicyholdersMinAge() : cmnBasic.getPolicyholdersMinAge();
                    long resultEnd = cmnBasic.getPolicyholdersMaxAge() >= cmnOverride.getPolicyholdersMaxAge()
                            ? cmnOverride.getPolicyholdersMaxAge() : cmnBasic.getPolicyholdersMaxAge();

                    calRec.setBasicId(cmnBasic.getBasicId());
                    calRec.setOverrideId(cmnOverride.getOverrideId());
                    calRec.setPolicyholdersMinAge(resultBegin);
                    calRec.setPolicyholdersMaxAge(resultEnd);
                } else if (cmnOverride.getPolicyholdersMinAge() < cmnBasic.getPolicyholdersMinAge()
                        && cmnOverride.getPolicyholdersMaxAge() > cmnBasic.getPolicyholdersMaxAge()) {

                    calRec.setBasicId(cmnBasic.getBasicId());
                    calRec.setOverrideId(cmnOverride.getOverrideId());
                    calRec.setPolicyholdersMinAge(cmnBasic.getPolicyholdersMinAge());
                    calRec.setPolicyholdersMaxAge(cmnBasic.getPolicyholdersMaxAge());
                }

                // 匹配时间
                if (calRec.getPolicyholdersMinAge() != null
                        && calRec.getPolicyholdersMaxAge() != null) {

                    long basicBegin = cmnBasic.getEffectiveStartDate().getTime();
                    long basicEnd = cmnBasic.getEffectiveEndDate().getTime();
                    long overrideBegin = cmnOverride.getEffectiveStartDate().getTime();
                    long overrideEnd = cmnOverride.getEffectiveEndDate().getTime();

                    if ((overrideBegin >= basicBegin && overrideBegin <= basicEnd)
                            || (overrideEnd >= basicBegin && overrideEnd <= basicEnd)) {
                        // 一定有重叠部分
                        long resultBegin = overrideBegin >= basicBegin ? overrideBegin : basicBegin;
                        long resultEnd = basicEnd >= overrideEnd ? overrideEnd : basicEnd;

                        Date resultBeginDate = new Date(resultBegin);
                        Date resultEndDate = new Date(resultEnd);

                        calRec.setEffectiveStartDate(resultBeginDate);
                        calRec.setEffectiveEndDate(resultEndDate);
                        recList.add(calRec);
                    } else if (overrideBegin < basicBegin && overrideEnd > basicEnd) {
                        Date resultBeginDate = new Date(basicBegin);
                        Date resultEndDate = new Date(basicEnd);

                        calRec.setEffectiveStartDate(resultBeginDate);
                        calRec.setEffectiveEndDate(resultEndDate);
                        recList.add(calRec);
                    }

                }

                // 取出关键字对应的Extra记录
                keyField.setChannelTypeCode(cmnOverride.getChannelTypeCode());
                keyField.setSupplierId(cmnOverride.getSupplierId());
                keyField.setParentOverrideId(cmnOverride.getOverrideId());

                List<CmnExtras> extraList = new ArrayList<CmnExtras>();
                if (supplierCommissionService.queryExtraList(keyField) != null) {
                    extraList = supplierCommissionService.queryExtraList(keyField);
                }

                List<CmnSpeCmnCalRec> resultRecList = new ArrayList<CmnSpeCmnCalRec>();
                // 循环extra 数据，循环recList数据年龄做匹配插入resultRecList
                if (extraList.size() > 0) {
                    int index = 1;
                    for (CmnExtras cmnExtras : extraList) {

                        // 匹配年龄
                        if (index == 1) {
                            resultRecList = matchAge(recList, cmnExtras);
                        } else {
                            resultRecList = matchAge(resultRecList, cmnExtras);
                        }

                        index = index + 1;
                    }
                } else {
                    // 没有extra 直接插入
                    for (CmnSpeCmnCalRec cmnSpeCmnCalRec : recList) {
                        resultRecList.add(cmnSpeCmnCalRec);
                    }
                }

                // 循环extra 数据，循环recList数据有效期做匹配插入
                if (extraList.size() > 0) {
                    for (CmnExtras cmnExtras : extraList) {
                        resultRecList = matchDate(resultRecList, cmnExtras);
                    }
                }

                /*String commissionNum = "";
                while ("".equals(commissionNum)) {
                    commissionNum = sequenceService.getSequence("SUPPLIER_COMMISSION");
                }*/
                String commissionNum = StringUtil.randomUUID();
                // 循环Rec做佣金值计算
                calculateCommission(resultRecList, levelNumber, commissionNum, parentCommissionNum);

                // 递归调用自己
                keyField.setChannelTypeCode(null);
                keyField.setSupplierId(null);   // 找下级通过parent_override_id 找
                processSupplierCommission(levelNumber, keyField, cmnBasic, commissionNum);
            }

        }

    }

    // 计算供应商佣金表交易路线值
    public void processRoute(Long basicId) {

        List<CmnSupplierCommission> speCommissionList = new ArrayList<CmnSupplierCommission>();
        CmnSupplierCommission paraSpeCmn = new CmnSupplierCommission();
        paraSpeCmn.setBasicId(basicId);
        speCommissionList = supplierCommissionService.selectAllFields(paraSpeCmn);

        for (CmnSupplierCommission speCommission: speCommissionList) {

            if (speCommission.getItemSupplierId() != speCommission.getSupplierId()) {
                String routeName = calculateRouteName(speCommission);
                speCommission.setDealPathName(routeName);

                //supplierCommissionService.updateByPrimaryKeySelective(iRequest, speCommission);
                supplierCommissionService.updateDealPathName(speCommission);
            }
        }
    }

    public String calculateRouteName(CmnSupplierCommission commission) {

        if ("".equals(commission.getParentCommissionNum())) {

            // 顶级供应商
            return commission.getSupplierName() + "." + commission.getItemSupplierName();

        } else {

            // 获取上级
            CmnSupplierCommission speCommission = new CmnSupplierCommission();
            speCommission.setCommissionNum(commission.getParentCommissionNum());
            if (supplierCommissionService.selectAllFields(speCommission) != null
                    && supplierCommissionService.selectAllFields(speCommission).size() > 0) {
                CmnSupplierCommission nowCommission = supplierCommissionService.selectAllFields(speCommission).get(0);
                return commission.getSupplierName() + "." + calculateRouteName(nowCommission);
            } else {

                return "";
            }

        }
    }

    /*// 计算供应商佣金表交易路线值
    public void processRoute(Long basicId) {

        // 取出供应商佣金表所有数据
        List<CmnSupplierCommission> speCommissionList = new ArrayList<CmnSupplierCommission>();
        CmnSupplierCommission paraSupplierCmn = new CmnSupplierCommission();
        paraSupplierCmn.setBasicId(basicId);
        speCommissionList = supplierCommissionService.selectAllFields(new CmnSupplierCommission());

        for (CmnSupplierCommission speCommission: speCommissionList) {

            if (speCommission.getItemSupplierId() != speCommission.getSupplierId()) {

                if ("".equals(speCommission.getParentCommissionNum())) {
                    // 只有两层的情况
                    Long ytId = supplierCommissionService.selectYtCmnId(speCommission.getLineId());

                    CmnSupplierRoute route = new CmnSupplierRoute();
                    route.setSupplierCommissionLineId(speCommission.getLineId());
                    route.setSeqNum(1L);
                    route.setSupplierId(speCommission.getSupplierId());
                    route.setLevelCommissionLineId(ytId);
                    route.setParentRouteId(null);
                    route.setObjectVersionNumber(1L);
                    supplierRouteService.insertSelective(iRequest, route);

                    CmnSupplierRoute topRoute = new CmnSupplierRoute();
                    topRoute.setSupplierCommissionLineId(speCommission.getLineId());
                    topRoute.setSeqNum(2L);
                    topRoute.setSupplierId(speCommission.getItemSupplierId());
                    topRoute.setLevelCommissionLineId(null);
                    topRoute.setParentRouteId(route.getRouteId());
                    topRoute.setObjectVersionNumber(1L);
                    supplierRouteService.insertSelective(iRequest, topRoute);

                    speCommission.setDealPathName(speCommission.getSupplierName() + "."
                            + speCommission.getItemSupplierName());
                    supplierCommissionService.updateByPrimaryKeySelective(iRequest, speCommission);

                } else {
                    CmnSupplierRoute route = new CmnSupplierRoute();
                    route.setSupplierCommissionLineId(speCommission.getLineId());
                    route.setSeqNum(1L);
                    route.setSupplierId(speCommission.getSupplierId());
                    route.setLevelCommissionLineId(speCommission.getLineId());
                    route.setParentRouteId(null);
                    route.setObjectVersionNumber(1L);
                    supplierRouteService.insertSelective(iRequest, route);

                    String routeName = speCommission.getSupplierName() + "." + calculateRouteName(speCommission,
                            route.getRouteId(),
                            1L,
                            speCommission.getLineId());

                    speCommission.setDealPathName(routeName);
                    supplierCommissionService.updateByPrimaryKeySelective(iRequest, speCommission);
                }
            }
        }
    }

    public String calculateRouteName(CmnSupplierCommission commission,
                                     Long parentRouteId,
                                     Long seqNum,
                                     Long supplierCommissionLineId) {

        // 获取上级
        CmnSupplierCommission speCommission = new CmnSupplierCommission();
        speCommission.setCommissionNum(commission.getParentCommissionNum());
        if (supplierCommissionService.selectAllFields(speCommission) != null
                && supplierCommissionService.selectAllFields(speCommission).size() > 0) {
            CmnSupplierCommission nowCommission = supplierCommissionService.selectAllFields(speCommission).get(0);

            if ("".equals(nowCommission.getParentCommissionNum())
                    || nowCommission.getParentCommissionNum() == null) {

                Long ytId = supplierCommissionService.selectYtCmnId(nowCommission.getLineId());

                CmnSupplierRoute route = new CmnSupplierRoute();
                route.setSupplierCommissionLineId(supplierCommissionLineId);
                route.setSeqNum(seqNum + 1);
                route.setSupplierId(nowCommission.getSupplierId());
                route.setLevelCommissionLineId(ytId);
                route.setParentRouteId(parentRouteId);
                route.setObjectVersionNumber(1L);
                supplierRouteService.insertSelective(iRequest, route);

                CmnSupplierRoute topRoute = new CmnSupplierRoute();
                topRoute.setSupplierCommissionLineId(supplierCommissionLineId);
                topRoute.setSeqNum(seqNum + 2);
                topRoute.setSupplierId(nowCommission.getItemSupplierId());
                topRoute.setLevelCommissionLineId(null);
                topRoute.setParentRouteId(route.getRouteId());
                topRoute.setObjectVersionNumber(1L);
                supplierRouteService.insertSelective(iRequest, topRoute);

                // 顶级供应商
                return nowCommission.getSupplierName() + "." + nowCommission.getItemSupplierName();

            } else {

                CmnSupplierRoute route = new CmnSupplierRoute();
                route.setSupplierCommissionLineId(supplierCommissionLineId);
                route.setSeqNum(seqNum + 1);
                route.setSupplierId(nowCommission.getSupplierId());
                route.setLevelCommissionLineId(nowCommission.getLineId());
                route.setParentRouteId(parentRouteId);
                route.setObjectVersionNumber(1L);
                supplierRouteService.insertSelective(iRequest, route);

                return nowCommission.getSupplierName() + "." + calculateRouteName(nowCommission,
                        route.getRouteId(), seqNum + 1, supplierCommissionLineId);
            }

        } else {

            return "";
        }
    }*/

}
