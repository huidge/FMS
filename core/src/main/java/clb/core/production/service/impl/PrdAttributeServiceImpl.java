package clb.core.production.service.impl;

/**
 * Created by jiaolong.li on 2017-04-13.
 */
import clb.core.production.mapper.PrdAttributeMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.production.dto.PrdAttribute;
import clb.core.production.service.IPrdAttributeService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PrdAttributeServiceImpl extends BaseServiceImpl<PrdAttribute> implements IPrdAttributeService {

    @Autowired
    private PrdAttributeMapper prdAttributeMapper;

    public List<PrdAttribute> selectAllFields(IRequest requestContext, PrdAttribute prdAttribute, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return prdAttributeMapper.selectAllFields(prdAttribute);
    }

    public String queryDefaultValue(String codeId, String meaning){
        return prdAttributeMapper.queryDefaultValue(codeId, meaning);
    }

	@Override
	public PrdAttribute selectAttrValue(Long attSetId, Long attId) {
		return prdAttributeMapper.selectAttrValue(attSetId, attId);
	}
}