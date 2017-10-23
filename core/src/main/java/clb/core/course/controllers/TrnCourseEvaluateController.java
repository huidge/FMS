package clb.core.course.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.course.dto.TrnCourse;
import clb.core.course.dto.TrnCourseEvaluate;
import clb.core.course.dto.TrnCourseFile;
import clb.core.course.service.ITrnCourseEvaluateService;
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
    public class TrnCourseEvaluateController extends BaseController{

    @Autowired
    private ITrnCourseEvaluateService service;


    @RequestMapping(value = "/fms/trn/course/evaluate/query")
    @ResponseBody
    public ResponseData query(TrnCourseEvaluate dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<TrnCourseEvaluate> selectAllField = service.selectAllField(requestContext, dto, page, pageSize);
        for (TrnCourseEvaluate trnCourseEvaluate : selectAllField) {
        	trnCourseEvaluate.setContactPhoneComb(trnCourseEvaluate.getPhoneCode()+" "+trnCourseEvaluate.getMobile());
        	if(!"".equals(trnCourseEvaluate.getEvaluateDate())){
				try {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date  date = formatter.parse(trnCourseEvaluate.getEvaluateDate());
					String dateString = formatter.format(date);
					trnCourseEvaluate.setEvaluateDate(dateString);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			   
			   //Date currentTime_2 = formatter.parse(dateString, pos);
		}
		}
        return new ResponseData(selectAllField);
        
    }
    
    @RequestMapping(value = "/fms/trn/course/evaluate/queryContent")
    @ResponseBody
    public ResponseData queryContent(TrnCourseEvaluate dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<TrnCourseEvaluate> selectAllField = service.selectAllField(requestContext, dto, page, pageSize);
        List<TrnCourseEvaluate> list = new ArrayList<>();
        for (TrnCourseEvaluate trnCourseEvaluate : selectAllField) {
        	if(trnCourseEvaluate.getEvaluateContent() !=null && !"".equals(trnCourseEvaluate.getEvaluateContent())){
        		//selectAllField.remove(trnCourseEvaluate);
        		list.add(trnCourseEvaluate);
        	}
        	
        	/*List<SysFile> sysFiles = new ArrayList<>();
    		for (TrnCourseFile courseFile : list) {
    			Long fileId = courseFile.getFileId();
    			SysFile sysFile = fileService.selectByPrimaryKey(requestContext, fileId);
    			sysFiles.add(sysFile);
    		}
    		for (SysFile sysFile : sysFiles) {
    		}*/
		}
        return new ResponseData(list);
    }

    @RequestMapping(value = "/fms/trn/course/evaluate/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<TrnCourseEvaluate> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/trn/course/evaluate/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<TrnCourseEvaluate> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }

    }