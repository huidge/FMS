package clb.core.production.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.dto.ImportResponse;
import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.FileReadUtil;
import clb.core.payment.utils.WXPayUtil;
import clb.core.production.dto.PrdPremium;
import clb.core.production.service.IPrdPremiumService;
import clb.core.system.controllers.ClbImportController;
/*****
 * @author tiansheng.ye
 * @Date 2017/05/16
 */
@Controller
@RequestMapping(value={"/production/prdPremium/"})
public class PrdPremiumController extends BaseController{
	@Autowired
    private IPrdPremiumService prdPremiumService;
	
	@RequestMapping(value="query")
	public ResponseData query(PrdPremium dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request){
		IRequest requestContext = createRequestContext(request);
        return new ResponseData(prdPremiumService.selectAllFields(requestContext, dto, page, pageSize));
	}
	
	@RequestMapping(value = "remove", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData deleteRole(@RequestBody List<PrdPremium> prdPremiums, BindingResult result, HttpServletRequest request)
            throws BaseException {
		prdPremiumService.batchDelete(prdPremiums);
        return new ResponseData();
    }
	
	@RequestMapping(value="verificationData", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData verificationData(HttpServletRequest request,MultipartFile file,Long itemId,String importNum) throws IOException{
		ResponseData responseData = new ResponseData();
		IRequest requestContext =createRequestContext(request);
        String fileName = file.getOriginalFilename();
        List<List<String>> dataList=FileReadUtil.fileRead(file,0);
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
    		OtherThread threa=new OtherThread(requestContext,dataList,null,itemId,importNum);
    		new Thread(threa).start();
        }
        return responseData;
	}
	
	@RequestMapping(value="uploadData", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData uploadData(HttpServletRequest request,Long itemId,String importNum) throws IOException{
		ResponseData responseData = new ResponseData(true);
		IRequest requestContext =createRequestContext(request);
		Map<String, Object> importDate=ClbImportController.importDate;
		Map<String, Object> map=(Map<String, Object>) importDate.get(importNum);
		List<PrdPremium> beanList=(List<PrdPremium>)map.get("beanList");
		importDate.remove(importNum);
		//导入唯一标识
    	responseData.setMessage(importNum);
    	//另起线程
		OtherThread threa=new OtherThread(requestContext,null,beanList,itemId,importNum);
		new Thread(threa).start();
        return responseData;
	}
	
	class OtherThread implements Runnable{
		private IRequest irequest;
		private List<List<String>> dataList;
		private List<PrdPremium> beanList;
		private Long itemId;
		private String importNum;
		
		public OtherThread(IRequest irequest,List<List<String>> dataList,List<PrdPremium> beanList,Long itemId,String importNum){
			this.irequest=irequest;
			this.dataList=dataList;
			this.beanList=beanList;
			this.itemId=itemId;
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
					prdPremiumService.saveBatch(irequest, beanList,itemId);
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
					List<PrdPremium> beanList=new ArrayList<PrdPremium>();
					List<ImportResponse>list=prdPremiumService.verificationData(irequest, dataList,beanList,itemId);
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
					responseDate.setMessage("上传数据:"+(dataList.size()-1)+"条,错误数据:"+errorTotal+"处，警告数据:"+warnTotal+"处   ");
					if(errorTotal>0){
						responseDate.setCode("ERROR");
					}else{
						map.put("beanList", beanList);
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
