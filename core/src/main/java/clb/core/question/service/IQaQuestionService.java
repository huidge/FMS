package clb.core.question.service;

import clb.core.question.mapper.QaQuestionMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.question.dto.QaQuestion;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface IQaQuestionService extends IBaseService<QaQuestion>, ProxySelf<IQaQuestionService>{

    List<QaQuestion> query(IRequest requestContext, QaQuestion qaQuestion);
}