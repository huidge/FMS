package clb.core.production.service.impl;

import clb.core.production.dto.PrdItemSubline;
import clb.core.production.mapper.PrdItemSublineMapper;
import clb.core.production.service.IPrdItemSublineService;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PrdItemSublineServiceImpl extends BaseServiceImpl<PrdItemSubline> implements IPrdItemSublineService {

    @Autowired
    private PrdItemSublineMapper prdItemPaymodeMapper;

    @Override
    public List<PrdItemSubline> query(IRequest iRequest, PrdItemSubline dto, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return prdItemPaymodeMapper.select(dto);
    }

    @Override
    public List<PrdItemSubline> batchUpdate(IRequest request, List<PrdItemSubline> dtoList) {
        for (PrdItemSubline dto : dtoList) {
            if (dto.getSublineId()==null || dto.getSublineId().equals("")) {
                prdItemPaymodeMapper.insertSelective(dto);
            } else {
                prdItemPaymodeMapper.updateByPrimaryKeySelective(dto);
            }
        }
        return dtoList;
    }

    @Override
    public List<PrdItemSubline> selectByChannel(IRequest requestContext, PrdItemSubline dto) {
        return prdItemPaymodeMapper.selectByChannel(dto);
    }

	@Override
	public Long selectByCondition(String sublineItemName, Long itemId) {
		return prdItemPaymodeMapper.selectByCondition(sublineItemName, itemId);
	}
}