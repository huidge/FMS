package clb.core.question.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.course.dto.TrnCourse;
import clb.core.course.dto.TrnCourseFile;
import clb.core.question.dto.QaConsultLine;
import clb.core.question.dto.QaQuestionConsult;
import clb.core.question.service.IQaConsultLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

    @Controller
    public class QaConsultLineController extends BaseController{

    @Autowired
    private IQaConsultLineService service;
    @Autowired
    private ISysFileService fileService;

    @RequestMapping(value = "/fms/qa/consult/line/query")
    @ResponseBody
    public ResponseData query(QaConsultLine dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAllFields(requestContext,dto,page,pageSize));
        //return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }
    
    @RequestMapping(value = "/fms/trn/course/file/queryToken")
    @ResponseBody
    public ResponseData queryToken(QaConsultLine dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<QaConsultLine> queryTokenList = new ArrayList<>();
        if(dto.getDownloadFileId() != null){
        	SysFile sysFile = fileService.selectByPrimaryKey(requestContext, dto.getDownloadFileId());
            if (sysFile != null) {
            	dto.set_token(sysFile.get_token());
            }else {
            	dto.set_token(null);
            }
            queryTokenList.add(dto);
            return new ResponseData(queryTokenList);
        }else{
        	queryTokenList.add(dto);
        	return new ResponseData(queryTokenList);
        }
        //return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }
    
    @RequestMapping(value = "/fms/qa/consult/line/selectAll")
    @ResponseBody
    public ResponseData selectAll(QaConsultLine dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAll(requestContext,dto));
        //return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }
    
    @RequestMapping(value = "/fms/qa/consult/line/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<QaConsultLine> dto,BindingResult result){
        //IRequest requestCtx = createRequestContext(request);
        getValidator().validate(dto, result);
        if (result.hasErrors()) {
          ResponseData rd = new ResponseData(false);
          rd.setMessage(getErrorMessage(result, request));
          return rd;
        }
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.batchUpdate(iRequest, dto));
        //return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @RequestMapping(value="/fms/qa/consult/line/addLine")
    @ResponseBody
    public ResponseData addLine(HttpServletRequest request, @RequestParam("id") String id,@RequestParam("question") String question,@RequestParam("questionFileId") String questionFileId){
	        IRequest requestContext = createRequestContext(request);
	        QaConsultLine dto = new QaConsultLine();
	        dto.setConsultId(Long.valueOf(id));
	        dto.setQuestion(question);
			if (! questionFileId.equals("null")) {
				dto.setQuestionFileId(Long.valueOf(questionFileId));
			}
			QaConsultLine insertSelective = service.insertSelective(requestContext, dto);
			List<QaConsultLine> list = new ArrayList<>();
			list.add(insertSelective);
			return new ResponseData(list);
		//return new ResponseData();
    }
    
    @RequestMapping(value = "/fms/qa/consult/line/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<QaConsultLine> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }