package clb.core.prc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.utils.CommonUtil;
import clb.core.prc.constants.Constants;
import clb.core.prc.dto.PrcItemScore;
import clb.core.prc.dto.PrcRadarLineDetail;
import clb.core.prc.mapper.PrcItemScoreMapper;
import clb.core.prc.service.IPrcItemScoreService;
import clb.core.prc.service.IPrcRadarLineDetailService;
import clb.core.production.dto.PrdAttribute;
import clb.core.production.dto.PrdItems;
import clb.core.production.mapper.PrdItemsMapper;
import clb.core.production.service.IPrdAttributeService;
import clb.core.production.service.IPrdItemsService;

@Service
@Transactional
public class PrcItemScoreServiceImpl extends BaseServiceImpl<PrcItemScore> implements IPrcItemScoreService{

	@Autowired
	private PrcItemScoreMapper prcItemScoreMapper;
	
	@Autowired
	private IPrcRadarLineDetailService prcRadarLineDetailService;
	
	@Autowired
	private IPrdAttributeService prdAttributeService;
	
	@Autowired
	private IPrdItemsService prdItemsService;
	
	@Autowired
	private PrdItemsMapper prdItemsMapper;
	
	
	@Override
	public List<Map<String, String>> queryColumns(Long attSetId) {
		return prcItemScoreMapper.queryColumns(attSetId);
	}


