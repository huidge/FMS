package clb.core.forecast.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.drools.lang.dsl.DSLMapParser.mapping_file_return;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import clb.core.common.exceptions.CommonException;
import clb.core.common.service.ICommonExportService;
import clb.core.common.utils.ExceptionUtil;
import clb.core.common.utils.ExportUtil;
import clb.core.forecast.dto.FetActualReceipt;
import clb.core.forecast.dto.FetReceivable;
import clb.core.forecast.mapper.FetReceivableMapper;
import clb.core.forecast.service.IFetReceivableService;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.service.IClbCodeService;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FetReceivableServiceImpl extends BaseServiceImpl<FetReceivable> implements IFetReceivableService{
	private static Logger log = LoggerFactory.getLogger(FetReceivableServiceImpl.class);

	@Autowired
	private FetReceivableMapper receivableMapper;
	
	@Autowired
	private IClbCodeService clbCodeService;
	
	@Autowired
    private ICommonExportService commonService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Override
	public List<FetReceivable> getData(IRequest iRequest,FetReceivable dto, int page, int pagesize) {
		if(pagesize != 0)PageHelper.startPage(page,pagesize);
		List<FetReceivable> data = receivableMapper.getData(dto);
		for(FetReceivable d:data){
    		//签单费或刷卡费时，订单金额为负值
    		if(d.getReceiptType().equals("QDF") || d.getReceiptType().equals("SKF")){
    			//如果金额大于0，变成负数
    			if(d.getAmount().compareTo(new BigDecimal(0)) > 0){
    				d.setAmount(new BigDecimal(0).subtract(d.getAmount()));
    			}
    		}
    	}
		/*  ----计算当前页面----  */
		//计算总共数据
		int total = (int) ((Page<?>) data).getTotal();
		//计算最后一页的页数
		int totalPage = (total%pagesize == 0)?total/pagesize:(total/pagesize)+1;
		//最后一页
		if(page == totalPage){
			//获取所有数据
			List<FetReceivable> allData = new ArrayList<FetReceivable>();
			if(totalPage == 1 ){
				//获取所有数据
				allData = data;
			}else{
				allData = receivableMapper.getData(dto);
			}
			FetReceivable summaryData = new FetReceivable();
			BigDecimal summaryHkd = new BigDecimal(0);
			for(FetReceivable d:allData){
				summaryHkd = summaryHkd.add(d.getHkdAmount());
			}
			summaryData.setHkdAmount(summaryHkd);
			data.add(summaryData);
		}
		return data;
	}

	@Override
	public List<FetReceivable> summaryQuery(IRequest iRequest,FetReceivable dto, int page, int pagesize) {
		if(pagesize != 0)PageHelper.startPage(page,pagesize);
		dto.setParamStatus("APPROVED");
		List<FetReceivable> data = receivableMapper.summaryQuery(dto);
		return data;
	}

	@Override
	public void exportData(IRequest iRequest,String sqlId, Object obj,HttpServletRequest request,HttpServletResponse response) throws CommonException {
		List<FetReceivable> data = (List<FetReceivable>) commonService.selectDatas(iRequest,sqlId,obj);
		BigDecimal summaryHkd = new BigDecimal(0);
		FetReceivable summaryData = new FetReceivable();
		//查询收入类型
		List<ClbCodeValue> receiptTypes = clbCodeService.selectCodeValuesByCodeName(iRequest,"FET.PAYMENT_TYPE");
		//查询币种
		List<ClbCodeValue> currencys = clbCodeService.selectCodeValuesByCodeName(iRequest,"PUB.CURRENCY");
		for(FetReceivable d:data){
			//签单费和刷卡费时，订单金额为负值
    		if(d.getReceiptType().equals("QDF") || d.getReceiptType().equals("SKF")){
    			//如果金额大于0，变成负数
    			if(d.getAmount().compareTo(new BigDecimal(0)) > 0){
    				d.setAmount(new BigDecimal(0).subtract(d.getAmount()));
    			}
    		}
    		summaryHkd = summaryHkd.add(d.getHkdAmount());
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
		summaryData.setHkdAmount(summaryHkd);
		data.add(summaryData);
		try{
			String paymentCompanyName = ((HashMap<String,String>)obj).get("paymentCompanyName");
			String receiveCompanyName = ((HashMap<String,String>)obj).get("receiveCompanyName");
			String receiptPeriod = ((HashMap<String,String>)obj).get("receiptPeriod");
			//设置标题数据
			String titleData = String.format("%s>%s>%s",receiptPeriod,receiveCompanyName,paymentCompanyName);
			HSSFWorkbook wb = ExportUtil.ExportData(data,request,messageSource);
			HSSFSheet sheet1=wb.getSheet("sheet1");
	        HSSFRow row = sheet1.getRow(sheet1.getLastRowNum());
	        row.getCell(8).setCellValue("汇总");
	        //插入一行
	        sheet1.shiftRows(0,sheet1.getLastRowNum(),1);
	        //合并单元格
	        //参数分别是起始行序号，终止行序号，起始列序号，终止列序号
	        sheet1.addMergedRegion(new CellRangeAddress(0,0,0,3));
	        row=sheet1.createRow(0);
	        Cell newCell = row.createCell(0);
	        newCell.setCellValue(titleData);
	        String name = new String((titleData+"应收").getBytes("gb2312"), "ISO8859-1");
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
	public List<FetReceivable> fetReceivableBatchUpdate(IRequest iRequest, List<FetReceivable> data)
			throws CommonException {
		FetReceivable param = new FetReceivable();
		for(FetReceivable d:data){
			/* ----校验key值是否重复---- */
			if(d.getReceivableId() == null)d.setReceivableId(0L);
			//设置参数并查询
			//期间
			param.setReceiptPeriod(d.getReceiptPeriod());
			//付款类型
			param.setReceiptType(d.getReceiptType());
			//订单号
			param.setOrderId(d.getOrderId());
			//版本号
			param.setVersion(d.getVersion());
			param = receivableMapper.selectOne(param);
			if(d.get__status().equals(DTOStatus.ADD)){
				//如果查到值，说明不唯一，报错
				if(param != null){
					throw new CommonException("FET","创建失败：存在重复值",null);
				}
				try{
					d.setCreatedBy(iRequest.getUserId());
					d.setLastUpdatedBy(iRequest.getUserId());
					d.setLastUpdateLogin(iRequest.getUserId());
					d.setCreationDate(new Date());
					d.setLastUpdateDate(new Date());
					this.insertSelective(iRequest,d);
				}catch(Exception e){
					int type = ExceptionUtil.getExceptionType(e);
					if(type == 1){
						throw new CommonException("FET","创建失败：存在重复值",null);
					}else{
						throw e;
					}
				}
				
			}else if(d.get__status().equals(DTOStatus.UPDATE)){
				if(param != null && !param.get_token().equals(d.get_token())){
					throw new CommonException("FET","更新失败：存在重复值",null);
				}
				d.setLastUpdatedBy(iRequest.getUserId());
				d.setLastUpdateLogin(iRequest.getUserId());
				d.setLastUpdateDate(new Date());
				this.updateByPrimaryKeySelective(iRequest,d);
			}
			param = new FetReceivable();
		}
		return data;
	}

	/**
	 * 批量操作方法
	 *
	 * @param list
	 * @param iRequest
	 * @return
	 */
	@Override
	public List<FetReceivable> batchHandle(List<FetReceivable> list, IRequest iRequest) {
		List<FetReceivable> addList = new ArrayList<>();
		for (FetReceivable fetChannelCheck : list) {
			switch (fetChannelCheck.get__status()) {
				case DTOStatus.ADD:
					addList.add(fetChannelCheck);
				case DTOStatus.UPDATE:
					this.updateByPrimaryKeySelective(iRequest, fetChannelCheck);
			}
		}
		if(CollectionUtils.isNotEmpty(addList)){
			receivableMapper.batchInsert(addList);
		}
		return list;
	}
}