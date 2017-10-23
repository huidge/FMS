package clb.core.commission.service.impl;

import clb.core.commission.mapper.CmnSupplierRouteMapper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.commission.dto.CmnSupplierRoute;
import clb.core.commission.service.ICmnSupplierRouteService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CmnSupplierRouteServiceImpl extends BaseServiceImpl<CmnSupplierRoute> implements ICmnSupplierRouteService{

    @Autowired
    private CmnSupplierRouteMapper supplierRouteMapper;

    public void deleteRouteData(CmnSupplierRoute supplierRoute) {
        supplierRouteMapper.deleteRouteData(supplierRoute);
    }

    public List<CmnSupplierRoute> querySupplierRoute(CmnSupplierRoute supplierRoute) {
        return supplierRouteMapper.querySupplierRoute(supplierRoute);
    }
}