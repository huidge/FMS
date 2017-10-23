package clb.core.production.service.impl;

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
import clb.core.production.dto.PrdCashValue;
import clb.core.production.dto.PrdItemPaymode;
import clb.core.production.dto.PrdItemSubline;
import clb.core.production.dto.PrdItems;
import clb.core.production.mapper.PrdCashValueMapper;
import clb.core.production.mapper.PrdItemPaymodeMapper;
import clb.core.production.mapper.PrdItemSublineMapper;
import clb.core.production.mapper.PrdItemsMapper;
import clb.core.production.service.IPrdCashValueService;
import clb.core.production.service.IPrdItemsService;
/*****
 * @author tiansheng.ye
 * @Date 2017/05/16
 */
@Service
@Transactional
public class PrdCashValueServiceImpl extends BaseServiceImpl<PrdCashValue> implements IPrdCashValueService{
	private static String WARN="警告";
	private static String ERROR="错误";
	@Autowired
    private PrdCashValueMapper prdCashValueMapper;
	@Autowired
	private IPrdItemsService prdItemsService;
	@Autowired
	private PrdItemPaymodeMapper prdItemPaymodeMapper;
	@Autowired
	private PrdItemSublineMapper prdItemSublineMapper;
	@Autowired
	private CodeValueMapper codeValueMapper;
	@Autowired
    private PrdItemsMapper prdItemsMapper;
	
	@Override
	public List<PrdCashValue> selectAllFields(IRequest requestContext, PrdCashValue prdCashValue, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return prdCashValueMapper.selectAllFields(prdCashValue);
	}

	@Override
	public List<PrdCashValue> selectAllFields(IRequest requestContext, PrdCashValue prdCashValue) {
		return prdCashValueMapper.selectAllFields(prdCashValue);
	}

	@Override
	public List<ImportResponse> verificationData(IRequest request, List<List<String>> dataList,List<PrdCashValue> beanList,Long itemId) {
		List<ImportResponse> responseList= convertToBean(request, dataList, beanList, itemId);
		return responseList;
	}
	
	public List<ImportResponse> convertToBean(IRequest request, List<List<String>> dataList,List<PrdCashValue> beanList,Long itemId){
		List<ImportResponse> listResPonse=new ArrayList<ImportResponse>();
		ImportResponse response=new ImportResponse();
		Integer lineNum=1;
		List<String> title=dataList.get(0);
		for(int i=1;i<dataList.size();i++){
			lineNum++;
			//是否有错误
			boolean hasError=false;
			PrdCashValue prdCash=new PrdCashValue();
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
					prdCash.setItemId(items.get(0).getItemId());
				}
			}else{
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(ERROR);
				response.setFieldName(title.get(0));
				response.setMessage("产品名称："+dataList.get(i).get(0)+" 在系统中不存在");
				listResPonse.add(response);
				hasError=true;
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
					prdItemPaymode.setItemId(prdCash.getItemId());
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
						prdCash.setCurrency(prdItemPaymodes.get(0).getCurrencyCode());
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
				try {
					prdCash.setInsuranceCoverage(Double.valueOf(dataList.get(i).get(2)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(2));
					response.setMessage("保额-格式不正确："+dataList.get(i).get(2));
					listResPonse.add(response);
					hasError=true;
				}
			}
			
			if(StringUtils.isNotBlank(dataList.get(i).get(3))){
				try {
					prdCash.setPremium(Double.valueOf(dataList.get(i).get(3)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(3));
					response.setMessage("保费-格式不正确："+dataList.get(i).get(3));
					listResPonse.add(response);
					hasError=true;
				}
			}
			
			if(StringUtils.isNotBlank(dataList.get(i).get(4))){
				PrdItemSubline prdItemSubline=new PrdItemSubline();
				prdItemSubline.setItemId(prdCash.getItemId());
				prdItemSubline.setSublineItemName(dataList.get(i).get(4));
				List<PrdItemSubline> lineList=prdItemSublineMapper.select(prdItemSubline);
				if(CollectionUtils.isEmpty(lineList)){
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(4));
					response.setMessage("该产品下不存在 年期："+dataList.get(i).get(4));
					listResPonse.add(response);
					hasError=true;
				}else{
					prdCash.setSublineId(lineList.get(0).getSublineId());
				}
			}else{
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(ERROR);
				response.setFieldName(title.get(4));
				response.setMessage("该产品下不存在 年期："+dataList.get(i).get(4));
				listResPonse.add(response);
				hasError=true;
			}
			
			if(StringUtils.isNotBlank(dataList.get(i).get(5))){
				if(dataList.get(i).get(5).equals("是")){
					prdCash.setSmokeFlag("Y");
				}else if(dataList.get(i).get(5).equals("否")){
					prdCash.setSmokeFlag("N");
				}else{
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(5));
					response.setMessage("是否吸烟-格式不正确："+dataList.get(i).get(5));
					listResPonse.add(response);
					hasError=true;
				}
			}
			
