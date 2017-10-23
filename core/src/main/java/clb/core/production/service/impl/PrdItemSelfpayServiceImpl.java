package clb.core.production.service.impl;

import clb.core.production.dto.PrdItemSecurityPlan;
import clb.core.production.dto.PrdItemSelfpay;
import clb.core.production.mapper.PrdItemSelfpayMapper;
import clb.core.production.service.IPrdItemSelfpayService;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PrdItemSelfpayServiceImpl extends BaseServiceImpl<PrdItemSelfpay> implements IPrdItemSelfpayService {

    @Autowired
    private PrdItemSelfpayMapper prdItemPaymodeMapper;

    @Override
    public List<PrdItemSelfpay> query(IRequest iRequest, PrdItemSelfpay dto, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return prdItemPaymodeMapper.select(dto);
    }

    @Override
    public List<PrdItemSelfpay> batchUpdate(IRequest request, List<PrdItemSelfpay> dtoList) {
        for (PrdItemSelfpay dto : dtoList) {
            if (dto.getSelfpayId()==null || dto.getSelfpayId().equals("")) {
                prdItemPaymodeMapper.insertSelective(dto);
            } else {
                prdItemPaymodeMapper.updateByPrimaryKeySelective(dto);
            }
        }
        return dtoList;
    }

	@Override
	public List<PrdItemSelfpay> selectByItemId(Long itemId) {
		return prdItemPaymodeMapper.selectByItemId(itemId);
	}

    @Override
    public List<PrdItemSelfpay> querySelfpay(IRequest requestContext, PrdItemSelfpay querySelfpay) {
        return prdItemPaymodeMapper.querySelfpay(querySelfpay);
    }

	@Override
	public Long querySelfpayId(String selfpay, Long itemId) {
		return prdItemPaymodeMapper.querySelfpayId(selfpay, itemId);
	}
}