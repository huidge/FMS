package clb.core.notification.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.notification.dto.NtnNotificationTemplate;

public interface INtnNotificationTemplateService extends IBaseService<NtnNotificationTemplate>, ProxySelf<INtnNotificationTemplateService>{
	
	List<NtnNotificationTemplate> selectAllField(IRequest requestContext, NtnNotificationTemplate ntnNotificationTemplate ,int page, int pagesize);
	
	List<NtnNotificationTemplate> selectAll(IRequest requestContext, NtnNotificationTemplate ntnNotificationTemplate);
}