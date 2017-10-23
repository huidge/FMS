package clb.core.production.service.impl;

import clb.core.production.dto.PrdItemLabels;
import clb.core.production.mapper.PrdItemLabelsMapper;
import clb.core.production.service.IPrdItemLabelsService;
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
public class PrdItemLabelsServiceImpl extends BaseServiceImpl<PrdItemLabels> implements IPrdItemLabelsService {
    
    @Autowired
    private PrdItemLabelsMapper prdItemsLabelsMapper;

    @Override
    public String selectLabelId(PrdItemLabels dto){
        return prdItemsLabelsMapper.selectLabelId(dto);
    }

}