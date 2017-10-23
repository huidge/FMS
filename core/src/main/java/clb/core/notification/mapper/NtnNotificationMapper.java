package clb.core.notification.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.notification.dto.NtnNotification;

public interface NtnNotificationMapper extends Mapper<NtnNotification>{

	/*****
	 * 查询通知消息，去除删除数据
	 * @param request
	 * @param dto
	 * @return
	 */
	public List<NtnNotification> queryList(NtnNotification dto);
	/*****
	 * 删除 day 天前的数据
	 * @param day
	 */
	public void deleteOverDueDay();
}