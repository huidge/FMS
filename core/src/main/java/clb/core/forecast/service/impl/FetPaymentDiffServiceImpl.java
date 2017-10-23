package clb.core.forecast.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.IProfileService;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.exceptions.CommonException;
import clb.core.common.service.ICommonExportService;
import clb.core.common.utils.ColorUtil;
import clb.core.common.utils.ExceptionUtil;
import clb.core.common.utils.ExportUtil;
import clb.core.forecast.dto.FetPayable;
import clb.core.forecast.dto.FetPaymentDiff;
import clb.core.forecast.dto.FetPeriod;
import clb.core.forecast.dto.FetReceiptDiff;
import clb.core.forecast.mapper.FetPayableMapper;
import clb.core.forecast.mapper.FetPaymentDiffMapper;
import clb.core.forecast.mapper.FetPeriodMapper;
import clb.core.forecast.service.IFetPaymentDiffService;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.service.IClbCodeService;

@Service
@Transactional
public class FetPaymentDiffServiceImpl extends BaseServiceImpl<FetPaymentDiff> implements IFetPaymentDiffService{

	private static Logger log = LoggerFactory.getLogger(FetPaymentDiffServiceImpl.class);

	@Autowired
	private FetPaymentDiffMapper paymentDiffMapper;
	
	@Autowired
	private IClbCodeService clbCodeService;
	
	@Autowired
    private ICommonExportService commonService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private FetPeriodMapper periodMapper;
	
	@Autowired
	private FetPayableMapper payableMapper;
	
	@Autowired
	private IProfileService profileService;
	
	private static final String SUPPLIER_TYPE = "SUPPLIER";
	
	@Override
	public List<FetPaymentDiff> getData(IRequest iRequest,FetPaymentDiff dto, int page, int pagesize) {
		if(pagesize != 0)PageHelper.startPage(page,pagesize);
		List<FetPaymentDiff> data = paymentDiffMapper.getData(dto);
		/*  ----计算当前页面----  */
		//计算总共数据
		int total = (int) ((Page<?>) data).getTotal();
		//计算最后一页的页数
		int totalPage = (total%pagesize == 0)?total/pagesize:(total/pagesize)+1;
		//最后一页
		if(page == totalPage){
			//获取所有数据
			List<FetPaymentDiff> allData = new ArrayList<FetPaymentDiff>();
			if(totalPage == 1 ){
				//获取所有数据
				allData = data;
			}else{
				allData = paymentDiffMapper.getData(dto);
			}
			FetPaymentDiff summaryData = new FetPaymentDiff();
			//应付
			BigDecimal summaryPayableHkd = new BigDecimal(0);
			//实付
			BigDecimal summaryActualPaymentHkd = new BigDecimal(0);
			//差异
			BigDecimal summaryDiffHkd = new BigDecimal(0);
			for(FetPaymentDiff d:allData){
				summaryPayableHkd = summaryPayableHkd.add(d.getPayableHkd());
				summaryActualPaymentHkd = summaryActualPaymentHkd.add(d.getActualPaymentHkd());
				summaryDiffHkd = summaryDiffHkd.add(d.getDiffHkd());
			}
			summaryData.setPayableHkd(summaryPayableHkd);
			summaryData.setActualPaymentHkd(summaryActualPaymentHkd);
			summaryData.setDiffHkd(summaryDiffHkd);
			if(summaryPayableHkd.compareTo(new BigDecimal(0))==0){
				summaryData.setDiffRate(new BigDecimal(0));
			}
			else summaryData.setDiffRate(summaryDiffHkd.divide(summaryPayableHkd,5,BigDecimal.ROUND_DOWN));
			data.add(summaryData);
		}
		return data;
	}

