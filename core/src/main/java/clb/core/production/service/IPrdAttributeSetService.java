package clb.core.production.service;
/**
 * Created by jiaolong.li on 2017-04-18.
 */
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.production.dto.PrdAttributeSet;

import java.util.List;

public interface IPrdAttributeSetService extends IBaseService<PrdAttributeSet>, ProxySelf<IPrdAttributeSetService>{

    public List<PrdAttributeSet> selectAllFields(IRequest requestContext, PrdAttributeSet prdAttributeSet, int page, int pageSize);
}