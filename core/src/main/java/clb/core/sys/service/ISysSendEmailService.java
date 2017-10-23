package clb.core.sys.service;

import java.util.List;
import java.util.Map;

import com.hand.hap.core.IRequest;
import com.hand.hap.mail.PriorityLevelEnum;
import com.hand.hap.mail.dto.Message;

import clb.core.notification.dto.NtnNotificationTemplate;
import net.sf.json.JSONObject;

public interface ISysSendEmailService{

	/***
	 * @param userId 如果userId为空，则取模板设置的用户，否则发该用户ID
	 * @param request
	 * @param templateCode  短信内容
	 * @param data  替换参数
	 * @param numList  电话号码集合
	 * @return
	 */
	public JSONObject sendEmailByTemplateCode(IRequest request,Long userId,String templateCode, Map data);
	/***
	 * 
	 * @param request
	 * @param userId 如果userId为空，则取模板设置的用户，否则发该用户ID
	 * @param template
	 * @param data
	 * @param supplier
	 * @return
	 */
	public JSONObject sendEmailByTemplate(IRequest request,Long userId,NtnNotificationTemplate template, Map data);
	/*****
	 * @param userId 发送用户
	 * @param accountCode 账号
	 * @param subject 主题
	 * @param content 内容
	 * @param priority 发送级别
	 * @param attachmentIds 附件ID
	 * @param emails 接收人邮箱
	 * @return
	 */
	Message addEmailMessage(IRequest request, String accountCode, String subject, String content, PriorityLevelEnum priority,
            List<Long> attachmentIds, List<String> emails);
	
	public JSONObject sendEmailByTemplateCode(IRequest request,String email,String templateCode, Map data);
}
