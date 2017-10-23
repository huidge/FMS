package clb.core.pln.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.pln.dto.PlnPlanSpiderUserpass;

import java.util.List;

public interface IPlnPlanSpiderUserpassService extends IBaseService<PlnPlanSpiderUserpass>, ProxySelf<IPlnPlanSpiderUserpassService>{
    /**
     * CREATED BY GAN ON 2017/8/23
     * DESC: 用来查询所有保险公司爬虫账户密码
     */
    List<PlnPlanSpiderUserpass> findAll(IRequest request, PlnPlanSpiderUserpass plnPlanSpiderUserpass, int page, int pagesize);

}