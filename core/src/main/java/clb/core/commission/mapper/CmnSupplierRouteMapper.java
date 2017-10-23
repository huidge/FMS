package clb.core.commission.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.commission.dto.CmnSupplierRoute;

import java.util.List;

public interface CmnSupplierRouteMapper extends Mapper<CmnSupplierRoute>{

    public void deleteRouteData(CmnSupplierRoute supplierRoute);

    public List<CmnSupplierRoute> querySupplierRoute(CmnSupplierRoute supplierRoute);
}