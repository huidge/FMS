package clb.core.forecast.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.ImportUtil.ImportMessage;
import clb.core.forecast.dto.FetActualReceipt;
import clb.core.supplier.exceptions.SpeException;

public interface IFetActualReceiptService extends IBaseService<FetActualReceipt>, ProxySelf<IFetActualReceiptService>{

	List<FetActualReceipt> getData(IRequest iRequest,FetActualReceipt dto,int page,int pagesize);
		
	List<FetActualReceipt> fetActualReceiptBatchUpdate(IRequest iRequest,List<FetActualReceipt> data) throws CommonException;
	
}