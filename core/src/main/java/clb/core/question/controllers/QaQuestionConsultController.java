package clb.core.question.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.course.dto.TrnCourse;
import clb.core.question.dto.QaConsultLine;
import clb.core.question.dto.QaQuestionConsult;
import clb.core.question.service.IQaConsultLineService;
import clb.core.question.service.IQaQuestionConsultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class QaQuestionConsultController extends BaseController{

    @Autowired
    private IQaQuestionConsultService service;
    @Autowired
    private IQaConsultLineService consultLineService;

    @RequestMapping(value = "/fms/qa/question/consult/query")
    @ResponseBody
    public ResponseData query(QaQuestionConsult dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<QaQuestionConsult> selectAllField = service.selectAllField(requestContext,dto,page,pageSize);
        for (QaQuestionConsult qaQuestionConsult : selectAllField) {
        	if(selectAllField.size()>0){
        		if("N".equals(qaQuestionConsult.getAnswerStatus())){
        			qaQuestionConsult.setAnswerDate(null);
        			QaQuestionConsult updateByPrimaryKeySelective = service.updateByPrimaryKeySelective(requestContext, qaQuestionConsult);
        		}
        		
        		int length = qaQuestionConsult.getQuestionDescription().length();
        		if(length>10){
        			String substring = qaQuestionConsult.getQuestionDescription().substring(0, 10);
        			qaQuestionConsult.setQuestionDescriptionTemp(substring+"...");
        		}else{
        			qaQuestionConsult.setQuestionDescriptionTemp(qaQuestionConsult.getQuestionDescription());
        		}
        	}
		}
        return new ResponseData(selectAllField);
        //return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }
    
    @RequestMapping(value = "/fms/qa/question/consult/selectAll")
    @ResponseBody
    public ResponseData selectAll(QaQuestionConsult dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	List<QaQuestionConsult> selectAll = service.selectAll(requestContext, dto);
    	/*for (TrnCourse trnCourse : selectAllField) {
        	System.out.println(trnCourse);
		}*/
    	return new ResponseData(selectAll);
    	//return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/qa/question/consult/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<QaQuestionConsult> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @RequestMapping(value = "/fms/qa/question/consult/updataStatus")
	@ResponseBody 
	public ResponseData updataReleaseStatus(HttpServletRequest request,@RequestParam("answerStatus") String answerStatus,@RequestParam("ids[]") List<Long> ids) {
		IRequest requestCtx = createRequestContext(request);
			QaQuestionConsult dto = new QaQuestionConsult();
			QaConsultLine qaConsultLine = new QaConsultLine();
			for (Long consultId : ids) {
    			qaConsultLine.setConsultId(consultId);
    			List<QaConsultLine> selectAll = consultLineService.selectAll(requestCtx,qaConsultLine);
    			for (QaConsultLine qaConsultLine2 : selectAll) {
					System.out.println(qaConsultLine2);
				}
    			int index = selectAll.size()-1;
    			QaConsultLine qaConsultLine3 = selectAll.get(index);
    			dto.setConsultId(consultId);
    			dto.setAnswerDate(qaConsultLine3.getLastUpdateDate());
    			dto.setAnswerStatus(answerStatus);
				QaQuestionConsult qaQuestionConsult = service.updateByPrimaryKeySelective(requestCtx, dto);
				//System.out.println(trnCourse);
			}
		return new ResponseData();
	}
    
    @RequestMapping(value = "/fms/qa/question/consult/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<QaQuestionConsult> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
}