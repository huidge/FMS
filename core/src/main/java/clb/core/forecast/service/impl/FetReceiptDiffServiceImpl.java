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
import clb.core.forecast.dto.FetPeriod;
import clb.core.forecast.dto.FetReceiptDiff;
import clb.core.forecast.dto.FetReceivable;
import clb.core.forecast.mapper.FetPeriodMapper;
import clb.core.forecast.mapper.FetReceiptDiffMapper;
import clb.core.forecast.mapper.FetReceivableMapper;
import clb.core.forecast.service.IFetReceiptDiffService;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.service.IClbCodeService;

@Service
@Transactional
public class FetReceiptDiffServiceImpl extends BaseServiceImpl<FetReceiptDiff> implements IFetReceiptDiffService{
	private static Logger log = LoggerFactory.getLogger(FetReceiptDiffServiceImpl.class);

	@Autowired
	private FetReceiptDiffMapper receiptDiffMapper;
	
	@Autowired
	private IClbCodeService clbCodeService;
	
	@Autowired
    private ICommonExportService commonService;
	
	@Autowired
    private IProfileService profileService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private FetPeriodMapper periodMapper;
	
	@Autowired
	private FetReceivableMapper receivableMapper;
	
	private static final String SUPPLIER_TYPE = "SUPPLIER";
	
	@Override
	public List<FetReceiptDiff> getData(IRequest iRequest,FetReceiptDiff dto, int page, int pagesize) {
		if(pagesize != 0)PageHelper.startPage(page,pagesize);
		List<FetReceiptDiff> data = receiptDiffMapper.getData(dto);
		/*  ----计算当前页面----  */
		//计算总共数据
		int total = (int) ((Page<?>) data).getTotal();
		//计算最后一页的页数
		int totalPage = (total%pagesize == 0)?total/pagesize:(total/pagesize)+1;
		//最后一页
		if(page == totalPage){
			//获取所有数据
			List<FetReceiptDiff> allData = new ArrayList<FetReceiptDiff>();
			if(totalPage == 1 ){
				//获取所有数据
				allData = data;
			}else{
				allData = receiptDiffMapper.getData(dto);
			}
			FetReceiptDiff summaryData = new FetReceiptDiff();
			//应收
			BigDecimal summaryReceivableHkd = new BigDecimal(0);
			//实收
			BigDecimal summaryActualReceiptHkd = new BigDecimal(0);
			//差异
			BigDecimal summaryDiffHkd = new BigDecimal(0);
			for(FetReceiptDiff d:allData){
				summaryReceivableHkd = summaryReceivableHkd.add(d.getReceivableHkd());
				summaryActualReceiptHkd = summaryActualReceiptHkd.add(d.getActualReceiptHkd());
				summaryDiffHkd = summaryDiffHkd.add(d.getDiffHkd());
			}
			summaryData.setReceivableHkd(summaryReceivableHkd);
			summaryData.setActualReceiptHkd(summaryActualReceiptHkd);
			summaryData.setDiffHkd(summaryDiffHkd);
			if(summaryReceivableHkd.compareTo(new BigDecimal(0))==0){
				summaryData.setDiffRate(new BigDecimal(0));
			}
			else summaryData.setDiffRate(summaryDiffHkd.divide(summaryReceivableHkd,5,BigDecimal.ROUND_DOWN));
			data.add(summaryData);
		}
		return data;
	}

	@Override
	public List<FetReceiptDiff> summaryQuery(IRequest iRequest,FetReceiptDiff dto, int page, int pagesize) {
		if(pagesize != 0)PageHelper.startPage(page,pagesize);
		dto.setParamStatus("APPROVED");
		List<FetReceiptDiff> data = receiptDiffMapper.summaryQuery(dto);
		return data;
	}

