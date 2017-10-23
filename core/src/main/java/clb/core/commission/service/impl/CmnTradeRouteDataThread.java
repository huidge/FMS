package clb.core.commission.service.impl;

import clb.core.commission.mapper.CmnTradeRouteShowMapper;

import java.io.Serializable;

/**
 * @wanjun.feng@hand-china.com
 * @create 2017-10-06
 **/
public class CmnTradeRouteDataThread implements Runnable, Serializable {

    private static final long serialVersionUID = 462090079590275461L;
    
    private CmnTradeRouteShowMapper routeShowMapper;

    public CmnTradeRouteDataThread(CmnTradeRouteShowMapper routeShowMapper){

        this.routeShowMapper = routeShowMapper;
    }

    @Override
    public void run() {

        routeShowMapper.deleteRouteData();
        routeShowMapper.insertShowRouteData();

    }
}