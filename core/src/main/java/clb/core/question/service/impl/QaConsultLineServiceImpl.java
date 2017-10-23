package clb.core.question.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.course.dto.TrnCourseFile;
import clb.core.question.dto.QaConsultLine;
import clb.core.question.mapper.QaConsultLineMapper;
import clb.core.question.service.IQaConsultLineService;

@Service
@Transactional
public class QaConsultLineServiceImpl extends BaseServiceImpl<QaConsultLine> implements IQaConsultLineService{
	@Autowired
	private QaConsultLineMapper qaConsultLineMapper;
	@Autowired
    private ISysFileService fileService;
	
	@Override
	public List<QaConsultLine> selectAllFields(IRequest requestContext, QaConsultLine qaConsultLine, int page,
			int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<QaConsultLine> list = qaConsultLineMapper.selectAllFields(qaConsultLine);
		/*for (QaConsultLine QaConsultLine : list) {
			if (QaConsultLine.getQuestionFileId() != null) {
                SysFile sysFile = fileService.selectByPrimaryKey(requestContext, QaConsultLine.getQuestionFileId());
                if (sysFile != null) {
                	QaConsultLine.set_token(sysFile.get_token());
                }else {
                	QaConsultLine.set_token(null);
                }
            }
			if (QaConsultLine.getAnswerFileId() != null) {
                SysFile sysFile = fileService.selectByPrimaryKey(requestContext, QaConsultLine.getAnswerFileId());
                if (sysFile != null) {
                }else {
                	QaConsultLine.set_token(null);
                }
            }
		}*/
		return list;
		//return qaConsultLineMapper.selectAllFields(qaConsultLine);
	}

	@Override
	public List<QaConsultLine> query(IRequest requestContext, QaConsultLine dto, int page, int pageSize) {
//		PageHelper.startPage(page, pageSize);
		return qaConsultLineMapper.query(dto);
	}

	public List<QaConsultLine> batchUpdate(IRequest request, List<QaConsultLine> qaConsultLine){
		for (QaConsultLine newLine : qaConsultLine) {
			//qaConsultLineMapper.updateByPrimaryKeySelective(newLine);
	        if ((newLine.getFlag()).equals("add")) {
	        	QaConsultLine qaConsultLine2 = new QaConsultLine();
	        	if(newLine.getNewfileId() != null){
	        		qaConsultLine2.setAnswerFileId(Long.parseLong(newLine.getNewfileId()));
	        	}
	        	qaConsultLine2.setLineId(newLine.getLineId());
	        	qaConsultLine2.setConsultId(newLine.getConsultId());
	        	qaConsultLine2.setQuestion(newLine.getQuestion());
	        	if(newLine.getQuestionFileId() != null){
	        		qaConsultLine2.setQuestionFileId(newLine.getQuestionFileId());
	        	}
	        	qaConsultLine2.setAnswer(newLine.getNewAnswer());
	        	qaConsultLineMapper.insertSelective(qaConsultLine2);
	      }else{
	    	  QaConsultLine qaConsultLine3 = new QaConsultLine();
	        	if(newLine.getNewfileId() != null){
	        		qaConsultLine3.setAnswerFileId(Long.parseLong(newLine.getNewfileId()));
	        	}
	        	qaConsultLine3.setLineId(newLine.getLineId());
	        	qaConsultLine3.setConsultId(newLine.getConsultId());
	        	qaConsultLine3.setQuestion(newLine.getQuestion());
	        	if(newLine.getQuestionFileId() != null){
	        		qaConsultLine3.setQuestionFileId(newLine.getQuestionFileId());
	        	}
	        	qaConsultLine3.setAnswer(newLine.getNewAnswer());
	    	  qaConsultLineMapper.updateByPrimaryKeySelective(qaConsultLine3);
	      }
		}
		return qaConsultLine;
	}

	@Override
	public List<QaConsultLine> selectAll(IRequest requestContext, QaConsultLine qaConsultLine) {
		return qaConsultLineMapper.selectAllFields(qaConsultLine);
		//return null;
	}
}