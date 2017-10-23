package clb.core.pln.service;

import clb.core.pln.dto.PlnPlanRequest;
import clb.core.pln.dto.PlnPlanSpiderSetting;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

/**
 * Created by Detai on 2017/5/17.
 */
public interface IPlnPlanSpiderService extends IBaseService<PlnPlanRequest>, ProxySelf<IPlnPlanSpiderService>{
//    void exePlnSpider(String requestNumber);
//
//    List<PlnPlanRequest> selectPlanRequest(Long requestId, IRequest requestContext);
    List<PlnPlanRequest> queryPlanRequest(PlnPlanRequest plnPlanRequest);

    List<PlnPlanSpiderSetting> querySpaderAuth(PlnPlanSpiderSetting dto);
}
