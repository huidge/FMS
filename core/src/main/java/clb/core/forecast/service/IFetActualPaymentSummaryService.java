package clb.core.forecast.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.ImportUtil.ImportMessage;
import clb.core.forecast.dto.FetActualPaymentSummary;

public interface IFetActualPaymentSummaryService extends IBaseService<FetActualPaymentSummary>, ProxySelf<IFetActualPaymentSummaryService>{

	List<FetActualPaymentSummary> getData(IRequest iRequest,FetActualPaymentSummary dto,int page,int pagesize);
	
	List<FetActualPaymentSummary> actualPaymentSummaryBatchUpdate(IRequest iRequest,List<FetActualPaymentSummary> dtos);
	
	List<ImportMessage> loadExcel(IRequest iRequest,HttpServletRequest request,MultipartFile files) throws CommonException;

	List<ImportMessage> uploadData(IRequest iRequest,List<ImportMessage> messages) throws CommonException;
	
}