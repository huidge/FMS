package clb.core.forecast.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import clb.core.common.exceptions.CommonException;
import clb.core.common.service.ICommonExportService;
import clb.core.common.utils.ExportUtil;
import clb.core.forecast.dto.FetPayable;
import clb.core.forecast.mapper.FetPayableMapper;
import clb.core.forecast.service.IFetPayableService;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.service.IClbCodeService;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FetPayableServiceImpl extends BaseServiceImpl<FetPayable> implements IFetPayableService{
	
	private static Logger log = LoggerFactory.getLogger(FetPayableServiceImpl.class);

	
	@Autowired
	private FetPayableMapper payableMapper;
	
	@Autowired
	private IClbCodeService clbCodeService;
	
	@Autowired
    private ICommonExportService commonService;
	
	@Autowired
	private MessageSource messageSource;
	
	
	
	
	@Override
	public List<FetPayable> getData(IRequest iRequest,FetPayable dto, int page, int pagesize) {
		if(pagesize != 0)PageHelper.startPage(page,pagesize);
		List<FetPayable> data = payableMapper.getData(dto);
		for(FetPayable d:data){
    		//签单费或刷卡费时，订单金额为负值
    		if(d.getPaymentType().equals("QDF") || d.getPaymentType().equals("SKF")){
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
			List<FetPayable> allData = new ArrayList<FetPayable>();
			if(totalPage == 1 ){
				//获取所有数据
				allData = data;
			}else{
				allData = payableMapper.getData(dto);
			}
			FetPayable summaryData = new FetPayable();
			BigDecimal summaryHkd = new BigDecimal(0);
			for(FetPayable d:allData){
				summaryHkd = summaryHkd.add(d.getHkdAmount());
			}
			summaryData.setHkdAmount(summaryHkd);
			data.add(summaryData);
		}
		return data;
	}

	@Override
	public List<FetPayable> summaryQuery(IRequest iRequest,FetPayable dto, int page, int pagesize) {
		if(pagesize != 0)PageHelper.startPage(page,pagesize);
		dto.setParamStatus("APPROVED");
		List<FetPayable> data = payableMapper.summaryQuery(dto);
		return data;
	}

	@Override
	public void exportData(IRequest iRequest, String sqlId, Object obj, HttpServletRequest request,
			HttpServletResponse response) throws CommonException {
		List<FetPayable> data = (List<FetPayable>) commonService.selectDatas(iRequest,sqlId,obj);
		BigDecimal summaryHkd = new BigDecimal(0);
		FetPayable summaryData = new FetPayable();
		//查询付款类型
		List<ClbCodeValue> paymentTypes = clbCodeService.selectCodeValuesByCodeName(iRequest,"FET.PAYMENT_TYPE");
		//查询币种
		List<ClbCodeValue> currencys = clbCodeService.selectCodeValuesByCodeName(iRequest,"PUB.CURRENCY");
		for(FetPayable d:data){
    		//签单费和刷卡费时，订单金额为负值
    		if(d.getPaymentType().equals("QDF") || d.getPaymentType().equals("SKF")){
    			//如果金额大于0，变成负数
    			if(d.getAmount().compareTo(new BigDecimal(0)) > 0){
    				d.setAmount(new BigDecimal(0).subtract(d.getAmount()));
    			}
    		}
    		summaryHkd = summaryHkd.add(d.getHkdAmount());
    		//设置付款类型
    		for(ClbCodeValue value:paymentTypes){
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
		summaryData.setHkdAmount(summaryHkd);
		data.add(summaryData);
		try{
			String paymentCompanyName = ((HashMap<String,String>)obj).get("paymentCompanyName");
			String receiveCompanyName = ((HashMap<String,String>)obj).get("receiveCompanyName");
			String paymentPeriod = ((HashMap<String,String>)obj).get("paymentPeriod");
			//设置标题数据
			String titleData = String.format("%s>%s>%s",paymentPeriod,paymentCompanyName,receiveCompanyName);
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
	        String name = new String((titleData+"应付").getBytes("gb2312"), "ISO8859-1");
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

	/**
	 * 批量操作方法
	 *
	 * @param list
	 * @param iRequest
	 * @return
	 */
	@Override
	public List<FetPayable> batchHandle(List<FetPayable> list, IRequest iRequest) {
		List<FetPayable> addList = new ArrayList<>();
		for (FetPayable fetChannelCheck : list) {
			switch (fetChannelCheck.get__status()) {
				case DTOStatus.ADD:
					addList.add(fetChannelCheck);
				case DTOStatus.UPDATE:
					this.updateByPrimaryKeySelective(iRequest, fetChannelCheck);
			}
		}
		if(CollectionUtils.isNotEmpty(addList)){
			payableMapper.batchInsert(addList);
		}
		return list;
	}
}