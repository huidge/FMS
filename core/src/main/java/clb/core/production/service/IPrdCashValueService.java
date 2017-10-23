package clb.core.production.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.dto.ImportResponse;
import clb.core.production.dto.PrdCashValue;
/*****
 * @author tiansheng.ye
 * @Date 2017/05/16
 */
public interface IPrdCashValueService extends IBaseService<PrdCashValue>,ProxySelf<IPrdCashValueService>{

	public List<PrdCashValue> selectAllFields(IRequest requestContext, PrdCashValue prdCashValue, int page, int pageSize);

	public List<PrdCashValue> selectAllFields(IRequest requestContext, PrdCashValue prdCashValue);
	
	public List<ImportResponse> verificationData(IRequest request,List<List<String>> dataList,List<PrdCashValue> beanList,Long itemId);
	
	public void saveBatch(IRequest request,List<PrdCashValue> beanList);
}
