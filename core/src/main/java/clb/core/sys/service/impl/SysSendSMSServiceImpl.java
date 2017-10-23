package clb.core.sys.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.dto.HapInterfaceOutbound;
import com.hand.hap.intergration.service.IHapInterfaceOutboundService;
import com.hand.hap.system.service.IProfileService;

import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.SendSMSUtil;
import clb.core.notification.dto.NtnNotificationTemplate;
import clb.core.notification.mapper.NtnNotificationTemplateMapper;
import clb.core.sys.service.ISysSendSMSService;
import clb.core.system.dto.ClbUser;
import clb.core.system.service.IClbUserService;
import net.sf.json.JSONObject;

/*****
 * @author tiansheng.ye
 * @Date 2017-05-18
 */
@Service
@Transactional
@SuppressWarnings("rawtypes")
public class SysSendSMSServiceImpl implements ISysSendSMSService{

	/*private String SMS_URL="SMS_URL";
	private String SMS_APPKEY="SMS_APPKEY";
	private String SMS_SECRET="SMS_SECRET";
	private String SMS_SIGNNAME="SMS_SIGNNAME";*/
	private String SMS_URL_YZX="SMS_URL_YZX";
	private String SMS_CLIENTID_YZX="SMS_CLIENTID_YZX";
	private String SMS_PASSWORD_YZX="SMS_PASSWORD_YZX";
	private String SMS_SMSTYPE_YZX="4";
	private String SMS_SIGNNAME_YZX="SMS_SIGNNAME_YZX";
	private String SMS_URL_HX="SMS_URL_HX";
	private String SMS_ACCOUNT_HX="SMS_ACCOUNT_HX";
	private String SMS_PASSWORD_HX="SMS_PASSWORD_HX";
	private String SMS_SIGNNAME_HX="SMS_SIGNNAME_HX";
	
	@Autowired
	private IProfileService profileService;
	@Autowired
	private NtnNotificationTemplateMapper ntnNotificationTemplateMapper;
	@Autowired
	private IHapInterfaceOutboundService interfaceOutboundService;
	@Autowired
	private IClbUserService clbUserService;
	
	@Override
	public JSONObject sendSMSByTemplateCode(IRequest request,Long userId,String templateCode, Map data,String supplier) {
		NtnNotificationTemplate ntn=new NtnNotificationTemplate();
		ntn.setTemplateCode(templateCode);
		ntn.setMessageFlag("Y");
		List<NtnNotificationTemplate> templateList=ntnNotificationTemplateMapper.select(ntn);
		JSONObject json=new JSONObject();
		if(!CollectionUtils.isEmpty(templateList)){
			sendSMSByTemplate(request,userId, templateList.get(0), data,supplier);
		}else{
			json.put("success", false);
			json.put("msg", "找不到模板");
		}
		return json;
	}
	
	@Override
	public JSONObject sendSMSByTemplate(IRequest request,Long userId, NtnNotificationTemplate template, Map data,String supplier) {
		JSONObject json=new JSONObject();
		json.put("success", true);
		if(template.getMessageFlag().equals("Y")){
			//手机号码
			List<String>numList =new ArrayList<String>();
			List<ClbUser> userList=clbUserService.queryUserForNotice(request, template.getObject(), userId);
			for(ClbUser user:userList){
				if(user.getPhone()!=null &&(user.getUserId()==null || (user.getNtcFlag().equals("Y") && user.getSmsFlag().equals("Y"))) ){
					if(StringUtils.isBlank(user.getPhoneCode())|| user.getPhoneCode().equals("86") || user.getPhoneCode().equals("+86")){
						numList.add(user.getPhone());
					}else{
						numList.add(user.getPhoneCode().replace("+", "")+user.getPhone());
					}
				}
			}
			if(!CollectionUtils.isEmpty(numList)){
				json=sendSMSDirect(request, CommonUtil.translateData(template.getMessageContent(), data), numList, null, supplier);
			}else{
				json.put("success", false);
				json.put("msg", "找不到用户");
			}
		}else{
			json.put("success", false);
			json.put("msg", "该模板设置不发短信");
		}
		return json;
	}

	@Override
	public JSONObject sendSMSDirect(IRequest request, String content, List<String> numList,Long outboundId,String supplier) {
		JSONObject json=new JSONObject();
		List<HapInterfaceOutbound> boundList=new ArrayList<HapInterfaceOutbound>();
		if(supplier!=null && supplier.equals("YZX")){
			String urlYZX=profileService.getProfileValue(request,SMS_URL_YZX);
			String clientidYZX=profileService.getProfileValue(request,SMS_CLIENTID_YZX);
			String passwordYZX=profileService.getProfileValue(request, SMS_PASSWORD_YZX);
			String signNameYZX=profileService.getProfileValue(request, SMS_SIGNNAME_YZX);
			SendSMSUtil smsUtil=new SendSMSUtil(urlYZX, clientidYZX, passwordYZX,SMS_SMSTYPE_YZX, signNameYZX);
			json=smsUtil.sendSMS(content,  numList,boundList,"YZX");
		}else{
			String urlHX=profileService.getProfileValue(request,SMS_URL_HX);
			String accountHX=profileService.getProfileValue(request,SMS_ACCOUNT_HX);
			String passwordHX=profileService.getProfileValue(request, SMS_PASSWORD_HX);
			String signNameHX=profileService.getProfileValue(request, SMS_SIGNNAME_HX);
			SendSMSUtil smsUtil=new SendSMSUtil(urlHX,accountHX,passwordHX,signNameHX);
			json=smsUtil.sendSMS(content,  numList,boundList,"HX");
		}
		if(outboundId!=null){
			for(HapInterfaceOutbound bound:boundList){
				bound.setOutboundId(outboundId);
				interfaceOutboundService.updateByPrimaryKeySelective(request, bound);
			}
		}else{
			for(HapInterfaceOutbound bound:boundList){
				interfaceOutboundService.insertSelective(request, bound);
			}
		}
		return json;
	}

	@Override
	public JSONObject sendSMSByTemplate(IRequest request, String phone, String phoneCode,String templateCode, Map data) {
		JSONObject json=new JSONObject();
		json.put("success", true);
		NtnNotificationTemplate ntn=new NtnNotificationTemplate();
		ntn.setTemplateCode(templateCode);
		ntn.setMessageFlag("Y");
		List<NtnNotificationTemplate> templateList=ntnNotificationTemplateMapper.select(ntn);
		if(!CollectionUtils.isEmpty(templateList)){
			if(templateList.get(0).getMessageFlag().equals("Y")){
				//手机号码
				List<String>numList =new ArrayList<String>();
				if(StringUtils.isBlank(phoneCode)|| phoneCode.equals("86") || phoneCode.equals("+86")){
					numList.add(phone);
				}else{
					numList.add(phoneCode.replace("+", "")+phone);
				}
				if(!CollectionUtils.isEmpty(numList)){
					json=sendSMSDirect(request, CommonUtil.translateData(templateList.get(0).getMessageContent(), data), numList, null, "YZX");
				}else{
					json.put("success", false);
					json.put("msg", "找不到用户");
				}
			}else{
				json.put("success", false);
				json.put("msg", "该模板设置不发短信");
			}
		}else{
			json.put("success", false);
			json.put("msg", "找不到模板");
		}
		return json;
	}

}
