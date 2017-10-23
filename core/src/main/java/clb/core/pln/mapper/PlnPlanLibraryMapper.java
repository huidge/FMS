package clb.core.pln.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.pln.dto.PlnPlanLibrary;

public interface PlnPlanLibraryMapper extends Mapper<PlnPlanLibrary>{

	/**
	 * 查询计划书库信息
	 * @param plnPlanLibrary
	 * @return
	 */
	List<PlnPlanLibrary> selectLibraryInfo(PlnPlanLibrary plnPlanLibrary);
	
	/**
	 * 查询计划书
	 * @param plnPlanLibrary
	 * @return
	 */
	PlnPlanLibrary selectLibraryByCondition(PlnPlanLibrary plnPlanLibrary);
	
	/**
	 * 查询计划书后端
	 * @param plnPlanLibrary
	 * @return
	 */
	PlnPlanLibrary selectLibraryByConditionForBack(PlnPlanLibrary plnPlanLibrary);
	
	/**
	 * 查询值列表
	 * @param code
	 * @param itemId
	 * @return
	 */
	List<PlnPlanLibrary> selectListByCodeAndItem(@Param(value="code")String code, @Param(value="itemId")Long itemId);
	
	
	/**
	 * 通过产品查付款方式值列表
	 * @param plnPlanLibrary
	 * @return
	 */
	List<PlnPlanLibrary> queryPaymethodByItem(PlnPlanLibrary plnPlanLibrary);
	
	/**
	 * 通过产品查安全级别值列表
	 * @param itemId
	 * @return
	 */
	List<PlnPlanLibrary> querySecurityLevelByItem(@Param(value="itemId")Long itemId);
	
	/**
	 * 通过产品查安全区域值列表
	 * @param itemId
	 * @return
	 */
	List<PlnPlanLibrary> querySecurityAreaByItem(@Param(value="itemId")Long itemId,@Param(value="securityLevel")String securityLevel);
	
	/**
	 * 通过产品查询年期
	 * @param itemId
	 * @return
	 */
	List<PlnPlanLibrary> querySublineItemNameByItem(@Param(value="itemId")Long itemId);
	
	/**
	 * 获取自付选项
	 * @param itemId
	 * @return
	 */
	List<PlnPlanLibrary> querySelfpayByItem(@Param(value="itemId")Long itemId);

	/**
	 * 通过渠道id查询计划书额度数量
	 * @param channelId
	 * @return
	 */
	List<PlnPlanLibrary> queryChannelPlanQuota(PlnPlanLibrary plnPlanLibrary);

	/**
	 * 带参查询我的计划书数量
	 *
	 * @param plnPlanLibrary
	 * @return
	 */
	int getMyPlanCount(PlnPlanLibrary plnPlanLibrary);
	
	
	/**
	 * 产品库查询计划书库
	 * @param plnPlanLibrary
	 * @return
	 */
	List<PlnPlanLibrary> selectLibraryInfoForPrd(PlnPlanLibrary plnPlanLibrary);
}