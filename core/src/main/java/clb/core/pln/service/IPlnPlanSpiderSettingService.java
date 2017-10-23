package clb.core.pln.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.pln.dto.PlnPlanSpiderSetting;

import java.util.List;

public interface IPlnPlanSpiderSettingService extends IBaseService<PlnPlanSpiderSetting>, ProxySelf<IPlnPlanSpiderSettingService>{

    List<PlnPlanSpiderSetting> findAll(IRequest request, PlnPlanSpiderSetting sorDeliveries, int page, int pagesize);
}