package clb.core.notification.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.notification.dto.NtnNotificationTemplate;

public interface NtnNotificationTemplateMapper extends Mapper<NtnNotificationTemplate>{
	
	List<NtnNotificationTemplate> selectAllField(NtnNotificationTemplate ntnNotificationTemplate);
	
}