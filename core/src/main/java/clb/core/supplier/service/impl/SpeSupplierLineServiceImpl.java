package clb.core.supplier.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.supplier.dto.SpeSupplierLine;
import clb.core.supplier.service.ISpeSupplierLineService;

/**
 * @name SpeSupplierServiceImpl
 * @description 供应商业务层
 * @author bo.wu@hand-china.com 2017年4月18日19:36:25
 * @version 1.0 
 */
@Service
@Transactional
public class SpeSupplierLineServiceImpl extends BaseServiceImpl<SpeSupplierLine> implements ISpeSupplierLineService{

}