package clb.core.payment.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;

import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.MD5Util;

public class PaymentClientUtil {
	
	private static Logger logger=LoggerFactory.getLogger(PaymentClientUtil.class);
	
	/******
	 * 
	 * @param gatewayUrl 支付宝网关
	 * @param appId 商户ID
	 * @param merchantPrivateKey 秘钥
	 * @param alipayPublicKey 公钥
	 * @param returnUrl 返回地址
	 * @param notifyUrl 通知地址
	 * @param tradeNo 商户订单号，商户网站订单系统中唯一订单号，必填
	 * @param amount 付款金额，必填
	 * @param subject 订单名称，必填
	 * @param body 商品描述，可空
	 * @throws AlipayApiException 
	 */
	public static String alipay(String gatewayUrl,String appId,String merchantPrivateKey,String alipayPublicKey,String returnUrl,
			String notifyUrl,String tradeNo,Double amount,String subject,String body){
		//获得初始化的AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,appId, merchantPrivateKey, "json","utf-8", alipayPublicKey, "RSA2");
		
		//设置请求参数
		AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
		alipayRequest.setReturnUrl(returnUrl);
		alipayRequest.setNotifyUrl(notifyUrl);
		
		alipayRequest.setBizContent("{\"out_trade_no\":\""+ tradeNo +"\"," 
				+ "\"total_amount\":\""+ amount +"\"," 
				+ "\"subject\":\""+ subject +"\"," 
				+ "\"body\":\""+ body +"\"," 
				+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
		
		//若想给BizContent增加其他可选请求参数，以增加自定义超时时间参数timeout_express来举例说明
		//alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"," 
		//		+ "\"total_amount\":\""+ total_amount +"\"," 
		//		+ "\"subject\":\""+ subject +"\"," 
		//		+ "\"body\":\""+ body +"\"," 
		//		+ "\"timeout_express\":\"10m\"," 
		//		+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
		//请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节
		
		//请求
		String result="";
		try {
			result = alipayClient.pageExecute(alipayRequest).getBody();
		} catch (AlipayApiException e) {
			CommonUtil.printStackTraceToStr(e);
		}
		logger.info("aliPay返回信息:"+result);
		//输出
		return result;
	}
	
	public static AlipayTradeQueryResponse queryAlipay(String gatewayUrl,String appId,String merchantPrivateKey,String alipayPublicKey,String tradeNo){
		//获得初始化的AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,appId, merchantPrivateKey, "json","utf-8", alipayPublicKey, "RSA2");
		//设置请求参数
		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
		request.setBizContent("{" +"\"out_trade_no\":\""+tradeNo+"\" }");
		AlipayTradeQueryResponse response=new AlipayTradeQueryResponse();
		try {
			response = alipayClient.execute(request);
		} catch (AlipayApiException e) {
			CommonUtil.printStackTraceToStr(e);
			response.setCode("99999");
			response.setMsg(e.getLocalizedMessage());
		}
		return response;
	}
	
	/** 
	 * 调统一下单API 
	 * @param orderInfo 
	 * @return 
	 */  
	public static Map<String, String> httpWXOrder(String orderInfo) {  
	    String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";  
	    Map<String, String> map=new HashMap<String, String>();
	    try {  
	        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();  
	        	//加入数据    
	           conn.setRequestMethod("POST");    
	           conn.setDoOutput(true);    
	               
	           BufferedOutputStream buffOutStr = new BufferedOutputStream(conn.getOutputStream());    
	           buffOutStr.write(orderInfo.getBytes("utf-8"));  
	           buffOutStr.flush();    
	           buffOutStr.close();    
	               
	           //获取输入流    
	           BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); 
	               
	           String line = null;    
	           StringBuffer sb = new StringBuffer();    
	           while((line = reader.readLine())!= null){
	               sb.append(line);    
	           }    
	           logger.info("微信支付返回信息:"+sb.toString());  
	           map=WXPayUtil.xmlToMap(sb.toString());
	    } catch (Exception e) {  
	        CommonUtil.printStackTraceToStr(e);
	    }  
	    return map;
	}  
	
	/** 
	 * 查询订单
	 * @param orderInfo 
	 * @return 
	 */  
	public static Map<String, String> queryWXOrder(String orderInfo) {  
	    String url = "https://api.mch.weixin.qq.com/pay/orderquery";  
	    Map<String, String> map=new HashMap<String, String>();
	    try {  
	        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();  
	        	//加入数据    
	           conn.setRequestMethod("POST");    
	           conn.setDoOutput(true);    
	               
	           BufferedOutputStream buffOutStr = new BufferedOutputStream(conn.getOutputStream());    
	           buffOutStr.write(orderInfo.getBytes("utf-8"));  
	           buffOutStr.flush();    
	           buffOutStr.close();    
	               
	           //获取输入流    
	           BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); 
	               
	           String line = null;    
	           StringBuffer sb = new StringBuffer();    
	           while((line = reader.readLine())!= null){
	               sb.append(line);    
	           }    
	           logger.info("查询微信订单返回信息:"+sb.toString());  
	           map=WXPayUtil.xmlToMap(sb.toString());
	    } catch (Exception e) {  
	        CommonUtil.printStackTraceToStr(e);
	    }  
	    return map;
	} 
	
	
	/**
	 * 生成签名
	 * 
	 * @param appid_value
	 * @param mch_id_value
	 * @param productId
	 * @param nonce_str_value
	 * @param trade_type
	 * @param notify_url
	 * @param spbill_create_ip
	 * @param total_fee
	 * @param out_trade_no
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String createWXSign(UnifiedOrderRequest unifiedOrderRequest, String key,String openid) {
		// 根据规则创建可排序的map集合
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid", unifiedOrderRequest.getAppid());
		packageParams.put("body", unifiedOrderRequest.getBody());
		packageParams.put("mch_id", unifiedOrderRequest.getMch_id());
		packageParams.put("nonce_str", unifiedOrderRequest.getNonce_str());
		packageParams.put("notify_url", unifiedOrderRequest.getNotify_url());
		packageParams.put("out_trade_no", unifiedOrderRequest.getOut_trade_no());
		packageParams.put("spbill_create_ip", unifiedOrderRequest.getSpbill_create_ip());
		packageParams.put("trade_type", unifiedOrderRequest.getTrade_type());
		packageParams.put("total_fee", unifiedOrderRequest.getTotal_fee());
		if(StringUtils.isNotBlank(openid)){
			packageParams.put("openid", openid);
		}
		StringBuffer sb = new StringBuffer();
		Set es = packageParams.entrySet();// 字典序
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			// 为空不参与签名、参数名区分大小写
			if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		// 第二步拼接key，key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置
		sb.append("key=" + key);
		String sign = MD5Util.getMD5Code(sb.toString()).toUpperCase();// MD5加密
		return sign;
	}
	
	public static String createWXSign(SortedMap<String, String> packageParams, String key) {
		StringBuffer sb = new StringBuffer();
		Set es = packageParams.entrySet();// 字典序
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			// 为空不参与签名、参数名区分大小写
			if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		// 第二步拼接key，key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置
		sb.append("key=" + key);
		String sign = MD5Util.getMD5Code(sb.toString()).toUpperCase();// MD5加密
		return sign;
	}
}
