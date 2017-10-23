package clb.core.production.service;
/**
 * Created by jiaolong.li on 2017-04-18.
 */
import clb.core.production.dto.PrdAttributeSet;
import clb.core.production.dto.PrdClass;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.production.dto.PrdAttributeSetLine;

import java.util.List;

public interface IPrdAttributeSetLineService extends IBaseService<PrdAttributeSetLine>, ProxySelf<IPrdAttributeSetLineService>{

    public List<PrdAttributeSetLine> selectAllFields(IRequest requestContext, PrdAttributeSetLine prdAttributeSetLine, int page, int pageSize);

}