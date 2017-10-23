package clb.core.production.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.production.dto.PrdDiscount;
/**
 * Created by wanjun.feng on 17/4/18.
 */
public abstract interface IPrdDiscountService extends IBaseService<PrdDiscount>, ProxySelf<IPrdDiscountService> {
    List<PrdDiscount> query(IRequest iRequest, PrdDiscount prdDiscount, int page, int pagesize);
    
    List<PrdDiscount> batchUpdate(IRequest request, List<PrdDiscount> prdDiscountList);
}