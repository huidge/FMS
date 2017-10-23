package clb.core.commission.service;
/**
 * Created by jiaolong.li on 2017-04-24.
 */

import clb.core.commission.dto.*;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

public interface ICmnSupplierCommissionService extends IBaseService<CmnSupplierCommission>, ProxySelf<ICmnSupplierCommissionService> {

    public List<CmnSupplierCommission> selectAllFields(IRequest requestContext,
                                                       CmnSupplierCommission cmnSupplierCommission,
                                                       int page,
                                                       int pageSize);

    public List<CmnSupplierCommission> selectShowAllFields(IRequest requestContext,
                                                           CmnSupplierCommission cmnSupplierCommission,
                                                           int page,
                                                           int pageSize);

    public List<CmnSupplierCommission> selectAllFields(CmnSupplierCommission cmnSupplierCommission);

    public List<CmnSupplierCommission> selectByCommissionNum(CmnSupplierCommission cmnSupplierCommission);

    public void deleteCmnData(CmnSupplierCommission cmnSupplierCommission);

    public void updateDealPathName(CmnSupplierCommission cmnSupplierCommission);

    public List<CmnBasic> queryBasic(IRequest requestContext, CmnBasic dto);

    public List<CmnBasic> queryBasicList(CmnSpeCmnKeyField cmnSpeCmnKeyField);

    public List<CmnOverride> queryOverrideList(CmnSpeCmnKeyField cmnSpeCmnKeyField);

    public List<CmnExtras> queryExtraList(CmnSpeCmnKeyField cmnSpeCmnKeyField);

    public Long selectYtCmnId(Long lineId);

    public void executeJob();
}