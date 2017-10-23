package clb.core.question.service;

import clb.core.question.dto.QaQuestion;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.question.dto.QaGuideline;

import java.util.List;

public interface IQaGuidelineService extends IBaseService<QaGuideline>, ProxySelf<IQaGuidelineService>{

    List<QaGuideline> query(IRequest requestContext, QaGuideline qaGuideline);

	List<QaGuideline> add(IRequest requestCtx, List<QaGuideline> dto)throws Exception;

	List<QaGuideline> submit(IRequest requestCtx, List<QaGuideline> dto)throws Exception;
}