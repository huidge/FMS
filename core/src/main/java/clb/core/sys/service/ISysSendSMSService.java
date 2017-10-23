package clb.core.sys.service;

import java.util.List;
import java.util.Map;

import com.hand.hap.core.IRequest;

import clb.core.notification.dto.NtnNotificationTemplate;
import net.sf.json.JSONObject;

/*****
 * @author tiansheng.ye
 * @Date 2017-05-18
 */
@SuppressWarnings("rawtypes")
public interface ISysSendSMSService {

	/***
	 * @param userId 如果userId为空，则取模板设置的用户，否则发该用户ID
	 * @param request
	 * @param templateCode  短信内容
	 * @param data  替换参数
	 * @param numList  电话号码集合
	 * @return
	 */
	public JSONObject sendSMSByTemplateCode(IRequest request,Long userId,String templateCode, Map data,String supplier);
	/***
	 * 
	 * @param request
	 * @param userId 如果userId为空，则取模板设置的用户，否则发该用户ID
	 * @param template
	 * @param data
	 * @param supplier
	 * @return
	 */
	public JSONObject sendSMSByTemplate(IRequest request,Long userId,NtnNotificationTemplate template, Map data,String supplier);
	/******
	 * 
	 * @param request
	 * @param content  短信内容
	 * @param data  替换参数
	 * @param numList  电话号码集合
	 * @return
	 */
	public JSONObject sendSMSDirect(IRequest request,String content, List<String> numList,Long outboundId,String supplier);
	
	public JSONObject sendSMSByTemplate(IRequest request,String phone,String phoneCode,String templateCode, Map data);
}
