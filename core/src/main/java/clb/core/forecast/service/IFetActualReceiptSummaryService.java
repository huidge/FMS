package clb.core.forecast.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.ImportUtil.ImportMessage;
import clb.core.forecast.dto.FetActualReceiptSummary;

public interface IFetActualReceiptSummaryService extends IBaseService<FetActualReceiptSummary>, ProxySelf<IFetActualReceiptSummaryService>{

	
	List<FetActualReceiptSummary> getData(IRequest iRequest,FetActualReceiptSummary dto,int page,int pagesize);
	
	List<FetActualReceiptSummary> actualReceiptSummaryBatchUpdate(IRequest iRequest,List<FetActualReceiptSummary> dtos);
	
	List<ImportMessage> loadExcel(IRequest iRequest,HttpServletRequest request,MultipartFile files) throws CommonException;

	List<ImportMessage> uploadData(IRequest iRequest,List<ImportMessage> messages) throws CommonException;

	
}