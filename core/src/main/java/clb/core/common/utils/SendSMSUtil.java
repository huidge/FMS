package clb.core.common.utils;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hand.hap.intergration.dto.HapInterfaceOutbound;

import clb.core.common.dto.AccessSmsBO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*****
 * @author tiansheng.ye
 * @Date 2017-05-18
 */
public class SendSMSUtil {

	Logger logger=LoggerFactory.getLogger(SendSMSUtil.class);
	/*****
	 * 云之讯短信接口
	 */
	private String url_yzx;
	private String clientId_yzx;
	private String passWord_yzx;
	private String smsType_yzx;// 0:通知   4：验证码   5:营销
	private String signName_yzx;
	private String url_hx;
	private String account_hx;
	private String password_hx;
	private String signName_hx;
	
	public SendSMSUtil(String url_yzx, String clientId_yzx, String passWord_yzx,String smsType_yzx,String signName_yzx) {
		this.url_yzx = url_yzx;
		this.clientId_yzx = clientId_yzx;
		this.passWord_yzx = passWord_yzx;
		this.smsType_yzx=smsType_yzx;
		this.signName_yzx=signName_yzx;
	}
	
	public SendSMSUtil(String url_hx,String account_hx,String password_hx,String signName_hx){
		this.url_hx=url_hx;
		this.account_hx=account_hx;
		this.password_hx=password_hx;
		this.signName_hx=signName_hx;
	}
	
	public JSONObject sendSMS(String content,List<String>numList,List<HapInterfaceOutbound> boundList,String supprier){
		if(supprier.equals("YZX")){
			return sendSMS_YZX(signName_yzx+content, numList,boundList);
		}else if(supprier.equals("HX")){
			return sendSMS_HX(content+signName_hx, numList, boundList);
		}else{
			return new JSONObject();
		}
	}
	
	public JSONObject sendSMS_YZX(String content,List<String>numList,List<HapInterfaceOutbound> boundList) {
		HapInterfaceOutbound bound=new HapInterfaceOutbound();
		JSONObject retjson=new JSONObject();
		JSONArray jsonArray=new JSONArray();
		Long startTime=System.currentTimeMillis();
		bound.setInterfaceName("短信接口_云之讯");
		bound.setInterfaceUrl(url_yzx);
		bound.setRequestTime(new Date());
		bound.setResponseCode("200");
		bound.setAttribute1("YZX");
		String resultJson = null;
		String requestParameter=null;
		try {
			JSONObject smsModel = new JSONObject();
			smsModel.put("clientid",clientId_yzx);
			smsModel.put("password",MD5Util.getMD5Code(passWord_yzx));
			smsModel.put("mobile",StringUtil.listConvertStr(numList));
			smsModel.put("content",content);
			smsModel.put("smstype",smsType_yzx);
			
			String smsp_access_url = url_yzx.replace("{clientid}",clientId_yzx);
			requestParameter=smsModel.toString();
			bound.setRequestParameter(requestParameter);
			resultJson = HttpUtils.httpPost(smsp_access_url,requestParameter , true);
			logger.info("请求短信结果：" + resultJson);
			jsonArray=JSONArray.fromObject(JSONObject.fromObject(resultJson).get("data"));
			//结束时间
			Long endTime=System.currentTimeMillis();
			bound.setResponseTime(endTime-startTime);
			for(int i=0;i<jsonArray.size();i++){
				retjson=jsonArray.getJSONObject(i);
				if(retjson.getInt("code")==0){
					retjson.put("success", true);
					bound.setRequestStatus("success");
				}else{
					retjson.put("success", false);
					bound.setRequestStatus("success");
				}
				bound.setResponseContent(retjson.toString());
				boundList.add(bound);
			}
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			retjson.put("success", false);
			retjson.put("msg", e.getMessage());
			bound.setStackTrace(CommonUtil.retStackTraceToStr(e));
			bound.setResponseCode("500");
			bound.setRequestStatus("failure");
			bound.setResponseContent(resultJson);
			//结束时间
			Long endTime=System.currentTimeMillis();
			bound.setResponseTime(endTime-startTime);
			boundList.add(bound);
		}
		return retjson;
	}

