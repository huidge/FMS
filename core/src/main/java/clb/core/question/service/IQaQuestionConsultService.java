package clb.core.question.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.question.dto.QaQuestionConsult;

public interface IQaQuestionConsultService extends IBaseService<QaQuestionConsult>, ProxySelf<IQaQuestionConsultService>{
	List<QaQuestionConsult> selectAllField(IRequest requestContext, QaQuestionConsult qaQuestionConsult ,int page, int pagesize);
	
	List<QaQuestionConsult> selectAll(IRequest requestContext, QaQuestionConsult qaQuestionConsult);
	
	List<QaQuestionConsult> selectByUserId(QaQuestionConsult qaQuestionConsult);

	List<QaQuestionConsult> query(IRequest requestContext, QaQuestionConsult dto, int page, int pageSize);
}