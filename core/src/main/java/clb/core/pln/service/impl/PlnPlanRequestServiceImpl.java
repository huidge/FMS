package clb.core.pln.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.IAttachCategoryService;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IProfileService;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.mapper.CnlChannelContractMapper;
import clb.core.channel.mapper.CnlChannelMapper;
import clb.core.common.service.ICommonUploadService;
import clb.core.common.utils.CalculateAge;
import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.SerialNum;
import clb.core.common.utils.SpringConfigTool;
import clb.core.pln.dto.PlnPlanLibrary;
import clb.core.pln.dto.PlnPlanRequest;
import clb.core.pln.dto.PlnPlanRequestAdtlRisk;
import clb.core.pln.dto.PlnPlanRequestExtract;
import clb.core.pln.mapper.PlnPlanRequestMapper;
import clb.core.pln.mapper.PlnPlanSpiderSettingMapper;
import clb.core.pln.service.IPlnPlanLibraryService;
import clb.core.pln.service.IPlnPlanRequestAdtlRiskService;
import clb.core.pln.service.IPlnPlanRequestExtractService;
import clb.core.pln.service.IPlnPlanRequestService;
import clb.core.production.dto.PrdItems;
import clb.core.production.mapper.PrdItemsMapper;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.service.ISpeSupplierService;
import clb.core.support.util.Pdfutils;
import clb.core.sys.service.ISysFunctionAllocationRuleService;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;

@Service
@Transactional
public class PlnPlanRequestServiceImpl extends BaseServiceImpl<PlnPlanRequest> implements IPlnPlanRequestService{

	@Autowired
	private ISpeSupplierService speSupplierService;
    /**
     * 提示信息名.
     */
    private static final String MESSAGE_NAME = "message";

    /**
     * 提示只能上传一个文件
     */
    private static final String MESG_UNIQUE = "Unique";
    /**
     * 提示成功.
     */
    private static final String MESG_SUCCESS = "success";
    /**
     * 文件不存在提示.
     */
    private static final String FILE_NOT_EXSIT = "file_not_exsit";
    /**
     * 提示信息 name.
     **/
    private static final String INFO_NAME = "info";
    /**
     * 附件上传存储目录未分配.
     **/
    private static final String TYPEORKEY_EMPTY = "TYPEORKEY_EMPTY";
    /**
     * sourceType 错误.
     */
    private static final String TYPE_ERROR = "SOURCETYPE_ERROR";
    /**
     * 数据库 错误.
     */
    private static final String DATABASE_ERROR = "DATABASE_ERROR";
    /**
     * 图片mime前缀.
     */
    private static final String IMAGE_MIME_PREFIX = "image";
    /**
     * file对象名.
     */
    private static final String FILE_NAME = "file";
    /**
     * buffer 大小.
     */
    private static final Integer BUFFER_SIZE = 1024;

    /**
     * 图片压缩大小.
     */
    private static final Float COMPRESS_SIZE = 40f;

    /**
     * 进制单位.
     */
    private static final Float BYTE_TO_KB = 1024f;
    /**
     * 文件下载默认编码.
     */
    private static final String ENC = "UTF-8";

    /**
     * 日志记录.
     **/
    private static Logger logger = LoggerFactory.getLogger(com.hand.hap.attachment.controllers.AttachmentController.class);

    /**
     * 下载到本地的文件目录:可设置服务器路径
     */
    private String localFileUrl = "";
    /**
     * 渠道对应的所有一级渠道1
     */
    private HashSet<String> topChannels = new HashSet<>();


    @Autowired
    private CnlChannelContractMapper cnlChannelContractMapper;
    @Autowired
    private CnlChannelMapper cnlChannelMapper;
    @Autowired
    private PlnPlanRequestMapper plnPlanRequestMapper;
    @Autowired
    private IPlnPlanLibraryService plnPlanLibraryService;
    @Autowired
    private IPlnPlanRequestAdtlRiskService plnPlanRequestAdtlRiskService;
    @Autowired
    private IPlnPlanRequestExtractService plnPlanRequestExtractService;
    @Autowired
    private ISysFunctionAllocationRuleService sysFunctionAllocationRuleService;
    @Autowired
    private ClbUserMapper clbUserMapper;
    @Autowired
    private PlnPlanSpiderSettingMapper plnPlanSpiderSettingMapper;
    @Autowired
    private PrdItemsMapper prdItemsMapper;

    @Autowired
    private ICommonUploadService commonUploadService;

    @Autowired
    private IAttachCategoryService categoryService;

    @Autowired
    private ISysFileService fileService;

    @Autowired
    private IProfileService profileService;


