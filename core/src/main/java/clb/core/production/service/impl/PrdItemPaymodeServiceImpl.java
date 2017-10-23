package clb.core.production.service.impl;

import clb.core.production.dto.PrdItemPaymode;
import clb.core.production.mapper.PrdItemPaymodeMapper;
import clb.core.production.service.IPrdItemPaymodeService;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PrdItemPaymodeServiceImpl extends BaseServiceImpl<PrdItemPaymode> implements IPrdItemPaymodeService {


    @Autowired
    private PrdItemPaymodeMapper prdItemPaymodeMapper;

    @Override
    public List<PrdItemPaymode> query(IRequest iRequest, PrdItemPaymode dto, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return prdItemPaymodeMapper.select(dto);
    }

    @Override
    public List<PrdItemPaymode> batchUpdate(IRequest request, List<PrdItemPaymode> dtoList) {
        for (PrdItemPaymode dto : dtoList) {
            if (dto.getPaymodeId()==null || dto.getPaymodeId().equals("")) {
                prdItemPaymodeMapper.insertSelective(dto);
            } else {
                prdItemPaymodeMapper.updateByPrimaryKeySelective(dto);
            }
        }
        return dtoList;
    }

	@Override
	public List<PrdItemPaymode> queryCurrencyByItemId(IRequest requestContext, PrdItemPaymode dto) {
		
		return prdItemPaymodeMapper.queryCurrencyByItemId(dto);
	}

    @Override
    public List<PrdItemPaymode> queryAllCurrency(IRequest requestContext, PrdItemPaymode dto) {

        return prdItemPaymodeMapper.queryAllCurrency(dto);
    }
}