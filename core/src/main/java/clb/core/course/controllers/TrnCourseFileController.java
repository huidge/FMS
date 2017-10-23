package clb.core.course.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.service.ISequenceService;
import clb.core.course.dto.TrnCourse;
import clb.core.course.dto.TrnCourseFile;
import clb.core.course.dto.TrnSupport;
import clb.core.course.service.ITrnCourseFileService;
import clb.core.course.service.ITrnCourseService;
import clb.core.order.service.IOrdOrderService;
import clb.core.system.dto.ClbUser;

    @Controller
    public class TrnCourseFileController extends BaseController{

    @Autowired
    private ITrnCourseFileService service;
    @Autowired
    private ITrnCourseService courseService;
    
    @Autowired
    private ISysFileService fileService;
    
    @Autowired
    private ISequenceService sequenceService;
    
    @RequestMapping(value = "/fms/trn/course/file/query")
    @ResponseBody
    public ResponseData query(TrnCourseFile dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        String courseId = request.getParameter("courseId");
		List<TrnCourseFile> list= service.selectBycourseId(requestContext, Long.valueOf(courseId), page, pageSize);
		
		
		/*List<CnlChannelArchive> cList = cnlChannelArchiveMapper.queryArchive(cnlChannelArchive);
        for (CnlChannelArchive k : cList) {
            if (k.getFileId() != null) {
                SysFile sysFile = sysFileService.selectByPrimaryKey(request, k.getFileId());
                if (sysFile != null) {
                  k.set_token(sysFile.get_token());
                }else {
                    k.set_token(null);
                }

            }
        }
        return cList;*/
		
		/*List<SysFile> sysFiles = new ArrayList<>();
		for (TrnCourseFile courseFile : list) {
			Long fileId = courseFile.getFileId();
			SysFile sysFile = fileService.selectByPrimaryKey(requestContext, fileId);
			sysFiles.add(sysFile);
		}
		for (SysFile sysFile : sysFiles) {
		}*/
		return new ResponseData(list);
        //return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }
    
    @RequestMapping(value = "/fms/trn/course/file/queryAll")
    @ResponseBody
    public ResponseData queryAll(TrnCourseFile dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
		List<TrnCourseFile> list= service.selectAllField(requestContext, dto, page, pageSize);
		/*for (TrnCourseFile trnCourseFile : list) {
			if(trnCourseFile.getLastUpdateDate() != null){
					try {
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String dateString = formatter.format(trnCourseFile.getLastUpdateDate());
						Date  date = formatter.parse(dateString);
						trnCourseFile.setLastUpdateDate(date);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				   
				   //Date currentTime_2 = formatter.parse(dateString, pos);
			}
		}*/
		return new ResponseData(list);
        //return new ResponseData(service.select(requestContext,dto,page,pageSize));
    }
    
  //申请失败
    @RequestMapping(value = "/fms/trn/course/file/courseSubmit")
	@ResponseBody 
    public ResponseData fileContent(HttpServletRequest request,@RequestParam("courseId") String courseId,@RequestParam("courseFileIdList[]") List<String> courseFileIdList) {
		IRequest requestCtx = createRequestContext(request);
		/*if("0".equals(courseFileId)){
			return new ResponseData();
		}*/
		long courseIdLong = Long.parseLong(courseId);
		TrnCourse trnCourse = new TrnCourse();
		trnCourse.setCourseId(courseIdLong);
        //利用插入了id的课程查出有大小分类和产品信息的课程对象
        TrnCourse course = courseService.selectByPrimaryKey(requestCtx, trnCourse);
		if (!"".equals(courseId) && courseFileIdList.size()>0) {
			for (String courseFileId : courseFileIdList) {
				TrnCourseFile trnCourseFile = new TrnCourseFile();
				trnCourseFile.setCourseId(courseIdLong);
				trnCourseFile.setFileId(Long.parseLong(courseFileId));
				trnCourseFile.setFileNum(sequenceService.getSequence("FILENUM"));
				SysFile sysFile = fileService.selectByPrimaryKey(requestCtx, Long.parseLong(courseFileId));
				//将产品公司设置进去
				trnCourseFile.setSupplierId(course.getSupplierId());
				  //将大分类设置进去
				trnCourseFile.setPrdDivision(course.getPrdDivision());
				  //将中分类设置进去
				trnCourseFile.setPrdClass(course.getPrdClass());
				trnCourseFile.setFileContent(sysFile.getFileName());
				TrnCourseFile insertSelective = service.insertSelective(requestCtx,trnCourseFile);
			}
		}
		return new ResponseData();
	}
    
    
    @RequestMapping(value = "/fms/trn/course/file/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<TrnCourseFile> dto){
        IRequest requestCtx = createRequestContext(request);
        Long courseId = Long.valueOf(request.getParameter("courseId"));
        TrnCourse trnCourse = new TrnCourse();
        trnCourse.setCourseId(courseId);
        //利用插入了id的课程查出有大小分类和产品信息的课程对象
        TrnCourse course = courseService.selectByPrimaryKey(requestCtx, trnCourse);
		  for (TrnCourseFile courseFile : dto) {
			  courseFile.setCourseId(courseId);
			  courseFile.setFileContent(courseFile.getFileName());
			  courseFile.setFileNum(sequenceService.getSequence("FILENUM"));
			  //将产品公司设置进去
			  courseFile.setSupplierId(course.getSupplierId());
			  //将大分类设置进去
			  courseFile.setPrdDivision(course.getPrdDivision());
			  //将中分类设置进去
			  courseFile.setPrdClass(course.getPrdClass());
		}
		List<TrnCourseFile> batchUpdate = service.batchUpdate(requestCtx, dto);
		System.out.println(batchUpdate.toString());
		return new ResponseData(batchUpdate);
        //return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @RequestMapping(value = "/fms/trn/course/file/submitTrain")
    @ResponseBody
    public ResponseData updateTrain(HttpServletRequest request,@RequestBody List<TrnCourseFile> dto){
        IRequest requestCtx = createRequestContext(request);
        	TrnCourseFile trnCourseFile = dto.get(0);
        	//TrnCourseFile trnCourseFile2 = service.updateByPrimaryKey(requestCtx, trnCourseFile);
        	//获得文件内容
        	String fileContent = trnCourseFile.getFileContent();
        	//判断文件内容是否为空  为空的话设为文件名
            if (fileContent==null||"".equals(fileContent.trim())) {
    			SysFile sysFile = fileService.selectByPrimaryKey(requestCtx, trnCourseFile.getFileId());
    			trnCourseFile.setFileContent(sysFile.getFileName());
    		}else {
    			trnCourseFile.setFileContent(fileContent);
			}
        	TrnCourseFile trnCourseFile2 = service.updateByPrimaryKeySelective(requestCtx, trnCourseFile);
	        List<TrnCourseFile> list = new ArrayList<>();
	        list.add(trnCourseFile2);
	        return new ResponseData(list);
		  //return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    
    @RequestMapping(value="/fms/trn/course/file/add")
    @ResponseBody
    public ResponseData add(HttpServletRequest request, @RequestBody List<TrnCourseFile> obj){

        IRequest requestContext = createRequestContext(request);
        TrnCourseFile trnCourseFile = obj.get(0);
        if (trnCourseFile.getFileNum() == null || trnCourseFile.getFileNum() == "") {
        	trnCourseFile.setFileNum(sequenceService.getSequence("FILENUM"));
        }
        //获取文件内容
        String fileContent = trnCourseFile.getFileContent();
        //判断文件内容是否为空  为空的话设置为文件名不为空设置到课程文件中
        if (fileContent==null||"".equals(fileContent.trim())) {
			SysFile sysFile = fileService.selectByPrimaryKey(requestContext, trnCourseFile.getFileId());
			trnCourseFile.setFileContent(sysFile.getFileName());
		}else {
			trnCourseFile.setFileContent(fileContent);
		}     
		TrnCourseFile insertSelective = service.insertSelective(requestContext,trnCourseFile);
		List<TrnCourseFile> list = new ArrayList<>();
		list.add(insertSelective);
        return new ResponseData(list);
    }
    
    /*@RequestMapping(value = "/fms/sys/workbench/module/cfg/add")
	@ResponseBody
	public ResponseData add(HttpServletRequest request, @RequestBody List<SysWorkbenchModuleCfg> dto) {
		IRequest requestCtx = createRequestContext(request);
		// 设置默认的发布状态为未发布
		SysWorkbenchModuleCfg sysWorkbenchModuleCfg = dto.get(0);
		SysWorkbenchModuleCfg ntnNotificationTemplate2 = service.insertSelective(requestCtx, sysWorkbenchModuleCfg);
		//下面的操作  没有意义  为了符合  内部定义的js的规范
		List<SysWorkbenchModuleCfg> list = new ArrayList<>();
		list.add(ntnNotificationTemplate2);
		return new ResponseData(list);
	}*/
    
    @RequestMapping(value = "/fms/trn/course/file/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<TrnCourseFile> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }

    }