    @Override
    public List<PlnPlanRequest> selectPlanRequest(PlnPlanRequest plnPlanRequest, IRequest requestCtx, int page,
                                                  int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<PlnPlanRequest> list = plnPlanRequestMapper.selectPlanRequest(plnPlanRequest);
        for (PlnPlanRequest plnPlanRequest2 : list) {
        	//去除金额后面多余的0
			String amount = plnPlanRequest2.getAmount();
			if (null!=amount&&!("".equals(amount.trim()))) {
				if(amount.indexOf(".") > 0){  
					amount = amount.replaceAll("0+?$", "");//去掉多余的0  
					amount = amount.replaceAll("[.]$", "");//如最后一位是.则去掉  
	            }
			}
			//前端计划书详情页面显示两位小数
			
			plnPlanRequest2.setAmount(amount);
		}
        return list;
    }


    @Override
    public ResponseData exePlnSpider(PlnPlanRequest dto, IRequest requestCtx){
        ResponseData responseData = new ResponseData();
        //校验结果初始化
        boolean flag = false;
        String status = "失败";

        String requestNumber =  dto.getRequestNumber();
        String message = "";


        //校验逻辑

        //判断计划书是否在爬虫设置界面设置
        int cou = plnPlanSpiderSettingMapper.selectPlanSpiderCount(requestNumber);
        if(dto.getAdditionalRiskFlag().equals("Y")||dto.getExtractFlag().equals("Y")||dto.getAdvancedMedicalFlag().equals("Y")||dto.getBacktrackFlag().equals("Y")){
            message = "申请单含有附加险/提取金额/添加高端医疗/保单回溯等信息，请手工处理";
        }else if(dto.getStatus().equals("CANCELLED")){
            message = "所选单状态为取消，不可进行操作";
        }else if(checkPlnSpider(dto,null)){
            status = "成功匹配";
            message = "成功匹配计划书库中的文件";
        }else if(cou==0){
            message = "申请未在计划书爬虫设置界面设置此产品,或者未启用";
        }else{
            flag = true;
            status = "获取中";
            message ="爬虫程序调用成功，请稍等几分钟后查看";
        }

        //获取要进行更新状态的计划书申请单
        PlnPlanRequest newPlnPlanRequest = new PlnPlanRequest();
        newPlnPlanRequest.setRequestNumber(requestNumber);
        newPlnPlanRequest = plnPlanRequestMapper.selectOne(newPlnPlanRequest);
        newPlnPlanRequest.setCrawlerState(status);
        newPlnPlanRequest.setCrawlerReturnMessage(message);
        plnPlanRequestMapper.updateByPrimaryKey(newPlnPlanRequest);

        //封装responceData返回前台
        responseData.setSuccess(flag);
        responseData.setMessage(message);

        if(flag) {
            String spiderName = plnPlanSpiderSettingMapper.selectSpiderName(requestNumber);

            //从配置文件获取IP端口
            String Ip = profileService.getProfileValue(requestCtx,"SPADER_IP_CONFIG");
            String GET_URL = "http://"+Ip+"/spider/" + spiderName + "/" + requestNumber;

            try {
			/*Process pr = Runtime.getRuntime().exec("cd /u01/spader/SCRAPY && scrapy crawl prudential -a applyNumer=666");
			BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				//line = decodeUnicode(line);
				System.out.println(line);
			}
			in.close();
			pr.waitFor();*/

                // 拼凑get请求的URL字串，使用URLEncoder.encode对特殊和不可见字符进行编码
                String getURL = GET_URL;
                URL getUrl = new URL(getURL);
                // 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
                // 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
                HttpURLConnection connection = (HttpURLConnection) getUrl
                        .openConnection();
                // 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到
                // 服务器
                connection.connect();
                // 取得输入流，并使用Reader读取
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String lines;
                while ((lines = reader.readLine()) != null) {
                    System.out.println(lines);
                }
                reader.close();
                // 断开连接
                connection.disconnect();
            } catch (Exception e) {
                CommonUtil.printStackTraceToStr(e);
            }
        }
        return responseData;
    }

    @Override
    public int selectPlanCountByUser(Long createdBy) {
        return plnPlanRequestMapper.selectPlanCountByUser(createdBy);
    }


    @Override
    public int selectPlanTotalByUser(Long userId) {
        return plnPlanRequestMapper.selectPlanTotalByUser(userId);
    }


    @Override
    public String selectFilePath(Long fileId) {
        return plnPlanRequestMapper.selectFilePath(fileId);
    }


    @Override
    public List<PlnPlanRequest> selectTeamUser(Long userId) {
        return plnPlanRequestMapper.selectTeamUser(userId);
    }


    @Override
    public PlnPlanRequest selectForCrawlersInfo(String requestNumber) {
        return plnPlanRequestMapper.selectForCrawlersInfo(requestNumber);
    }


