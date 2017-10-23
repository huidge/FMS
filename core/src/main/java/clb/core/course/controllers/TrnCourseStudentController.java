package clb.core.course.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.service.ICnlChannelService;
import clb.core.course.dto.TrnCourseStudent;
import clb.core.course.service.ITrnCourseStudentService;

    @Controller
    public class TrnCourseStudentController extends BaseController{

    @Autowired
    private ITrnCourseStudentService service;
    @Autowired
    private ICnlChannelService channelService;


    @RequestMapping(value = "/fms/trn/course/student/query")
    @ResponseBody
    public ResponseData query(TrnCourseStudent dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<TrnCourseStudent> selectAllField = service.selectAllField(requestContext,dto,page,pageSize);
        List<TrnCourseStudent> arrayList = new ArrayList<TrnCourseStudent>();
        for (TrnCourseStudent trnCourseStudent : selectAllField) {
        	trnCourseStudent.setContactPhoneComb(trnCourseStudent.getPhoneCode()+" "+trnCourseStudent.getPhoneNumber());
        	/*//1.0的是说显示渠道名字优先
			if(trnCourseStudent.getChannelName() != null ){
				trnCourseStudent.setChannelOrName(trnCourseStudent.getChannelName());
			}else{
				trnCourseStudent.setChannelOrName(trnCourseStudent.getName());
			}*/
		}
        return new ResponseData(selectAllField);
    }
    
    @RequestMapping(value = "/fms/trn/course/student/checkingPhone")
    @ResponseBody
    public ResponseData checkingPhone(TrnCourseStudent dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
    		@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	List<TrnCourseStudent> selectAllField = service.checkingPhone(requestContext,dto,page,pageSize);
    	List<TrnCourseStudent> arrayList = new ArrayList<TrnCourseStudent>();
    	for (TrnCourseStudent trnCourseStudent : selectAllField) {
    		if(!"".equals(trnCourseStudent.getPhoneNumber())){//这个判断其实没必要的，只是前期数据有点杂，不合规范
    			CnlChannel cnlChannel = new CnlChannel();
    			cnlChannel.setContactPhone(trnCourseStudent.getPhoneNumber());
    			List<CnlChannel> queryChannel = channelService.queryChannel(requestContext,cnlChannel);
    			if(queryChannel.size()>0){
    				trnCourseStudent.setBelongTo("COMPANY");
    			}else{
    				trnCourseStudent.setBelongTo("FOREIGN");
    			}
    		}else{
    			trnCourseStudent.setBelongTo("FOREIGN");
    		}
    		trnCourseStudent.setContactPhoneComb(trnCourseStudent.getPhoneCode()+" "+trnCourseStudent.getPhoneNumber());
    		if(trnCourseStudent.getChannelName() != null ){
    			trnCourseStudent.setChannelOrName(trnCourseStudent.getChannelName());
    		}else{
    			trnCourseStudent.setChannelOrName(trnCourseStudent.getName());
    		}
    	}
    	return new ResponseData(selectAllField);
    }
    
    @RequestMapping(value = "/fms/trn/course/student/updataTotal")
	@ResponseBody 
	public ResponseData updataReleaseStatus(HttpServletRequest request,@RequestParam("total") String total,@RequestParam("ids[]") List<Long> ids) {
		IRequest requestCtx = createRequestContext(request);
			for (Long courseId : ids) {
				TrnCourseStudent dto = new TrnCourseStudent();
				dto.setTotal(total);
				dto.setCourseId(courseId);
				TrnCourseStudent trnCourse = service.updateByPrimaryKeySelective(requestCtx, dto);
			}
		return new ResponseData();
	}
    
    @RequestMapping(value = "/fms/trn/course/student/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<TrnCourseStudent> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/trn/course/student/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<TrnCourseStudent> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }

    }