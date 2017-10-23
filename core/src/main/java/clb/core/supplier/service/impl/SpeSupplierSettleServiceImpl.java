package clb.core.supplier.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.supplier.dto.SpeSupplierSettle;
import clb.core.supplier.mapper.SpeSupplierSettleMapper;
import clb.core.supplier.service.ISpeSupplierSettleService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SpeSupplierSettleServiceImpl extends BaseServiceImpl<SpeSupplierSettle> implements ISpeSupplierSettleService{

	@Autowired
	private SpeSupplierSettleMapper supplierSettleMapper;
	
	@Override
	public List<SpeSupplierSettle> selectData(SpeSupplierSettle dto) {
		return supplierSettleMapper.select(dto);
	}

}