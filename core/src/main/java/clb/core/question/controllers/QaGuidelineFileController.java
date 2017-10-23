package clb.core.question.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.question.dto.QaGuideline;
import clb.core.question.dto.QaGuidelineFile;
import clb.core.question.service.IQaGuidelineFileService;
import clb.core.release.dto.NtcSlideshow;
import oracle.jdbc.proxy.annotation.Post;

import org.apache.regexp.recompile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({ "/question/guideline/file"})
public class QaGuidelineFileController extends BaseController {

	@Autowired
	private IQaGuidelineFileService service;
	 @Autowired
	private ISysFileService fileService;

	@RequestMapping(value = "/quertSort")
	@ResponseBody
	public ResponseData quertSort(QaGuidelineFile dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		List<QaGuidelineFile> list = new ArrayList<>();
		if(dto.getGuidelineId() != null && dto.getGuidelineId() != 0){
			list = service.quertSort(requestContext, dto, page, pageSize);
			if(list.size() > 0){
				for (QaGuidelineFile qaGuidelineFile : list) {
					if(qaGuidelineFile.getFileId() != null){
						SysFile sysFile = fileService.selectByPrimaryKey(requestContext, qaGuidelineFile.getFileId());
						if (sysFile != null) {
							qaGuidelineFile.set_token(sysFile.get_token());
						} else {
							qaGuidelineFile.set_token(null);
						}
					}
				}
			}
		}
		return new ResponseData(list);
	}

	@RequestMapping(value = "/submit")
	@ResponseBody
	public ResponseData update(HttpServletRequest request, @RequestBody List<QaGuidelineFile> dto) {
		IRequest requestCtx = createRequestContext(request);
		return new ResponseData(service.batchUpdate(requestCtx, dto));
	}
	/**
	 * 自定义根据fileId删除记录
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/remove")
	@ResponseBody
	public ResponseData removeByFileId(HttpServletRequest request, Long fileId) {
		IRequest requestCtx = createRequestContext(request);
		SysFile file = new SysFile();
		if(fileId != null){
			service.removeByFileId(fileId);
			file.setFileId(fileId);
			fileService.deleteImage(requestCtx, file);
		}
		
		return new ResponseData();
	}
	/**
	 * 保存上传文件的ID
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/saveFileIds", method=RequestMethod.POST)
	@ResponseBody
	public ResponseData saveFileIds(HttpServletRequest request,@RequestParam("guidelineId") long guidelineId,
            @RequestParam("fileIds[]") List<Long> fileIds) {
		IRequest requestCtx = createRequestContext(request);
		//判断传递过来的数组有没有数据
		if(fileIds.size() > 0){
			for (Long fileId : fileIds) {
				QaGuidelineFile guidelineFile = new QaGuidelineFile();
				guidelineFile.setFileId(fileId);
				guidelineFile.setGuidelineId(guidelineId);
				service.insertSelective(requestCtx, guidelineFile);
			}
		}
		return new ResponseData();
	}
	
	/**
	 * 查询文件的路径
	 * @param request
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/queryFilePathByFileId", method=RequestMethod.POST)
	@ResponseBody
	public ResponseData queryFilePathByFileId(HttpServletRequest request,@RequestParam("fileId") long fileId) {
		IRequest requestCtx = createRequestContext(request);
		
		SysFile sysFile = fileService.selectByPrimaryKey(requestCtx, fileId);
		List<SysFile> list = new ArrayList<>();
		list.add(sysFile);
		return new ResponseData(list);
	}
	
	@RequestMapping(value = "/moveUp")
	@ResponseBody
	public ResponseData moveUp(HttpServletRequest request,QaGuidelineFile dto) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		
		QaGuidelineFile qaGuidelineFile = service.queryUpRank(dto);
		
		if(qaGuidelineFile == null){
			responseData.setSuccess(false);
			responseData.setMessage("已经在最上面了!");
			return responseData;
		}else{
			try {
				Long rank = qaGuidelineFile.getRank();
				qaGuidelineFile.setRank(dto.getRank());
				dto.setRank(rank);
				
				service.updateByPrimaryKeySelective(requestCtx, qaGuidelineFile);
				service.updateByPrimaryKeySelective(requestCtx, dto);
				/*responseData.setMessage("操作成功!");*/
				return responseData;
			} catch (Exception e) {
				responseData.setSuccess(false);
				/*responseData.setMessage("操作失败!");*/
				return responseData;
			}
		}
	}
	@RequestMapping(value = "/moveDown")
	@ResponseBody
	public ResponseData moveDown(HttpServletRequest request,QaGuidelineFile dto) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		
		QaGuidelineFile qaGuidelineFile = service.queryDownRank(dto);
		if(qaGuidelineFile == null){
			responseData.setSuccess(false);
			responseData.setMessage("已经在最下面了!");
			return responseData;
		}else{
			try {
				Long rank = qaGuidelineFile.getRank();
				qaGuidelineFile.setRank(dto.getRank());
				dto.setRank(rank);
				service.updateByPrimaryKeySelective(requestCtx, qaGuidelineFile);
				service.updateByPrimaryKeySelective(requestCtx, dto);
				
				responseData.setMessage("操作成功!");
				return responseData;
			} catch (Exception e) {
				responseData.setSuccess(false);
				responseData.setMessage("操作失败!");
				return responseData;
			}
		}
	}

}