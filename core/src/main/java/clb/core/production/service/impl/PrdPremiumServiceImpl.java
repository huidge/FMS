package clb.core.production.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.mapper.CodeValueMapper;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.dto.ImportResponse;
import clb.core.production.dto.PrdItemPaymode;
import clb.core.production.dto.PrdItemSecurityPlan;
import clb.core.production.dto.PrdItemSelfpay;
import clb.core.production.dto.PrdItemSubline;
import clb.core.production.dto.PrdItems;
import clb.core.production.dto.PrdPremium;
import clb.core.production.mapper.PrdItemPaymodeMapper;
import clb.core.production.mapper.PrdItemSecurityPlanMapper;
import clb.core.production.mapper.PrdItemSelfpayMapper;
import clb.core.production.mapper.PrdItemSublineMapper;
import clb.core.production.mapper.PrdItemsMapper;
import clb.core.production.mapper.PrdPremiumMapper;
import clb.core.production.service.IPrdItemsService;
import clb.core.production.service.IPrdPremiumService;
import clb.core.system.service.IClbCodeService;
/*****
 * @author tiansheng.ye
 * @Date 2017/05/16
 */
@Service
@Transactional
public class PrdPremiumServiceImpl extends BaseServiceImpl<PrdPremium> implements IPrdPremiumService{
	private static String WARN="警告";
	private static String ERROR="错误";
	
	@Autowired
    private PrdPremiumMapper prdPremiumMapper;
	@Autowired
	private IPrdItemsService prdItemsService;
	@Autowired
	private PrdItemPaymodeMapper prdItemPaymodeMapper;
	@Autowired
	private PrdItemSublineMapper prdItemSublineMapper;
	@Autowired
	private CodeValueMapper codeValueMapper;
	@Autowired
	private PrdItemSecurityPlanMapper prdItemSecurityPlanMapper;
	@Autowired
	private PrdItemSelfpayMapper prdItemSelfpayMapper;
	@Autowired
    private IClbCodeService clbCodeService;
	@Autowired
    private PrdItemsMapper prdItemsMapper;
	
	@Override
	public List<PrdPremium> selectAllFields(IRequest requestContext, PrdPremium prdPremium, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return prdPremiumMapper.selectAllFields(prdPremium);
	}
	
	public void updateOrInsert(IRequest request,PrdPremium prdPremium){
		//查询 是否存在该数据
		List<PrdPremium>list=this.selectPrdPremiumByParam(prdPremium);
		if(CollectionUtils.isEmpty(list)){
			prdPremiumMapper.insertSelective(prdPremium);
		}else{
			for(PrdPremium prd:list){
				prdPremium.setPremiumId(prd.getPremiumId());
				self().updateByPrimaryKey(request, prdPremium);
			}
		}
		
	}
	/*****
	 * 根据(产品),(币种), (是否吸烟),(性别),(年龄),(年期) 查询 数据
	 * 修订版： 根据(产品),(币种), (是否吸烟),(性别),(年期)，（国籍类别），（保障级别），（保障地区），（自付选项）
	 * @param prdCash
	 * @return
	 */
	public List<PrdPremium> selectPrdPremiumByParam(PrdPremium prdPremium){
		PrdPremium prdPremiumNew=new PrdPremium();
		prdPremiumNew.setItemId(prdPremium.getItemId());
		prdPremiumNew.setCurrency(prdPremium.getCurrency());
		prdPremiumNew.setSmokeFlag(prdPremium.getSmokeFlag());
		prdPremiumNew.setGender(prdPremium.getGender());
		prdPremiumNew.setSublineId(prdPremium.getSublineId());
		prdPremiumNew.setNationalityClass(prdPremium.getNationalityClass());
		prdPremiumNew.setPlanId(prdPremium.getPlanId());
		prdPremiumNew.setSelfpayId(prdPremium.getSelfpayId());
		List<PrdPremium>list=prdPremiumMapper.select(prdPremiumNew);
		return list;
	}
	/******
	 * 修订版：(产品),(币种), (是否吸烟),(性别),(年期)，（国籍类别），（保障级别），（保障地区），（自付选项）
	 * @param num
	 * @param list
	 * @param dataList
	 * @return
	 */
	public boolean hasExist(int num,List<String>list,List<List<String>> dataList){
		boolean hasExist=false;
		for(int i=1;i<dataList.size() && !hasExist;i++){
			if(num==i){
				continue;
			}
			hasExist=true;
			for(int j=0;j<9;j++){
				if(!list.get(j).equals(dataList.get(i).get(j))){
					hasExist=false;
					break;
				}
			}
		}
		return hasExist;
	}

