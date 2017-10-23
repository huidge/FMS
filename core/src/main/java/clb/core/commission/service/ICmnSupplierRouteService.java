package clb.core.commission.service;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.commission.dto.CmnSupplierRoute;

import java.util.List;

public interface ICmnSupplierRouteService extends IBaseService<CmnSupplierRoute>, ProxySelf<ICmnSupplierRouteService>{

    public void deleteRouteData(CmnSupplierRoute supplierRoute);

    public List<CmnSupplierRoute> querySupplierRoute(CmnSupplierRoute supplierRoute);
}