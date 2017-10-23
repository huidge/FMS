package clb.core.production.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.production.dto.PrdHotRecommend;

public interface IPrdHotRecommendService extends IBaseService<PrdHotRecommend>, ProxySelf<IPrdHotRecommendService>{

	/**
	 * 通过类型查询
	 * @param requestCtx
	 * @param productType
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<PrdHotRecommend> selectInfoByType(IRequest requestCtx,String productType,int page,int pageSize);
}