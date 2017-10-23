package clb.core.sys.service;

import java.util.List;
import java.util.Map;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.sys.dto.SysFunctionAllocationRule;
/*****
 * @author tiansheng.ye
 * @Date 2017-05-25
 */
public interface ISysFunctionAllocationRuleService extends IBaseService<SysFunctionAllocationRule>,ProxySelf<ISysFunctionAllocationRuleService>{
	/*****
	 * @Desc 批量修改功能处理分配规则
	 * @param request
	 * @param dto
	 * @return
	 */
	public List<SysFunctionAllocationRule> batchSubmit(IRequest request,List<SysFunctionAllocationRule> dto); 
	/********
	 * @Description 功能处理分配规则查询
	 * @param request
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List<SysFunctionAllocationRule> selectByDto(IRequest request,SysFunctionAllocationRule dto,int page,int pageSize);
	/****
	 *  @Description 根据功能名+字段名+字段值 分配
	 * @param request
	 * @param userType 用户类型
	 * @param createBy 创建者ID
	 * @param allocationUserId 分配用户/如果不为空，则直接用
	 * @param supplierId 供应商ID
	 * @param channelId 渠道ID
	 * @param noticeChannelId 通知渠道ID
	 * @param customerId 客户ID
	 * @param sourceId 来源ID
	 * @param @param functionCode 功能名
	 * @param columnCode 字段名
	 * @param columnValue 字段值
	 * @param noticeMap 发送通知的参数map 
	 * @return 返回分配用户ID
	 *
	 */
	public Long allocationPerson(IRequest request,String userType,Long createBy,Long allocationUserId,Long supplierId,Long channelId,Long noticeChannelId,Long customerId,Long sourceId, String functionCode,String columnCode,String columnValue,Map<String,Object> noticeMap);

	/**
	 * 同上方法,区别:是否发送短信
	 *
	 * @param request
	 * @param userType
	 * @param createBy
	 * @param allocationUserId
	 * @param supplierId
	 * @param channelId
	 * @param noticeChannelId
	 * @param customerId
	 * @param sourceId
	 * @param functionCode
	 * @param columnCode
	 * @param columnValue
	 * @param noticeMap
	 * @param whetherSendMes
	 * @return
	 */
	public Long allocationPerson2(IRequest request, String userType, Long createBy, Long allocationUserId, Long supplierId, Long channelId, Long noticeChannelId, Long customerId, Long sourceId, String functionCode, String columnCode, String columnValue, Map<String, Object> noticeMap, String whetherSendMes);

}
