package clb.core.production.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.production.dto.PrdPremiumAdjust;

import java.util.List;

/*****
 * @author tiansheng.ye
 * @Date 2017/07/21
 */
public interface IPrdPremiumAdjustService extends IBaseService<PrdPremiumAdjust>,ProxySelf<IPrdPremiumAdjustService>{

    public List<PrdPremiumAdjust> selectAllFields(IRequest requestContext, PrdPremiumAdjust prdPremium, int page, int pageSize);
}