			if(StringUtils.isNotBlank(dataList.get(i).get(6))){
				if(dataList.get(i).get(6).equals("男")){
					prdCash.setGender("M");
				}else if(dataList.get(i).get(6).equals("女")){
					prdCash.setGender("F");
				}else{
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(6));
					response.setMessage("性别-格式不正确："+dataList.get(i).get(6));
					listResPonse.add(response);
					hasError=true;
				}
			}else{
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(ERROR);
				response.setFieldName(title.get(6));
				response.setMessage("性别-不能为空");
				listResPonse.add(response);
				hasError=true;
			}
			
			if(StringUtils.isNotBlank(dataList.get(i).get(7))){
				try {
					prdCash.setAge(Long.valueOf(dataList.get(i).get(7)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(7));
					response.setMessage("年龄-格式不正确："+dataList.get(i).get(7));
					listResPonse.add(response);
					hasError=true;
				}
			}else{
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(ERROR);
				response.setFieldName(title.get(7));
				response.setMessage("年龄-不能为空");
				listResPonse.add(response);
				hasError=true;
			}
			
			if(StringUtils.isNotBlank(dataList.get(i).get(8))){
				try {
					prdCash.setInsuranceYear(Long.valueOf(dataList.get(i).get(8)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(8));
					response.setMessage("保单年度-格式不正确："+dataList.get(i).get(8));
					listResPonse.add(response);
					hasError=true;
				}
			}else{
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(ERROR);
				response.setFieldName(title.get(8));
				response.setMessage("保单年度-不能为空"+dataList.get(i).get(8));
				listResPonse.add(response);
				hasError=true;
			}
			
			if(StringUtils.isNotBlank(dataList.get(i).get(9))){
				try {
					prdCash.setSurrenderDeposit(Double.valueOf(dataList.get(i).get(9)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(9));
					response.setMessage("退保－保证金额-格式不正确："+dataList.get(i).get(9));
					listResPonse.add(response);
				}
			}else{
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(WARN);
				response.setFieldName(title.get(9));
				response.setMessage("退保－保证金额 为空");
				listResPonse.add(response);
				prdCash.setSurrenderDeposit(0.0);
			}
			
			if(StringUtils.isNotBlank(dataList.get(i).get(10))){
				try {
					prdCash.setSurrenderNotBonus(Double.valueOf(dataList.get(i).get(10)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(10));
					response.setMessage("退保－非保证红利-格式不正确："+dataList.get(i).get(10));
					listResPonse.add(response);
				}
			}else{
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(WARN);
				response.setFieldName(title.get(10));
				response.setMessage("退保－非保证红利 为空");
				listResPonse.add(response);
				prdCash.setSurrenderNotBonus(0.0);
			}
			
			if(StringUtils.isNotBlank(dataList.get(i).get(11))){
				try {
					prdCash.setDieDeposit(Double.valueOf(dataList.get(i).get(11)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(11));
					response.setMessage("身故/重疾－保证金额-格式不正确："+dataList.get(i).get(11));
					listResPonse.add(response);
				}
			}else{
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(WARN);
				response.setFieldName(title.get(11));
				response.setMessage("身故/重疾－保证金额为空");
				listResPonse.add(response);
				prdCash.setDieDeposit(0.0);
			}
			
			if(StringUtils.isNotBlank(dataList.get(i).get(12))){
				try {
					prdCash.setDieExtra(Double.valueOf(dataList.get(i).get(12)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(12));
					response.setMessage("身故/重疾－额外保证-格式不正确："+dataList.get(i).get(12));
					listResPonse.add(response);
				}
			}else{
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(WARN);
				response.setFieldName(title.get(12));
				response.setMessage("身故/重疾－额外保证 为空");
				listResPonse.add(response);
				prdCash.setDieExtra(0.0);
			}
			
			if(StringUtils.isNotBlank(dataList.get(i).get(13))){
				try {
					prdCash.setDieNotBonus(Double.valueOf(dataList.get(i).get(13)));
				} catch (NumberFormatException e) {
					response=new ImportResponse();
					response.setLineId(lineNum);
					response.setType(ERROR);
					response.setFieldName(title.get(13));
					response.setMessage("身故/重疾－非保证红利-格式不正确："+dataList.get(i).get(13));
					listResPonse.add(response);
				}
			}else{
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(WARN);
				response.setFieldName(title.get(13));
				response.setMessage("身故/重疾－非保证红利 为空");
				listResPonse.add(response);
				prdCash.setDieNotBonus(0.0);
			}
			
			//文件中是否重复数据
			if(hasExist(i, dataList.get(i), dataList)){
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(ERROR);
				response.setMessage("文件中存在数据重复：("+dataList.get(i).get(0)+"),("+dataList.get(i).get(1)+"),("+dataList.get(i).get(2)+")"
						+ ", ("+dataList.get(i).get(3)+"), ("+dataList.get(i).get(4)+"),("+dataList.get(i).get(5)+"),("+dataList.get(i).get(6)+"),("+dataList.get(i).get(7)+"),("+dataList.get(i).get(8)+") ");
				listResPonse.add(response);
				hasError=true;
			}
			
			//查询 是否存在该数据
			List<PrdCashValue>list=this.selectPrdCashByParam(prdCash);
			if(!CollectionUtils.isEmpty(list) && !hasError){
				response=new ImportResponse();
				response.setLineId(lineNum);
				response.setType(WARN);
				response.setMessage("数据在系统中已存在：("+dataList.get(i).get(0)+"),("+dataList.get(i).get(1)+"),("+dataList.get(i).get(2)+")"
						+ ", ("+dataList.get(i).get(3)+"), ("+dataList.get(i).get(4)+"),("+dataList.get(i).get(5)+"),("+dataList.get(i).get(6)+"),("+dataList.get(i).get(7)+"),("+dataList.get(i).get(8)+") ");
				listResPonse.add(response);
			}
			
			beanList.add(prdCash);
		}
		return listResPonse;
	}

	@Override
	public void saveBatch(IRequest request,List<PrdCashValue> beanList) {
		for(PrdCashValue prdCash:beanList){
			this.updateOrInsert(request,prdCash);
		}
	}
	
	public void updateOrInsert(IRequest request,PrdCashValue prdCash){
		//查询 是否存在该数据
		List<PrdCashValue>list=this.selectPrdCashByParam(prdCash);
		if(CollectionUtils.isEmpty(list)){
			prdCashValueMapper.insertSelective(prdCash);
		}else{
			for(PrdCashValue prd:list){
				prdCash.setCashValueId(prd.getCashValueId());
				self().updateByPrimaryKey(request, prdCash);
			}
		}
		
	}
	/*****
	 * 根据(产品),(币种),(保额), (保费), (年期),(是否吸烟),(性别),(年龄),(保单年度) 查询 数据
	 * @param prdCash
	 * @return
	 */
	public List<PrdCashValue> selectPrdCashByParam(PrdCashValue prdCash){
		PrdCashValue prdCashNew=new PrdCashValue();
		prdCashNew.setItemId(prdCash.getItemId());
		prdCashNew.setCurrency(prdCash.getCurrency());
		prdCashNew.setInsuranceCoverage(prdCash.getInsuranceCoverage());
		prdCashNew.setPremium(prdCash.getPremium());
		prdCashNew.setInsuranceYear(prdCash.getInsuranceYear());
		prdCashNew.setSmokeFlag(prdCash.getSmokeFlag());
		prdCashNew.setGender(prdCash.getGender());
		prdCashNew.setAge(prdCash.getAge());
		prdCashNew.setSublineId(prdCash.getSublineId());
		List<PrdCashValue>list=prdCashValueMapper.select(prdCashNew);
		return list;
	}
	/******
	 * (产品),(币种),(保额), (保费), (年期),(是否吸烟),(性别),(年龄),(保单年度) 在文件中不允许重复
	 * @param num
	 * @param list
	 * @param dataList
	 * @return
	 */
	public boolean hasExist(int num,List<String>list,List<List<String>> dataList){
		boolean hasExist=false;
		for(int i=1;i<dataList.size()  && !hasExist;i++){
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

}
