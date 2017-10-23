package clb.core.supplier.service;

import java.util.List;

import org.activiti.engine.impl.cmd.CreateAttachmentCmd;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.service.IBaseService;

import clb.core.supplier.dto.SpeCollectionTerms;
import clb.core.supplier.exceptions.SpeException;

public interface ISpeCollectionTermsService extends IBaseService<SpeCollectionTerms>, ProxySelf<ISpeCollectionTermsService>{
	
	public List<SpeCollectionTerms> selectData(SpeCollectionTerms dto,int page,int pagesize);

	public List<SpeCollectionTerms> selectAllData(SpeCollectionTerms dto);
	
	public List<CodeValue> getProductDivision(IRequest iRequest);
	
	public List<SpeCollectionTerms> collectionTermsBatchUpdate(IRequest iRequest,List<SpeCollectionTerms> data) throws SpeException;
}