    @Override
    public List<PlnPlanRequest> selectTeamPlanRequest(PlnPlanRequest plnPlanRequest, IRequest requestCtx, int page,
                                                      int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<PlnPlanRequest> list = plnPlanRequestMapper.selectTeamPlanRequest(plnPlanRequest);
        for (PlnPlanRequest plnPlanRequest2 : list) {
        	//去除金额后面多余的0
			String amount = plnPlanRequest2.getAmount();
			if (null!=amount&&!("".equals(amount.trim()))) {
				if(amount.indexOf(".") > 0){  
					amount = amount.replaceAll("0+?$", "");//去掉多余的0  
					amount = amount.replaceAll("[.]$", "");//如最后一位是.则去掉  
	            }
			}
			
			plnPlanRequest2.setAmount(amount);
		}
        return list;
    }

    @Override
    public ResponseData requestUpdate(IRequest request, PlnPlanRequest dto) {

    	ResponseData responseData = new ResponseData(); 
        //插入头信息
        dto.setStatus("PROCESSING");
        dto.setRequestDate(new Date());
        self().updateByPrimaryKey(request, dto);
        if(dto.getPlnPlanRequestAdtlRiskList() != null && dto.getPlnPlanRequestAdtlRiskList().size()>=1){
        	List<PlnPlanRequestAdtlRisk> plnPlanRequestAdtlRiskListUpdate = new ArrayList<PlnPlanRequestAdtlRisk>();
        	List<PlnPlanRequestAdtlRisk> plnPlanRequestAdtlRiskListDelete = new ArrayList<PlnPlanRequestAdtlRisk>();
            for (PlnPlanRequestAdtlRisk plnPlanRequestAdtlRisk : dto.getPlnPlanRequestAdtlRiskList()) {
            	if("delete".equals(plnPlanRequestAdtlRisk.getType())){
            		plnPlanRequestAdtlRiskListDelete.add(plnPlanRequestAdtlRisk);
            	}
            	else{
            		if(plnPlanRequestAdtlRisk.getLineId() != null){
                        plnPlanRequestAdtlRisk.set__status("update");
                    }else{
                        plnPlanRequestAdtlRisk.set__status("add");
                    }
            		plnPlanRequestAdtlRiskListUpdate.add(plnPlanRequestAdtlRisk);
            	}
            }
        	plnPlanRequestAdtlRiskService.batchUpdate(request, plnPlanRequestAdtlRiskListUpdate);
        	plnPlanRequestAdtlRiskService.batchDelete(plnPlanRequestAdtlRiskListDelete);
        }

        //循环提取行
        if(dto.getPlnPlanRequestExtractList() != null && dto.getPlnPlanRequestExtractList().size()>=1){
        	List<PlnPlanRequestExtract> PlnPlanRequestExtractListDelete = new ArrayList<PlnPlanRequestExtract>(); 
        	List<PlnPlanRequestExtract> PlnPlanRequestExtractListUpdate = new ArrayList<PlnPlanRequestExtract>(); 
            for (PlnPlanRequestExtract plnPlanRequestExtract : dto.getPlnPlanRequestExtractList()) {
            	if("delete".equals(plnPlanRequestExtract.getType())){
            		PlnPlanRequestExtractListDelete.add(plnPlanRequestExtract);
            	}else{
            		 if(plnPlanRequestExtract.getLineId() != null){
                         plnPlanRequestExtract.set__status("update");
                     }else{
                         plnPlanRequestExtract.set__status("add");
                     }
            		 PlnPlanRequestExtractListUpdate.add(plnPlanRequestExtract);
            	}
            }
            plnPlanRequestExtractService.batchUpdate(request, PlnPlanRequestExtractListUpdate);
            plnPlanRequestExtractService.batchDelete(PlnPlanRequestExtractListDelete);
        }
        
        //如果三种类型都不是，才匹配
        if("N".equals(dto.getAdvancedMedicalFlag()) && "N".equals(dto.getAdditionalRiskFlag()) && "N".equals(dto.getExtractFlag()) && "N".equals(dto.getBacktrackFlag())){
        //验证计划书库是否存在
        PlnPlanLibrary plnPlanLibrary = new PlnPlanLibrary();
        //转化后的计划书库
        plnPlanLibrary = this.exchangeToPlnPlanLibrary(dto);
        //
        plnPlanLibrary = plnPlanLibraryService.selectLibraryByCondition(plnPlanLibrary);
        //判断计划书是否存在
        if(plnPlanLibrary != null){
        	dto.setStatus("COMPLETED");
        	dto.setCrawlerState("成功匹配");
        	dto.setQuantCalcFlag("N");
        	dto.setCrawlerReturnMessage("已成功匹配计划书库中的文件");
            if(plnPlanLibrary.getFileId()!=null){
            	dto.setFileId(plnPlanLibrary.getFileId());
            }
            self().updateByPrimaryKeySelective(request, dto);
            responseData.setMessage("成功匹配计划书库中的文件!");
          }
        }
        return responseData;
    }

