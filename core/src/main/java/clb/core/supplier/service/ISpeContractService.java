package clb.core.supplier.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.supplier.dto.SpeContract;
import clb.core.system.dto.ClbCode;
import clb.core.system.dto.ClbCodeValue;

public interface ISpeContractService extends IBaseService<SpeContract>, ProxySelf<ISpeContractService>{

	List<SpeContract> supplierContractBatchUpdate(IRequest request,List<SpeContract> data);
	
}