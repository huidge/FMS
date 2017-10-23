package clb.core.production.service.impl;
/**
 * Created by jiaolong.li on 2017-04-18.
 */
import clb.core.production.mapper.PrdAttributeSetLineMapper;
import clb.core.production.mapper.PrdAttributeSetMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.production.dto.PrdAttributeSetLine;
import clb.core.production.service.IPrdAttributeSetLineService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PrdAttributeSetLineServiceImpl extends BaseServiceImpl<PrdAttributeSetLine> implements IPrdAttributeSetLineService {

    @Autowired
    private PrdAttributeSetLineMapper prdAttributeSetLineMapper;

    public List<PrdAttributeSetLine> selectAllFields(IRequest requestContext, PrdAttributeSetLine prdAttributeSetLine, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return prdAttributeSetLineMapper.selectAllFields(prdAttributeSetLine);
    }
}