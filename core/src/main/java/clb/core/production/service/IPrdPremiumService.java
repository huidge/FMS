package clb.core.production.service;

import java.math.BigDecimal;
import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.dto.ImportResponse;
import clb.core.production.dto.PrdPremium;
/*****
 * @author tiansheng.ye
 * @Date 2017/05/16
 */
public interface IPrdPremiumService extends IBaseService<PrdPremium>,ProxySelf<IPrdPremiumService>{

	public List<PrdPremium> selectAllFields(IRequest requestContext, PrdPremium prdPremium, int page, int pageSize);
	
	public List<ImportResponse> verificationData(IRequest request,List<List<String>> dataList,List<PrdPremium> beanList,Long itemId);
	
	public void saveBatch(IRequest request,List<PrdPremium> beanList,Long itemId);
	
	/**
	 * 电子计划书查询金额
	 * @param request
	 * @param prdPremium
	 */
	public Double queryAmount(IRequest request,PrdPremium prdPremium);
}
