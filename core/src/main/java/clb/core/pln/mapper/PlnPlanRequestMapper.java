package clb.core.pln.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.pln.dto.PlnPlanRequest;

public interface PlnPlanRequestMapper extends Mapper<PlnPlanRequest>{

	/**
	 * 前端查看计划书申请接口
	 * @param plnPlanRequest
	 * @return
	 */
	List<PlnPlanRequest> selectPlanRequest(PlnPlanRequest plnPlanRequest);
	
	/**
	 * 查询我的计划书数量
	 * @param createdBy
	 * @return
	 */
	int selectPlanCountByUser(Long userId);
	
	/**
	 * 查询计划书总数量
	 * @param userId
	 * @return
	 */
	int selectPlanTotalByUser(Long userId);
	
	/**
	 * 查询文件路径
	 * @param fileId
	 * @return
	 */
	String selectFilePath(Long fileId);
	
	/**
	 * 查询组员
	 * @param userId
	 * @return
	 */
	List<PlnPlanRequest> selectTeamUser(Long userId);
	
	/**
	 * 获取计划书信息
	 * @param requestNumber
	 * @return
	 */
	PlnPlanRequest selectForCrawlersInfo(String requestNumber);
	
	/**
	 * 获取team计划书信息
	 * @param plnPlanRequest
	 * @return
	 */
	List<PlnPlanRequest> selectTeamPlanRequest(PlnPlanRequest plnPlanRequest);
	
	/**
	 * 用户当月申请单数量
	 * @param createdBy
	 * @return
	 */
	int selectPlanCountByUserMonth(Long createdBy);
	
	/**
	 * 签单数量
	 * @param createdBy
	 * @return
	 */
	int selectEffectiveCountByUser(Long createdBy);
	
	/**
	 * 后端计划书查询
	 * @param plnPlanRequest
	 * @return
	 */
	List<PlnPlanRequest> selectPlanRequestForBack(PlnPlanRequest plnPlanRequest);
	
	/**
	 * 查询角色类型
	 * @param userId
	 * @return
	 */
	String selectRoleType(Long userId);
	
	/**
	 * 通过计划书行政id查询计划书
	 * @param userId
	 * @return
	 */
	List<PlnPlanRequest> selectTeamUserByAgency(Long userId);
	
	/**
	 * 获取codemeaning
	 * @param code
	 * @param codeValue
	 * @return
	 */
	String selectCodeMeaning(@Param(value="code")String code, @Param(value="codeValue")String codeValue);
	
	/**
	 * 查询渠道名称
	 * @param userId
	 * @return
	 */
	String selectChannelName(Long userId);
	/**
	 * 查询渠道申请的计划书数量
	 * @param channelId
	 * @return
	 */
	int selectPlanCountByChannelId(Long channelId);
	/**
	 * 查询渠道签单的计划书数量
	 * @param channelId
	 * @return
	 */
	int selectEffectiveCountByChannelId(Long channelId);
	/**
	 * 微信查询本年申请的计划书数量
	 * @param dto
	 * @return
	 */
	int queryMyPlanCount(PlnPlanRequest dto);
	/**
	 * 微信  我的计划书查询
	 * @param dto
	 * @return
	 */
	List<PlnPlanRequest> selectMyPlanForWX(PlnPlanRequest dto);

}