    @Override
    public ResponseData requestSubmit(IRequest request, PlnPlanRequest dto) {
    	
    	ResponseData responseData = new ResponseData(); 
        //获取序列号
        dto.setRequestNumber(SerialNum.getMoveOrderNo());
        //插入头信息
        dto.setStatus("PROCESSING");
        dto.setRequestDate(new Date());
        self().insertSelective(request, dto);
        PlnPlanRequest plnPlanRequest =selectForCrawlersInfo(dto.getRequestNumber());
        //循环附加险
        if(dto.getPlnPlanRequestAdtlRiskList() != null){
            for (PlnPlanRequestAdtlRisk lnPlanRequestAdtlRisk : dto.getPlnPlanRequestAdtlRiskList()) {
                lnPlanRequestAdtlRisk.setPlanId(plnPlanRequest.getPlanId());
                plnPlanRequestAdtlRiskService.insertSelective(request, lnPlanRequestAdtlRisk);
            }
        }

        //循环提取行
        if(dto.getPlnPlanRequestExtractList() != null){
            for (PlnPlanRequestExtract plnPlanRequestExtract : dto.getPlnPlanRequestExtractList()) {
                plnPlanRequestExtract.setPlanId(plnPlanRequest.getPlanId());
                plnPlanRequestExtractService.insertSelective(request, plnPlanRequestExtract);
            }
        }
        
        //如果三种类型都不是，才匹配
        if("N".equals(plnPlanRequest.getAdvancedMedicalFlag()) && "N".equals(plnPlanRequest.getAdditionalRiskFlag()) && "N".equals(plnPlanRequest.getExtractFlag()) && "N".equals(plnPlanRequest.getBacktrackFlag())){
        //验证计划书库是否存在
        PlnPlanLibrary plnPlanLibrary = new PlnPlanLibrary();
        //转化后的计划书库
        plnPlanLibrary = this.exchangeToPlnPlanLibrary(plnPlanRequest);
        //
        plnPlanLibrary = plnPlanLibraryService.selectLibraryByCondition(plnPlanLibrary);
        //判断计划书是否存在
        if(plnPlanLibrary != null){
            plnPlanRequest.setStatus("COMPLETED");
            plnPlanRequest.setCrawlerState("成功匹配");
            plnPlanRequest.setQuantCalcFlag("N");
            plnPlanRequest.setCrawlerReturnMessage("已成功匹配计划书库中的文件");
            if(plnPlanLibrary.getFileId()!=null){
            	plnPlanRequest.setFileId(plnPlanLibrary.getFileId());
            }
            self().updateByPrimaryKeySelective(request, plnPlanRequest);
            responseData.setMessage("成功匹配计划书库中的文件!");
        }else{
            //调用规则接口
            //--------查询通知模板的信息
        	plnPlanRequest.setCrawlerState("失败");
            plnPlanRequest.setCrawlerReturnMessage("申请单含有附加险/提取金额/添加高端医疗/保单回溯等信息，请手工处理");
            self().updateByPrimaryKeySelective(request, plnPlanRequest);
            PrdItems item=prdItemsMapper.selectByPrimaryKey(dto.getItemId());
            Map sendNoticeMap=new HashMap();
            sendNoticeMap.put("itemName", item.getItemName());
            sendNoticeMap.put("requestNumber", dto.getRequestNumber());
            sysFunctionAllocationRuleService.allocationPerson(request, "CLB_SUPPLIER", request.getUserId(),null,
                    dto.getSublineId(), request.getAttribute("relatedPartyId"), request.getAttribute("relatedPartyId"),null, plnPlanRequest.getPlanId(),
                    "PLAN_JOB", "STATUS", plnPlanRequest.getStatus(), sendNoticeMap);
        }
        }else{
            //调用规则接口
            //--------查询通知模板的信息
        	plnPlanRequest.setCrawlerState("失败");
            plnPlanRequest.setCrawlerReturnMessage("申请单含有附加险/提取金额/添加高端医疗/保单回溯等信息，请手工处理");
            self().updateByPrimaryKeySelective(request, plnPlanRequest);
            PrdItems item=prdItemsMapper.selectByPrimaryKey(dto.getItemId());
            Map sendNoticeMap=new HashMap();
            sendNoticeMap.put("itemName", item.getItemName());
            sendNoticeMap.put("requestNumber", dto.getRequestNumber());
            sysFunctionAllocationRuleService.allocationPerson(request, "CLB_SUPPLIER", request.getUserId(),null,
                    dto.getSublineId(), request.getAttribute("relatedPartyId"), request.getAttribute("relatedPartyId"),null, plnPlanRequest.getPlanId(),
                    "PLAN_JOB", "STATUS", plnPlanRequest.getStatus(), sendNoticeMap);
        }
        return responseData;
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
        if (dto.getAmount()!=null) {
            if ("IC".equals(dto.getAmountType())) {
                plnPlanLibrary.setAmount(new BigDecimal(dto.getAmount()));
            } else {
                plnPlanLibrary.setPremium(new BigDecimal(dto.getAmount()));
            }
        }
        SpeSupplier speSupplier = new SpeSupplier();
        speSupplier.setSupplierId(Long.valueOf(dto.getSupplierName()));
        String type = "ASCSR";
        String ageCalculateStandard = speSupplierService.selectData(speSupplier,1,1).get(0).getAgeCalculateStandard();
        if(ageCalculateStandard != null && !"".equals(ageCalculateStandard)){
        	type = ageCalculateStandard;
        }
        //年龄
        plnPlanLibrary.setAge(Long.valueOf(CalculateAge.accessAge(dto.getInsurantBirth(), type)));
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
    
	public int getAge(Date dateOfBirth) {
		int age = 0;
		Calendar born = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		if (dateOfBirth != null) {
			now.setTime(new Date());
			born.setTime(dateOfBirth);
			if (born.after(now)) {
				throw new IllegalArgumentException("年龄不能超过当前日期");
			}
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



    @Override
    public ResponseData reviewPlan(IRequest request, PlnPlanRequest dto) {
        PlnPlanRequest plnPlanRequest = selectByPrimaryKey(request, dto);
        Long createdBy=plnPlanRequest.getCreatedBy();
        if(plnPlanRequest.getProcessComments() !=null && plnPlanRequest.getProcessComments() !=""){
            plnPlanRequest.setProcessComments(plnPlanRequest.getProcessComments() + ";" + dto.getReviewProcessComments());
        }else{
            plnPlanRequest.setProcessComments(dto.getReviewProcessComments());
        }
        plnPlanRequest.setStatus("REVIEW");
        self().updateByPrimaryKeySelective(request, plnPlanRequest);
        //调用规则接口
        //--------查询通知模板的信息
        PrdItems item=prdItemsMapper.selectByPrimaryKey(plnPlanRequest.getItemId());
        Map sendNoticeMap=new HashMap();
        sendNoticeMap.put("itemName", item.getItemName());
        sendNoticeMap.put("requestNumber", plnPlanRequest.getRequestNumber());
        //---获取前端创建者信息
        ClbUser user=clbUserMapper.selectByPrimaryKey(createdBy);
        sysFunctionAllocationRuleService.allocationPerson(request, "CLB_SUPPLIER",createdBy ,null, plnPlanRequest.getSublineId(), user.getRelatedPartyId(), user.getRelatedPartyId(),null, plnPlanRequest.getPlanId(), "PLAN_JOB", "STATUS", plnPlanRequest.getStatus(), sendNoticeMap);
        return new ResponseData();
    }


    @Override
    public ResponseData completedPlan(IRequest request, List<PlnPlanRequest> dto) {
    	//从计划书申请中取出各种值然后经过判断之后传入到计划书库中如果计划书中没有对应的存在则插入一条新的计划书
    	if("N".equals(dto.get(0).getAdvancedMedicalFlag()) && "N".equals(dto.get(0).getAdditionalRiskFlag()) && "N".equals(dto.get(0).getExtractFlag()) && "N".equals(dto.get(0).getBacktrackFlag())){
	    	PlnPlanLibrary plnPlanLibrary = new PlnPlanLibrary();
	        plnPlanLibrary = this.exchangeToPlnPlanLibrary(dto.get(0));
	        //增加年龄,保费,保额的设置
	        Long age = dto.get(0).getAge();
	        if (age<0||null==age||"".equals(age)) {
	        	plnPlanLibrary.setAge(0L);
			}else {
				plnPlanLibrary.setAge(age);
			}
	        if(StringUtils.isNotBlank(dto.get(0).getPremium())){
	        	plnPlanLibrary.setPremium(new BigDecimal(dto.get(0).getPremium()));
	        }
	        if(StringUtils.isNotBlank(dto.get(0).getLibraryAmount())){
	        	plnPlanLibrary.setAmount(new BigDecimal(dto.get(0).getLibraryAmount()));
	        }
	        //判断计划书是否存在
	        PlnPlanLibrary library = plnPlanLibraryService.selectLibraryByCondition(plnPlanLibrary);
	        if(library == null){
	            plnPlanLibraryService.insert(request, plnPlanLibrary);
	        }else {
	        	library.setFileId(dto.get(0).getFileId());
	        	plnPlanLibraryService.updateByPrimaryKey(request, library);
	        }
    	}
        PlnPlanRequest plnPlanRequest1 = self().selectByPrimaryKey(request, dto.get(0));
        dto.get(0).setRequestDate(plnPlanRequest1.getRequestDate());
        dto.get(0).setStatus("COMPLETED");
        PlnPlanRequest planRequest = self().updateByPrimaryKeySelective(request, dto.get(0));
        //调用规则接口
        //---获取前端创建者信息
        PlnPlanRequest plnPlanRequest =selectByPrimaryKey(request,dto.get(0) );
        ClbUser user=clbUserMapper.selectByPrimaryKey(plnPlanRequest.getCreatedBy());
        //--------查询通知模板的信息
        PrdItems item=prdItemsMapper.selectByPrimaryKey(plnPlanRequest.getItemId());
        Map sendNoticeMap=new HashMap();
        sendNoticeMap.put("itemName", item.getItemName());
        sendNoticeMap.put("requestNumber", plnPlanRequest.getRequestNumber());
        sysFunctionAllocationRuleService.allocationPerson(request, "CLB_SUPPLIER", plnPlanRequest.getCreatedBy(),null, plnPlanRequest.getSublineId(), user.getRelatedPartyId(), user.getRelatedPartyId(),null, plnPlanRequest.getPlanId(), "PLAN_JOB", "STATUS", "COMPLETED", sendNoticeMap);
        return new ResponseData(dto);
    }

    @Override
    public boolean checkPlnSpider(PlnPlanRequest plnPlanRequest,IRequest request) {
        //验证计划书库是否存在
        PlnPlanLibrary plnPlanLibrary = new PlnPlanLibrary();
        //转化后的计划书库
        plnPlanLibrary = exchangeToPlnPlanLibrary(plnPlanRequest);
        //
        plnPlanLibrary = plnPlanLibraryService.selectLibraryByCondition(plnPlanLibrary);
        //判断计划书是否存在
        if(plnPlanLibrary != null){
            plnPlanRequest = selectByPrimaryKey(request, plnPlanRequest);
            plnPlanRequest.setStatus("COMPLETED");
            if(plnPlanLibrary.getFileId()!=null){
                plnPlanRequest.setFileId(plnPlanLibrary.getFileId());
            }
            self().updateByPrimaryKeySelective(null, plnPlanRequest);
            return true;
        }else{
            return false;
        }
    }


    @Override
    public void handingPdf(PlnPlanRequest dto,HttpServletResponse response, HttpServletRequest request,IRequest requestContext){
        String spiderName = "";
        Long newFileId = -1L;
        boolean flag = true;
        PlnPlanRequest plnPlanRequest = selectForCrawlersInfo(dto.getRequestNumber());;
        if(dto.getFileId() == -1){
            flag = false;
        }
        if(flag) {
            //step1 从阿里云服务器下载所要进行处理的PDF得到文件名
            String fileName = downloadPdf(dto.getFileId(), response, request);
            //得到spiderName参数
            if (fileName.indexOf("aiahk") != -1) {
                spiderName = "aiahk";
            } else if (fileName.indexOf("prudential") != -1) {
                spiderName = "prudential";
            } else if (fileName.indexOf("metlife") != -1) {
                spiderName = "metlife";
            } else if (fileName.indexOf("manulife") != -1) {
                spiderName = "manulife";
            }
            //step2 进行PDF处理,得到处理后的PDF文件
            Pdfutils pdfutil = new Pdfutils();
            String handingfilePath = this.localFileUrl + fileName;
            //由于客户所需要的是中文名字的上传文件
            String replaceName = plnPlanRequest.getItemName()+"_"+plnPlanRequest.getSublineItemName();
            handingfilePath = pdfutil.handingPdf(spiderName, handingfilePath,replaceName);
            //step3 进行文件上传
            newFileId = processUploadedFile(requestContext, handingfilePath);

            //step4 插入计划书库
            PlnPlanLibrary plnPlanLibrary = new PlnPlanLibrary();
            plnPlanLibrary.setItemId(plnPlanRequest.getItemId());
            plnPlanLibrary.setSublineId(plnPlanRequest.getSublineId());
            plnPlanLibrary.setPayMethod(plnPlanRequest.getPayMethod());
            plnPlanLibrary.setCurrency(plnPlanRequest.getCurrency());
            //保额
            if(plnPlanRequest.getAmount()!=null) {
                if ("IC".equals(plnPlanRequest.getAmountType())) {
                    plnPlanLibrary.setAmount(new BigDecimal(plnPlanRequest.getAmount()));
                } else {
                    plnPlanLibrary.setPremium(new BigDecimal(plnPlanRequest.getAmount()));
                }
            }
            SpeSupplier speSupplier = new SpeSupplier();
            speSupplier.setSupplierId(Long.valueOf(plnPlanRequest.getSupplierName()));
            String type = "ASCSR";
            if(speSupplierService.selectData(speSupplier,1,1).get(0).getAgeCalculateStandard() != null && speSupplierService.selectData(speSupplier,1,1).get(0).getAgeCalculateStandard() !=""){
                type = speSupplierService.selectData(speSupplier,1,1).get(0).getAgeCalculateStandard();
            }
            //年龄
            plnPlanLibrary.setAge(Long.valueOf(CalculateAge.accessAge(plnPlanRequest.getInsurantBirth(), type)));

            plnPlanLibrary.setGender(plnPlanRequest.getInsurantGender());
            plnPlanLibrary.setSmokeFlag(plnPlanRequest.getInsurantSmokeFlag());
            plnPlanLibrary.setNationality(plnPlanRequest.getInsurantNationality());
            plnPlanLibrary.setResidence(plnPlanRequest.getInsurantResidence());
            plnPlanLibrary.setCity(plnPlanRequest.getCity());
            plnPlanLibrary.setSecurityLevel(plnPlanRequest.getSecurityLevel());
            plnPlanLibrary.setSecurityArea(plnPlanRequest.getSecurityArea());
            plnPlanLibrary.setSelfpayId(plnPlanRequest.getSelfpayId());
            plnPlanLibrary.setFileId(newFileId);
            plnPlanLibrary.setObjectVersionNumber(1L);
            plnPlanLibrary.setCreatedBy(-1L);
            plnPlanLibrary.setLastUpdateDate(new Date());
            plnPlanLibrary.setLastUpdatedBy(-1L);
            plnPlanLibrary.setCreationDate(new Date());
            plnPlanLibraryService.insert(null,plnPlanLibrary);
            //更新计划书状态为已完成
            plnPlanRequest.setStatus("COMPLETED");
        }
            //step5 进行数据更新
            plnPlanRequest.setCrawlerState(dto.getCrawlerState());
            plnPlanRequest.setCrawlerReturnMessage(dto.getCrawlerReturnMessage());
            plnPlanRequest.setFileId(newFileId);
            updateByPrimaryKeySelective(requestContext, plnPlanRequest);

    }

    public Long processUploadedFile(IRequest requestContext,String filepath) {
        String OSSUrl = "CLBDATA/CNL/2017720";
        SysFile sysFile = null;
        File f = null;
        try {
            f = new File(filepath);
            IRequest irequest = RequestHelper.newEmptyRequest();
            irequest.setUserId(-1L);
            irequest.setRoleId(-1L);
            profileService = (IProfileService) SpringConfigTool.getBean("profileServiceImpl");
            String endpoint=profileService.getProfileValue(irequest, "ENDPOINT");
            String accesskeyid=profileService.getProfileValue(irequest, "ACCESSKEYID");
            String accesskeysecret=profileService.getProfileValue(irequest, "ACCESSKEYSECRET");
            String bucketnameUnPic=profileService.getProfileValue(irequest, "BUCKETNAME_UNPIC");
            sysFile = new SysFile();
            sysFile.setFileName(f.getName());
            sysFile.setFileSize(f.length());
            sysFile.setUploadDate(new Date());
            sysFile.setCreatedBy(-1L);
            sysFile.setLastUpdatedBy(-1L);
            sysFile.setFilePath(OSSUrl + f.getName());
            sysFile.setFileType("application/pdf");
            try{
                ClientConfiguration config = new ClientConfiguration();
                config.setCrcCheckEnabled(false);
                OSSClient client = new OSSClient(endpoint, accesskeyid, accesskeysecret,config);
                client.putObject(bucketnameUnPic,OSSUrl + f.getName(),f);
                client.shutdown();
            }catch (Exception e){
                f.delete();
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            f.delete();
        }
        SysFile newFile = fileService.insert(requestContext,sysFile);
        return newFile.getFileId();
    }

    public String downloadPdf(Long fileIdLogo, HttpServletResponse response, HttpServletRequest request) {
        SysFile sysFile = fileService.selectByPrimaryKey(null, Long.valueOf(fileIdLogo));
        try {
            if (sysFile != null && StringUtils.isNotBlank(sysFile.getFilePath())) {
                // Modified by Rex.Hua@20170516
                IRequest irequest = RequestHelper.newEmptyRequest();
                profileService = (IProfileService) SpringConfigTool.getBean("profileServiceImpl");
                String endpoint=profileService.getProfileValue(irequest, "ENDPOINT");
                String accesskeyid=profileService.getProfileValue(irequest, "ACCESSKEYID");
                String accesskeysecret=profileService.getProfileValue(irequest, "ACCESSKEYSECRET");
                String bucketnamePic=profileService.getProfileValue(irequest, "BUCKETNAME_PIC");
                String bucketnameUnPic=profileService.getProfileValue(irequest, "BUCKETNAME_UNPIC");

                ClientConfiguration config = new ClientConfiguration();
                config.setCrcCheckEnabled(false);
                OSSClient client = new OSSClient(endpoint, accesskeyid, accesskeysecret,config);

                OSSObject ossObject ;
                // 获取OSS对象
                if("image/jpeg".equals(sysFile.getFileType())){
                    ossObject = client.getObject(bucketnamePic, sysFile.getFilePath());
                }else if("image/jpg".equals(sysFile.getFileType())){
                    ossObject = client.getObject(bucketnamePic, sysFile.getFilePath());
                }else if("image/png".equals(sysFile.getFileType())){
                    ossObject = client.getObject(bucketnamePic, sysFile.getFilePath());
                }else{
                    ossObject = client.getObject(bucketnameUnPic, sysFile.getFilePath());
                }
                // 获取数据流
                InputStream inputStream = ossObject.getObjectContent();

                // 设置Response相应对象
                response.setContentType(sysFile.getFileType() + ";charset=" + ENC);
                response.setHeader("Accept-Ranges", "bytes");
                response.addHeader("Content-Disposition",  "attachment;filename=\"" + URLEncoder.encode(sysFile.getFileName(), ENC) + "\"");
                Long fileLength = sysFile.getFileSize();
                response.setContentLength(fileLength.intValue());
                if (fileLength > 0) {
                    byte[] buf = new byte[BUFFER_SIZE];
                    try (OutputStream os = new FileOutputStream(new File(localFileUrl+sysFile.getFileName()));) {
                        int readLength;
                        while (((readLength = inputStream.read(buf)) != -1)) {
                            os.write(buf, 0, readLength);
                        }
                        os.flush();
                        os.close();
                    }
                }
                inputStream.close();
                // 关闭连接
                client.shutdown();
            }else {
                response.getWriter().write(FILE_NOT_EXSIT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sysFile.getFileName();
    }

    public void writeFileToResp(HttpServletResponse response, File file){
        byte[] buf = new byte[BUFFER_SIZE];
        try{
            try (InputStream inStream = new FileInputStream(file);
                 ServletOutputStream outputStream = response.getOutputStream()) {
                int readLength;
                while (((readLength = inStream.read(buf)) != -1)) {
                    outputStream.write(buf, 0, readLength);
                }
                outputStream.flush();

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

	@Override
	public int selectPlanCountByUserMonth(Long createdBy) {
		return plnPlanRequestMapper.selectPlanCountByUserMonth(createdBy);
	}


	@Override
	public int selectEffectiveCountByUser(Long createdBy) {
		return plnPlanRequestMapper.selectEffectiveCountByUser(createdBy);
	}

	@Override
	public List<PlnPlanRequest> selectPlanRequestForBack(PlnPlanRequest plnPlanRequest, IRequest requestCtx, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return plnPlanRequestMapper.selectPlanRequestForBack(plnPlanRequest);
	}


	@Override
	public String selectRoleType(Long userId) {
		return plnPlanRequestMapper.selectRoleType(userId);
	}


	@Override
	public List<PlnPlanRequest> selectTeamUserByAgency(Long userId) {
		return plnPlanRequestMapper.selectTeamUserByAgency(userId);
	}


	@Override
	public String selectCodeMeaning(String code, String codeValue) {
		return plnPlanRequestMapper.selectCodeMeaning(code, codeValue);
	}

	/**
	 * 查询渠道申请的计划书数量
	 */
	@Override
	public int selectPlanCountByChannelId(Long channelId) {
		return plnPlanRequestMapper.selectPlanCountByChannelId(channelId);
	}

	/**
	 * 查询渠道签单的计划书数量
	 */
	@Override
	public int selectEffectiveCountByChannelId(Long channelId) {
		return plnPlanRequestMapper.selectEffectiveCountByChannelId(channelId);
	}

	/**
	 * 微信查询本年申请的计划书数量
	 */
	@Override
	public int queryMyPlanCount(IRequest requestCtx, PlnPlanRequest dto) {
		return plnPlanRequestMapper.queryMyPlanCount(dto);
	}

	/**
	 * 微信  我的计划书查询
	 */
	@Override
	public List<PlnPlanRequest> selectMyPlanForWX(PlnPlanRequest dto, IRequest requestContext, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return plnPlanRequestMapper.selectMyPlanForWX(dto);
	}

	
	@Override
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