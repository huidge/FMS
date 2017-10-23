package clb.core.production.service.impl;
/**
 * Created by jiaolong.li on 2017-04-18.
 */
import clb.core.production.mapper.PrdAttributeSetMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.production.dto.PrdAttributeSet;
import clb.core.production.service.IPrdAttributeSetService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PrdAttributeSetServiceImpl extends BaseServiceImpl<PrdAttributeSet> implements IPrdAttributeSetService{

    @Autowired
    private PrdAttributeSetMapper prdAttributeSetMapper;

    public List<PrdAttributeSet> selectAllFields(IRequest requestContext, PrdAttributeSet prdAttributeSet, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return prdAttributeSetMapper.selectAllFields(prdAttributeSet);
    }
}