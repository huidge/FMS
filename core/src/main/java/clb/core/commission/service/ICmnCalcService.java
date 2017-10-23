package clb.core.commission.service;

import clb.core.commission.dto.CmnCalc;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

public interface ICmnCalcService extends IBaseService<CmnCalc>, ProxySelf<ICmnCalcService>{

    ResponseData productionCalcPremium(IRequest requestContext,CmnCalc dto, int page, int pageSize);


}