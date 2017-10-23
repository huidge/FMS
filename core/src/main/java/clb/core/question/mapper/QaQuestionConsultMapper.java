package clb.core.question.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.question.dto.QaQuestionConsult;

public interface QaQuestionConsultMapper extends Mapper<QaQuestionConsult>{
	List<QaQuestionConsult> selectAllField(QaQuestionConsult qaQuestionConsult);
	
	List<QaQuestionConsult> selectByUserId(QaQuestionConsult qaQuestionConsult);

    List<QaQuestionConsult> query(QaQuestionConsult dto);
}