	public JSONObject sendSMS_HX(String content,List<String>numList,List<HapInterfaceOutbound> boundList) {
		HapInterfaceOutbound bound=new HapInterfaceOutbound();
		JSONObject retjson=new JSONObject();
		Long startTime=System.currentTimeMillis();
		bound.setInterfaceName("短信接口_华信");
		bound.setInterfaceUrl(url_hx);
		bound.setRequestTime(new Date());
		bound.setResponseCode("200");
		bound.setAttribute1("HX");
		String resultJson = null;
		JSONObject requestJson=new JSONObject();
		requestJson.put("account", account_hx);
		requestJson.put("password", password_hx);
		requestJson.put("mobile", StringUtil.listConvertStr(numList));
		requestJson.put("content", content);
		try {
			String posturl=url_hx+"?action=send&userid=&account="+account_hx+"&password="+password_hx
					+"&mobile="+StringUtil.listConvertStr(numList)+"&sendTime=&extno=";
			logger.info(posturl);
			resultJson = HttpUtils.sendPost(posturl,content);
			logger.info("请求短信结果：" + resultJson);
			retjson=JSONObject.fromObject(resultJson);
			if(retjson.getString("returnstatus").equals("Faild")){
				retjson.put("success", false);
				retjson.put("msg", retjson.getString("message"));
			}else{
				retjson.put("success", true);
			}
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			retjson.put("success", false);
			retjson.put("msg", e.getMessage());
			bound.setStackTrace(CommonUtil.retStackTraceToStr(e));
			bound.setResponseCode("500");
		}
		//结束时间
		Long endTime=System.currentTimeMillis();
		bound.setResponseContent(resultJson);
		bound.setResponseTime(endTime-startTime);
		bound.setRequestStatus(retjson.getBoolean("success")?"success":"failure"); 
		bound.setRequestParameter(requestJson.toString());
		boundList.add(bound);
		return retjson;
	}
	
/*	public static void main(String[] args) {
		SendSMSUtil send=new SendSMSUtil("http://sz.ipyy.com/smsJson.aspx", "szzd00161", "fuqiangtianyi.123456");
		List<HapInterfaceOutbound> boundList=new ArrayList<HapInterfaceOutbound>();
		List<String>numList=new ArrayList<String>();
		numList.add("13160810306");
		numList.add("13131");
		JSONObject retjson=send.sendSMS_HX("你的验证是123456【财联邦服务平台】",numList, boundList);
//		SendSMSUtil send=new SendSMSUtil("https://api.ucpaas.com/sms-partner/access/b000q7/sendsms", "b000q7", "ecd33b35","4");
//		HapInterfaceOutbound bound=new HapInterfaceOutbound();
//		List<String>numList=new ArrayList<String>();
//		numList.add("13160810306");
//		numList.add("13131");
//		JSONObject retjson=send.sendSMS_YZX("【财联邦】你的验证是123456", numList, new ArrayList<HapInterfaceOutbound>());
		System.out.println(retjson);
	}*/
	
	/*
	 * 阿里大于 短信接口
	private String url;
	private String appkey;
	private String secret;
	private String signName;

	public SendSMSUtil(String url, String appkey, String secret,String signName) {
		this.url = url;
		this.appkey = appkey;
		this.secret = secret;
		this.signName=signName;
	}

	public JSONObject sendSMS(String templateCode,JSONObject jsonParam,List<String>numList,HapInterfaceOutbound bound) {
		//开始时间
		Long startTime=System.currentTimeMillis();
		JSONObject retjson=new JSONObject();
		bound.setInterfaceName("短信接口");
		bound.setInterfaceUrl(url);
		bound.setRequestTime(new Date());
		
		try {
			TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
			AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
			req.setSmsType("normal");
			req.setSmsFreeSignName(signName);
			if(jsonParam!=null){
				req.setSmsParamString(jsonParam.toString());
			}
			req.setRecNum(numList.toString().substring(1, numList.toString().length()-1));
			req.setSmsTemplateCode(templateCode);
			AlibabaAliqinFcSmsNumSendResponse rsp;
			rsp = client.execute(req);
			//开始时间
			Long endTime=System.currentTimeMillis();
			bound.setResponseContent(rsp.getBody());
			bound.setResponseTime(endTime-startTime);
			retjson=JSONObject.fromObject(rsp.getBody());
			if(retjson.get("alibaba_aliqin_fc_sms_num_send_response")!=null){
				retjson= retjson.getJSONObject("alibaba_aliqin_fc_sms_num_send_response").getJSONObject("result");
			}else{
				if(retjson.get("error_response")!=null){
					retjson=retjson.getJSONObject("error_response");
				} 
				retjson.put("success", false);
			}
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			retjson.put("success", false);
			retjson.put("msg", e.getMessage());
			bound.setStackTrace(CommonUtil.retStackTraceToStr(e));
		}
		bound.setRequestStatus(retjson.getBoolean("success")?"success":"failure"); 
		bound.setResponseCode(retjson.getBoolean("success")?"success":"failure");
		if(jsonParam==null){
			jsonParam=new JSONObject();
		}
		jsonParam.put("phone", numList.toString().substring(1, numList.toString().length()-1));
		jsonParam.put("templateCode", templateCode);
		bound.setRequestParameter(jsonParam==null?null:jsonParam.toString());
		return retjson;
	}*/

}