	@Override
	public void saveBatch(IRequest request, List<PrdPremium> beanList,Long itemId) {
		for(PrdPremium prdPremium:beanList){
			this.updateOrInsert(request,prdPremium);
		}
	}

	@Override
	public List<ImportResponse> verificationData(IRequest request, List<List<String>> dataList,
			List<PrdPremium> beanList,Long itemId) {
		List<ImportResponse> responseList= convertToBean(request, dataList, beanList,itemId);
		return responseList;
	}

	public List<ImportResponse> convertToBean(IRequest request, List<List<String>> dataList,List<PrdPremium> beanList,Long itemId){
		List<ImportResponse> listResPonse=new ArrayList<ImportResponse>();
		ImportResponse response=new ImportResponse();
		Integer lineNum=1;
		List<String> title=dataList.get(0);
		for(int i=1;i<dataList.size();i++){
			lineNum++;
			PrdPremium prdPremium=new PrdPremium();
			//产品小类
			String minClass="";
			String minClassName="";
			//是否有错误
			boolean hasError=false;
			if(StringUtils.isNotBlank(dataList.get(i).get(0))){
				PrdItems prdItems=new PrdItems();
				prdItems.setItemName(dataList.get(i).get(0));
				List<PrdItems> items=prdItemsMapper.selectByItemName(prdItems);
				if(CollectionUtils.isEmpty(items)){
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(0));
					response.setMessage("产品名称："+dataList.get(i).get(0)+" 在系统中不存在");
					listResPonse.add(response);
					hasError=true;
				}else{
					if(!(itemId+"").equals(items.get(0).getItemId()+"")){
						response=new ImportResponse();
						response.setLineId(lineNum);
						response.setType(ERROR);
						response.setFieldName(title.get(0));
						response.setMessage("产品名称："+dataList.get(i).get(0)+" 与现在维护的产品名称不匹配");
						listResPonse.add(response);
						hasError=true;
						continue;
					}
					prdPremium.setItemId(items.get(0).getItemId());
					minClass=items.get(0).getMinClass();
					minClassName=items.get(0).getMinClassName();
				}
			}else{
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(ERROR);
				response.setFieldName(title.get(0));
				response.setMessage("产品名称："+dataList.get(i).get(0)+" 在系统中不存在");
				listResPonse.add(response);
				hasError=true;
				continue;
			}
			
			if(StringUtils.isNotBlank(dataList.get(i).get(1))){
				List<CodeValue> codeList=codeValueMapper.selectCodeValuesByCodeName("PUB.CURRENCY");
				String valueCode=null;
				for(CodeValue code:codeList){
					if(code.getMeaning().equals(dataList.get(i).get(1))){
						valueCode=code.getValue();
						break;
					}
				}
				if(valueCode==null){
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(1));
					response.setMessage("该产品下不存在 币种："+dataList.get(i).get(1));
					listResPonse.add(response);
					hasError=true;
				}else{
					PrdItemPaymode prdItemPaymode=new PrdItemPaymode();
					prdItemPaymode.setItemId(prdPremium.getItemId());
					prdItemPaymode.setCurrencyCode(valueCode);
					List<PrdItemPaymode> prdItemPaymodes=prdItemPaymodeMapper.select(prdItemPaymode);
					if(CollectionUtils.isEmpty(prdItemPaymodes)){
						response=new ImportResponse();
						response.setLineId(lineNum);
						response.setType(ERROR);
						response.setFieldName(title.get(1));
						response.setMessage("该产品下不存在 币种："+dataList.get(i).get(1));
						listResPonse.add(response);
						hasError=true;
					}else{
						prdPremium.setCurrency(prdItemPaymodes.get(0).getCurrencyCode());
					}
				}
			}else{
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(ERROR);
				response.setFieldName(title.get(1));
				response.setMessage("该产品下不存在 币种："+dataList.get(i).get(1));
				listResPonse.add(response);
				hasError=true;
			}
			
