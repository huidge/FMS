package clb.core.pln.service;

import java.io.File;
import java.util.List;
import java.util.Set;

import clb.core.pln.dto.PlnPlanLibrary;
import com.hand.hap.attachment.exception.StoragePathNotExsitException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;

import clb.core.pln.dto.PlnPlanRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;

public interface IPlnPlanRequestService extends IBaseService<PlnPlanRequest>, ProxySelf<IPlnPlanRequestService>{

	List<PlnPlanRequest> selectPlanRequest(PlnPlanRequest plnPlanRequest, IRequest requestCtx,int page, int pageSize);

	ResponseData exePlnSpider(PlnPlanRequest plnPlanRequest, IRequest requestCtx);

	int selectPlanCountByUser(Long createdBy);

	int selectPlanTotalByUser(Long userId);

	String selectFilePath(Long fileId);

	List<PlnPlanRequest> selectTeamUser(Long userId);

	PlnPlanRequest selectForCrawlersInfo(String requestNumber);

	List<PlnPlanRequest> selectTeamPlanRequest(PlnPlanRequest plnPlanRequest, IRequest requestCtx,int page, int pageSize);

	public ResponseData requestSubmit(IRequest request,PlnPlanRequest dto);

	public ResponseData reviewPlan(IRequest request, PlnPlanRequest dto);

	public ResponseData completedPlan(IRequest request, List<PlnPlanRequest> dto);

	boolean checkPlnSpider(PlnPlanRequest plnPlanRequest,IRequest request);

	public ResponseData requestUpdate(IRequest request,PlnPlanRequest dto);

	void handingPdf(PlnPlanRequest dto, HttpServletResponse response, HttpServletRequest request,IRequest requestContext);
	
	int selectPlanCountByUserMonth(Long createdBy);
	
	int selectEffectiveCountByUser(Long createdBy);
	
	List<PlnPlanRequest> selectPlanRequestForBack(PlnPlanRequest plnPlanRequest, IRequest requestCtx,int page, int pageSize);
	
	/**
	 * 通过用户id查用户类型
	 * @param userId
	 * @return
	 */
	String selectRoleType(Long userId);
	
	/**
	 * 通过计划书行政查询该行政下面的记录信息
	 * @param userId
	 * @return
	 */
	List<PlnPlanRequest> selectTeamUserByAgency(Long userId);
	
	/**
	 * 获取code meaning
	 * @param code
	 * @param codeValue
	 * @return
	 */
	String selectCodeMeaning(String code, String codeValue);
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
	 * @param requestCtx
	 * @param dto
	 * @return
	 */
	int queryMyPlanCount(IRequest requestCtx,PlnPlanRequest dto);
	/**
	 * 微信  我的计划书查询
	 * @param dto
	 * @param requestContext
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<PlnPlanRequest> selectMyPlanForWX(PlnPlanRequest dto, IRequest requestContext, int page, int pageSize);

	Set<String> getTopChannels(Long channelId);
}