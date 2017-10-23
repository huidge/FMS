package clb.core.commission.service.impl;

import clb.core.commission.dto.*;
import clb.core.commission.mapper.*;
import clb.core.commission.service.ICmnSupplierCommissionService;
import clb.core.common.utils.ListUtil;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.commission.service.ICmnTradeRouteService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Transactional
public class CmnTradeRouteServiceImpl extends BaseServiceImpl<CmnTradeRoute> implements ICmnTradeRouteService{

    @Autowired
    private CmnTradeRouteMapper cmnTradeRouteMapper;

    @Autowired
    private CmnSupplierCommissionShowMapper supplierCommissionShowMapper;

    @Autowired
    private CmnChannelCommissionShowMapper commissionShowMapper;

    @Autowired
    private CmnTradeRouteShowMapper routeShowMapper;


    public void deleteRouteData(CmnTradeRoute cmnTradeRoute){
        cmnTradeRouteMapper.deleteRouteData(cmnTradeRoute);
    }

    public void updateParentRouteId(CmnTradeRoute cmnTradeRoute) {
        cmnTradeRouteMapper.updateParentRouteId(cmnTradeRoute);
    }

    public void updateDealPath(CmnTradeRoute cmnTradeRoute){
        cmnTradeRouteMapper.updateDealPath(cmnTradeRoute);
    }


    public void updateCommissionDealPath(CmnTradeRoute cmnTradeRoute){
        cmnTradeRouteMapper.updateCommissionDealPath(cmnTradeRoute);
    }

    public List<CmnTradeRoute> queryDealPath(CmnTradeRoute cmnTradeRoute) {
        return cmnTradeRouteMapper.queryDealPath(cmnTradeRoute);
    }

    public List<CmnTradeRoute> queryCmnTradeRouteByCondition(CmnTradeRoute cmnTradeRoute) {
        return cmnTradeRouteMapper.queryCmnTradeRouteByCondition(cmnTradeRoute);
    }

    /**
     * 查询
     * @param request
     * @param cmnTradeRoute
     * @return
     */
    @Override
    public List<CmnTradeRoute> queryCmnTradeRoute(IRequest request, CmnTradeRoute cmnTradeRoute) {
        return cmnTradeRouteMapper.queryCmnTradeRoute(cmnTradeRoute);
    }

    public void transferData() {

        supplierCommissionShowMapper.deleteCmnData();
        supplierCommissionShowMapper.insertShowCmnData();
        commissionShowMapper.deleteCmnData();
        commissionShowMapper.insertShowCmnData();
        routeShowMapper.deleteRouteData();
        routeShowMapper.insertShowRouteData();
    }
}