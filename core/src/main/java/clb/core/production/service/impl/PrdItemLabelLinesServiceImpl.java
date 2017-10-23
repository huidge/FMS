package clb.core.production.service.impl;

import clb.core.production.dto.PrdItemLabelLines;
import clb.core.production.mapper.PrdItemLabelLinesMapper;
import clb.core.production.service.IPrdItemLabelLinesService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Rex.Hua on 17/4/12.
 */
@Service
@Transactional
public class PrdItemLabelLinesServiceImpl extends BaseServiceImpl<PrdItemLabelLines> implements IPrdItemLabelLinesService {
    
    @Autowired
    private PrdItemLabelLinesMapper prdItemsLabelLinesMapper;

    @Override
    public List<PrdItemLabelLines> selectAlive(PrdItemLabelLines dto){
        return prdItemsLabelLinesMapper.selectByParam(dto);
    }

    @Override
    public PrdItemLabelLines deleteByItemLabel(PrdItemLabelLines dto){
        return prdItemsLabelLinesMapper.deleteByItemLabel(dto);
    }

    @Override
    public List<PrdItemLabelLines> choiceTags(PrdItemLabelLines dto){
        return prdItemsLabelLinesMapper.choiceTags(dto);
    };
}