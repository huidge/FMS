package clb.core.pln.controllers;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IProfileService;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.mapper.CnlChannelContractMapper;
import clb.core.channel.mapper.CnlChannelMapper;
import clb.core.common.utils.CalculateAge;
import clb.core.common.utils.CommonUtil;
import clb.core.pln.dto.PlnPlanLibrary;
import clb.core.pln.dto.PlnPlanRequest;
import clb.core.pln.dto.PlnPlanRequestAdtlRisk;
import clb.core.pln.dto.PlnPlanRequestExtract;
import clb.core.pln.mapper.PlnPlanSpiderSettingMapper;
import clb.core.pln.service.IPlnPlanLibraryService;
import clb.core.pln.service.IPlnPlanRequestAdtlRiskService;
import clb.core.pln.service.IPlnPlanRequestExtractService;
import clb.core.pln.service.IPlnPlanRequestService;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.service.ISpeSupplierService;
import clb.core.sys.controllers.ClbBaseController;
import clb.core.system.service.IClbUserService;

    @Controller
    public class PlnPlanRequestController extends ClbBaseController{

    @Autowired
    private IPlnPlanRequestService plnPlanRequestService;

    @Autowired
	private IPlnPlanLibraryService plnPlanLibraryService;
    
    @Autowired
    private ISysFileService fileService;

	@Autowired
	private PlnPlanSpiderSettingMapper plnPlanSpiderSettingMapper;

	@Autowired
	private IProfileService profileService;
	
	@Autowired
	private ISpeSupplierService speSupplierService;
	
	@Autowired
	private IPlnPlanRequestAdtlRiskService plnPlanRequestAdtlRiskService;

	@Autowired
	private IPlnPlanRequestExtractService plnPlanRequestExtractService;
	
	@Autowired
	private IClbUserService clbUserService;
	
    /**
     * 渠道对应的所有一级渠道
     */
    private HashSet<String> topChannels = new HashSet<>();
    @Autowired
    private CnlChannelContractMapper cnlChannelContractMapper;
    @Autowired
    private CnlChannelMapper cnlChannelMapper;

    @RequestMapping(value = "/fms/pln/plan/request/query")
    @ResponseBody
    public ResponseData query(PlnPlanRequest dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        dto.setProcessUserId(requestContext.getUserId());
        List<PlnPlanRequest> plnPlanRequestList = plnPlanRequestService.selectPlanRequestForBack(dto, requestContext, page, pageSize);
        for (PlnPlanRequest plnPlanRequest : plnPlanRequestList) {
        	int planCount = 0;//本月计划书申请数量
        	int effectiveCount = 0;//本月签单的数量
        	if(plnPlanRequest.getChannelId() != null) {
        		//查询本月计划书申请数量
    			planCount = plnPlanRequestService.selectPlanCountByChannelId(plnPlanRequest.getChannelId());
    			//查询本月签单的数量
    			effectiveCount = plnPlanRequestService.selectEffectiveCountByChannelId(plnPlanRequest.getChannelId());
        	}
			
			plnPlanRequest.setAttribute1(planCount+"/"+effectiveCount);
			String type = "ASCSR";
			if(dto.getSupplierName() != null && !"".equals(dto.getSupplierName())){
				SpeSupplier speSupplier = new SpeSupplier();
				speSupplier.setSupplierId(Long.valueOf(dto.getSupplierName()));
				String ageCalculateStandard = speSupplierService.selectData(speSupplier,1,1).get(0).getAgeCalculateStandard();
				if(ageCalculateStandard != null && !"".equals(ageCalculateStandard)){
				   type = ageCalculateStandard;
			    }
			}
			plnPlanRequest.setAge(Long.valueOf(CalculateAge.accessAge(plnPlanRequest.getInsurantBirth(), type)));
        	
			SysFile sysFile = fileService.selectByPrimaryKey(requestContext, plnPlanRequest.getFileId());
			if(sysFile != null){
        		plnPlanRequest.set_token(sysFile.get_token());
        	}
			//去除金额后面多余的0
			/*String amount = plnPlanRequest.getAmount();
			if (null!=amount&&!("".equals(amount.trim()))) {
				if(amount.indexOf(".") > 0){  
					amount = amount.replaceAll("0+?$", "");//去掉多余的0  
					amount = amount.replaceAll("[.]$", "");//如最后一位是.则去掉  .
	            }
			}*/
			//计划书详情页面显示两位小数.
			String amount = plnPlanRequest.getAmount();
			amount = String.format("%.2f", Double.valueOf(amount));
			plnPlanRequest.setAmount(amount);
			//调用方法查出渠道对应的一级渠道
			Set<String> topchannels = this.getTopChannels(plnPlanRequest.getChannelId());
			//去掉中括号存入topchannels
			plnPlanRequest.setTopChannels(StringUtils.strip(topchannels.toString(),"[]"));
			
			topChannels.clear();
			
			
		}
        return new ResponseData(plnPlanRequestList);
    }

    @RequestMapping(value = "/fms/pln/plan/request/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<PlnPlanRequest> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(plnPlanRequestService.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/fms/pln/plan/request/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<PlnPlanRequest> dto){
    	plnPlanRequestService.batchDelete(dto);
        return new ResponseData();
    }
    
    /**
     * 编辑复查信息
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/fms/pln/plan/request/editReview")
    @ResponseBody
    public ResponseData editReview(HttpServletRequest request, PlnPlanRequest dto){
    	IRequest requestCtx = createRequestContext(request);
    	try {
			return plnPlanRequestService.reviewPlan(requestCtx, dto);
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			ResponseData responseData = new ResponseData(false);
			responseData.setMessage(e.getMessage());
			return responseData;
		}
    }
    
    /**
     * 编辑上传
     * @param request
     * @param dto
     * @return
     */
    @RequestMapping(value = "/fms/pln/plan/request/editUpload")
    @ResponseBody
    public ResponseData editUpload(HttpServletRequest request, @RequestBody List<PlnPlanRequest> dto){
    	IRequest requestCtx = createRequestContext(request);
    	try {
			return plnPlanRequestService.completedPlan(requestCtx, dto);
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			ResponseData responseData = new ResponseData(false);
			responseData.setMessage(e.getMessage());
			return responseData;
		}
    }
    
    

    /**
     * 转换PlnPlanRequest to PlnPlanLibrary
     * @param dto
     * @return
     */
	private PlnPlanLibrary exchangeToPlnPlanLibrary(PlnPlanRequest dto) {
		//新建计划书库
		PlnPlanLibrary plnPlanLibrary = new PlnPlanLibrary();
		//产品id
		plnPlanLibrary.setItemId(dto.getItemId());
		//子产品
		plnPlanLibrary.setSublineId(dto.getSublineId());
		//付款方式
		plnPlanLibrary.setPayMethod(dto.getPayMethod());
		//币种
		plnPlanLibrary.setCurrency(dto.getCurrency());
		//保额
		plnPlanLibrary.setAmount(new BigDecimal(dto.getLibraryAmount()));
		//保费
		plnPlanLibrary.setPremium(new BigDecimal(dto.getPremium()));
		//年龄
		plnPlanLibrary.setAge(dto.getAge());
		//性别
		plnPlanLibrary.setGender(dto.getInsurantGender());
		//是否吸烟
		plnPlanLibrary.setSmokeFlag(dto.getInsurantSmokeFlag());
		//国籍
		plnPlanLibrary.setNationality(dto.getInsurantNationality());
		//居住地
		plnPlanLibrary.setResidence(dto.getInsurantResidence());
		//城市
		plnPlanLibrary.setCity(dto.getCity());
		//保障级别
		plnPlanLibrary.setSecurityLevel(dto.getSecurityLevel());
		//保障区域
		plnPlanLibrary.setSecurityArea(dto.getSecurityArea());
		//自付选项
		plnPlanLibrary.setSelfpayId(dto.getSelfpayId());
		//文件id
		plnPlanLibrary.setFileId(dto.getFileId());
		//版本号
		plnPlanLibrary.setObjectVersionNumber(1L);
		return plnPlanLibrary;
	}

	

		@RequestMapping(value = "/fms/pln/plan/spider/exePlnSpider")
		@ResponseBody
		public ResponseData exePlnSpider(@RequestBody  PlnPlanRequest plnPlanRequest, HttpServletRequest request) {
			IRequest requestContext = createRequestContext(request);
			return plnPlanRequestService.exePlnSpider(plnPlanRequest, requestContext);
		}

		@RequestMapping(value = "/fms/pln/plan/spider/checkPlnSpider")
		@ResponseBody
		public ResponseData checkPlnSpider(@RequestBody PlnPlanRequest plnPlanRequest, HttpServletRequest request) {
			 IRequest requestContext = createRequestContext(request); 
			return new ResponseData(plnPlanRequestService.checkPlnSpider(plnPlanRequest,requestContext));

			//return new ResponseData(plnPlanRequestService.selectPlanRequest(dto, requestContext, page, pageSize));
		}
		
		
		@RequestMapping(value = "/fms/pln/plan/request/queryAdtRisk")
		@ResponseBody
		public ResponseData queryAdtRisk(PlnPlanRequestAdtlRisk dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
										 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
			IRequest requestContext = createRequestContext(request);
			return new ResponseData(plnPlanRequestAdtlRiskService.selectAdtlRisk(dto.getPlanId()));
		}
		
		@RequestMapping(value = "/fms/pln/plan/request/queryExtract")
		@ResponseBody
		public ResponseData queryExtract(PlnPlanRequestExtract dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
										 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
			IRequest requestContext = createRequestContext(request);
			return new ResponseData(plnPlanRequestExtractService.select(requestContext,dto,page,pageSize));
		}


		public int getAge(Date dateOfBirth) {
			int age = 0;
			Calendar born = Calendar.getInstance();
			Calendar now = Calendar.getInstance();
			if (dateOfBirth != null) {
				now.setTime(new Date());
				born.setTime(dateOfBirth);
//				if (born.after(now)) {
//					throw new IllegalArgumentException("年龄不能超过当前日期");
//				}
				age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
				int nowDayOfYear = now.get(Calendar.DAY_OF_YEAR);
				int bornDayOfYear = born.get(Calendar.DAY_OF_YEAR);
				System.out.println("nowDayOfYear:" + nowDayOfYear + " bornDayOfYear:" + bornDayOfYear);
				if (nowDayOfYear < bornDayOfYear) {
					age -= 1;
				}
			}
			return age;
		}
		
		public Set<String> getTopChannels(Long channelId) {
			//递归调用查出所有的一级渠道
			topChannels(channelId);
			return topChannels;
		}


		private void topChannels(Long channelId) {
			CnlChannelContract cnlChannelContract = new CnlChannelContract();
			cnlChannelContract.setChannelId(channelId);
			//根据渠道id查出对应的合约
			List<CnlChannelContract> contracts = cnlChannelContractMapper.queryContract(cnlChannelContract);
			//遍历合约
			for (CnlChannelContract contract : contracts) {
				//如果合约主体是供应商则判断为一级渠道存入set
				if (contract.getPartyType()!=null&&"SUPPLIER".equals(contract.getPartyType().trim())) {
					CnlChannel cnlChannel = new CnlChannel();
					cnlChannel.setChannelId(channelId);
					CnlChannel cnlChannel2 = cnlChannelMapper.selectByPrimaryKey(cnlChannel);
					topChannels.add(cnlChannel2.getChannelName());
				}else {
					//否则递归调用
					topChannels(contract.getPartyId());
				}
			}
			
		}
}