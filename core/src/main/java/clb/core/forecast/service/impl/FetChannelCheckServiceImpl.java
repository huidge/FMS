package clb.core.forecast.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hand.hap.account.dto.Role;
import com.hand.hap.account.dto.UserRole;
import com.hand.hap.account.mapper.RoleMapper;
import com.hand.hap.account.mapper.UserRoleMapper;
import com.hand.hap.account.service.IUserRoleService;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import clb.core.common.exceptions.CommonException;
import clb.core.common.service.ICommonExportService;
import clb.core.common.utils.ExportUtil;
import clb.core.forecast.dto.FetChannelCheck;
import clb.core.forecast.dto.FetQuestion;
import clb.core.forecast.dto.FetChannelCheck;
import clb.core.forecast.mapper.FetChannelCheckMapper;
import clb.core.forecast.mapper.FetQuestionMapper;
import clb.core.forecast.service.IFetChannelCheckService;
import clb.core.forecast.service.IFetQuestionService;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.service.IClbCodeService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@Service
@Transactional
public class FetChannelCheckServiceImpl extends BaseServiceImpl<FetChannelCheck> implements IFetChannelCheckService{

private static Logger log = LoggerFactory.getLogger(FetChannelCheckServiceImpl.class);

	
	@Autowired
	private FetChannelCheckMapper checkMapper;
	
	@Autowired
	private FetQuestionMapper questionMapper;
	
	@Autowired
	private IFetQuestionService questionService;
	
	@Autowired
	private IClbCodeService clbCodeService;
	
	@Autowired
    private ICommonExportService commonService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Override
	public List<FetChannelCheck> getData(IRequest iRequest,FetChannelCheck dto, int page, int pagesize) {
		if(pagesize != 0)PageHelper.startPage(page,pagesize);
		List<FetChannelCheck> data = checkMapper.getData(dto);
		for(FetChannelCheck d:data){
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
			List<FetChannelCheck> allData = new ArrayList<FetChannelCheck>();
			if(totalPage == 1 ){
				//获取所有数据
				allData = data;
			}else{
				allData = checkMapper.getData(dto);
			}
			FetChannelCheck summaryData = new FetChannelCheck();
			BigDecimal summaryHkd = new BigDecimal(0);
			for(FetChannelCheck d:allData){
				summaryHkd = summaryHkd.add(d.getHkdAmount());
			}
			summaryData.setHkdAmount(summaryHkd);
			data.add(summaryData);
		}
		return data;
	}

	@Override
	public List<FetChannelCheck> summaryQuery(IRequest iRequest,FetChannelCheck dto, int page, int pagesize) {
		if(pagesize != 0)PageHelper.startPage(page,pagesize);
		dto.setParamStatus("APPROVED");
		return checkMapper.summaryQuery(dto);
	}

	@Override
	public void exportData(IRequest iRequest, String sqlId, Object obj, HttpServletRequest request,
			HttpServletResponse response) throws CommonException {
		List<FetChannelCheck> data = (List<FetChannelCheck>) commonService.selectDatas(iRequest,sqlId,obj);
		BigDecimal summaryHkd = new BigDecimal(0);
		FetChannelCheck summaryData = new FetChannelCheck();
		//查询付款类型
		List<ClbCodeValue> paymentTypes = clbCodeService.selectCodeValuesByCodeName(iRequest,"FET.PAYMENT_TYPE");
		//查询币种
		List<ClbCodeValue> currencys = clbCodeService.selectCodeValuesByCodeName(iRequest,"PUB.CURRENCY");
		for(FetChannelCheck d:data){
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
			String receiptPeriod = ((HashMap<String,String>)obj).get("checkPeriod");
			//设置标题数据
			String titleData = String.format("%s>%s>%s",receiptPeriod,receiveCompanyName,paymentCompanyName);
			HSSFWorkbook wb = ExportUtil.ExportData(data,request,messageSource);
			HSSFSheet sheet1=wb.getSheet("sheet1");
	        HSSFRow row = sheet1.getRow(sheet1.getLastRowNum());
	        row.getCell(7).setCellValue("汇总");
	        //插入一行
	        sheet1.shiftRows(0,sheet1.getLastRowNum(),1);
	        //合并单元格
	        //参数分别是起始行序号，终止行序号，起始列序号，终止列序号
	        sheet1.addMergedRegion(new CellRangeAddress(0,0,0,3));
	        row=sheet1.createRow(0);
	        Cell newCell = row.createCell(0);
	        newCell.setCellValue(titleData);
	        String name = new String((titleData+"渠道对账表").getBytes("gb2312"), "ISO8859-1");
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
	public List<FetChannelCheck> enSure(IRequest iRequest,FetChannelCheck dto) {
		List<FetChannelCheck> data = checkMapper.select(dto);
		for(FetChannelCheck d:data){
			d.setStatus("YQR");
			d.set__status(DTOStatus.UPDATE);
		}
		//拿到问题
		FetQuestion question = new FetQuestion();
		question.setCheckPeriod(dto.getCheckPeriod());
		question.setPaymentCompanyType(dto.getPaymentCompanyType());
		question.setPaymentCompanyId(dto.getPaymentCompanyId());
		question.setChannelId(dto.getReceiveCompanyId());
		question.setVersion(dto.getVersion());
		question = questionMapper.selectOne(question);
		self().batchUpdate(iRequest,data);
		if(question != null){
			question.set__status(DTOStatus.UPDATE);
			question.setStatus("YJJ");	
			questionService.updateByPrimaryKeySelective(iRequest, question);

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
	public List<FetChannelCheck> batchHandle(List<FetChannelCheck> list, IRequest iRequest) {
		List<FetChannelCheck> addList = new ArrayList<>();
		for (FetChannelCheck fetChannelCheck : list) {
			switch (fetChannelCheck.get__status()) {
				case DTOStatus.ADD:
					addList.add(fetChannelCheck);
				case DTOStatus.UPDATE:
					this.updateByPrimaryKeySelective(iRequest, fetChannelCheck);
			}
		}
		if(CollectionUtils.isNotEmpty(addList)){
			checkMapper.batchInsert(addList);
		}
		return list;
	}

	/**
	 * 获取对账单角标
	 *
	 * @param paramStatus
	 * @param status
	 * @param userId
	 * @return
	 */
	@Override
	public Integer getCheckCount(String paramStatus, String status,Long userId) {
		return checkMapper.getCheckCount(paramStatus,status,userId);
	}
}