			if(StringUtils.isNotBlank(dataList.get(i).get(2))){
				if(dataList.get(i).get(2).equals("是")){
					prdPremium.setSmokeFlag("Y");
				}else if(dataList.get(i).get(2).equals("否")){
					prdPremium.setSmokeFlag("N");
				}else{
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(2));
					response.setMessage("是否吸烟-格式不正确："+dataList.get(i).get(2));
					listResPonse.add(response);
					hasError=true;
				}
			}
			
			if(StringUtils.isNotBlank(dataList.get(i).get(3))){
				if(dataList.get(i).get(3).equals("男")){
					prdPremium.setGender("M");
				}else if(dataList.get(i).get(3).equals("女")){
					prdPremium.setGender("F");
				}else{
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(3));
					response.setMessage("性别-格式不正确："+dataList.get(i).get(3));
					listResPonse.add(response);
					hasError=true;
				}
			}
			
			if(StringUtils.isNotBlank(dataList.get(i).get(4))){
				String value=clbCodeService.getCodeValueByMeaning(request, "PUB.NATION", dataList.get(i).get(4));
				if(StringUtils.isBlank(value)){
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(4));
					response.setMessage("国籍类别 快码中不存在："+dataList.get(i).get(4));
					listResPonse.add(response);
					hasError=true;
				}else{
					prdPremium.setNationalityClass(value);
				}
			}else{
				/*response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(WARN);
				response.setFieldName(title.get(4));
				response.setMessage("国籍类别为空");
				listResPonse.add(response);*/
			}
			
			if(StringUtils.isNotBlank(dataList.get(i).get(5))){
				PrdItemSubline prdItemSubline=new PrdItemSubline();
				prdItemSubline.setItemId(prdPremium.getItemId());
				prdItemSubline.setSublineItemName(dataList.get(i).get(5));
				List<PrdItemSubline> lineList=prdItemSublineMapper.select(prdItemSubline);
				if(CollectionUtils.isEmpty(lineList)){
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(5));
					response.setMessage("该产品下不存在 年期："+dataList.get(i).get(5));
					listResPonse.add(response);
					hasError=true;
				}else{
					prdPremium.setSublineId(lineList.get(0).getSublineId());
				}
			}else{
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(ERROR);
				response.setFieldName(title.get(5));
				response.setMessage("该产品下不存在 年期："+dataList.get(i).get(5));
				listResPonse.add(response);
				hasError=true;
			}
			
			//保障计划
			List<PrdItemSecurityPlan> planList=new ArrayList<PrdItemSecurityPlan>();
			if(StringUtils.isNotBlank(dataList.get(i).get(6))){
				PrdItemSecurityPlan prdItemSecurityPlan=new PrdItemSecurityPlan();
				prdItemSecurityPlan.setItemId(prdPremium.getItemId());
				prdItemSecurityPlan.setSecurityLevel(dataList.get(i).get(6));
				//保障地区
				if(StringUtils.isNoneBlank(dataList.get(i).get(7))){
					prdItemSecurityPlan.setSecurityRegion(dataList.get(i).get(7));
				}else{
					if(minClass.equals("GD")){
						response=new ImportResponse();
						response.setLineId(lineNum);
						response.setType(ERROR);
						response.setFieldName(title.get(7));
						response.setMessage("该产品为"+minClassName+","+title.get(7)+"不能为空");
						listResPonse.add(response);
						hasError=true;
					}
				}
				planList=prdItemSecurityPlanMapper.select(prdItemSecurityPlan);
				if(CollectionUtils.isEmpty(planList)){
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(6)+","+title.get(7));
					response.setMessage("该产品下不存在 " +title.get(6)+"："+dataList.get(i).get(6)+"-" +title.get(7)+"："+dataList.get(i).get(7));
					listResPonse.add(response);
					hasError=true;
				}else{
					prdPremium.setPlanId(planList.get(0).getPlanId());
				}
			}else{
				if(minClass.equals("GD")){
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(6));
					response.setMessage("该产品为"+minClassName+","+title.get(6)+"不能为空");
					listResPonse.add(response);
					hasError=true;
				}
			}
			
			
			if(StringUtils.isNotBlank(dataList.get(i).get(8))){
				PrdItemSelfpay prdItemSelfpay=new PrdItemSelfpay();
				prdItemSelfpay.setItemId(prdPremium.getItemId());
				prdItemSelfpay.setSelfpay(dataList.get(i).get(8));
				List<PrdItemSelfpay>payList=prdItemSelfpayMapper.select(prdItemSelfpay);
				if(CollectionUtils.isEmpty(payList)){
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(8));
					response.setMessage("该产品下不存在 " +title.get(8)+"："+dataList.get(i).get(8));
					listResPonse.add(response);
					hasError=true;
				}else{
					prdPremium.setSelfpayId(payList.get(0).getSelfpayId());
				}
			}else{
				if(minClass.equals("GD")){
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(8));
					response.setMessage("该产品为"+minClassName+","+title.get(8)+"不能为空");
					listResPonse.add(response);
					hasError=true;
				}
			}
			
			int mun=9;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium0(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium1(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium2(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium3(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium4(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium5(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium6(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium7(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium8(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium9(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium10(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium11(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium12(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium13(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium14(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium15(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium16(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium17(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium18(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium19(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium20(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium21(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium22(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium23(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium24(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium25(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium26(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium27(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium28(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium29(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium30(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium31(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium32(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium33(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium34(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium35(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium36(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium37(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium38(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium39(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium40(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium41(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium42(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium43(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium44(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium45(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium46(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium47(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium48(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium49(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium50(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium51(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium52(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium53(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium54(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium55(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium56(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium57(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium58(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium59(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium60(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium61(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium62(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium63(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium64(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium65(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium66(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium67(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium68(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium69(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium70(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium71(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium72(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium73(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium74(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium75(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium76(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium77(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium78(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium79(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium80(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium81(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium82(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium83(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium84(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium85(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium86(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium87(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium88(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium89(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium90(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium91(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium92(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium93(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium94(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium95(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium96(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium97(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium98(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium99(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			mun++;
			if(StringUtils.isNotBlank(dataList.get(i).get(mun))){
				try {
					prdPremium.setPremium100(Double.valueOf(dataList.get(i).get(mun)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(mun));
					response.setMessage(title.get(mun)+"-格式不正确："+dataList.get(i).get(mun));
					listResPonse.add(response);
				}
			}
			
			//文件中是否重复数据
			if(hasExist(i, dataList.get(i), dataList)){
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(ERROR);
				response.setMessage("文件中存在数据重复：("+dataList.get(i).get(0)+"),("+dataList.get(i).get(1)+"),("+dataList.get(i).get(2)+")"
						+ ", ("+dataList.get(i).get(3)+"), ("+dataList.get(i).get(4)+") "+ ", ("+dataList.get(i).get(5)+"), ("+dataList.get(i).get(6)+ "), ("+dataList.get(i).get(7)+ "), ("+dataList.get(i).get(8)+")");
				listResPonse.add(response);
				hasError=true;
			}
			
			//查询 是否存在该数据 当前面无错误数据时才查询
			List<PrdPremium>list=this.selectPrdPremiumByParam(prdPremium);
			if(!CollectionUtils.isEmpty(list) && !hasError){
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(WARN);
				response.setMessage("数据在系统中已存在：("+dataList.get(i).get(0)+"),("+dataList.get(i).get(1)+"),("+dataList.get(i).get(2)+")"
						+ ", ("+dataList.get(i).get(3)+"), ("+dataList.get(i).get(4)+") "+ ", ("+dataList.get(i).get(5)+"), ("+dataList.get(i).get(6)+ "), ("+dataList.get(i).get(7)+ "), ("+dataList.get(i).get(8)+")");
				listResPonse.add(response);
			}
			
			beanList.add(prdPremium);
		}
		return listResPonse;
	}
	/**
	 * 电子计划书查询金额
	 */
	@Override
	public Double queryAmount(IRequest request, PrdPremium prdPremium) {
		return prdPremiumMapper.queryAmount(prdPremium);
	}


}
