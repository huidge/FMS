package clb.core.order.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.order.dto.OrdAddition;
import clb.core.order.dto.OrdAfter;
import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdOrderRenewal;
import clb.core.order.dto.OrdTemplate;
import clb.core.order.dto.OrdTemplateLine;

public interface IOrdAfterService extends IBaseService<OrdAfter>, ProxySelf<IOrdAfterService> {
	/**
	 * 售后汇总列表查询接口
	 * 
	 * @param requestContext
	 * @param dto
	 * @return
	 */
	List<OrdAfter> queryWSOrdAfterList(IRequest requestContext, OrdAfter dto, int page, int pagesize);


	Long queryOrdAfterListTotal(IRequest requestContext, OrdAfter dto);


	/**
	 * 售后汇总列表查询接口
	 * 
	 * @param requestContext
	 * @param dto
	 * @return
	 */
	List<OrdAfter> queryOrdAfterList(IRequest requestContext, OrdAfter dto, int page, int pagesize);

	/**
	 * 续保清单列表查询
	 * 
	 * @param requestCtx
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<OrdOrderRenewal> queryOrdRenewalList(IRequest requestCtx, OrdOrderRenewal dto, int page, int pageSize);
	/**
	 * 续保清单列表查询
	 * 
	 * @param requestCtx
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<OrdOrderRenewal> queryWSOrdRenewalList(IRequest requestCtx, OrdOrderRenewal dto, int page, int pageSize);

	/**
	 * 根据订单id查询续保清单
	 * 
	 * @param requestCtx
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<OrdOrderRenewal> queryRenewalByOrderId(IRequest requestCtx, OrdOrderRenewal dto, int page, int pageSize);

	/**
	 * 根据订单id或者保单编号 查询订单的详情
	 * 
	 * @param requestCtx
	 * @param dto
	 * @return
	 */
	List<OrdOrder> queryOrdOrderByOrderId(IRequest requestCtx, OrdOrder dto);

	/**
	 * 查询退保表格  by orderId
	 * 
	 * @param requestCtx
	 * @param ordAddition
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<OrdAddition> queryOrdAdditionByOrderId(IRequest requestCtx, OrdAddition ordAddition, int page, int pageSize);

	/**
	 * 售后跟进=>续保表格
	 * 
	 * @param requestCtx
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<OrdOrderRenewal> queryRenewalInfoByOrderId(IRequest requestCtx, OrdOrderRenewal dto, int page, int pageSize);

	/**
	 * 续保信息/订单详情 通过订单id查询
	 * 
	 * @param requestCtx
	 * @param dto
	 * @return
	 */
	List<OrdOrder> queryOrderByOrdOrderId(IRequest requestCtx, OrdOrder dto);

	/**
	 * 新建售后--->提交
	 * @param request
	 * @param dto
	 * @return
	 */
	List<OrdAfter> ordAfterApplication(IRequest requestContext, OrdAfter dto)throws Exception;
	/**
	 * 售后跟进页面 --->保单信息
	 * @param requestContext
	 * @param dto
	 * @return
	 */
	List<OrdAfter> queryOrderInfoByAfterId(IRequest requestContext, OrdAfter dto);
	/**
	 * 售后列表团队数据
	 * @param requestContext
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<OrdAfter> queryWSOrdAfterTeamList(IRequest requestContext, OrdAfter dto, int page, int pageSize);

	/**
	 * 接口查询订单售后信息
	 * daiqian.shi@hand-china.com
	 * @param request
	 * @param dto
	 * @return
	 */
	List<OrdAfter> queryWsOrdAfter(IRequest request, OrdAfter dto);
	/**
	 * 续保团队列表
	 * @param requestCtx
	 * @param dto
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<OrdOrderRenewal> ClbWsOrdRenewalTeamList(IRequest requestCtx, OrdOrderRenewal dto, int page, int pageSize);
	/**
	 * 后端售后跟进提交
	 * @param requestCtx
	 * @param dto
	 */
	void submit(IRequest requestCtx, List<OrdAfter> dto) throws Exception;
	/**
	 * 售后列表跟进页面---->提交  主要是添加售后跟进记录  还要传递退保表格
	 * @param requestCtx
	 * @param dto
	 */
	void ordAfterSubmit(IRequest requestCtx, OrdAfter dto) throws Exception;
	/**
	 * 前台查询售后项目
	 * @param requestCtx
	 * @param dto
	 * @return
	 */
	List<OrdTemplate> queryOrdAfterProject(IRequest requestCtx, OrdTemplate dto);
	/**
	 * 前台查询售后类型
	 * @param requestCtx
	 * @param dto
	 * @return
	 */
	List<OrdTemplate> queryApplyItem(IRequest requestCtx, OrdTemplate dto);
	
	/**
	 * 修改续保表中当前期数下主险和附加险的信息   修改状态  币种  续保到期日
	 * @param requestCtx
	 * @param dto
	 * @return
	 */
	void updateRenewalInfoByOrderId(IRequest requestCtx, OrdOrderRenewal dto);

	/**
	 * 修改续保表中当前期数下主险和附加险的缴费金额
	 * @param requestCtx
	 * @param dto
	 * @return
	 */
	void updateRenewalTotalAmountByOrderId(IRequest requestCtx, OrdOrderRenewal dto);
	/**
	 * 售后状态为 成功之后 修改下一年的缴费信息  和    续保表中的下一期的缴费总额
	 * @param requestCtx
	 * @param ordOrder
	 */
	void updateOrder(IRequest requestCtx, OrdOrder ordOrder);

	/**
	 * 查询续保的条数
	 * @param requestCtx
	 * @param dto
	 * @return
	 */
	Long queryRenewalTotal(IRequest requestCtx, OrdOrderRenewal dto);

	/**
	 * 取消售后
	 * @param requestContext
	 * @param dto
	 */
	void cancel(IRequest requestContext, OrdAfter dto);
	/**
	 * 查询售后项目 通过售后id
	 * @param dto
	 * @return
	 */
	public String queryAfterProject(OrdAfter dto);
	/**
	 * 查询分配处理人的姓名
	 * @param requestCtx
	 * @param list
	 * @return
	 */
	public List<OrdAfter> queryHandleUserName(IRequest requestCtx,List<OrdAfter> list);
	
	/**
	 * 新增售后跟进记录
	 * @param requestCtx
	 * @param dto
	 */
	public void saveAfterFollow(IRequest requestCtx,OrdAfter dto);
}