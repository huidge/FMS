package clb.core.commission.service.impl;

import clb.core.commission.mapper.CmnSupplierCommissionShowMapper;

import java.io.Serializable;

/**
 * @wanjun.feng@hand-china.com
 * @create 2017-10-06
 **/
public class CmnSupplierCommissionDataThread implements Runnable, Serializable {

    private static final long serialVersionUID = -1516144008738810061L;
    
    private CmnSupplierCommissionShowMapper supplierCommissionShowMapper;

    public CmnSupplierCommissionDataThread(CmnSupplierCommissionShowMapper supplierCommissionShowMapper){

        this.supplierCommissionShowMapper = supplierCommissionShowMapper;
    }

    @Override
    public void run() {

        supplierCommissionShowMapper.deleteCmnData();
        supplierCommissionShowMapper.insertShowCmnData();

    }
}