package clb.core.production.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.order.dto.MenuAuth;
import clb.core.production.dto.PrdClass;
/**
 * Created by wanjun.feng on 17/4/12.
 */
public abstract interface IPrdClassService extends IBaseService<PrdClass>, ProxySelf<IPrdClassService> {
    List<PrdClass> query(IRequest iRequest, PrdClass prdClass, int page, int pagesize);
    
    List<PrdClass> batchUpdate(IRequest request, List<PrdClass> prdClassList);
}