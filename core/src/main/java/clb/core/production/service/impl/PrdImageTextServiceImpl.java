package clb.core.production.service.impl;

import clb.core.production.dto.PrdImageText;
import clb.core.production.dto.PrdItemLabels;
import clb.core.production.mapper.PrdImageTextMapper;
import clb.core.production.mapper.PrdItemLabelsMapper;
import clb.core.production.service.IPrdImageTextService;
import clb.core.production.service.IPrdItemLabelsService;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Rex.Hua on 17/6/12.
 */
@Service
@Transactional
public class PrdImageTextServiceImpl extends BaseServiceImpl<PrdImageText> implements IPrdImageTextService{
    
    @Autowired
    private PrdImageTextMapper prdImageTextMapper;

    @Override
    public List<PrdImageText> query(PrdImageText dto) {
        return prdImageTextMapper.select(dto);
    }

    @Override
    public List<PrdImageText> batchUpdate(IRequest request, List<PrdImageText> dtoList) {
        for (PrdImageText dto : dtoList) {
            if (dto.getImageId()==null) {
                prdImageTextMapper.insertSelective(dto);
            } else {
                prdImageTextMapper.updateByPrimaryKeySelective(dto);
            }
        }
        return dtoList;
    }
}