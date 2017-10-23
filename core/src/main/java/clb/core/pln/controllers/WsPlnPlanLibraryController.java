package clb.core.pln.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IProfileService;

import clb.core.common.utils.CommonUtil;
import clb.core.pln.dto.PlnPlanLibrary;
import clb.core.pln.dto.PlnPlanRequest;
import clb.core.pln.dto.PlnPlanRequestAdtlRisk;
import clb.core.pln.dto.PlnPlanRequestExtract;
import clb.core.pln.dto.QueryAmount;
import clb.core.pln.service.IPlnPlanLibraryService;
import clb.core.pln.service.IPlnPlanRequestAdtlRiskService;
import clb.core.pln.service.IPlnPlanRequestExtractService;
import clb.core.pln.service.IPlnPlanRequestService;
import clb.core.production.dto.PrdItemSelfpay;
import clb.core.production.service.IPrdItemSelfpayService;
import clb.core.sys.controllers.ClbBaseController;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class WsPlnPlanLibraryController extends ClbBaseController{

	@Autowired
	private IPlnPlanLibraryService plnPlanLibraryService;

	@Autowired
	private IPlnPlanRequestService plnPlanRequestService;

	@Autowired
	private IPlnPlanRequestAdtlRiskService plnPlanRequestAdtlRiskService;

	@Autowired
	private IPlnPlanRequestExtractService plnPlanRequestExtractService;

	@Autowired
	private ISysFileService fileService;

	@Autowired
	private IPrdItemSelfpayService prdItemSelfpayService;
	
	@Autowired
	private IProfileService profileService;


	/**
	 * 计划书库查询
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsPlnPlanLibrary")
	@RequestMapping(value = "/api/plan/library", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData querLlist(@RequestBody PlnPlanLibrary dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
								  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request){
		IRequest requestContext = createRequestContext(request);
		List<PlnPlanLibrary> PlnPlanLibraryList = plnPlanLibraryService.selectLibraryInfo(requestContext,dto,page,pageSize);
		return new ResponseData(PlnPlanLibraryList);
	}
	
	/**
	 * 计划书库查询为产品
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsPlnPlanLibrary")
	@RequestMapping(value = "/api/plan/libraryForPrd", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData querLlistForPrd(@RequestBody PlnPlanLibrary dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
								  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request){
		IRequest requestContext = createRequestContext(request);
		List<PlnPlanLibrary> PlnPlanLibraryList = plnPlanLibraryService.selectLibraryInfoForPrd(requestContext,dto,page,pageSize);
		return new ResponseData(PlnPlanLibraryList);
	}

	/**
	 * 我的计划书查询
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsPlnMyPlan")
	@RequestMapping(value = "/api/plan/myPlan", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData myPlan(@RequestBody PlnPlanRequest dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
							   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request){
		IRequest requestContext = createRequestContext(request);
		//如果是个人
		if("personal".equalsIgnoreCase(dto.getType())){
//			dto.setCreatedBy(requestContext.getUserId());
			return new ResponseData(plnPlanRequestService.selectPlanRequest(dto, requestContext, page, pageSize));
		}
		//如果是团队
		if("team".equalsIgnoreCase(dto.getType())){
			//查询团队的人
			List<PlnPlanRequest> plnPlanRequestList = plnPlanRequestService.selectTeamUser(requestContext.getUserId());
			List<Long> teamIds = new ArrayList<Long>();
			//判断集合是否为空
			if(plnPlanRequestList != null){
				for (PlnPlanRequest plnPlanRequest : plnPlanRequestList) {
					teamIds.add(plnPlanRequest.getUserId());
				}
			}
			if(teamIds !=null && teamIds.size()>=1){
				dto.setTeamIds(teamIds);
				return new ResponseData(plnPlanRequestService.selectTeamPlanRequest(dto, requestContext, page, pageSize));
			}else{
				return new ResponseData();
			}

		}else{
			return new ResponseData();
		}

	}

    /**
     * 获取我的计划书角标数量
     *
     * @param plnPlanLibrary
     * @param request
     * @return
     */
	@Timed
	@HapInbound(apiName = "ClbWsPlnMyPlanCount")
	@RequestMapping(value = "/api/plan/getMyPlanCount", method = RequestMethod.POST)
	@ResponseBody
	public Integer getMyPlanCount(@RequestBody PlnPlanLibrary plnPlanLibrary, HttpServletRequest request){
		IRequest requestContext = createRequestContext(request);
		plnPlanLibrary.setUserId(requestContext.getUserId());
		return plnPlanLibraryService.queryMyPlanCount(plnPlanLibrary, requestContext);
	}

	/**
	 * 查询计划书额度信息
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsPlnQueryAmount")
	@RequestMapping(value = "/api/plan/queryAmount", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData queryAmount(@RequestBody PlnPlanRequest dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
									@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request){
		List<QueryAmount> queryAmountList= new ArrayList<QueryAmount>();
		QueryAmount queryAmount = new QueryAmount();
		queryAmount.setTotalAmount(plnPlanRequestService.selectPlanTotalByUser(dto.getUserId()));
		queryAmount.setUsedAmount(plnPlanRequestService.selectPlanCountByUser(dto.getUserId()));
		queryAmount.setAvilAmount(queryAmount.getTotalAmount()-queryAmount.getUsedAmount());
		queryAmountList.add(queryAmount);
		return new ResponseData(queryAmountList);
	}

	/**
	 * 查询团队成员额度
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsPlnQueryTeamAmount")
	@RequestMapping(value = "/api/plan/queryTeamAmount", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData queryTeamAmount(@RequestBody PlnPlanRequest dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
										@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request){
		List<QueryAmount> queryAmountList= new ArrayList<QueryAmount>();
		String userType = plnPlanRequestService.selectRoleType(dto.getUserId());
		List<PlnPlanRequest> plnPlanRequestList = new ArrayList<PlnPlanRequest>();
		if("ADMINISTRATION".equals(userType)){
			plnPlanRequestList = plnPlanRequestService.selectTeamUserByAgency(dto.getUserId());
		}else{
		    plnPlanRequestList = plnPlanRequestService.selectTeamUser(dto.getUserId());
		}

		for (PlnPlanRequest plnPlanRequest : plnPlanRequestList) {
			QueryAmount queryAmount = new QueryAmount();
			queryAmount.setTotalAmount(plnPlanRequestService.selectPlanTotalByUser(Long.valueOf(plnPlanRequest.getUserId())));
			queryAmount.setUsedAmount(plnPlanRequestService.selectPlanCountByUser(Long.valueOf(plnPlanRequest.getUserId())));
			queryAmount.setAvilAmount(queryAmount.getTotalAmount()-queryAmount.getUsedAmount());
			queryAmount.setChannelName(plnPlanRequest.getChannelName());
			queryAmountList.add(queryAmount);
		}
		return new ResponseData(queryAmountList);
	}


	/**
	 * 计划书申请保存
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsPlnRequestSubmit")
	@RequestMapping(value = "/api/plan/requestSubmit", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData requestSubmit(HttpServletRequest request,@RequestBody PlnPlanRequest dto){
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		try {
			if(dto.getPlanId() == null){
				responseData = plnPlanRequestService.requestSubmit(requestCtx, dto);
			}else{
				responseData = plnPlanRequestService.requestUpdate(requestCtx, dto);
			}
			//自动执行爬虫获取
			if(responseData.getMessage()!="成功匹配计划书库中的文件!"){
				if("Y".equals(dto.getCrawlerFlag())){
					ResponseData res = plnPlanRequestService.exePlnSpider(dto, requestCtx);
					responseData.setMessage("计划书获取需要一定时间，请耐心等待站内通知");
				}else{
					responseData.setMessage("计划书获取需要一定时间，请耐心等待站内通知");
				}
			}

			return responseData;
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			responseData.setSuccess(false);
			responseData.setMessage(e.getMessage()==null?"未知异常!":e.getMessage());
			return responseData;
		}

	}

	/**
	 * 文件下载
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsPlnFilePath")
	@RequestMapping(value = "/api/plan/filePath", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData queryFilePath(HttpServletRequest request,@RequestBody PlnPlanLibrary dto){
		IRequest requestCtx = createRequestContext(request);
		List<SysFile> sysFileList= new ArrayList<SysFile>();
		SysFile sysFile = fileService.selectByPrimaryKey(requestCtx, dto.getFileId());
		if(sysFile != null){
			sysFileList.add(sysFile);
		}
		return new ResponseData(sysFileList);
	}

	/**
	 * 查询附加险信息
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsPlnQueryAdtRisk")
	@RequestMapping(value = "/api/plan/queryAdtRisk", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData queryAdtRisk(@RequestBody PlnPlanRequestAdtlRisk dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
									 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(plnPlanRequestAdtlRiskService.selectAdtlRisk(dto.getPlanId()));
	}

	/**
	 * 提交附加险信息
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsPlnSubmitAdtRisk")
	@RequestMapping(value = "/api/plan/updateAdtRisk", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData updateAdtRisk(HttpServletRequest request,@RequestBody List<PlnPlanRequestAdtlRisk> dto){
		IRequest requestCtx = createRequestContext(request);
		for (PlnPlanRequestAdtlRisk plnPlanRequestAdtlRisk : dto) {
			if(plnPlanRequestAdtlRisk.getLineId() != null){
				plnPlanRequestAdtlRisk.set__status("update");
			}else{
				plnPlanRequestAdtlRisk.set__status("add");
			}
		}
		return new ResponseData(plnPlanRequestAdtlRiskService.batchUpdate(requestCtx, dto));
	}

	/**
	 * 查询提取信息
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsPlnQueryExtract")
	@RequestMapping(value = "/api/plan/queryExtract", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData queryExtract(@RequestBody PlnPlanRequestExtract dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
									 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(plnPlanRequestExtractService.select(requestContext,dto,page,pageSize));
	}

	/**
	 * 提取信息更新
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsPlnUpdateExtract")
	@RequestMapping(value = "/api/plan/updateExtract", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData updateExtract(HttpServletRequest request,@RequestBody List<PlnPlanRequestExtract> dto){
		IRequest requestCtx = createRequestContext(request);
		for (PlnPlanRequestExtract plnPlanRequestExtract : dto) {
			if(plnPlanRequestExtract.getLineId() != null){
				plnPlanRequestExtract.set__status("update");
			}else{
				plnPlanRequestExtract.set__status("add");
			}
		}
		return new ResponseData(plnPlanRequestExtractService.batchUpdate(requestCtx, dto));
	}


	@Timed
	@HapInbound(apiName = "ClbWsPlnSelfpayQuery")
	@RequestMapping(value = "/api/plan/item/selfpay", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData querySelfpayByItemId(HttpServletRequest request,@RequestBody PrdItemSelfpay dto){
		return new ResponseData(prdItemSelfpayService.selectByItemId(dto.getItemId()));
	}


	@Timed
	@HapInbound(apiName = "ClbWsUpdatePlnCrawlersInfo")
	@RequestMapping(value = "/api/plan/update/crawlersInfo", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData updatePlnCrawlersInfo(HttpServletRequest request,@RequestBody PlnPlanRequest dto,HttpServletResponse response){
		IRequest requestCtx = createRequestContext(request);
        plnPlanRequestService.handingPdf(dto,response,request,requestCtx);
		return new ResponseData();
	}

	@Timed
	@HapInbound(apiName = "ClbWsQueryPlnQuerySecurityLevelCode")
	@RequestMapping(value = "/api/pln/plan/library/querySecurityLevelCode", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData querySecurityLevelCode(HttpServletRequest request,@RequestBody PlnPlanLibrary dto){
		IRequest requestCtx = createRequestContext(request);
		return new ResponseData(plnPlanLibraryService.querySecurityLevelByItem(dto.getItemId()));
	}


	@Timed
	@HapInbound(apiName = "ClbWsQueryPlnQuerySecurityAreaCode")
	@RequestMapping(value = "/api/pln/plan/library/querySecurityAreaCode", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData querySecurityAreaCode(HttpServletRequest request,@RequestBody PlnPlanLibrary dto){
		IRequest requestCtx = createRequestContext(request);
		return new ResponseData(plnPlanLibraryService.querySecurityAreaByItem(dto.getItemId(),dto.getSecurityLevel()));
	}


	@Timed
	@HapInbound(apiName = "ClbWsQueryPlnQuerySublineCode")
	@RequestMapping(value = "/api/pln/plan/library/querySublineCode", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData querySublineCode(HttpServletRequest request,@RequestBody PlnPlanLibrary dto){
		IRequest requestCtx = createRequestContext(request);
		return new ResponseData(plnPlanLibraryService.querySublineItemNameByItem(dto.getItemId()));
	}

	@Timed
	@HapInbound(apiName = "ClbWsQueryPlnQuerySelfPayByItem")
	@RequestMapping(value = "/api/pln/plan/library/querySelfPayByItem", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData querySelfPayByItem(HttpServletRequest request,@RequestBody PlnPlanLibrary dto){
		IRequest requestCtx = createRequestContext(request);
		return new ResponseData(plnPlanLibraryService.querySelfpayByItem(dto.getItemId()));
	}

	@Timed
	@HapInbound(apiName = "ClbWsUpdatePlnStatusInfo")
	@RequestMapping(value = "/api/plan/update/plnStatusInfo", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData plnStatusInfo(HttpServletRequest request,@RequestBody PlnPlanRequest dto){
		IRequest requestCtx = createRequestContext(request);
		PlnPlanRequest plnPlanRequest = plnPlanRequestService.selectByPrimaryKey(requestCtx, dto);
		plnPlanRequest.setStatus(dto.getStatus());
		if(plnPlanRequest.getStatus().equals("PLN_CANCELLED")){
			plnPlanRequest.setQuantCalcFlag("N");
		}
		plnPlanRequestService.updateByPrimaryKeySelective(requestCtx, plnPlanRequest);
		return new ResponseData();
	}

	/**
	 * 更新状态
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsUpdatePlnDownloadFlagInfo")
	@RequestMapping(value = "/api/plan/update/downloadFlagInfo", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData downloadFlagInfo(HttpServletRequest request,@RequestBody PlnPlanRequest dto){
		IRequest requestCtx = createRequestContext(request);
		PlnPlanRequest plnPlanRequest = plnPlanRequestService.selectByPrimaryKey(requestCtx, dto);
		plnPlanRequest.setDownloadFlag(dto.getDownloadFlag());
		plnPlanRequestService.updateByPrimaryKeySelective(requestCtx, plnPlanRequest);
		return new ResponseData();
	}

	@Timed
	@HapInbound(apiName = "ClbWsQueryPaymethodByItem")
	@RequestMapping(value = "/api/pln/plan/library/queryPaymethodByItem", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData queryPaymethodByItem(HttpServletRequest request,@RequestBody PlnPlanLibrary dto){
	    IRequest requestCtx = createRequestContext(request);
	    return new ResponseData(plnPlanLibraryService.queryPaymethodByItem(requestCtx,dto));
	}

	@Timed
	@HapInbound(apiName = "ClbWsQueryChanneAvilAmount")
	@RequestMapping(value = "/api/pln/plan/library/queryChanneAvilAmount", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData queryChanneAvilAmount(HttpServletRequest request,@RequestBody PlnPlanLibrary dto){
	    IRequest requestCtx = createRequestContext(request);
	    return new ResponseData(plnPlanLibraryService.queryChannelPlanQuota(dto));
	}
	
	/**
	 * 我的计划书查询
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @param request
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsPlnMyPlanForWX")
	@RequestMapping(value = "/api/plan/queryMyPlanForWX", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData queryMyPlanForWX(@RequestBody PlnPlanRequest dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
							   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request){
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(plnPlanRequestService.selectMyPlanForWX(dto, requestContext, page, pageSize));
	}
	/**
	 * 微信查询本年申请的计划书数量
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsQueryMyPlanCount")
	@RequestMapping(value = "/api/plan/request/queryMyPlanCount", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public int queryMyPlanCount(HttpServletRequest request,@RequestBody PlnPlanRequest dto){
		IRequest requestCtx = createRequestContext(request);
		return plnPlanRequestService.queryMyPlanCount(requestCtx,dto);
	}
	/**
	 * 查询配置文件配置的增加的计划书额度
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "ClbWsQueryAddPlanCount")
	@RequestMapping(value = "/api/plan/request/queryAddPlanCount", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public String queryAddPlanCount(HttpServletRequest request,@RequestBody PlnPlanRequest dto){
		IRequest requestCtx = createRequestContext(request);
		return profileService.getProfileValue(requestCtx, "ADD_PLAN_QUOTA");
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

}
