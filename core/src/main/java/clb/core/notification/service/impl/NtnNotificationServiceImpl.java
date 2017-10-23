package clb.core.notification.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.dto.HapInterfaceOutbound;
import com.hand.hap.intergration.service.IHapInterfaceOutboundService;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.utils.CommonUtil;
import clb.core.notification.dto.NtnNotification;
import clb.core.notification.dto.NtnNotificationTemplate;
import clb.core.notification.mapper.NtnNotificationMapper;
import clb.core.notification.mapper.NtnNotificationTemplateMapper;
import clb.core.notification.service.INtnNotificationService;
import clb.core.sys.service.ISysSendEmailService;
import clb.core.sys.service.ISysSendSMSService;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;
import clb.core.system.service.IClbCodeService;
import clb.core.wecorp.dto.BasicTemplateDTO;
import clb.core.wecorp.dto.MessageTemplateDTO;
import clb.core.wecorp.service.IWxService;
import clb.core.whtcustom.dto.WhtOfficialAccountCfg;
import clb.core.whtcustom.mapper.WhtOfficialAccountCfgMapper;
import net.sf.json.JSONObject;

@Service
@Transactional
@SuppressWarnings("rawtypes")
public class NtnNotificationServiceImpl extends BaseServiceImpl<NtnNotification> implements INtnNotificationService{

	private static Logger logger=LoggerFactory.getLogger(NtnNotificationServiceImpl.class);
	
	@Autowired
    private ClbUserMapper userMapper;
	@Autowired
	private NtnNotificationMapper notificationMapper;
	@Autowired
	private NtnNotificationTemplateMapper ntnNotificationTemplateMapper;
	@Autowired
	private IHapInterfaceOutboundService interfaceOutboundService;
	@Autowired
	private ISysSendSMSService  sendSMSService;
	@Autowired
	private ISysSendEmailService sendEmailService;
	@Autowired
	private IClbCodeService clbCodeService;
	@Autowired
	private IWxService wxService;
	@Autowired
	private WhtOfficialAccountCfgMapper whtOfficialAccountCfgMapper;
	
	@Override
	public List<NtnNotification> queryList(IRequest request, NtnNotification dto, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return notificationMapper.queryList(dto);
	}
	
	@Override
	public ResponseData updateStatus(IRequest request, List<NtnNotification> dto) {
		ResponseData response=new ResponseData(true);
		try {
			for(NtnNotification bean:dto){
				NtnNotification beanNew=new NtnNotification();
				beanNew.setNotificationId(bean.getNotificationId());
				beanNew.setStatus(bean.getStatus());
				updateByPrimaryKeySelective(request, beanNew);
			}
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			CommonUtil.printStackTraceToStr(e);
		}
		return response;
	}
	
	@Override
	public void saveNotificationByTemplate(IRequest request,Long userId, NtnNotificationTemplate template,Map data) {
		if(template.getPlatformFlag().equals("Y")){
			NtnNotification notification=new NtnNotification();
			notification.setTitle(template.getName());
			notification.setContent(CommonUtil.translateData(template.getPlatformContent(), data));
			notification.setStatus("NEW");
			if(StringUtils.isNotBlank(template.getRedirectPage())){
				String valueDesc=clbCodeService.getCodeDescByValue(request, "NOTIFICATION.REDIRECT_PAGE", template.getRedirectPage());
				notification.setRequestUrl(CommonUtil.translateData(valueDesc, data));
			}
			
			notification.setImportantFlag(template.getImportantFlag());
			if(userId==null){
				//userId为空，则取模板的用户
				ClbUser user=new ClbUser();
				user.setUserType(template.getObject());
				List<ClbUser> userlist=userMapper.select(user);
				for(ClbUser user1:userlist){
					insertNotificationForUser(user1, notification);
				}
			}else{
				ClbUser user1=userMapper.selectByPrimaryKey(userId);
				insertNotificationForUser(user1, notification);
			}
		}
		
	}

	public void insertNotificationForUser(ClbUser user,NtnNotification notification){
		//是否设置接收站内信
		if(user.getNtcFlag().equals("Y") && user.getPlatformFlag().equals("Y")){
			notification.setUserId(user.getUserId());
			notificationMapper.insertSelective(notification);
		}
	}
	
	@Override
	public void saveNotificationByTemplateCode(IRequest request,Long userId, String templateCode, Map data) {
		NtnNotificationTemplate ntn=new NtnNotificationTemplate();
		ntn.setTemplateCode(templateCode);
		ntn.setPlatformFlag("Y");
		List<NtnNotificationTemplate> templateList=ntnNotificationTemplateMapper.select(ntn);
		if(!CollectionUtils.isEmpty(templateList)){
			saveNotificationByTemplate(request ,userId, templateList.get(0), data);
		}
	}

