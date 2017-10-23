package clb.core.sys.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;

import com.hand.hap.core.IRequest;
import com.hand.hap.mail.PriorityLevelEnum;
import com.hand.hap.mail.ReceiverTypeEnum;
import com.hand.hap.mail.dto.Message;
import com.hand.hap.mail.dto.MessageReceiver;
import com.hand.hap.mail.service.IMessageService;

import clb.core.common.utils.CommonUtil;
import clb.core.notification.dto.NtnNotificationTemplate;
import clb.core.notification.mapper.NtnNotificationTemplateMapper;
import clb.core.sys.service.ISysSendEmailService;
import clb.core.system.dto.ClbUser;
import clb.core.system.service.IClbUserService;
import net.sf.json.JSONObject;

/*****
 * 
 * @author tiansheng.ye@hand-china.com
 * @Desc 邮件发送
 */
@Service
public class SysSendEmailServiceImpl implements ISysSendEmailService{

	@Autowired
	private IMessageService messageService;
	@Autowired
	private DataSourceTransactionManager transactionManager;
	@Autowired
	private NtnNotificationTemplateMapper ntnNotificationTemplateMapper;
	@Autowired
	private IClbUserService clbUserService;
	
	@Override
	public JSONObject sendEmailByTemplateCode(IRequest request, Long userId, String templateCode, Map data) {
		NtnNotificationTemplate ntn=new NtnNotificationTemplate();
		ntn.setTemplateCode(templateCode);
		ntn.setEmailFlag("Y");
		List<NtnNotificationTemplate> templateList=ntnNotificationTemplateMapper.select(ntn);
		JSONObject json=new JSONObject();
		if(!CollectionUtils.isEmpty(templateList)){
			sendEmailByTemplate(request,userId, templateList.get(0), data);
		}else{
			json.put("success", false);
			json.put("msg", "找不到模板");
		}
		return json;
	}

	@Override
	public JSONObject sendEmailByTemplate(IRequest request, Long userId, NtnNotificationTemplate template, Map data) {
		JSONObject json=new JSONObject();
		json.put("success", true);
		if(template.getEmailFlag().equals("Y")){
			//邮箱
			List<String>emails =new ArrayList<String>();
			List<ClbUser> userList=clbUserService.queryUserForNotice(request, template.getObject(), userId);
			for(ClbUser user:userList){
				if(StringUtils.isNotBlank(user.getEmail()) &&(user.getUserId()==null || (user.getNtcFlag().equals("Y") && user.getEmailFlag().equals("Y")))){
					emails.add(user.getEmail());
				}
			}
			
			if(!CollectionUtils.isEmpty(emails)){
				addEmailMessage(request, "CLB",CommonUtil.translateData(template.getEmailSubject(), data) 
						, CommonUtil.translateData(template.getEmailContent(), data), PriorityLevelEnum.NORMAL, null, emails);
			}else{
				json.put("success", false);
				json.put("msg", "找不到用户");
			}
		}else{
			json.put("success", false);
			json.put("msg", "该模板设置不发Email");
		}
		return json;
	}
	
	@Override
	public Message addEmailMessage(IRequest request, String accountCode, String subject, String content,
			PriorityLevelEnum priority, List<Long> attachmentIds, List<String> emails) {
		/***
		 * PROPAGATION_NOT_SUPPORTED （非事务方式）
		 * 存在事务则将这个事务挂起，并使用新的数据库连接。新的数据库连接不使用事务
		 * 防止发email 异常时，造成事务回滚
		 */
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
		TransactionStatus transactionStatus = transactionManager.getTransaction(def);
		
		try {
			Long userId=request.getUserId();
			userId=(userId==null || userId<1)?10001L:userId;
			List<MessageReceiver> receivers=new ArrayList<MessageReceiver>();
			for(String email:emails) {
				if(StringUtils.isNotBlank(email) && CommonUtil.isEmail(email)){
					MessageReceiver mr = new MessageReceiver();
					mr.setMessageAddress(email);
					mr.setMessageType(ReceiverTypeEnum.NORMAL.getCode());
					receivers.add(mr);
				}
			}
			if(CollectionUtils.isEmpty(receivers)){
				return null;
			}
			return messageService.addEmailMessage(userId, accountCode, subject, content, priority, attachmentIds, receivers);
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
		}finally {
			transactionManager.commit(transactionStatus);
		}
		return null;
	}

	@Override
	public JSONObject sendEmailByTemplateCode(IRequest request, String email, String templateCode, Map data) {
		NtnNotificationTemplate ntn=new NtnNotificationTemplate();
		ntn.setTemplateCode(templateCode);
		ntn.setEmailFlag("Y");
		List<NtnNotificationTemplate> templateList=ntnNotificationTemplateMapper.select(ntn);
		JSONObject json=new JSONObject();
		if(!CollectionUtils.isEmpty(templateList)){
			//邮箱
			List<String>emails =new ArrayList<String>();
			if(StringUtils.isNotBlank(email)){
				emails.add(email);
			}
			if(!CollectionUtils.isEmpty(emails)){
				addEmailMessage(request, "CLB",CommonUtil.translateData(templateList.get(0).getEmailSubject(), data) 
						, CommonUtil.translateData(templateList.get(0).getEmailContent(), data), PriorityLevelEnum.NORMAL, null, emails);
			}else{
				json.put("success", false);
				json.put("msg", "邮箱为空");
			}
		}else{
			json.put("success", false);
			json.put("msg", "找不到模板");
		}
		return json;
	}


}
