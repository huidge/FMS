package clb.core.commission.service.impl;
/**
 * Created by jiaolong.li on 2017-04-24.
 */

import clb.core.commission.dto.*;
import clb.core.commission.mapper.*;
import clb.core.common.service.ISequenceService;
import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.DateUtil;
import clb.core.system.service.IClbUserService;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.commission.service.ICmnSupplierCommissionService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CmnSupplierCommissionServiceImpl extends BaseServiceImpl<CmnSupplierCommission> implements ICmnSupplierCommissionService {

    @Autowired
    private CmnSupplierCommissionMapper cmnSupplierCommissionMapper;

    @Autowired
    private CmnBasicMapper cmnBasicMapper;

    @Autowired
    private CmnOverrideMapper cmnOverrideMapper;

    @Autowired
    private CmnExtrasMapper cmnExtrasMapper;

    @Autowired
    private CmnSupplierRouteMapper cmnSupplierRouteMapper;

    @Autowired
    private IClbUserService clbUserService;

    @Autowired
    private ISequenceService sequenceService;

    public List<CmnSupplierCommission> selectAllFields(IRequest requestContext,
                                                       CmnSupplierCommission cmnSupplierCommission,
                                                       int page,
                                                       int pageSize) {
        //是否超级管理员
        boolean isAdmin = clbUserService.isAdmin(requestContext);
        if (!isAdmin) {
            Long supplierId = clbUserService.getSupplierId(requestContext);
            if (supplierId == null) {
                return new ArrayList<CmnSupplierCommission>();
            }
            cmnSupplierCommission.setSupplierId(supplierId);
        }

        PageHelper.startPage(page, pageSize);
        List<CmnSupplierCommission> cmnList = cmnSupplierCommissionMapper.selectAllFields(cmnSupplierCommission);
        return cmnList;
    }

    public List<CmnSupplierCommission> selectShowAllFields(IRequest requestContext,
                                                           CmnSupplierCommission cmnSupplierCommission,
                                                           int page,
                                                           int pageSize) {
        //是否超级管理员
        boolean isAdmin = clbUserService.isAdmin(requestContext);
        if (!isAdmin) {
            Long supplierId = clbUserService.getSupplierId(requestContext);
            if (supplierId == null) {
                return new ArrayList<CmnSupplierCommission>();
            }
            cmnSupplierCommission.setSupplierId(supplierId);
        }

        PageHelper.startPage(page, pageSize);
        List<CmnSupplierCommission> cmnList = cmnSupplierCommissionMapper.selectShowAllFields(cmnSupplierCommission);
        return cmnList;
    }

    public List<CmnSupplierCommission> selectAllFields(CmnSupplierCommission cmnSupplierCommission) {
        List<CmnSupplierCommission> cmnList = cmnSupplierCommissionMapper.selectAllFields(cmnSupplierCommission);
        return cmnList;
    }

    public void deleteCmnData(CmnSupplierCommission cmnSupplierCommission) {
        cmnSupplierCommissionMapper.deleteCmnData(cmnSupplierCommission);
    }

    public void updateDealPathName(CmnSupplierCommission cmnSupplierCommission) {
        cmnSupplierCommissionMapper.updateDealPathName(cmnSupplierCommission);
    }

    public List<CmnBasic> queryBasic(IRequest requestContext, CmnBasic dto) {

        return cmnBasicMapper.queryBasic(dto);
    }

    public List<CmnBasic> queryBasicList(CmnSpeCmnKeyField cmnSpeCmnKeyField) {
        return cmnSupplierCommissionMapper.queryBasicList(cmnSpeCmnKeyField);
    }

    public List<CmnOverride> queryOverrideList(CmnSpeCmnKeyField cmnSpeCmnKeyField) {
        return cmnSupplierCommissionMapper.queryOverrideList(cmnSpeCmnKeyField);
    }

    public List<CmnExtras> queryExtraList(CmnSpeCmnKeyField cmnSpeCmnKeyField) {
        return cmnSupplierCommissionMapper.queryExtraList(cmnSpeCmnKeyField);
    }

    public List<CmnSupplierCommission> selectByCommissionNum(CmnSupplierCommission cmnSupplierCommission) {
        return cmnSupplierCommissionMapper.selectByCommissionNum(cmnSupplierCommission);
    }

    public Long selectYtCmnId(Long lineId) {
        return cmnSupplierCommissionMapper.selectYtCmnId(lineId);
    }

    /**
     ***************************************************************************************
     * 执行job
     ***************************************************************************************
     */
    public void executeJob() {

        // 清理供应商佣金表
        cmnSupplierCommissionMapper.deleteCmnData(new CmnSupplierCommission());

        // 查询出所有basic记录
        List<CmnBasic> baiscList = cmnBasicMapper.queryBasic(null);

        // 循环Basic记录
        for (CmnBasic cmnBasic : baiscList) {

            // 设置Key关键字
            CmnSpeCmnKeyField basicKey = new CmnSpeCmnKeyField();
            basicKey.setItemId(cmnBasic.getItemId());
            basicKey.setPriceType(cmnBasic.getPriceType());
            basicKey.setPayMethod(cmnBasic.getPayMethod());
            basicKey.setCurrency(cmnBasic.getCurrency());
            basicKey.setContributionPeriod(cmnBasic.getContributionPeriod());
            basicKey.setSupplierId(cmnBasic.getSupplierId());

            processSupplierCommission(1L, basicKey, cmnBasic, "");
        }

        // 计算供应商交易路线
        this.processRoute();

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
            //basicData = iCmnBasicService.selectByPrimaryKey(null, basicData);
            basicData = cmnBasicMapper.queryBasic(basicData).get(0);

            overrideData.setOverrideId(cmnSpeCmnCalRec.getOverrideId());
            overrideData = cmnOverrideMapper.selectByPrimaryKey(overrideData);

            if (cmnSpeCmnCalRec.getExtraId() != null) {
                extraData.setExtraId(cmnSpeCmnCalRec.getExtraId());
                extraData = cmnExtrasMapper.selectByPrimaryKey(extraData);
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
            cmnSupplierCommissionMapper.insertSelective(csc);
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
                if (cmnExtras.getPolicyholdersMaxAge() <= cmnSpeCmnCalRec.getPolicyholdersMinAge()
                        || cmnExtras.getPolicyholdersMinAge() >= cmnSpeCmnCalRec.getPolicyholdersMaxAge()) {

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
            if (cmnExtras.getEffectiveEndDate().compareTo(cmnRec.getEffectiveStartDate()) <= 0
                    || cmnExtras.getEffectiveStartDate().compareTo(cmnRec.getEffectiveEndDate()) >= 0
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
        if (cmnSupplierCommissionMapper.queryOverrideList(keyField) != null) {
            overrideList = cmnSupplierCommissionMapper.queryOverrideList(keyField);
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
                if (cmnSupplierCommissionMapper.queryExtraList(keyField) != null) {
                    extraList = cmnSupplierCommissionMapper.queryExtraList(keyField);
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

                String commissionNum = sequenceService.getSequence("SUPPLIER_COMMISSION");
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
    public void processRoute() {

        // 取出供应商佣金表所有数据
        List<CmnSupplierCommission> speCommissionList = new ArrayList<CmnSupplierCommission>();
        speCommissionList = cmnSupplierCommissionMapper.selectAllFields(new CmnSupplierCommission());

        // 清空数据
        cmnSupplierRouteMapper.deleteRouteData(new CmnSupplierRoute());

        for (CmnSupplierCommission speCommission: speCommissionList) {

            if (speCommission.getItemSupplierId() != speCommission.getSupplierId()) {

                if ("".equals(speCommission.getParentCommissionNum())) {
                    // 只有两层的情况
                    Long ytId = cmnSupplierCommissionMapper.selectYtCmnId(speCommission.getLineId());

                    CmnSupplierRoute route = new CmnSupplierRoute();
                    route.setSupplierCommissionLineId(speCommission.getLineId());
                    route.setSeqNum(1L);
                    route.setSupplierId(speCommission.getSupplierId());
                    route.setLevelCommissionLineId(ytId);
                    route.setParentRouteId(null);
                    route.setObjectVersionNumber(1L);
                    cmnSupplierRouteMapper.insertSelective(route);

                    CmnSupplierRoute topRoute = new CmnSupplierRoute();
                    topRoute.setSupplierCommissionLineId(speCommission.getLineId());
                    topRoute.setSeqNum(2L);
                    topRoute.setSupplierId(speCommission.getItemSupplierId());
                    topRoute.setLevelCommissionLineId(null);
                    topRoute.setParentRouteId(route.getRouteId());
                    topRoute.setObjectVersionNumber(1L);
                    cmnSupplierRouteMapper.insertSelective(topRoute);

                    speCommission.setDealPathName(speCommission.getSupplierName() + "."
                            + speCommission.getItemSupplierName());
                    cmnSupplierCommissionMapper.updateByPrimaryKeySelective(speCommission);

                } else {
                    CmnSupplierRoute route = new CmnSupplierRoute();
                    route.setSupplierCommissionLineId(speCommission.getLineId());
                    route.setSeqNum(1L);
                    route.setSupplierId(speCommission.getSupplierId());
                    route.setLevelCommissionLineId(speCommission.getLineId());
                    route.setParentRouteId(null);
                    route.setObjectVersionNumber(1L);
                    cmnSupplierRouteMapper.insertSelective(route);

                    String routeName = speCommission.getSupplierName() + "." + calculateRouteName(speCommission,
                                                        route.getRouteId(),
                                                        1L,
                                                        speCommission.getLineId());

                    speCommission.setDealPathName(routeName);
                    cmnSupplierCommissionMapper.updateByPrimaryKeySelective(speCommission);
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
            if (cmnSupplierCommissionMapper.selectAllFields(speCommission) != null
                    && cmnSupplierCommissionMapper.selectAllFields(speCommission).size() > 0) {
                CmnSupplierCommission nowCommission = cmnSupplierCommissionMapper.selectAllFields(speCommission).get(0);

                if ("".equals(nowCommission.getParentCommissionNum())
                        || nowCommission.getParentCommissionNum() == null) {

                    Long ytId = cmnSupplierCommissionMapper.selectYtCmnId(nowCommission.getLineId());

                    CmnSupplierRoute route = new CmnSupplierRoute();
                    route.setSupplierCommissionLineId(supplierCommissionLineId);
                    route.setSeqNum(seqNum + 1);
                    route.setSupplierId(nowCommission.getSupplierId());
                    route.setLevelCommissionLineId(ytId);
                    route.setParentRouteId(parentRouteId);
                    route.setObjectVersionNumber(1L);
                    cmnSupplierRouteMapper.insertSelective(route);

                    CmnSupplierRoute topRoute = new CmnSupplierRoute();
                    topRoute.setSupplierCommissionLineId(supplierCommissionLineId);
                    topRoute.setSeqNum(seqNum + 2);
                    topRoute.setSupplierId(nowCommission.getItemSupplierId());
                    topRoute.setLevelCommissionLineId(null);
                    topRoute.setParentRouteId(route.getRouteId());
                    topRoute.setObjectVersionNumber(1L);
                    cmnSupplierRouteMapper.insertSelective(topRoute);

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
                    cmnSupplierRouteMapper.insertSelective(route);

                    return nowCommission.getSupplierName() + "." + calculateRouteName(nowCommission,
                            route.getRouteId(), seqNum + 1, supplierCommissionLineId);
                }

            } else {

                return "";
            }

    }

}