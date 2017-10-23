package clb.core.pln.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import clb.core.pln.dto.PlnPlanRequest;
import clb.core.pln.mapper.PlnPlanRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.utils.GetFoldFileNames;
import clb.core.pln.dto.PlnPlanLibrary;
import clb.core.pln.mapper.PlnPlanLibraryMapper;
import clb.core.pln.service.IPlnPlanLibraryService;
import clb.core.production.dto.PrdItems;
import clb.core.production.service.IPrdItemSublineService;
import clb.core.production.service.IPrdItemsService;

@Service
@Transactional
public class PlnPlanLibraryServiceImpl extends BaseServiceImpl<PlnPlanLibrary> implements IPlnPlanLibraryService{

	@Autowired
	private PlnPlanLibraryMapper plnPlanLibraryMapper;

	@Autowired
    private IPrdItemsService prdItemsService;

    @Autowired
    private IPrdItemSublineService prdItemSublineService;

    @Autowired
    private PlnPlanRequestMapper plnPlanRequestMapper;

    @Override
    public List<PlnPlanLibrary> selectLibraryInfo(IRequest request, PlnPlanLibrary plnPlanLibrary, int page,
                                                  int pageSize) {
        PageHelper.startPage(page, pageSize);
        return plnPlanLibraryMapper.selectLibraryInfo(plnPlanLibrary);
    }

	@Override
	public PlnPlanLibrary selectLibraryByCondition(PlnPlanLibrary plnPlanLibrary) {
		return plnPlanLibraryMapper.selectLibraryByCondition(plnPlanLibrary);
	}

	@Override
	public List<PlnPlanLibrary> selectListByCodeAndItem(String code, Long itemId) {
		return plnPlanLibraryMapper.selectListByCodeAndItem(code, itemId);
	}

	@Override
	public List<PlnPlanLibrary> queryPaymethodByItem(IRequest requestCtx, PlnPlanLibrary dto) {
		PrdItems prdItems = new PrdItems();
    	prdItems.setItemId(dto.getItemId());
    	prdItems = prdItemsService.selectByPrimaryKey(requestCtx, prdItems);
    	if(prdItems==null){
    		return new ArrayList<PlnPlanLibrary>();
    	}
    	List<String> payMethodList = new ArrayList<String>();
		if("Y".equals(prdItems.getFullyear())){
			payMethodList.add("WP");
		}
		if("Y".equals(prdItems.getOneyear())){
			payMethodList.add("AP");
		}
		if("Y".equals(prdItems.getHalfyear())){
			payMethodList.add("SAP");
		}
		if("Y".equals(prdItems.getQuarter())){
			payMethodList.add("QP");
		}
		if("Y".equals(prdItems.getOnemonth())){
			payMethodList.add("MP");
		}
		dto.setPayMethodList(payMethodList);
    	return plnPlanLibraryMapper.queryPaymethodByItem(dto);
	}

	@Override
	public List<PlnPlanLibrary> querySecurityLevelByItem(Long itemId) {
		return plnPlanLibraryMapper.querySecurityLevelByItem(itemId);
	}

	@Override
	public List<PlnPlanLibrary> querySecurityAreaByItem(Long itemId,String securityLevel) {
		List<PlnPlanLibrary> list = plnPlanLibraryMapper.querySecurityAreaByItem(itemId,securityLevel);
		//若查出结果为null则返回""
		for (PlnPlanLibrary plnPlanLibrary : list) {
			if (plnPlanLibrary.getMeaning()==null) {
				plnPlanLibrary.setMeaning("");
			}
			if (plnPlanLibrary.getValue()==null) {
				plnPlanLibrary.setValue("");
			}
		}
		return list;
	}

	@Override
	public List<PlnPlanLibrary> querySublineItemNameByItem(Long itemId) {
		return plnPlanLibraryMapper.querySublineItemNameByItem(itemId);
	}

	@Override
	public List<PlnPlanLibrary> querySelfpayByItem(Long itemId) {
		return plnPlanLibraryMapper.querySelfpayByItem(itemId);
	}