	@Override
	public boolean resetPullSMSByOutboundIds(IRequest request, String[] outboundIds) {
		boolean resetFlag=false;
		for(String outboundId:outboundIds){
			HapInterfaceOutbound outbound=new HapInterfaceOutbound();
			outbound.setOutboundId(Long.valueOf(outboundId));
			outbound.setRequestStatus("failure");
			outbound=interfaceOutboundService.selectByPrimaryKey(request, outbound);
			if(outbound!=null){
				resetFlag=true;
				String requestParameter=outbound.getRequestParameter();
				JSONObject requestjson= JSONObject.fromObject(requestParameter);
				String content=requestjson.getString("content");
				String responseParameter=outbound.getResponseContent();
				JSONObject responsejson= JSONObject.fromObject(responseParameter);
				if(outbound.getInterfaceName().equals("短信接口_云之讯")){
					if(responsejson.getInt("code")!=0){
						String numStr=responsejson.getString("mobile");
						List<String>numList=Arrays.asList(numStr.split(","));
						sendSMSService.sendSMSDirect(request, content,numList , Long.valueOf(outboundId),"YZX");
					}
				}else{
					if(responsejson.getString("returnstatus").equals("Faild")){
						String numStr=requestjson.getString("mobile");
						List<String>numList=Arrays.asList(numStr.split(","));
						sendSMSService.sendSMSDirect(request, content,numList , Long.valueOf(outboundId),"YZX");
					}
				}
			}
		}
		return resetFlag;
	}

	@Override
	public void sendNotification(IRequest request,Long userId, String templateCode, Map sysdata) {
		NtnNotificationTemplate ntn=new NtnNotificationTemplate();
		ntn.setTemplateCode(templateCode);
		ntn.setStatus("Y");
		List<NtnNotificationTemplate> templateList=ntnNotificationTemplateMapper.select(ntn);
		if(!CollectionUtils.isEmpty(templateList)){
			/*****
			 * 2017-08-03
			 * 如果用户绑定了微信，则不发短信； 
			 */
			ClbUser clbUser=new ClbUser();
			clbUser.setUserId(userId);
			clbUser =userMapper.selectAllFields(clbUser).get(0);
			if(StringUtils.isBlank(clbUser.getWechatOpenid()) || !clbUser.getWechatBindType().equals("Y")
				 || !clbUser.getWechatConcernType().equals("Y")	|| clbUser.getWechatFlag().equals("N")){
				//短信通知
				sendSMSService.sendSMSByTemplate(request,userId, templateList.get(0), sysdata,"YZX");
			}else{
				//是否设置接收微信
				if(clbUser.getNtcFlag().equals("Y") && clbUser.getWechatFlag().equals("Y")){
					//微信通知
					try {
						MessageTemplateDTO messageTemplateDTO=new MessageTemplateDTO();
						messageTemplateDTO.setTouser(clbUser.getWechatOpenid());
						messageTemplateDTO.setTemplate_id(templateList.get(0).getWebchatApi());
						messageTemplateDTO.setUrl(CommonUtil.translateData(templateList.get(0).getWhtJumpUrl(), sysdata));
						String content=templateList.get(0).getWebchatContent();
						if(StringUtils.isNotBlank(content)){
							if(sysdata!=null){
								content=CommonUtil.translateData(content, sysdata);
							}
							JSONObject json=new JSONObject();
							json=JSONObject.fromObject(content);
							Iterator iterator = json.keys();
							Map<String,BasicTemplateDTO> data=new HashMap<String,BasicTemplateDTO>();
							while(iterator.hasNext()){
								String key = (String) iterator.next();
								String  value = json.getString(key);
								JSONObject j= JSONObject.fromObject(value);
								BasicTemplateDTO dto=(BasicTemplateDTO) JSONObject.toBean(j, BasicTemplateDTO.class);
								data.put(key, dto);
							}
							messageTemplateDTO.setData(data);
							WhtOfficialAccountCfg whtOfficialAccountCfg=new WhtOfficialAccountCfg();
							whtOfficialAccountCfg.setOriginalId(templateList.get(0).getOriginalId());
							JSONObject sendWXResult=wxService.sendTemplate(messageTemplateDTO,whtOfficialAccountCfgMapper.select(whtOfficialAccountCfg).get(0).getBackgroundAppid() );
							logger.info("微信发送通知结果："+sendWXResult);
						}else{
							logger.info("微信发送为空！");
						}
					} catch (Exception e) {
						CommonUtil.printStackTraceToStr(e);
					}
				}
			}
			
			
			//站内通知
			saveNotificationByTemplate(request,userId, templateList.get(0), sysdata);
			//email通知
			sendEmailService.sendEmailByTemplate(request, userId, templateList.get(0), sysdata);
		}
		
	}

	
}