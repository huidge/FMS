package clb.core.commission.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.commission.dto.CmnSupplierCommissionShow;

public interface CmnSupplierCommissionShowMapper extends Mapper<CmnSupplierCommissionShow>{

    public void deleteCmnData();

    public void insertShowCmnData();
}