package clb.core.payment.service;

import clb.core.course.dto.TrnCourse;
import clb.core.course.dto.TrnCourseStudent;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;

import clb.core.payment.dto.PaymentOrder;
import net.sf.json.JSONObject;

import java.util.List;

/*****
 * @author tiansheng.ye
 */
public interface IPaymentOrderService extends IBaseService<PaymentOrder>, ProxySelf<IPaymentOrderService>  {

	/***
	 * 创建订单
	 * @param request
	 * @param dto
	 * @return
	 */
	public JSONObject createOrder(IRequest request,PaymentOrder dto,String ip);
	/****
	 * 阿里支付单
	 * @param request
	 * @param dto
	 * @return
	 */
	public ResponseData alipayStart(IRequest request,PaymentOrder dto);
	
	/**
	 * 根据来源查询数据
	 * @param request
	 * @param sourceType
	 * @param sourceId
	 * @return
	 */
	public PaymentOrder queryBySource(IRequest request,String sourceType,String sourceId);
	/****
	 * 根据订单编号查询
	 * @param request
	 * @param orderNum
	 * @return
	 */
	public PaymentOrder queryByOrderNumber(IRequest request,String orderNum);
	
	/*****
	 * 更新或者保存订单数据
	 * @param request
	 * @param dto
	 * @return
	 */
	public PaymentOrder saveOrUpdateOrder(IRequest request,PaymentOrder dto);
	/****
	 * 创建微信支付单
	 * @return
	 */
	public JSONObject createWXOrderInfo(IRequest request,PaymentOrder dto,String ip);
	
	public String queryWxOrderInfo(IRequest request,PaymentOrder dto,String ip);
	/***
	 * 支付结束，修改状态
	 * @param request
	 * @param dto
	 * @return
	 */
	public ResponseData payOff(IRequest request,PaymentOrder dto,String ip);

	/****
	 * App支付完成后插入记录
	 * @return
	 */
	public ResponseData insertPaymentOrder(IRequest request, TrnCourseStudent dto);
	
}