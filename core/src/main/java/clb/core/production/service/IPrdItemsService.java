package clb.core.production.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;

import clb.core.commission.dto.CmnCalc;
import clb.core.common.dto.ImportResponse;
import clb.core.production.dto.PrdImageText;
import clb.core.production.dto.PrdItemPaymode;
import clb.core.production.dto.PrdItemSecurityPlan;
import clb.core.production.dto.PrdItemSelfpay;
import clb.core.production.dto.PrdItemSubline;
import clb.core.production.dto.PrdItems;

/**
 * Created by Rex.Hua on 17/4/16.
 */
public abstract interface IPrdItemsService extends IBaseService<PrdItems>,ProxySelf<IPrdItemsService> {
    List<PrdItems> query(PrdItems prdItems);
    
    List<PrdItems> batchUpdate(IRequest request, List<PrdItems> prdItemsList);

    List<PrdItems> selectAlive(PrdItems prdItems,int page, int pagesize);
    
	List<PrdItems> queryitemBySupplierId(IRequest requestContext, PrdItems dto);

    List<PrdItems> wsSelectHeaderAlive(PrdItems prdItems, int page, int pagesize);

    List<PrdItems> wsSelectAlive(PrdItems prdItems,int page, int pagesize);

    List<PrdImageText> getImageText(PrdImageText dto, int page, int pagesize);
    
    ResponseData prdCompare(IRequest requestContext,List<CmnCalc> dto);
    /*****
     * 根据预期年龄，动态算现金价值
     * @param requestContext
     * @param dtoList
     * @return
     */
    public ResponseData queryCashValueByAge(IRequest requestContext, List<CmnCalc> dtoList);
    
    ResponseData prdRadarChart(IRequest requestContext,List<CmnCalc> dto);
    /**
     * 查询产品的大中小分类
     * @param requestContext
     * @param dto
     * @return
     */
	List<PrdItems> queryitemClass(IRequest requestContext, PrdItems dto);

    List<PrdItems> selectByChannel(IRequest requestContext, PrdItems dto);
    //校验导入数据
    public List<ImportResponse> verificationItems(IRequest request,List<List<String>> dataList,List<PrdItems> beanList);
    
    public List<ImportResponse> verificationPaymode(IRequest request,List<List<String>> dataList,List<PrdItems> itemList,List<PrdItemPaymode> beanList);
    
    public List<ImportResponse> verificationSubline(IRequest request,List<List<String>> dataList,List<PrdItems> itemList,List<PrdItemSubline> beanList);
    
    public List<ImportResponse> verificationPlan(IRequest request,List<List<String>> dataList,List<PrdItems> itemList,List<PrdItemSecurityPlan> beanList);
    
    public List<ImportResponse> verificationSelfpay(IRequest request,List<List<String>> dataList,List<PrdItems> itemList,List<PrdItemSelfpay> beanList);
	//导入
	public void importInitialData(IRequest request,List<PrdItems> beanList,List<PrdItemPaymode> modeList,List<PrdItemSubline> SublineList,List<PrdItemSecurityPlan> planList,List<PrdItemSelfpay> selfList);
	
	Long selectItemIdByItemName(String itemName);

    String selectSupplierAgeTypeByItem(Long itemId);
}
