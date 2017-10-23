package clb.core.notification.service;

import java.util.List;
import java.util.Map;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;

import clb.core.notification.dto.NtnNotification;
import clb.core.notification.dto.NtnNotificationTemplate;

public interface INtnNotificationService extends IBaseService<NtnNotification>, ProxySelf<INtnNotificationService>{
	/*****
	 * 查询通知消息，去除删除数据
	 * @param request
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List<NtnNotification> queryList(IRequest request,NtnNotification dto, int page,int pageSize);
	
	public ResponseData updateStatus(IRequest request,List<NtnNotification> dto);
	
	/***
	 * 根据模板代码，保存通知信息
	 * @param userId 如果userId为空，则取模板设置的用户，否则发该用户ID
	 * @param request
	 * @param template 模板
	 * @param data 替换值
	 */
	@SuppressWarnings("rawtypes")
	public void saveNotificationByTemplate(IRequest request,Long userId,NtnNotificationTemplate template,Map data);
	
	/***
	 * 根据模板代码，保存通知信息
	 * @param userId 如果userId为空，则取模板设置的用户，否则发该用户ID
	 * @param request
	 * @param templateCode 模板代码
	 * @param data 替换值
	 */
	@SuppressWarnings("rawtypes")
	public void saveNotificationByTemplateCode(IRequest request,Long userId,String templateCode,Map data);
	/*****
	 * 根据接口调用记录ID，重发错误短信
	 * @param request
	 * @param outboundIds
	 */
	public boolean resetPullSMSByOutboundIds(IRequest request,String[] outboundIds);
	
	/*******
	 * 根据模板，发送站内通知、短信
	 * @param userId 如果userId为空，则取模板设置的用户，否则发该用户ID
	 * @param request
	 * @param templateCode 模板代码
	 * @param sysdata 通知参数
	 */
	@SuppressWarnings("rawtypes")
	public void sendNotification(IRequest request,Long userId,String templateCode,Map sysdata);
}