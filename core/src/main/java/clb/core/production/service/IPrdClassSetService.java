package clb.core.production.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.production.dto.PrdClassSet;
/**
 * Created by wanjun.feng on 17/4/12.
 */
public abstract interface IPrdClassSetService extends IBaseService<PrdClassSet>, ProxySelf<IPrdClassSetService> {
    List<PrdClassSet> query(IRequest iRequest, PrdClassSet prdClassSet, int page, int pagesize);
    
    List<PrdClassSet> batchUpdate(IRequest request, List<PrdClassSet> classSetList);
}