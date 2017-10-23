package clb.core.commission.service.impl;

import clb.core.commission.mapper.CmnChannelCommissionShowMapper;

import java.io.Serializable;

/**
 * @wanjun.feng@hand-china.com
 * @create 2017-10-06
 **/
public class CmnChannelCommissionDataThread implements Runnable, Serializable {

    private static final long serialVersionUID = -5936970255900025251L;
    
    private CmnChannelCommissionShowMapper commissionShowMapper;

    public CmnChannelCommissionDataThread(CmnChannelCommissionShowMapper commissionShowMapper){

        this.commissionShowMapper = commissionShowMapper;
    }

    @Override
    public void run() {

        commissionShowMapper.deleteCmnData();
        commissionShowMapper.insertShowCmnData();

    }
}