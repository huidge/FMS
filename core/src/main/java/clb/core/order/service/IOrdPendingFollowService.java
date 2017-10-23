package clb.core.order.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.system.service.IBaseService;
import clb.core.order.dto.OrdPendingFollow;

import java.util.List;

public interface IOrdPendingFollowService extends IBaseService<OrdPendingFollow>, ProxySelf<IOrdPendingFollowService>{
    List<OrdPendingFollow> queryOrdPendingFollow(IRequest request, OrdPendingFollow ordPendingFollow, int page, int pagesize);

    List<OrdPendingFollow> queryWsOrdPendingFollow(IRequest request, OrdPendingFollow ordPendingFollow);

    List<OrdPendingFollow> followBatchUpdate(IRequest request, @StdWho List<OrdPendingFollow> ordPendingFollows);
}
