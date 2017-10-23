package clb.core.order.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.order.dto.OrdTeamVisitor;

import java.util.List;

public interface IOrdTeamVisitorService extends IBaseService<OrdTeamVisitor>, ProxySelf<IOrdTeamVisitorService>{
    List<OrdTeamVisitor> queryWsOrdTeamVisitor(IRequest request, OrdTeamVisitor ordTeamVisitor);
}