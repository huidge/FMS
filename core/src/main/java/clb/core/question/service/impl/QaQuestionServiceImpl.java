package clb.core.question.service.impl;

import clb.core.question.mapper.QaQuestionMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.question.dto.QaQuestion;
import clb.core.question.service.IQaQuestionService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QaQuestionServiceImpl extends BaseServiceImpl<QaQuestion> implements IQaQuestionService{

    @Autowired
    private QaQuestionMapper mapper;


    /**
     * 不分页
     * @param qaQuestion
     * @return
     */
    public List<QaQuestion> query(IRequest requestContext, QaQuestion qaQuestion){
        return mapper.select(qaQuestion);
    }
}