	@Override
	public void importPlanInfo(IRequest requestCtx, PlnPlanLibrary dto) {
		List<Map<String,String>> getFileNameType = new ArrayList<Map<String,String>>();
		List<Map<String,String>> getFileNameTypeOneList = new ArrayList<Map<String,String>>();
		List<Map<String,String>> getFileNameTypeTwoList = new ArrayList<Map<String,String>>();
		List<Map<String,String>> getFileNameTypeThreeList = new ArrayList<Map<String,String>>();
		//用来存放需要插入的所有信息
	    List<PlnPlanLibrary> plnPlanLibraryList = new ArrayList<PlnPlanLibrary>();
		//获取文件夹1的内容
		getFileNameTypeOneList = GetFoldFileNames.getFileNameTypeOne();
		//获取文件夹2的内容
		getFileNameTypeTwoList = GetFoldFileNames.getFileNameTypeTwo();
		//获取文件夹3的内容
		getFileNameTypeThreeList = GetFoldFileNames.getFileNameTypeThree();
		getFileNameType.addAll(getFileNameTypeOneList);
		getFileNameType.addAll(getFileNameTypeTwoList);
		getFileNameType.addAll(getFileNameTypeThreeList);
		//循环所有文件信息
        for (Map<String, String> map : getFileNameType) {
			//需要插入的单个计划书库
			PlnPlanLibrary plnPlanLibrary = new PlnPlanLibrary();
			for (Map.Entry<String,String> mapInfo : map.entrySet()) {
				plnPlanLibrary.setCurrency("USD");
				plnPlanLibrary.setNationality("China");
				plnPlanLibrary.setResidence("China");
				//产品名称
				if(mapInfo.getKey().equals("itemName")){
					Long itemId = prdItemsService.selectItemIdByItemName(mapInfo.getValue());
					plnPlanLibrary.setItemId(itemId);
				}
				//年期名称
				if(mapInfo.getKey().equals("sublineName")){
					Long itemId = prdItemsService.selectItemIdByItemName(map.get("itemName"));
					Long sublineId = prdItemSublineService.selectByCondition(mapInfo.getValue(), itemId);
					plnPlanLibrary.setSublineId(sublineId);
				}
				//性别
                if(mapInfo.getKey().equals("gender")){
                	plnPlanLibrary.setGender(mapInfo.getValue());
				}
                //年龄
                if(mapInfo.getKey().equals("age")){
                	plnPlanLibrary.setAge(Long.valueOf(mapInfo.getValue()));
				}
                //是否吸烟
                if(mapInfo.getKey().equals("smokeFlag")){
                	plnPlanLibrary.setSmokeFlag(mapInfo.getValue());
				}
                //保费
                if(mapInfo.getKey().equals("premium")){
                	plnPlanLibrary.setPremium(new BigDecimal(mapInfo.getValue()));
				}
                //保额
                if(mapInfo.getKey().equals("amount")){
                	plnPlanLibrary.setAmount(new BigDecimal(mapInfo.getValue()));
				}
                //缴费方式
                if(mapInfo.getKey().equals("payMethod")){
                	if("年缴".equals(mapInfo.getValue())){
                		plnPlanLibrary.setPayMethod("AP");
            		}
                	if("月缴".equals(mapInfo.getValue())){
                		plnPlanLibrary.setPayMethod("MP");
            		}
                	if("季缴".equals(mapInfo.getValue())){
                		plnPlanLibrary.setPayMethod("QP");
            		}
                	if("半年缴".equals(mapInfo.getValue())){
                		plnPlanLibrary.setPayMethod("SAP");
            		}
                	if("预缴".equals(mapInfo.getValue())){
                		plnPlanLibrary.setPayMethod("FJ");
            		}
                	if("趸缴".equals(mapInfo.getValue())){
                		plnPlanLibrary.setPayMethod("WP");
            		}
				}
			}
			plnPlanLibraryList.add(plnPlanLibrary);
		}

        for (PlnPlanLibrary plnPlanLibrary2 : plnPlanLibraryList) {
        	plnPlanLibrary2.set__status("add");
		}

        self().batchUpdate(requestCtx, plnPlanLibraryList);
	}

    @Override
    public List<PlnPlanLibrary> queryChannelPlanQuota(PlnPlanLibrary plnPlanLibrary) {
        return plnPlanLibraryMapper.queryChannelPlanQuota(plnPlanLibrary);
    }

    /**
     * 带参查询我的计划书数量
     *
     * @param plnPlanLibrary
     * @param requestContext
     * @return
     */
    @Override
    public Integer queryMyPlanCount(PlnPlanLibrary plnPlanLibrary, IRequest requestContext) {
        return plnPlanLibraryMapper.getMyPlanCount(plnPlanLibrary);
    }

	@Override
	public PlnPlanLibrary selectLibraryByConditionForBack(PlnPlanLibrary plnPlanLibrary) {
		return plnPlanLibraryMapper.selectLibraryByConditionForBack(plnPlanLibrary);
	}

	@Override
	public List<PlnPlanLibrary> selectLibraryInfoForPrd(IRequest request, PlnPlanLibrary plnPlanLibrary, int page,
			int pageSize) {
		return plnPlanLibraryMapper.selectLibraryInfoForPrd(plnPlanLibrary);
	}
}