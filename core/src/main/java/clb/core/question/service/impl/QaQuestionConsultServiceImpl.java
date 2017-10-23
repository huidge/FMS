package clb.core.question.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.course.dto.TrnCourse;
import clb.core.question.dto.QaQuestionConsult;
import clb.core.question.mapper.QaQuestionConsultMapper;
import clb.core.question.service.IQaQuestionConsultService;

@Service
@Transactional
public class QaQuestionConsultServiceImpl extends BaseServiceImpl<QaQuestionConsult> implements IQaQuestionConsultService{
	@Autowired
	private QaQuestionConsultMapper qaQuestionConsultMapper;
	
	@Override
	public List<QaQuestionConsult> selectAllField(IRequest requestContext, QaQuestionConsult qaQuestionConsult,
			int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		//List<QaQuestionConsult> selectAllField = qaQuestionConsultMapper.selectAllField(qaQuestionConsult);
	
		return qaQuestionConsultMapper.selectAllField(qaQuestionConsult);
	}

	@Override
	public List<QaQuestionConsult> selectByUserId(QaQuestionConsult qaQuestionConsult) {
		// TODO Auto-generated method stub
		return qaQuestionConsultMapper.selectByUserId(qaQuestionConsult);
	}

	@Override
	public List<QaQuestionConsult> query(IRequest requestContext, QaQuestionConsult dto, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return qaQuestionConsultMapper.query(dto);
	}

	@Override
	public List<QaQuestionConsult> selectAll(IRequest requestContext, QaQuestionConsult qaQuestionConsult) {
		List<QaQuestionConsult> list = qaQuestionConsultMapper.selectAllField(qaQuestionConsult);
		return list;
		//return null;
	}

}