	@Override
	public void exportData(IRequest iRequest, String sqlId, Object obj, HttpServletRequest request,
			HttpServletResponse response) throws CommonException {
		List<FetReceiptDiff> data = (List<FetReceiptDiff>) commonService.selectDatas(iRequest,sqlId,obj);
		//应收
		BigDecimal summaryReceivableHkd = new BigDecimal(0);
		//实收
		BigDecimal summaryActualReceiptHkd = new BigDecimal(0);
		//差异
		BigDecimal summaryDiffHkd = new BigDecimal(0);
		FetReceiptDiff summaryData = new FetReceiptDiff();
		//查询收入类型
		List<ClbCodeValue> receiptTypes = clbCodeService.selectCodeValuesByCodeName(iRequest,"FET.PAYMENT_TYPE");
		//查询币种
		List<ClbCodeValue> currencys = clbCodeService.selectCodeValuesByCodeName(iRequest,"PUB.CURRENCY");
		
		for(FetReceiptDiff d:data){
    		summaryReceivableHkd = summaryReceivableHkd.add(d.getReceivableHkd());
			summaryActualReceiptHkd = summaryActualReceiptHkd.add(d.getActualReceiptHkd());
			summaryDiffHkd = summaryDiffHkd.add(d.getDiffHkd());
			//设置收入类型
    		for(ClbCodeValue value:receiptTypes){
				if(value.getValue().equals(d.getReceiptType())){
					d.setReceiptType(value.getMeaning());
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
		summaryData.setReceivableHkd(summaryReceivableHkd);
		summaryData.setActualReceiptHkd(summaryActualReceiptHkd);
		summaryData.setDiffHkd(summaryDiffHkd);
		if(summaryReceivableHkd.compareTo(new BigDecimal(0))==0){
			summaryData.setDiffRate(new BigDecimal(0));
		}
		else summaryData.setDiffRate(summaryDiffHkd.divide(summaryReceivableHkd,5,BigDecimal.ROUND_DOWN));
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
	        String name = new String("应收差异表".getBytes("gb2312"), "ISO8859-1");
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
	public List<FetReceiptDiff> batchUpdateVesion(IRequest iRequest,List<FetReceiptDiff> dtos) throws CommonException {
		//设置全部要插入的数据
		List<FetReceiptDiff> allReceiptDiffData = new ArrayList<>();
		FetReceiptDiff param = new FetReceiptDiff();
		//先查询出差异数据
		for(FetReceiptDiff dto:dtos){
			//查询最大版本号
			Long version = dto.getVersion();
			param.setReceiptPeriod(dto.getReceiptPeriod());
			param.setReceiveSupplierId(dto.getReceiveSupplierId());
			param.setPaymentSupplierId(dto.getPaymentSupplierId());
			param.setVersion(version+1);
			if(CollectionUtils.isNotEmpty(receiptDiffMapper.select(param))){
				throw new CommonException("FET",String.format("数据:%s-%s-%s不是最新版本！",dto.getReceiptPeriod(),dto.getReceiveSupplierName(),dto.getPaymentSupplierName()),null);
			}
			List<FetReceiptDiff> diffDatas = receiptDiffMapper.getDiffData(dto);
			for(FetReceiptDiff diffData:diffDatas){
				//设置who字段
				diffData.setCreatedBy(iRequest.getUserId());
				diffData.setLastUpdatedBy(iRequest.getUserId());
				diffData.setLastUpdateLogin(iRequest.getUserId());
				diffData.setCreationDate(new Date());
				diffData.setLastUpdateDate(new Date());
				diffData.setVersion(version+1);
				diffData.set__status(DTOStatus.ADD);
				//没有实收数据
				if(diffData.getActualReceiptHkd() == null && diffData.getReceivableHkd() != null){
					diffData.setActualReceiptHkd(BigDecimal.ZERO);
					diffData.setDiffHkd(BigDecimal.ZERO.subtract(diffData.getReceivableHkd()));
					diffData.setDiffRate(BigDecimal.ZERO);
				}
				//没有应收数据
				if(diffData.getReceivableHkd() == null && diffData.getActualReceiptHkd() != null){
					diffData.setReceivableHkd(BigDecimal.ZERO);
					diffData.setDiffHkd(diffData.getActualReceiptHkd().subtract(BigDecimal.ZERO));
					diffData.setDiffRate(BigDecimal.ZERO.subtract(BigDecimal.ONE));
				}
			}
			allReceiptDiffData = (List<FetReceiptDiff>) CollectionUtils.union(allReceiptDiffData, diffDatas);
			param = new FetReceiptDiff();
		}
		try{
			this.batchUpdate(iRequest,allReceiptDiffData);
		}catch(Exception e){
			int type = ExceptionUtil.getExceptionType(e);
			if(type == 1){
				throw new CommonException("FET","创建失败：存在重复值",null);
			}else{
				throw e;
			}
		}
		return allReceiptDiffData;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<FetReceiptDiff> updateAllVersion(IRequest iRequest,Long paymentCompanyId,Long periodId) throws CommonException {
		
		List<FetReceivable> receivableDatas = new ArrayList<>();
		
		//付款方和期间都为空
		if(paymentCompanyId == null && periodId == null){
			//获取所有应收数据
			receivableDatas = receivableMapper.summaryQuery(null);
		}
		//只有单个付款方的情况
		else if(paymentCompanyId != null && periodId == null){
			//获取指定付款方的应收数据
			FetReceivable receivable = new FetReceivable();
			receivable.setPaymentCompanyType("SUPPLIER");
			receivable.setPaymentCompanyId(paymentCompanyId);
			receivableDatas = receivableMapper.summaryQuery(receivable);
		}
		else if(paymentCompanyId != null && periodId != null){
			//查询期间
			FetPeriod period = new FetPeriod();
			period.setPeriodId(periodId);
			period = periodMapper.selectByPrimaryKey(period);
			//获取指定付款方，指定期间的应收数据
			FetReceivable receivable = new FetReceivable();
			receivable.setPaymentCompanyType("SUPPLIER");
			receivable.setPaymentCompanyId(paymentCompanyId);
			receivable.setReceiptPeriod(period.getPeriodName());
			receivableDatas = receivableMapper.summaryQuery(receivable);
		}
		
		Map<String,FetReceivable> receivableDataMap = new HashMap<>();
		for(FetReceivable receivableData:receivableDatas){
			if(SUPPLIER_TYPE.equals(receivableData.getReceiveCompanyType()) && SUPPLIER_TYPE.equals(receivableData.getPaymentCompanyType())){
				String key = receivableData.getReceiptPeriod()+"&"+receivableData.getReceiveCompanyId()+"&"+receivableData.getPaymentCompanyId();
				if(receivableDataMap.get(key) == null){
					receivableDataMap.put(key, receivableData);
				}
			}
		}
		List<FetReceiptDiff> dtos = new ArrayList<>();
		for(Map.Entry<String,FetReceivable> entry : receivableDataMap.entrySet()){
			FetReceiptDiff dto = new FetReceiptDiff();
			dto.setReceiptPeriod(entry.getValue().getReceiptPeriod());
			dto.setReceiveSupplierId(entry.getValue().getReceiveCompanyId());
			dto.setPaymentSupplierId(entry.getValue().getPaymentCompanyId());
			dtos.add(dto);
		}
		//设置全部要插入的数据
		List<FetReceiptDiff> allReceiptDiffData = new ArrayList<>();
		//先查询出差异数据
		for(FetReceiptDiff dto:dtos){
			//查询最大版本号
			Long version = receiptDiffMapper.getMaxVerion(dto);
			if(version == null || version == 0)version = 0L;
			List<FetReceiptDiff> diffDatas = receiptDiffMapper.getDiffData(dto);
			for(FetReceiptDiff diffData:diffDatas){
				//设置who字段
				diffData.setCreatedBy(iRequest.getUserId());
				diffData.setLastUpdatedBy(iRequest.getUserId());
				diffData.setLastUpdateLogin(iRequest.getUserId());
				diffData.setCreationDate(new Date());
				diffData.setLastUpdateDate(new Date());
				diffData.setVersion(version+1);
				diffData.set__status(DTOStatus.ADD);
				//没有实收数据
				if(diffData.getActualReceiptHkd() == null && diffData.getReceivableHkd() != null){
					diffData.setActualReceiptHkd(BigDecimal.ZERO);
					diffData.setDiffHkd(BigDecimal.ZERO.subtract(diffData.getReceivableHkd()));
					diffData.setDiffRate(BigDecimal.ZERO);
				}
				//没有应收数据
				if(diffData.getReceivableHkd() == null && diffData.getActualReceiptHkd() != null){
					diffData.setReceivableHkd(BigDecimal.ZERO);
					diffData.setDiffHkd(diffData.getActualReceiptHkd().subtract(BigDecimal.ZERO));
					diffData.setDiffRate(BigDecimal.ZERO.subtract(BigDecimal.ONE));
				}
			}
			allReceiptDiffData = (List<FetReceiptDiff>) CollectionUtils.union(allReceiptDiffData, diffDatas);
		}
		try{
			this.batchUpdate(iRequest,allReceiptDiffData);
		}catch(Exception e){
			int type = ExceptionUtil.getExceptionType(e);
			if(type == 1){
				throw new CommonException("FET","创建失败：存在重复值",null);
			}else{
				throw e;
			}
		}
		return allReceiptDiffData;
	}

	@Override
	public List<FetReceiptDiff> mergeOffset(IRequest iRequest, List<FetReceiptDiff> dtos) throws CommonException {
		//设置全部要插入的数据
		List<FetReceiptDiff> allReceiptDiffData = new ArrayList<>();
		FetReceiptDiff param = new FetReceiptDiff();
		for(int i=0;i<dtos.size();i++){
			for(int j=i+1;j<dtos.size();j++){
				String key1 = dtos.get(i).getReceiveSupplierId().toString()+"&"+dtos.get(i).getPaymentSupplierId().toString();
				String key2 = dtos.get(j).getReceiveSupplierId().toString()+"&"+dtos.get(j).getPaymentSupplierId().toString();
				if(!key1.equals(key2)){
					throw new CommonException("FET","合并失败:数据收款方和付款方不一致！",null);
				}
			}
			param.setReceiptPeriod(dtos.get(i).getReceiptPeriod());
			param.setReceiveSupplierId(dtos.get(i).getReceiveSupplierId());
			param.setPaymentSupplierId(dtos.get(i).getPaymentSupplierId());
			param.setVersion(dtos.get(i).getVersion());
			List<FetReceiptDiff> receiptDiffs = receiptDiffMapper.select(param);
			if(CollectionUtils.isNotEmpty(receiptDiffs)){
				allReceiptDiffData = (List<FetReceiptDiff>) CollectionUtils.union(allReceiptDiffData,receiptDiffs);
			}
		}
		//所有应收金额
		BigDecimal allReceive = BigDecimal.ZERO;
		//所有实收金额
		BigDecimal allActualReceipt = BigDecimal.ZERO;
		//差异比例
		BigDecimal rate = BigDecimal.ZERO;
		//差异比例
		BigDecimal deviation = BigDecimal.ZERO;
		for(FetReceiptDiff dto:dtos){
			allReceive = allReceive.add(dto.getReceivableHkd());
			allActualReceipt = allActualReceipt.add(dto.getActualReceiptHkd());
		}
		if(BigDecimal.ZERO.equals(allActualReceipt) && BigDecimal.ZERO.equals(allReceive)){
			rate = BigDecimal.ONE;
		}
		else if(!BigDecimal.ZERO.equals(allActualReceipt) && BigDecimal.ZERO.equals(allReceive)){
			throw new CommonException("FET","合并失败:应收金额之和为0!",null);
		}else{
			rate = allActualReceipt.divide(allReceive,2,BigDecimal.ROUND_DOWN);
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
			throw new CommonException("FET",String.format("合并失败:数据实收和应收之比不在允差范围:%s内!",deviation.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_DOWN).toString()+"%"),null);
		}
		UUID uuid = UUID.randomUUID();
		for(FetReceiptDiff dto:allReceiptDiffData){
			dto.setMergeSeqence(uuid.toString());
			dto.set__status(DTOStatus.UPDATE);
		}
		self().batchUpdate(iRequest,allReceiptDiffData);
		return allReceiptDiffData;
	}
}