	@Override
	public List<FetPaymentDiff> summaryQuery(IRequest iRequest,FetPaymentDiff dto, int page, int pagesize) {
		if(pagesize != 0)PageHelper.startPage(page,pagesize);
		dto.setParamStatus("APPROVED");
		List<FetPaymentDiff> data = paymentDiffMapper.summaryQuery(dto);
		return data;
	}

	@Override
	public void exportData(IRequest iRequest, String sqlId, Object obj, HttpServletRequest request,
			HttpServletResponse response) throws CommonException {
		List<FetPaymentDiff> data = (List<FetPaymentDiff>) commonService.selectDatas(iRequest,sqlId,obj);
		//应付
		BigDecimal summaryPayableHkd = new BigDecimal(0);
		//实付
		BigDecimal summaryActualPaymentHkd = new BigDecimal(0);
		//差异
		BigDecimal summaryDiffHkd = new BigDecimal(0);
		FetPaymentDiff summaryData = new FetPaymentDiff();
		//查询收入类型
		List<ClbCodeValue> receiptTypes = clbCodeService.selectCodeValuesByCodeName(iRequest,"FET.PAYMENT_TYPE");
		//查询币种
		List<ClbCodeValue> currencys = clbCodeService.selectCodeValuesByCodeName(iRequest,"PUB.CURRENCY");
		
		for(FetPaymentDiff d:data){
    		summaryPayableHkd = summaryPayableHkd.add(d.getPayableHkd());
			summaryActualPaymentHkd = summaryActualPaymentHkd.add(d.getActualPaymentHkd());
			summaryDiffHkd = summaryDiffHkd.add(d.getDiffHkd());
			//设置收入类型
    		for(ClbCodeValue value:receiptTypes){
				if(value.getValue().equals(d.getPaymentType())){
					d.setPaymentType(value.getMeaning());
				}
			}
    		//设置币种
    		for(ClbCodeValue value:currencys){
				if(value.getValue().equals(d.getOrderCurrency())){
					d.setOrderCurrency(value.getMeaning());
				}
			}
    	}
		//设置汇总值
		summaryData.setPayableHkd(summaryPayableHkd);
		summaryData.setActualPaymentHkd(summaryActualPaymentHkd);
		summaryData.setDiffHkd(summaryDiffHkd);
		if(summaryPayableHkd.compareTo(new BigDecimal(0))==0){
			summaryData.setDiffRate(new BigDecimal(0));
		}
		else summaryData.setDiffRate(summaryDiffHkd.divide(summaryPayableHkd,5,BigDecimal.ROUND_DOWN));
		data.add(summaryData);
		try{ 
			HSSFWorkbook wb = ExportUtil.ExportData(data,request,messageSource);
			//自定义cell颜色  
			HSSFPalette palette = wb.getCustomPalette();   
			//这里的9是索引  
			palette.setColorAtIndex((short)9, (byte)ColorUtil.getRed("#99CCFF"), (byte)ColorUtil.getGreen("#99CCFF"), (byte)ColorUtil.getBlue("#99CCFF"));  
			HSSFCellStyle style = wb.createCellStyle();    
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); 
			style.setFillForegroundColor((short)9); 
			HSSFSheet sheet1=wb.getSheet("sheet1");
	        HSSFRow row = sheet1.getRow(sheet1.getLastRowNum());
	        //处理差异比例
	        for (Row r : sheet1){
	        	if(r.getRowNum() == 0)continue;
	        	BigDecimal diffRate = new BigDecimal(r.getCell(9).getStringCellValue());
	        	if(diffRate.compareTo(new BigDecimal(0))<0){
	        		r.getCell(9).setCellStyle(style);
	        	}
	        	//四舍五入
	        	r.getCell(9).setCellValue(diffRate.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP).toString()+"%");
	        }
	        //添加汇总字段
	        row.getCell(5).setCellValue("汇总");
	        String name = new String("应付差异表".getBytes("gb2312"), "ISO8859-1");
	        ExportUtil.downloadFile(wb, response, name);
		}catch(Exception e){
        	log.error("导出失败",e);
        	if(e instanceof IOException){
        		throw new CommonException("COMMON","生成文件失败！",null);
        	}
        	else {
        		throw new CommonException("COMMON","导出失败！",null);
        	}
        }
		
		
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<FetPaymentDiff> batchUpdateVesion(IRequest iRequest, List<FetPaymentDiff> dtos) throws CommonException {
		//设置全部要插入的数据
		List<FetPaymentDiff> allPaymentDiffData = new ArrayList<>();
		FetPaymentDiff param = new FetPaymentDiff();
		//先查询出差异数据
		for(FetPaymentDiff dto:dtos){
			//查询最大版本号
			Long version = dto.getVersion();
			param.setPaymentPeriod(dto.getPaymentPeriod());
			param.setReceiveCompanyType(dto.getReceiveCompanyType());
			param.setReceiveCompanyId(dto.getReceiveCompanyId());
			param.setPaymentSupplierId(dto.getPaymentSupplierId());
			param.setVersion(version+1);
			if(CollectionUtils.isNotEmpty(paymentDiffMapper.select(param))){
				throw new CommonException("FET",String.format("数据:%s-%s-%s不是最新版本！",dto.getPaymentPeriod(),dto.getReceiveCompanyName(),dto.getPaymentSupplierName()),null);
			}
			List<FetPaymentDiff> diffDatas = paymentDiffMapper.getDiffData(dto);
			for(FetPaymentDiff diffData:diffDatas){
				//设置who字段
				diffData.setCreatedBy(iRequest.getUserId());
				diffData.setLastUpdatedBy(iRequest.getUserId());
				diffData.setLastUpdateLogin(iRequest.getUserId());
				diffData.setCreationDate(new Date());
				diffData.setLastUpdateDate(new Date());
				diffData.setVersion(version+1);
				diffData.set__status(DTOStatus.ADD);
				//没有实付数据
				if(diffData.getActualPaymentHkd() == null && diffData.getPayableHkd() != null){
					diffData.setActualPaymentHkd(BigDecimal.ZERO);
					diffData.setDiffHkd(BigDecimal.ZERO.subtract(diffData.getPayableHkd()));
					diffData.setDiffRate(BigDecimal.ZERO.subtract(BigDecimal.ONE));
				}
				//没有应付数据
				if(diffData.getPayableHkd() == null && diffData.getActualPaymentHkd() != null){
					diffData.setPayableHkd(BigDecimal.ZERO);
					diffData.setDiffHkd(diffData.getActualPaymentHkd().subtract(BigDecimal.ZERO));
					diffData.setDiffRate(BigDecimal.ZERO); 
				}
			}
			allPaymentDiffData = (List<FetPaymentDiff>) CollectionUtils.union(allPaymentDiffData, diffDatas);
			param = new FetPaymentDiff();
		}
		try{
			this.batchUpdate(iRequest,allPaymentDiffData);
		}catch(Exception e){
			int type = ExceptionUtil.getExceptionType(e);
			if(type == 1){
				throw new CommonException("FET","创建失败：存在重复值",null);
			}else{
				throw e;
			}
		}
		return allPaymentDiffData;
	}

	@Override
	public List<FetPaymentDiff> updateAllVersion(IRequest iRequest,Long paymentCompanyId,Long periodId) throws CommonException {
		
		
		List<FetPayable> payableDatas = new ArrayList<>();
		
		//付款方和期间都为空
		if(paymentCompanyId == null && periodId == null){
			//获取所有应付数据
			payableDatas = payableMapper.summaryQuery(null);
		}
		//只有单个付款方的情况
		else if(paymentCompanyId != null && periodId == null){
			//获取指定付款方的应付数据
			FetPayable payable = new FetPayable();
			payable.setPaymentCompanyType("SUPPLIER");
			payable.setPaymentCompanyId(paymentCompanyId);
			payableDatas = payableMapper.summaryQuery(payable);
		}
		else if(paymentCompanyId != null && periodId != null){
			//获取指定付款方，指定期间的应付数据
			//查询期间
			FetPeriod period = new FetPeriod();
			period.setPeriodId(periodId);
			period = periodMapper.selectByPrimaryKey(period);
			//查询应付数据
			FetPayable payable = new FetPayable();
			payable.setPaymentCompanyType("SUPPLIER");
			payable.setPaymentCompanyId(paymentCompanyId);
			payable.setPaymentPeriod(period.getPeriodName());
			payableDatas = payableMapper.summaryQuery(payable);
		}
		
		Map<String,FetPayable> payableDataMap = new HashMap<>();
		for(FetPayable payableData:payableDatas){
			if(SUPPLIER_TYPE.equals(payableData.getPaymentCompanyType())){
				String key = payableData.getPaymentPeriod()+"&"+payableData.getReceiveCompanyType()+"&"+payableData.getReceiveCompanyId()+"&"+payableData.getPaymentCompanyId();
				if(payableDataMap.get(key) == null){
					payableDataMap.put(key,payableData);
				}
			}
		}
		List<FetPaymentDiff> dtos = new ArrayList<>();
		for(Map.Entry<String,FetPayable> entry : payableDataMap.entrySet()){
			FetPaymentDiff dto = new FetPaymentDiff();
			dto.setPaymentPeriod(entry.getValue().getPaymentPeriod());
			dto.setReceiveCompanyType(entry.getValue().getReceiveCompanyType());
			dto.setReceiveCompanyId(entry.getValue().getReceiveCompanyId());
			dto.setPaymentSupplierId(entry.getValue().getPaymentCompanyId());
			dtos.add(dto);
		}
		//设置全部要插入的数据
		List<FetPaymentDiff> allPaymentDiffData = new ArrayList<>();
		//先查询出差异数据
		for(FetPaymentDiff dto:dtos){
			//查询最大版本号
			Long version = paymentDiffMapper.getMaxVerion(dto);
			if(version == null || version == 0)version = 0L;
			List<FetPaymentDiff> diffDatas = paymentDiffMapper.getDiffData(dto);
			for(FetPaymentDiff diffData:diffDatas){
				//设置who字段
				diffData.setCreatedBy(iRequest.getUserId());
				diffData.setLastUpdatedBy(iRequest.getUserId());
				diffData.setLastUpdateLogin(iRequest.getUserId());
				diffData.setCreationDate(new Date());
				diffData.setLastUpdateDate(new Date());
				diffData.setVersion(version+1);
				diffData.set__status(DTOStatus.ADD);
				//没有实付数据
				if(diffData.getActualPaymentHkd() == null && diffData.getPayableHkd() != null){
					diffData.setActualPaymentHkd(BigDecimal.ZERO);
					diffData.setDiffHkd(BigDecimal.ZERO.subtract(diffData.getPayableHkd()));
					diffData.setDiffRate(BigDecimal.ZERO.subtract(BigDecimal.ONE));
				}
				//没有应付数据
				if(diffData.getPayableHkd() == null && diffData.getActualPaymentHkd() != null){
					diffData.setPayableHkd(BigDecimal.ZERO);
					diffData.setDiffHkd(diffData.getActualPaymentHkd().subtract(BigDecimal.ZERO));
					diffData.setDiffRate(BigDecimal.ZERO); 
				}
			}
			allPaymentDiffData = (List<FetPaymentDiff>) CollectionUtils.union(allPaymentDiffData, diffDatas);
		}
		try{
			this.batchUpdate(iRequest,allPaymentDiffData);
		}catch(Exception e){
			int type = ExceptionUtil.getExceptionType(e);
			if(type == 1){
				throw new CommonException("FET","创建失败：存在重复值",null);
			}else{
				throw e;
			}
		}
		return allPaymentDiffData;
	}

	@Override
	public List<FetPaymentDiff> mergeOffset(IRequest iRequest, List<FetPaymentDiff> dtos) throws CommonException {
		//设置全部要插入的数据
		List<FetPaymentDiff> allPaymentData = new ArrayList<>();
		FetPaymentDiff param = new FetPaymentDiff();
		for(int i=0;i<dtos.size();i++){
			for(int j=i+1;j<dtos.size();j++){
				String key1 = dtos.get(i).getReceiveCompanyType()+"&"+dtos.get(i).getReceiveCompanyId().toString()+"&"+dtos.get(i).getPaymentSupplierId().toString();
				String key2 = dtos.get(j).getReceiveCompanyType()+"&"+dtos.get(j).getReceiveCompanyId().toString()+"&"+dtos.get(j).getPaymentSupplierId().toString();
				if(!key1.equals(key2)){
					throw new CommonException("FET","合并失败:数据收款方和付款方不一致！",null);
				}
			}
			param.setPaymentPeriod(dtos.get(i).getPaymentPeriod());
			param.setReceiveCompanyType(dtos.get(i).getReceiveCompanyType());
			param.setReceiveCompanyId(dtos.get(i).getReceiveCompanyId());
			param.setPaymentSupplierId(dtos.get(i).getPaymentSupplierId());
			param.setVersion(dtos.get(i).getVersion());
			List<FetPaymentDiff> paymentDiffs = paymentDiffMapper.select(param);
			if(CollectionUtils.isNotEmpty(paymentDiffs)){
				allPaymentData = (List<FetPaymentDiff>) CollectionUtils.union(allPaymentData,paymentDiffs);
			}
		}
		//所有应付金额
		BigDecimal allPayable = BigDecimal.ZERO;
		//所有实付金额
		BigDecimal allActualPayment = BigDecimal.ZERO;
		//差异比例
		BigDecimal rate = BigDecimal.ZERO;
		//差异比例
		BigDecimal deviation = BigDecimal.ZERO;
		for(FetPaymentDiff dto:dtos){
			allPayable = allPayable.add(dto.getPayableHkd());
			allActualPayment = allActualPayment.add(dto.getActualPaymentHkd());
		}
		if(BigDecimal.ZERO.equals(allActualPayment) && BigDecimal.ZERO.equals(allPayable)){
			rate = BigDecimal.ONE;
		}
		else if(!BigDecimal.ZERO.equals(allActualPayment) && BigDecimal.ZERO.equals(allPayable)){
			throw new CommonException("FET","合并失败:应付金额之和为0!",null);
		}else{
			rate = allActualPayment.divide(allPayable,2,BigDecimal.ROUND_DOWN);
		}
		String deviationString = profileService.getProfileValue(iRequest,"ALLOW_DEVIATION");
		if(deviationString == null){
			throw new CommonException("FET","请在配置维护中维护结算差异允差范围,代码：ALLOW_DEVIATION!",null);
		}
		try{
			deviation = new BigDecimal(deviationString);
		}catch(Exception e){
			throw new CommonException("FET","结算差异允差范围不是数字类型!",null);
		}
		
		BigDecimal beginRate = BigDecimal.ONE.subtract(deviation);
		BigDecimal endRate = BigDecimal.ONE.add(deviation);
		
		if(rate.compareTo(beginRate) < 0 || rate.compareTo(endRate) > 0){
			throw new CommonException("FET",String.format("合并失败:数据实付和实收之比不在允差范围:%s内!",deviation.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN).toString()+"%"),null);
		}
		UUID uuid = UUID.randomUUID();
		for(FetPaymentDiff dto:allPaymentData){
			dto.setMergeSeqence(uuid.toString());
			dto.set__status(DTOStatus.UPDATE);
		}
		self().batchUpdate(iRequest,allPaymentData);
		return allPaymentData;
	}

}