	@Override
	public List<Map<String,String>> selectItemScoreInfo(Long attSetId,IRequest requestCtx) {
		
		//查询维度的集合
		List<PrcItemScore> prcDimList = new ArrayList<PrcItemScore>();
		
		//查询产品的集合
	    List<PrcItemScore> prcItemList = new ArrayList<PrcItemScore>();
	    
	    //记录查询的行维度信息
	    List<PrcItemScore> prcDimLineList = new ArrayList<PrcItemScore>();
	    
	    //通过头id查询该行的行id
	    prcDimLineList = prcItemScoreMapper.queryRadarLine(attSetId);
	    
	    //遍历行信息
	    for (PrcItemScore prcDimLine : prcDimLineList) {
	    	//行记录维度添加到维度集合
	    	prcDimList.add(prcDimLine);
	    	//记录查询的行明细维度信息
		    List<PrcItemScore> prcDimLineDetailList = new ArrayList<PrcItemScore>();
		    //查询行明细集合
		    prcDimLineDetailList = prcItemScoreMapper.queryRadarLineDetail(prcDimLine.getRadarLineId());
		    //循环行名细，将行名细维度加入到维度集合中
		    for (PrcItemScore prcDimLineDetail : prcDimLineDetailList) {
		    	prcDimLineDetail.setCompDimName(prcDimLineDetail.getDetailName());
		    	prcDimList.add(prcDimLineDetail);
			}
		}
		
		//查询的所有产品名称
		prcItemList = prcItemScoreMapper.queryItems(attSetId);
		
		List<Map<String,String>> prcScoreItemList = new ArrayList<Map<String,String>>();
		
		//循环遍历所有名称
		for (PrcItemScore prcItemScore : prcDimList) {
			//遍历一行数据，用来存放返回前台的一行数据
			Map<String,String> scoreItemMap = new HashMap<String,String>();
			//将维度名称存放在map中
			scoreItemMap.put(Constants.DIM, prcItemScore.getCompDimName());
			//循环产品列信息
			for (PrcItemScore prcItem : prcItemList) {
				
				PrcItemScore prcItemScoreInfo = new PrcItemScore();
				
				//通过头id，维度，产品id查出唯一的分数
				prcItemScoreInfo = prcItemScoreMapper.selectItemScoreInfo(prcItemScore.getCompDimName().replace("-", ""),prcItem.getItemId(),attSetId);
				//判断是否为行名细，如果是行名细
				if(prcItemScore.getCompDimName().contains("----")){
					PrcRadarLineDetail prcRadarLineDetail = prcRadarLineDetailService.selectLineDetailInfo(attSetId, prcItemScore.getDetailName().substring(4,prcItemScore.getCompDimName().length()),prcItemScore.getDetailId());
					if("CPSX".equals(prcRadarLineDetail.getSourceType())){
						PrdAttribute prdAttribute =  prdAttributeService.selectAttrValue(attSetId, prcRadarLineDetail.getAttId());
						PrdItems prdItems = new PrdItems();
						prdItems.setItemId(prcItem.getItemId());
						scoreItemMap.put(prcItem.getItemIdConnectItem(),CommonUtil.getMethodValue(prdItemsService.selectByPrimaryKey(requestCtx, prdItems), prdAttribute.getFieldCode().toLowerCase()));
					}
					//如果是固定值类型 
					if("GDZ".equals(prcRadarLineDetail.getSourceType())){
						if(prcItemScoreInfo != null){
							scoreItemMap.put(prcItem.getItemIdConnectItem(), prcItemScoreInfo.getScore().toString());
							scoreItemMap.put("TYPE", "GDZ");
						}
						else{
							scoreItemMap.put(prcItem.getItemIdConnectItem(), null);
							scoreItemMap.put("TYPE", "GDZ");
						}
					}
					//如果是sql类型
					if("SQL".equals(prcRadarLineDetail.getSourceType())){
						scoreItemMap.put(prcItem.getItemIdConnectItem(), null);
					}
				}else{
					if(prcItemScoreInfo != null){
						scoreItemMap.put(prcItem.getItemIdConnectItem(), prcItemScoreInfo.getScore().toString());
					}
					else{
						scoreItemMap.put(prcItem.getItemIdConnectItem(), null);
					}
				}
				
			}
			//添加返回的行map信息到集合
			prcScoreItemList.add(scoreItemMap);
		}
		
		return prcScoreItemList;
	}

	
	@Override
	public void updateScoreItem(Map<String, String> map, Long attSetId, IRequest request ) {

		//定义一个对象，用来存放行或者行明细维度信息
		PrcItemScore prcDimInfo = new PrcItemScore();
		
		List<PrcItemScore> prcItemScoreList = new ArrayList<PrcItemScore>();
		
		//通过维度名称查行维度
		prcDimInfo = prcItemScoreMapper.queryRadarLineByName(map.get(Constants.DIM).replace("-", ""),attSetId);
		//如果行维度为空，查询行名细维度
		if(prcDimInfo == null){
			prcDimInfo = prcItemScoreMapper.queryRadarLineDetailByName(map.get(Constants.DIM).replace("-", ""));
		}
		
		//查询产品的集合
	    List<PrcItemScore> prcItemList = new ArrayList<PrcItemScore>();
		
		//查询的所有产品名称
	    prcItemList = prcItemScoreMapper.queryItems(attSetId);
	    
	    //循环查询的item信息
	    for (PrcItemScore prcItem : prcItemList) {
	    	
	    	//创建一个产品分数对象
	    	PrcItemScore prcItemScore =  new PrcItemScore();
	    	
	    	//通过map中的数据获取对应itemId的分数信息
	    	if(map.get(prcItem.getItemIdConnectItem()) != null && map.get(prcItem.getItemIdConnectItem()) != ""){
	    		prcItemScore.setScore(map.get(prcItem.getItemIdConnectItem()));
		    	prcItemScore.setAttSetId(attSetId);
		    	//给itemId设值
		    	prcItemScore.setItemId(prcItem.getItemId());
		    	
		    	//给行ID或者行名细ID字段赋值
		    	if(prcDimInfo.getCompDimName() == null || "".equals(prcDimInfo.getCompDimName())){
		    		prcItemScore.setRadarLineDetailId(prcDimInfo.getRadarLineDetailId());
		    	}else{
		    		prcItemScore.setRadarLineId(prcDimInfo.getRadarLineId());
		    	}
		    	prcItemScoreList.add(prcItemScore);
	    	}
		}
	    
	    //批量更新
	    for (PrcItemScore prcItemScore : prcItemScoreList) {
	    	if(prcItemScoreMapper.selectByCondition(prcItemScore) == null){
	    		prcItemScoreMapper.insertItemScore(prcItemScore);
	    	}else{
	    		prcItemScore.setId(prcItemScoreMapper.selectByCondition(prcItemScore).getId());
	    		prcItemScoreMapper.updateItemScore(prcItemScore);
	    	}
		}
	}


}