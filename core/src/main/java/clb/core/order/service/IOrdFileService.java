package clb.core.order.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.order.dto.OrdFile;

import java.util.List;

public interface IOrdFileService extends IBaseService<OrdFile>, ProxySelf<IOrdFileService>{
    List<OrdFile> queryOrdFile(IRequest request, OrdFile ordFile);
}