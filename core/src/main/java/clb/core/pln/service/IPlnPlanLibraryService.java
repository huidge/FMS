package clb.core.pln.service;

import java.util.List;

import clb.core.pln.dto.PlnPlanRequest;
import org.apache.ibatis.annotations.Param;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.pln.dto.PlnPlanLibrary;

public interface IPlnPlanLibraryService extends IBaseService<PlnPlanLibrary>, ProxySelf<IPlnPlanLibraryService>{

	/**
	 * 查询计划书库信息
	 * @param request
	 * @param plnPlanLibrary
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<PlnPlanLibrary> selectLibraryInfo(IRequest request, PlnPlanLibrary plnPlanLibrary,int page,int pageSize);

	/**
	 * 查询计划书库信息
	 * @param plnPlanLibrary
	 * @return
	 */
	PlnPlanLibrary selectLibraryByCondition(PlnPlanLibrary plnPlanLibrary);
	
	/**
	 * 查询计划书库信息后端
	 * @param plnPlanLibrary
	 * @return
	 */
	PlnPlanLibrary selectLibraryByConditionForBack(PlnPlanLibrary plnPlanLibrary);

	/**
	 * 编码和产品查值列表
	 * @param code
	 * @param itemId
	 * @return
	 */
	List<PlnPlanLibrary> selectListByCodeAndItem(String code, Long itemId);

	/**
	 * 查询产品缴费方式
	 * @param requestCtx
	 * @param dto
	 * @return
	 */
	List<PlnPlanLibrary> queryPaymethodByItem(IRequest requestCtx, PlnPlanLibrary dto);

	/**
	 * 查询产品安全区域
	 * @param requestCtx
	 * @param dto
	 * @return
	 */
	List<PlnPlanLibrary> querySecurityLevelByItem(Long itemId);

	/**
	 * 查询产品安全区域
	 * @param itemId
	 * @return
	 */
	List<PlnPlanLibrary> querySecurityAreaByItem(Long itemId,String securityLevel);

	/**
	 * 查询年期通过产品id
	 * @param itemId
	 * @return
	 */
	List<PlnPlanLibrary> querySublineItemNameByItem(Long itemId);

	/**
	 * 获取自付选项
	 * @param itemId
	 * @return
	 */
	List<PlnPlanLibrary> querySelfpayByItem(Long itemId);

	/**
	 * 计划书库导入
	 */
	void importPlanInfo(IRequest requestCtx, PlnPlanLibrary dto);

	/**
	 * 通过渠道查询该渠道的可用额度
	 * @param channelId
	 * @return
	 */
	List<PlnPlanLibrary> queryChannelPlanQuota(PlnPlanLibrary dto);

	/**
	 * 带参查询我的计划书数量
	 *
	 * @param plnPlanLibrary
	 * @param requestContext
	 * @return
	 */
	Integer queryMyPlanCount(PlnPlanLibrary plnPlanLibrary, IRequest requestContext);
	
	/**
	 * 通过产品查计划书库
	 * @param request
	 * @param plnPlanLibrary
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<PlnPlanLibrary> selectLibraryInfoForPrd(IRequest request, PlnPlanLibrary plnPlanLibrary,int page,int pageSize);
}