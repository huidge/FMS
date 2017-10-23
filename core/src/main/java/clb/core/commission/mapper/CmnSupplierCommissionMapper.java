package clb.core.commission.mapper;
/**
 * Created by jiaolong.li on 2017-04-24.
 */
import clb.core.commission.dto.*;
import clb.core.production.dto.PrdAttribute;
import com.hand.hap.mybatis.common.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CmnSupplierCommissionMapper extends Mapper<CmnSupplierCommission> {

    public List<CmnSupplierCommission> selectAllFields(CmnSupplierCommission cmnSupplierCommission);

    public List<CmnSupplierCommission> selectShowAllFields(CmnSupplierCommission cmnSupplierCommission);

    public List<CmnSupplierCommission> selectByCommissionNum(CmnSupplierCommission cmnSupplierCommission);

    public Long selectYtCmnId(Long lineId);

    public void deleteCmnData(CmnSupplierCommission cmnSupplierCommission);

    public List<CmnBasic> queryBasicList(CmnSpeCmnKeyField cmnSpeCmnKeyField);

    public List<CmnOverride> queryOverrideList(CmnSpeCmnKeyField cmnSpeCmnKeyField);

    public List<CmnExtras> queryExtraList(CmnSpeCmnKeyField cmnSpeCmnKeyField);

    public void updateDealPathName(CmnSupplierCommission cmnSupplierCommission);
}