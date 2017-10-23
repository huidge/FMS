package clb.core.production.service;

/**
 * Created by jiaolong.li on 2017-04-13.
 */
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.production.dto.PrdAttribute;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface IPrdAttributeService extends IBaseService<PrdAttribute>, ProxySelf<IPrdAttributeService>{

    public List<PrdAttribute> selectAllFields(IRequest requestContext, PrdAttribute prdAttribute, int page, int pageSize);

    public String queryDefaultValue(String codeId, String meaning);
    
    PrdAttribute selectAttrValue(Long attSetId, Long attId);
}