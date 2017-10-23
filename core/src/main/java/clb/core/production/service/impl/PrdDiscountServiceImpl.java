package clb.core.production.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.production.dto.PrdDiscount;
import clb.core.production.mapper.PrdDiscountMapper;
import clb.core.production.service.IPrdDiscountService;

/**
 * Created by wanjun.feng on 17/4/18.
 */
@Service
@Transactional
public class PrdDiscountServiceImpl extends BaseServiceImpl<PrdDiscount> implements IPrdDiscountService {
    
    @Autowired
    private PrdDiscountMapper prdDiscountMapper;
    
    @Override
    public List<PrdDiscount> query(IRequest iRequest, PrdDiscount prdDiscount, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return prdDiscountMapper.selectDiscounts(prdDiscount);
    }
}