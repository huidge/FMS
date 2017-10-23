package clb.core.notification.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.message.IMessagePublisher;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.notification.dto.NtnNotificationTemplate;
import clb.core.notification.mapper.NtnNotificationTemplateMapper;
import clb.core.notification.service.INtnNotificationTemplateService;

@Service
@Transactional
public class NtnNotificationTemplateServiceImpl extends BaseServiceImpl<NtnNotificationTemplate> implements INtnNotificationTemplateService{
	
	@Autowired
    private IMessagePublisher messagePublisher;
	
	@Autowired
	private NtnNotificationTemplateMapper ntnNotificationTemplateMapper;
	
	@Override
	public List<NtnNotificationTemplate> selectAllField(IRequest requestContext,
			NtnNotificationTemplate ntnNotificationTemplate, int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		return ntnNotificationTemplateMapper.selectAllField(ntnNotificationTemplate);
		//return null;
	}
	
	@Override
	public List<NtnNotificationTemplate> selectAll(IRequest requestContext,
			NtnNotificationTemplate ntnNotificationTemplate) {
		return ntnNotificationTemplateMapper.selectAllField(ntnNotificationTemplate);
		//return null;
	}
	
	/*@Override
    public List<NtnNotificationTemplate> batchUpdate(IRequest request, List<NtnNotificationTemplate> list) {
        super.batchUpdate(request, list);
        System.out.println("有了2222222222");
        for (NtnNotificationTemplate e : list) {
            messagePublisher.publish("employee.change", e);
        }
        return list;
    }*/
}