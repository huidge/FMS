package clb.core.order.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;

import clb.core.order.dto.OrdChangeHeader;

public interface IOrdChangeHeaderService extends IBaseService<OrdChangeHeader>, ProxySelf<IOrdChangeHeaderService>{

    public ResponseData queryOrdChange(IRequest request, OrdChangeHeader ordChangeHeader);

}