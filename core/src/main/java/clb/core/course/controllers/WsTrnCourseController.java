package clb.core.course.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import clb.core.course.dto.*;
import clb.core.payment.service.IPaymentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;

import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.mapper.CnlChannelContractMapper;
import clb.core.common.utils.CommonUtil;
import clb.core.course.mapper.TrnCourseEvaluateMapper;
import clb.core.course.mapper.TrnCourseStudentMapper;
import clb.core.course.mapper.TrnSupportTeacherMapper;
import clb.core.course.service.ITrnCourseEvaluateService;
import clb.core.course.service.ITrnCourseFileService;
import clb.core.course.service.ITrnCourseService;
import clb.core.course.service.ITrnCourseStudentService;
import clb.core.course.service.ITrnSupportService;
import clb.core.sys.controllers.ClbBaseController;
import clb.core.system.service.IClbUserService;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class WsTrnCourseController extends ClbBaseController {
	@Autowired
	private ITrnCourseService trnCourseService;
	@Autowired
	private ITrnCourseFileService trnCourseFileService;
	@Autowired
	private ITrnCourseEvaluateService trnCourseEvaluateService;
	@Autowired
	private ITrnCourseStudentService trnCourseStudentService;
	@Autowired
	private ITrnSupportService trnSupportService;
	@Autowired
	private IClbUserService clbUserService;
	@Autowired
	private TrnCourseEvaluateMapper trnCourseEvaluateMapper;
	@Autowired
	private TrnSupportTeacherMapper trnSupportTeacherMapper;
	@Autowired
	private TrnCourseStudentMapper trnCourseStudentMapper;
	@Autowired
	private CnlChannelContractMapper cnlChannelContractMapper;
	@Autowired
	private IPaymentOrderService paymentOrderService;
	@Autowired
	private MessageSource messageSource;
	/****
	 * 课程列表
	 * 
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsCourseList")
	@RequestMapping(value = "/api/course/list", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData queryList(@RequestBody TrnCourse dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		List<TrnCourse> selectAllField = trnCourseService.wsSelectAllField(requestContext, dto, page, pageSize);
		for(TrnCourse trnCourse : selectAllField){
			if(trnCourse.getFilePath()!=null&&trnCourse.getFilePath()!=""){
				String  path = messageSource.getMessage("fms.pub.oss_web_path",null, RequestContextUtils.getLocale(request));
				trnCourse.setFilePath(path+trnCourse.getFilePath());
			}
		}
		return new ResponseData(selectAllField);
	}

	/****
	 * 课程列表public
	 *
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsCourseListPublic")
	@RequestMapping(value = "/api/public/course/list", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData queryList_public(@RequestBody TrnCourse dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
								  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		List<TrnCourse> selectAllField = trnCourseService.wsSelectAllField(requestContext, dto, page, pageSize);
		List<AppTrnCourse> returnList = new ArrayList<>();
		for(TrnCourse trnCourse : selectAllField){
			if(trnCourse.getFilePath() != null && !"".equals(trnCourse.getFilePath())){
				String  path = messageSource.getMessage("fms.pub.oss_web_path",null, RequestContextUtils.getLocale(request));
				trnCourse.setFilePath(path+trnCourse.getFilePath());
			}
			AppTrnCourse appTrnCourse = new AppTrnCourse(trnCourse.getTopic(), trnCourse.getCourseDate(), trnCourse.getAddress(),
					trnCourse.getFilePath(), trnCourse.getStudentNum(), trnCourse.getCourseIntroduction(), trnCourse.getLecturer(),
					trnCourse.getLecturerIntroduction(), trnCourse.getUrl(), trnCourse.getPassword(), trnCourse.getTrainingMethod(),
					trnCourse.getCoursePrice(), trnCourse.getAppPrice(), trnCourse.getBoutiqueVideo(), trnCourse.getBoutiqueUrl());
			returnList.add(appTrnCourse);
		}
		return new ResponseData(returnList);
	}

	/****
	 * APP近期课程列表
	 *
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "AppWsCourseListPublicPublic")
	@RequestMapping(value = "/api/public/app/course/list", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData queryAppCourseList(@RequestBody TrnCourse dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
								  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		List<TrnCourse> selectAllField = trnCourseService.queryAppCL(requestContext, dto, page, pageSize);
		return new ResponseData(selectAllField);
	}
	
	/****
	 * 课程列表
	 * 
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsCourseList")
	@RequestMapping(value = "/api/course/enrollList", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData enrollList(@RequestBody TrnCourse dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		dto.setCreatedBy(requestContext.getUserId());
		List<TrnCourse> selectAllField = trnCourseService.wsSelectAllField(requestContext, dto, page, pageSize);
		return new ResponseData(selectAllField);
	}

	/******
	 * 课程附件
	 * 
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsCourseFileList")
	@RequestMapping(value = "/api/course/fileList", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData fileList(@RequestBody TrnCourseFile dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		List<TrnCourseFile> selectAllField = trnCourseFileService.selectByParams(requestContext, dto, page, pageSize);
		// 判断是否签约渠道，如果不是 ，则不显示直播密码 ，不允许课程资料下载、培训资料 下载
		// -------渠道信息是否存在合约信息
		boolean isSignChannel = true;
		CnlChannelContract cnlChannelContract = new CnlChannelContract();
		if(requestContext.getAttribute("channelId")!=null){
			cnlChannelContract.setChannelId(requestContext.getAttribute("channelId"));
		}
		if (CollectionUtils.isEmpty(cnlChannelContractMapper.select(cnlChannelContract))) {
			isSignChannel = false;
		}
		if(!isSignChannel){
			for(TrnCourseFile file:selectAllField){
				file.setFilePath(null);
				file.setFileId(null);
			}
		}
		return new ResponseData(selectAllField);
	}

	/*****
	 * 更新下载次数
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsCourseUDTimes")
	@RequestMapping(value = "/api/course/updateDowloadTimest", method = RequestMethod.POST, produces = {
			"application/json" })
	@ResponseBody
	public ResponseData updateDowloadTimest(@RequestBody TrnCourseFile dto, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		try {
			trnCourseFileService.updateDowloadTimes(requestContext, dto);
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			return new ResponseData(false);
		}
		return new ResponseData(true);
	}

	/****
	 * 评价
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsCoursEevaluate")
	@RequestMapping(value = "/api/course/evaluate", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData evaluate(HttpServletRequest request, @RequestBody TrnCourseEvaluate dto) {
		IRequest requestCtx = createRequestContext(request);
		try {
			TrnCourseStudent student = new TrnCourseStudent();
			student.setPhoneNumber(requestCtx.getAttribute("phone"));
			student.setCourseId(dto.getCourseId());
			student.setCreatedBy(requestCtx.getUserId());
			List<TrnCourseStudent>studentList=trnCourseStudentService.selectEnrollByParams(requestCtx,student);
			if (CollectionUtils.isEmpty(studentList)) {
				ResponseData response = new ResponseData(false);
				response.setMessage("该课程未报名,无权评价！");
				return response;
			}

			TrnCourseEvaluate trnCourseEvaluate = new TrnCourseEvaluate();
			trnCourseEvaluate.setCourseId(dto.getCourseId());
			trnCourseEvaluate.setCreatedBy(requestCtx.getUserId());
			if (CollectionUtils.isEmpty(trnCourseEvaluateMapper.selectEvaluateByParams(trnCourseEvaluate))) {
				if(requestCtx.getAttribute("channelId")!=null){
					dto.setChannelId(requestCtx.getAttribute("channelId"));
				}
				trnCourseEvaluateService.insertSelective(requestCtx, dto);
			} else {
				ResponseData response = new ResponseData(false);
				response.setMessage("该课程已评价,无法再次评价！");
				return response;
			}
		} catch (Exception e) {
			ResponseData response = new ResponseData(false);
			response.setMessage(e.getMessage());
			return response;
		}
		return new ResponseData(true);
	}

	/****
	 * 评价列表
	 * 
	 * @param request
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsCoursEevaluateList")
	@RequestMapping(value = "/api/course/evaluateList", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData evaluateList(HttpServletRequest request, @RequestBody TrnCourseEvaluate dto,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
		IRequest requestCtx = createRequestContext(request);
		return new ResponseData(trnCourseEvaluateService.selectAllField(requestCtx, dto, page, pageSize));
	}

	/***
	 * 报名
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsStudentEnroll")
	@RequestMapping(value = "/api/student/enroll", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData enroll(HttpServletRequest request, @RequestBody List<TrnCourseStudent> dto) {
		IRequest requestCtx = createRequestContext(request);
		return trnCourseStudentService.batchEnroll(requestCtx, dto);
	}

	/***
	 * 支援申请
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsSupportSubmit")
	@RequestMapping(value = "/api/support/submit", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData supportSubmit(HttpServletRequest request, @RequestBody TrnSupport dto) {
		ResponseData response = new ResponseData(true);
		IRequest requestCtx = createRequestContext(request);
		try {
			if (dto.getSupportId() == null) {
				if(requestCtx.getAttribute("channelId")!=null){
					dto.setChannelId(requestCtx.getAttribute("channelId"));
				}
				dto.setStatus("APPROVAL");
				trnSupportService.insertSelective(requestCtx, dto);
				dto.setSupportNum("S" + (10000 + dto.getSupportId()));
				trnSupportService.updateByPrimaryKeySelective(requestCtx, dto);
			} else {
				dto.setStatus("APPROVAL");
				trnSupportService.updateByPrimaryKeySelective(requestCtx, dto);
			}
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			response.setSuccess(false);
			response.setMessage(e.getMessage());
		}
		return response;
	}

	/***
	 * 支援申请 保存
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsSupportUpdate")
	@RequestMapping(value = "/api/support/update", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData supportUpdate(HttpServletRequest request, @RequestBody TrnSupport dto) {
		ResponseData response = new ResponseData(true);
		IRequest requestCtx = createRequestContext(request);
		try {
			if (dto.getSupportId() == null) {
				response.setSuccess(false);
				response.setMessage("申请单ID不能为空");
			} else {
				// 防止前端恶意修改状态
				dto.setStatus(null);
				trnSupportService.updateByPrimaryKeySelective(requestCtx, dto);
			}
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			response.setSuccess(false);
			response.setMessage(e.getMessage());
		}
		return response;
	}

	/****
	 * 取消支援申请
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsSupportCancel")
	@RequestMapping(value = "/api/support/cancel", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData supportCancel(HttpServletRequest request, @RequestBody TrnSupport dto) {
		ResponseData response = new ResponseData(true);
		IRequest requestCtx = createRequestContext(request);
		try {
			if (dto.getSupportId() == null) {
				response.setSuccess(false);
				response.setMessage("申请单ID不能为空");
			} else {
				dto.setStatus("CANCEL");
				trnSupportService.updateByPrimaryKeySelective(requestCtx, dto);
			}
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			response.setSuccess(false);
			response.setMessage(e.getMessage());
		}
		return response;
	}

	/*****
	 * 我的预约--业务支援
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsSupportQuery")
	@RequestMapping(value = "/api/support/query", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData supportQuery(HttpServletRequest request, @RequestBody TrnSupport dto,
			@RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
		ResponseData response = new ResponseData(true);
		List<TrnSupport> supportList = new ArrayList<TrnSupport>();
		IRequest requestCtx = createRequestContext(request);
		try {
			if (dto.getChannelId() == null && requestCtx.getAttribute("channelId")!=null) {
				dto.setChannelId(requestCtx.getAttribute("channelId"));
			}
			if (dto.getSupportId() != null) {
				dto = trnSupportService.selectByPrimaryKey(requestCtx, dto);
				TrnSupportTeacher trnSupportTeacher = new TrnSupportTeacher();
				trnSupportTeacher.setSupportId(dto.getSupportId());
				;
				dto.setTeacherList(trnSupportTeacherMapper.select(trnSupportTeacher));
				supportList.add(dto);
			} else {
				supportList = trnSupportService.selectAllField(requestCtx, dto, page, pageSize);
			}
			return new ResponseData(supportList);
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			response.setSuccess(false);
			response.setMessage(e.getMessage());
		}
		return response;
	}

	/*****
	 * 我的预约--业务支援  角标显示
	 *
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsSupportQueryTotal")
	@RequestMapping(value = "/api/support/supportQueryTotal", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public Long supportQueryTotal(HttpServletRequest request, @RequestBody TrnSupport dto) {
		IRequest requestCtx = createRequestContext(request);
		Long total = 0L;
		try {
			if (dto.getChannelId() == null && requestCtx.getAttribute("channelId")!=null) {
				dto.setChannelId(requestCtx.getAttribute("channelId"));
			}
			 total = trnSupportService.supportQueryTotal(requestCtx, dto);
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
		}
		return total;
	}

	/****
	 * 支付成功--支援申请状态修改为已支付
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsSupportPayOff")
	@RequestMapping(value = "/api/support/payOff", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData supportPayOff(HttpServletRequest request, @RequestBody TrnSupport dto) {
		ResponseData response = new ResponseData(true);
		IRequest requestCtx = createRequestContext(request);
		try {
			if (dto.getSupportId() == null) {
				response.setSuccess(false);
				response.setMessage("申请单ID不能为空");
			} else {
				/*
				 * TrnSupport newDto=new TrnSupport();
				 * newDto.setSupportId(dto.getSupportId());
				 * newDto=trnSupportService.selectByPrimaryKey(requestCtx,
				 * newDto); if(newDto.getStatus().equals("PAYMENT")){
				 * dto.setStatus("AMOUNT");
				 * trnSupportService.updateByPrimaryKeySelective(requestCtx,
				 * dto); }else{ response.setSuccess(false);
				 * response.setMessage("该申请单的状态非待支付状态！"); }
				 */
				dto.setStatus("AMOUNT");
				trnSupportService.updateByPrimaryKeySelective(requestCtx, dto);
			}
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			response.setSuccess(false);
			response.setMessage(e.getMessage());
		}
		return response;
	}

	/****
	 * app支付成功后调用的接口（跟新数据及跟新记录表）
	 *
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsUpdateCourseStatus")
	@RequestMapping(value = "/api/trn/course/updateCourseStatus", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData updateCourseStatus(HttpServletRequest request,@RequestBody TrnCourseStudent dto){
		IRequest requestContext = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		paymentOrderService.insertPaymentOrder(requestContext,dto);
		return responseData;
	}

	/****
	 * app支付成功后调用的接口（跟新数据及跟新记录表）
	 *
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsUpdateCourseStatusPublic")
	@RequestMapping(value = "/api/public/trn/course/updateCourseStatus", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public ResponseData updateCourseStatusPublic(HttpServletRequest request,@RequestBody TrnCourseStudent dto){
		IRequest requestContext = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		paymentOrderService.insertPaymentOrder(requestContext,dto);
		return responseData;
	}
}
