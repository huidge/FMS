package clb.core.supplier.service;

import java.util.List;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.supplier.dto.SpeSupplierSettle;

public interface ISpeSupplierSettleService extends IBaseService<SpeSupplierSettle>, ProxySelf<ISpeSupplierSettleService>{

	public List<SpeSupplierSettle> selectData(SpeSupplierSettle dto);
	
}