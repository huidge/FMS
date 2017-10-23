package clb.core.production.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.dto.ImportResponse;
import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.FileReadUtil;
import clb.core.payment.utils.WXPayUtil;
import clb.core.production.dto.PrdItemPaymode;
import clb.core.production.dto.PrdItemSecurityPlan;
import clb.core.production.dto.PrdItemSelfpay;
import clb.core.production.dto.PrdItemSubline;
import clb.core.production.dto.PrdItems;
import clb.core.production.service.IPrdItemSublineService;
import clb.core.production.service.IPrdItemsService;
import clb.core.system.controllers.ClbImportController;

/**
 * Created by rex.hua on 17/4/12.
 */
@Controller
@RequestMapping({"/production/items"})
public class PrdItemsController extends BaseController {
    
    @Autowired
    private IPrdItemsService prdItemsService;
    @Autowired
    private ISysFileService sysFileService;
    @Autowired
    private IPrdItemSublineService prdItemSublineService;

    /**
     * 查询产品
     * @param request
     * @return
     */
    @RequestMapping(value = {"/query"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData query(PrdItems prdItems, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<PrdItems> prdItemsList = prdItemsService.selectAlive(prdItems,page,pagesize);
        return new ResponseData(prdItemsList);
    }

    /**
     * 保存产品
     * @param request
     * @return
     */
    @RequestMapping(value = {"/submit"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData submit(HttpServletRequest request, @RequestBody List<PrdItems> dtoList, BindingResult result)  {
        getValidator().validate(dtoList, result);
        if (result.hasErrors()) {
          ResponseData rd = new ResponseData(false);
          rd.setMessage(getErrorMessage(result, request));
          return rd;
        }
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(prdItemsService.batchUpdate(iRequest, dtoList));
    }

    @RequestMapping(value = "/remove")
    public ResponseData delete(HttpServletRequest request, @RequestBody List<PrdItems> dtoList){
         prdItemsService.batchDelete(dtoList);
        return new ResponseData();
    }

    @RequestMapping({"/fms/sys/attach/file/queryFiles"})
    public SysFile query(HttpServletRequest request, SysFile sysFile, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10")int pagesize) {
        IRequest requestCtx = this.createRequestContext(request);
        SysFile dto = sysFileService.selectByPrimaryKey(requestCtx, sysFile.getFileId());
        return dto;
    }
    @RequestMapping(value = {"/update"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData update(HttpServletRequest request, PrdItems dto)  {
        IRequest iRequest = createRequestContext(request);
        try {
			prdItemsService.updateByPrimaryKeySelective(iRequest, dto);
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			ResponseData response=new ResponseData(false);
			response.setMessage(e.getMessage());
			return response;
		}
        return new ResponseData(true);
    }

    /**
     * 查询产品
     * @param request
     * @return
     */
    @RequestMapping(value = {"/queryByChannel"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData queryByChannel(PrdItems prdItems, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        List<PrdItems> prdItemsList = prdItemsService.selectByChannel(iRequest,prdItems);
        return new ResponseData(prdItemsList);
    }

    /**
     * 查询子产品
     * @param request
     * @return
     */
    @RequestMapping(value = {"/querySublineByChannel"}, method = {RequestMethod.POST})
    @ResponseBody
    public ResponseData querySublineByChannel(PrdItemSubline prdItemSubline, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(prdItemSublineService.selectByChannel(requestContext,prdItemSubline));
    }
    
    @RequestMapping(value="/verificationData", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData verificationData(HttpServletRequest request,MultipartFile file,String importNum) throws IOException{
		ResponseData responseData = new ResponseData();
		IRequest requestContext =createRequestContext(request);
        String fileName = file.getOriginalFilename();
        List<List<String>> dataList=FileReadUtil.fileRead(file,0);
        List<List<String>> dataList1=FileReadUtil.fileRead(file,1);
        List<List<String>> dataList2=FileReadUtil.fileRead(file,2);
        List<List<String>> dataList3=FileReadUtil.fileRead(file,3);
        List<List<String>> dataList4=FileReadUtil.fileRead(file,4);
        if(CollectionUtils.isEmpty(dataList)){
        	responseData.setMessage(fileName + "是一个空文件！");
            responseData.setSuccess(false);
        }else if(dataList.size()==1){
        	responseData.setMessage(fileName + "无数据可导入！");
            responseData.setSuccess(false);
        }else{
        	
        	responseData.setSuccess(true);
        	//导入唯一标识
        	importNum=(StringUtils.isBlank(importNum))? WXPayUtil.getCurrentTimestampMs()+"":importNum;
        	responseData.setMessage(importNum);
        	//另起线程
    		OtherThread threa=new OtherThread(requestContext, dataList, dataList1, dataList2, dataList3, dataList4, null, null, null, null, null, importNum);
    		new Thread(threa).start();
        }
        return responseData;
	}
    
    @RequestMapping(value="/uploadData", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData uploadData(HttpServletRequest request,String importNum) throws IOException{
		ResponseData responseData = new ResponseData(true);
		IRequest requestContext =createRequestContext(request);
		Map<String, Object> importDate=ClbImportController.importDate;
		Map<String, Object> map=(Map<String, Object>) importDate.get(importNum);
		List<PrdItems> itemList=(List<PrdItems>) map.get("itemList");
    	List<PrdItemPaymode> modeList=(List<PrdItemPaymode>) map.get("modeList");
    	List<PrdItemSubline> subLineList=(List<PrdItemSubline>) map.get("subLineList");
    	List<PrdItemSecurityPlan> planList=(List<PrdItemSecurityPlan>)map.get("planList");
    	List<PrdItemSelfpay> selfList=(List<PrdItemSelfpay>)map.get("selfList");
		importDate.remove(importNum);
		//导入唯一标识
    	responseData.setMessage(importNum);
    	//另起线程
		OtherThread threa=new OtherThread(requestContext, null, null, null, null, null, itemList, modeList, subLineList, planList, selfList, importNum);
		new Thread(threa).start();
        return responseData;
	}
    
    class OtherThread implements Runnable{
		private IRequest irequest;
		private List<List<String>> dataList;
		private List<List<String>> dataList1;
		private List<List<String>> dataList2;
		private List<List<String>> dataList3;
		private List<List<String>> dataList4;
		private List<PrdItems> itemList;
		private List<PrdItemPaymode> modeList;
		private List<PrdItemSubline> subLineList;
		private List<PrdItemSecurityPlan> planList;
		private List<PrdItemSelfpay> selfList;
		private String importNum;
		
		public OtherThread(IRequest irequest,List<List<String>> dataList,List<List<String>> dataList1,List<List<String>> dataList2,List<List<String>> dataList3,List<List<String>> dataList4,
				List<PrdItems> itemList,List<PrdItemPaymode> modeList,List<PrdItemSubline> subLineList,List<PrdItemSecurityPlan> planList,List<PrdItemSelfpay> selfList,String importNum){
			this.irequest=irequest;
			this.dataList=dataList;
			this.dataList1=dataList1;
			this.dataList2=dataList2;
			this.dataList3=dataList3;
			this.dataList4=dataList4;
			this.itemList=itemList;
			this.modeList=modeList;
			this.subLineList=subLineList;
			this.planList=planList;
			this.selfList=selfList;
			this.importNum=importNum;
		}
		
		@Override
		public void run() {
			if(dataList==null){
				ResponseData response=new ResponseData(true);
				response.setCode("sucess");
				//执行导入
				try {
					//执行导入
					prdItemsService.importInitialData(irequest, itemList, modeList, subLineList, planList, selfList);
				} catch (Exception e) {
					CommonUtil.printStackTraceToStr(e);
					response.setCode("ERROR");
					response.setMessage("系统异常："+e.getMessage());
				}
				Map<String, Object> importDate=ClbImportController.importDate;
				importDate.put(importNum, response);
			}else{
				Map<String, Object> importDate=ClbImportController.importDate;
				//把信息放到一个map中，再把这个map放到 导入map内存中
				Map<String, Object> map=new HashMap<String, Object>();
				ResponseData responseDate=new ResponseData(true);
				//执行校验
				try {
					List<PrdItems> itemList=new ArrayList<PrdItems>();
		        	List<PrdItemPaymode> modeList=new ArrayList<PrdItemPaymode>();
		        	List<PrdItemSubline> subLineList=new ArrayList<PrdItemSubline>();
		        	List<PrdItemSecurityPlan> planList=new ArrayList<PrdItemSecurityPlan>();
		        	List<PrdItemSelfpay> selfList=new ArrayList<PrdItemSelfpay>();
		        	List<ImportResponse>list1=prdItemsService.verificationItems(irequest, dataList, itemList);
		        	List<ImportResponse>list2=prdItemsService.verificationPaymode(irequest, dataList1,itemList, modeList);
		        	List<ImportResponse>list3=prdItemsService.verificationSubline(irequest, dataList2,itemList, subLineList);
		        	List<ImportResponse>list4=prdItemsService.verificationPlan(irequest, dataList3,itemList, planList);
		        	List<ImportResponse>list5=prdItemsService.verificationSelfpay(irequest, dataList4,itemList, selfList);
		        	
		        	List<ImportResponse>list=new ArrayList<ImportResponse>();
		        	if(!CollectionUtils.isEmpty(list1)) list.addAll(list1);
		        	if(!CollectionUtils.isEmpty(list2)) list.addAll(list2);
		        	if(!CollectionUtils.isEmpty(list3)) list.addAll(list3);
		        	if(!CollectionUtils.isEmpty(list4)) list.addAll(list4);
		        	if(!CollectionUtils.isEmpty(list5)) list.addAll(list5);
		        	
		        	//把返回的数据放到
					responseDate=new ResponseData(list);
		        	
		        	int errorTotal=0;
		        	int warnTotal=0;
		        	for(ImportResponse response:list){
		        		if(response.getType().equals("错误")){
		        			errorTotal++;
		        		}else if(response.getType().equals("警告")){
		        			warnTotal++;
		        		}
		        	}
		        	responseDate.setMessage("上传数据:"+(dataList.size()+dataList1.size()+dataList2.size()+dataList3.size()+dataList4.size()-10)+"条,错误数据:"+errorTotal+"处，警告数据:"+warnTotal+"处   ");
		        	if(errorTotal>0){
		        		responseDate.setCode("ERROR");
		        	}else{
		        		map.put("itemList", itemList);
		        		map.put("modeList", modeList);
		        		map.put("subLineList", subLineList);
		        		map.put("planList", planList);
		        		map.put("selfList", selfList);
		        	}
				} catch (Exception e) {
					CommonUtil.printStackTraceToStr(e);
					responseDate.setCode("ERROR");
					responseDate.setMessage("系统异常："+e.getMessage());
				}
				map.put("responseDate", responseDate);
				importDate.put(importNum, map);
				
			}
		}
	}
}