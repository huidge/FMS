package clb.core.question.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.question.dto.QaConsultLine;

public interface IQaConsultLineService extends IBaseService<QaConsultLine>, ProxySelf<IQaConsultLineService>{
	List<QaConsultLine> selectAllFields(IRequest requestContext, QaConsultLine qaConsultLine,int page, int pagesize);
	
	List<QaConsultLine> selectAll(IRequest requestContext, QaConsultLine qaConsultLine);

    List<QaConsultLine> query(IRequest requestContext, QaConsultLine dto, int page, int pageSize);
}