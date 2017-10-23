package clb.core.payment.service.impl;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import clb.core.system.dto.ClbUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alipay.api.response.AlipayTradeQueryResponse;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IProfileService;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.utils.CommonUtil;
import clb.core.course.dto.TrnCourse;
import clb.core.course.dto.TrnCourseStudent;
import clb.core.course.dto.TrnSupport;
import clb.core.course.service.ITrnCourseService;
import clb.core.course.service.ITrnCourseStudentService;
import clb.core.course.service.ITrnSupportService;
import clb.core.notification.service.INtnNotificationService;
import clb.core.order.dto.OrdOrder;
import clb.core.order.service.IOrdOrderService;
import clb.core.payment.dto.PaymentOrder;
import clb.core.payment.mapper.PaymentOrderMapper;
import clb.core.payment.service.IPaymentOrderService;
import clb.core.payment.utils.PaymentClientUtil;
import clb.core.payment.utils.UnifiedOrderRequest;
import clb.core.payment.utils.WXPayUtil;
import clb.core.production.dto.PrdItems;
import clb.core.production.service.IPrdItemsService;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.mapper.ClbUserMapper;
import clb.core.system.service.IClbCodeService;
import net.sf.json.JSONObject;

@Service
@Transactional
public class PaymentOrderServiceImpl extends BaseServiceImpl<PaymentOrder> implements IPaymentOrderService {

	private Logger logger = LoggerFactory.getLogger(PaymentOrderServiceImpl.class);

	@Autowired
	private IClbCodeService clbCodeService;
	@Autowired
	private IProfileService profileService;
	@Autowired
	private PaymentOrderMapper paymentOrderMapper;
	@Autowired
	private ITrnCourseStudentService trnCourseStudentService;
	@Autowired
	private ITrnCourseService trnCourseService;
	@Autowired
	private IOrdOrderService ordOrderService;
	@Autowired
	private ITrnSupportService trnSupportService;
	@Autowired
	private IPrdItemsService prdItemsService;
	@Autowired
	private INtnNotificationService ntnNotificationService;
	@Autowired
	private ClbUserMapper clbUserMapper;
	@Autowired
	private ITrnCourseStudentService service;

	private static String PAY_NOTIFY_URL;
	private String PAY_RETURN_URL;
	private String ALIPAY_APPID;
	private String ALIPAY_PRIVATE_KEY;
	private String ALIPAY_PUBLIC_KEY;
	private String ALIPAY_GATEWAY_URL;
	private String WXPAY_APPID;
	private String WXPAY_MCHID;
	private String WXPAY_KEY;

	public void queryProfile(IRequest request) {
		// 服务器异步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
		PAY_NOTIFY_URL = profileService.getProfileValue(request, "PAY_NOTIFY_URL");
		// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
		PAY_RETURN_URL = profileService.getProfileValue(request, "PAY_RETURN_URL");
		// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
		ALIPAY_APPID = profileService.getProfileValue(request, "ALIPAY_APPID");
		// 商户私钥，您的PKCS8格式RSA2私钥
		ALIPAY_PRIVATE_KEY = profileService.getProfileValue(request, "ALIPAY_PRIVATE_KEY");
		// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm
		// 对应APPID下的支付宝公钥。
		ALIPAY_PUBLIC_KEY = profileService.getProfileValue(request, "ALIPAY_PUBLIC_KEY");
		// 支付宝网关
		ALIPAY_GATEWAY_URL = profileService.getProfileValue(request, "ALIPAY_GATEWAY_URL");
		// 支付配置信息
		WXPAY_APPID = profileService.getProfileValue(request, "WXPAY_APPID");
		WXPAY_MCHID = profileService.getProfileValue(request, "WXPAY_MCHID");
		WXPAY_KEY = profileService.getProfileValue(request, "WXPAY_KEY");
	}

	@Override
	public JSONObject createOrder(IRequest request, PaymentOrder dto, String ip) {
		JSONObject response = new JSONObject();
		response.put("success", true);
		response.put("createWx", true);
		if (StringUtils.isBlank(dto.getPaymentType())) {
			dto.setPaymentType("ALIPAY");
		}
		if (StringUtils.isBlank(dto.getSourceId())) {
			response.put("success", false);
			response.put("message", "支付来源ID不能为空");
			return response;
		}
		if (StringUtils.isBlank(dto.getSourceType())) {
			response.put("success", false);
			response.put("message", "支付来源不能为空");
			return response;
		} else {
			ClbCodeValue code = new ClbCodeValue();
			code.setValue(dto.getSourceType());
			code.setCode("PAY.SOURCETYPE");
			if (CollectionUtils.isEmpty(clbCodeService.selectCodeValuesByParam(code))) {
				response.put("success", false);
				response.put("message", "快码中查询不到该支付来源:" + dto.getSourceType());
				return response;
			}
		}
		// 获取订单源数据的金额-------以及增加支付限制时间参数
		DecimalFormat df=new DecimalFormat("######0.00");
		Double amountSource = queryAmount(request, dto);
		amountSource=Double.valueOf(df.format(amountSource));
		if (!amountSource.equals(dto.getAmount())) {
			response.put("success", false);
			response.put("message", "订单金额与源数据金额不匹配,源金额：" + amountSource);
			return response;
		}

		// 查询订单，是否存在
		PaymentOrder order = queryBySource(request, dto.getSourceType(), dto.getSourceId());
		if (order.getStatus() != null && order.getStatus().equals("PAID")) {
			response.put("success", false);
			response.put("message", "该订单已支付!");
			return response;
		}
		// 保存或者更新订单
		dto.setPaymentId(order.getPaymentId());
		dto.setOrderNumber(order.getOrderNumber());
		dto = saveOrUpdateOrder(request, dto);

		response = createWXOrderInfo(request, dto, ip);

		response.put("orderNumber", dto.getOrderNumber());
		response.put("paymentUrl", dto.getPaymentUrl());
		return response;
	}

	@Override
	public ResponseData alipayStart(IRequest request, PaymentOrder dto) {
		ResponseData response = new ResponseData(true);
		// 查询订单，是否存在
		if (StringUtils.isBlank(dto.getOrderNumber())) {
			response.setSuccess(false);
			response.setMessage("订单编号不能为空!");
			return response;
		}
		PaymentOrder order = queryByOrderNumber(request, dto.getOrderNumber());
		if (order == null) {
			response.setSuccess(false);
			response.setMessage("查询不到该订单" + dto.getOrderNumber());
			return response;
		}
		if (order.getStatus() != null && order.getStatus().equals("PAID")) {
			response.setSuccess(false);
			response.setMessage("该订单已支付!");
			return response;
		}
		// 加载配置
		queryProfile(request);

		String result = PaymentClientUtil.alipay(ALIPAY_GATEWAY_URL, ALIPAY_APPID, ALIPAY_PRIVATE_KEY,
				ALIPAY_PUBLIC_KEY, PAY_RETURN_URL, PAY_NOTIFY_URL, order.getOrderNumber(), order.getAmount(),
				order.getOrderSubject(), order.getOrderContent());
		response.setMessage(result);
		return response;
	}

	@Override
	public PaymentOrder queryBySource(IRequest request, String sourceType, String sourceId) {
		PaymentOrder newDto = new PaymentOrder();
		newDto.setSourceId(sourceId);
		newDto.setSourceType(sourceType);
		List<PaymentOrder> list = paymentOrderMapper.select(newDto);
		if (CollectionUtils.isEmpty(list)) {
			return new PaymentOrder();
		}
		return list.get(0);
	}

	@Override
	public PaymentOrder saveOrUpdateOrder(IRequest request, PaymentOrder dto) {
		if (dto.getPaymentId() == null) {
			dto.setOrderNumber(CommonUtil.getTradeNo());
			dto.setStatus("UNPAID");
			dto = self().insertSelective(request, dto);
		} else {
			// 如果金额、商品标题、商品详情改变了，则更新订单编号
			PaymentOrder oldOrder = selectByPrimaryKey(request, dto);
			if ((dto.getAmount() != null && !oldOrder.getAmount().equals(dto.getAmount()))
					|| (dto.getOrderSubject() != null && !oldOrder.getOrderSubject().equals(dto.getOrderSubject()))
					|| (dto.getOrderContent() != null && !oldOrder.getOrderContent().equals(dto.getOrderContent()))
					|| (StringUtils.isNotBlank(dto.getTradeType()) && StringUtils.isNotBlank(oldOrder.getPaymentUrl()) )
					|| (StringUtils.isBlank(oldOrder.getPaymentUrl()) && StringUtils.isBlank(dto.getTradeType()) )) {
				dto.setOrderNumber(CommonUtil.getTradeNo());
			}

			dto = self().updateByPrimaryKeySelective(request, dto);
		}
		return dto;
	}
	public PaymentOrder updateOrderUrl(IRequest request, PaymentOrder dto) {
		dto = self().updateByPrimaryKeySelective(request, dto);
		return dto;
	}

	/**
	 * 生成订单
	 *
	 * @param orderId
	 * @return
	 */
	public JSONObject createWXOrderInfo(IRequest request, PaymentOrder dto, String ip) {
		// 加载配置
		queryProfile(request);

		JSONObject response = new JSONObject();
		response.put("success", true);
		response.put("createWx", true);
		String orderInfo = null;
		// 生成订单对象
		UnifiedOrderRequest unifiedOrderRequest = new UnifiedOrderRequest();
		unifiedOrderRequest.setAppid(WXPAY_APPID);// 公众账号ID
		unifiedOrderRequest.setMch_id(WXPAY_MCHID);// 商户号
		unifiedOrderRequest.setNonce_str(dto.getOrderNumber());// 随机字符串
		unifiedOrderRequest.setBody(dto.getOrderSubject());// 商品描述
		unifiedOrderRequest.setOut_trade_no(dto.getOrderNumber());// 商户订单号
		unifiedOrderRequest.setTotal_fee(new DecimalFormat("#.##").format((dto.getAmount() * 100))); // 金额需要扩大100倍:1代表支付时是0.01
		unifiedOrderRequest.setSpbill_create_ip(ip);// 终端IP
		unifiedOrderRequest.setNotify_url(PAY_NOTIFY_URL);// 通知地址
		unifiedOrderRequest.setTrade_type(StringUtils.isBlank(dto.getTradeType())? "NATIVE":dto.getTradeType());// JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付
		String sign=PaymentClientUtil.createWXSign(unifiedOrderRequest, WXPAY_KEY,dto.getOpenid());
		unifiedOrderRequest.setSign(sign);// 签名<span
		Map<String, String> data = CommonUtil.classToMapOfValueStr(unifiedOrderRequest);
		if(StringUtils.isNotBlank(dto.getOpenid())){
			data.put("openid", dto.getOpenid());
		}

		try {
			orderInfo = WXPayUtil.mapToXml(data);
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			orderInfo = null;
		}
		if (orderInfo == null) {
			logger.error("生成微信订单信息异常");
			response.put("createWx", false);
			response.put("message", "生成微信订单信息异常");
		} else {
			// 调统一下单API
			logger.info("下单信息："+orderInfo);
			Map<String, String> orderMap = PaymentClientUtil.httpWXOrder(orderInfo);
			logger.info("下单返回信息："+orderMap);
			if ((orderMap.get("return_code") + "").equals("FAIL")) {
				logger.error("调用微信支付异常：" + orderMap.get("return_msg"));
				response.put("createWx", false);
				response.put("message", "调用微信支付异常：" + orderMap.get("return_msg"));
			} else {
				if ((orderMap.get("result_code") + "").equals("FAIL")) {
					logger.error("调用微信支付异常：" + orderMap.get("err_code_des"));
					response.put("createWx", false);
					response.put("message", "调用微信支付异常：" + orderMap.get("err_code_des"));
				} else {
					// 返回code不等于FAIL时，取返回的地址
					dto.setPaymentUrl(orderMap.get("code_url")==null?"":orderMap.get("code_url"));
					dto = updateOrderUrl(request, dto);
					if(StringUtils.isNotBlank(dto.getOpenid())){
						//需要提供给微信的参数
						String nonceStr=WXPayUtil.generateUUID();
						String timeStamp=WXPayUtil.getCurrentTimestamp()+"";
						response.put("sign", sign);
						response.put("package", "prepay_id="+orderMap.get("prepay_id"));
						response.put("timeStamp", timeStamp);
						response.put("nonceStr", nonceStr);
						response.put("returnResult", orderMap);
						SortedMap<String, String> packageParams = new TreeMap<String, String>();
						packageParams.put("appId",WXPAY_APPID);
						packageParams.put("nonceStr",nonceStr );
						packageParams.put("package", "prepay_id="+orderMap.get("prepay_id"));
						packageParams.put("signType","MD5" );
						packageParams.put("timeStamp",timeStamp );
						response.put("paySign", PaymentClientUtil.createWXSign(packageParams, WXPAY_KEY));
					}
				}
			}
		}
		return response;
	}

	@Override
	public String queryWxOrderInfo(IRequest request, PaymentOrder dto, String ip) {
		// 加载配置
		queryProfile(request);

		String orderInfo = null;

		// 生成订单对象
		UnifiedOrderRequest unifiedOrderRequest = new UnifiedOrderRequest();
		unifiedOrderRequest.setAppid(WXPAY_APPID);// 公众账号ID
		unifiedOrderRequest.setMch_id(WXPAY_MCHID);// 商户号
		unifiedOrderRequest.setOut_trade_no(dto.getOrderNumber());// 商户订单号
		unifiedOrderRequest.setNonce_str(dto.getOrderNumber());// 随机字符串
		unifiedOrderRequest.setSign(PaymentClientUtil.createWXSign(unifiedOrderRequest, WXPAY_KEY,null));// 签名<span

		Map<String, String> data = CommonUtil.classToMapIgnoreNull(unifiedOrderRequest);

		try {
			orderInfo = WXPayUtil.mapToXml(data);
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			orderInfo = null;
		}
		return orderInfo;
	}

	@Override
	public ResponseData payOff(IRequest request, PaymentOrder dto, String ip) {

		ResponseData response = new ResponseData(true);
		String paymentType = dto.getPaymentType();
		if (StringUtils.isBlank(dto.getOrderNumber())) {
			response.setSuccess(false);
			response.setMessage("支付单号不能为空");
			return response;
		}
		PaymentOrder newDto = new PaymentOrder();
		newDto.setOrderNumber(dto.getOrderNumber());
		List<PaymentOrder> list = paymentOrderMapper.select(newDto);
		if (CollectionUtils.isEmpty(list)) {
			response.setSuccess(false);
			response.setMessage("查询不到该支付单据");
			return response;
		}
		dto = list.get(0);
		if (dto.getStatus() != null && dto.getStatus().equals("PAID")) {
			response.setSuccess(true);
			response.setMessage("该订单已支付!");
			return response;
		}
		if (paymentType != null && paymentType.equals("WXPAY")) {
			// 订单为空/微信支付url为空，则生成
			// 生成订单
			String orderInfo = queryWxOrderInfo(request, dto, ip);
			if (orderInfo == null) {
				response.setSuccess(false);
				response.setMessage("生成微信订单信息异常");
				return response;
			} else {
				// 查询订单API
				Map<String, String> orderMap = PaymentClientUtil.queryWXOrder(orderInfo);
				if ((orderMap.get("return_code") + "").equals("FAIL")) {
					response.setSuccess(false);
					response.setMessage("调用微信支付异常：" + orderMap.get("return_msg"));
					return response;
				} else {
					if ((orderMap.get("result_code") + "").equals("FAIL")) {
						response.setSuccess(false);
						response.setMessage("调用微信支付异常：" + orderMap.get("err_code_des"));
						return response;
					} else {
						// 返回code不等于FAIL时，取返回的地址
						if (!(orderMap.get("trade_state") + "").equals("SUCCESS")) {
							response.setSuccess(false);
							response.setMessage(orderMap.get("trade_state_desc"));
							return response;
						} else {
							response.setMessage("支付完成");
						}
					}
				}
			}
		} else {
			// 加载配置
			queryProfile(request);

			AlipayTradeQueryResponse queryResponse = PaymentClientUtil.queryAlipay(ALIPAY_GATEWAY_URL, ALIPAY_APPID,
					ALIPAY_PRIVATE_KEY, ALIPAY_PUBLIC_KEY, dto.getOrderNumber());
			logger.info("支付宝返回信息：" + queryResponse == null ? null : queryResponse.getBody());
			if (queryResponse.getCode().equals("10000")) {
				response.setMessage("支付完成");
				dto.setPaymentType("ALIPAY");
			} else {
				response.setSuccess(false);
				response.setMessage(queryResponse.getMsg());
				return response;
			}
		}

		dto.setStatus("PAID");
		dto.setPayDate(new Date());
		// 清空支付地址
		dto.setPaymentUrl(" ");
		updateByPrimaryKeySelective(request, dto);
		/*****
		 * 根据 sourceId、sourceType 查询到对应的表数据，进行更新
		 */
		updateSourceDate(request, dto);

		response.setMessage("支付完成");
		return response;
	}

	/*****
	 * 校验订单金额
	 *
	 * @param request
	 * @param dto
	 * @return
	 */
	public Double queryAmount(IRequest request, PaymentOrder dto) {
		Double price = 0.0;
		String[] ids = dto.getSourceId().split(",");
		if (dto.getSourceType().equals("COURSE")) {
			for (String id : ids) {
				if (StringUtils.isNotBlank(id)) {
					TrnCourseStudent cs = new TrnCourseStudent();
					cs.setLineId(Long.valueOf(id));
					cs = trnCourseStudentService.selectByPrimaryKey(request, cs);
					TrnCourse trnCourse = new TrnCourse();
					trnCourse.setCourseId(cs.getCourseId());
					trnCourse = trnCourseService.selectByPrimaryKey(request, trnCourse);
					price += trnCourse.getPrice() == null ? 0.0 : trnCourse.getPrice().doubleValue();
				}
			}
		} else if (dto.getSourceType().equals("ORDER")) {
			for (String id : ids) {
				if (StringUtils.isNotBlank(id)) {
					OrdOrder or = new OrdOrder();
					or.setOrderId(Long.valueOf(id));
					or = ordOrderService.selectByPrimaryKey(request, or);
					price += or.getPrice() == null ? 0.0 : Double.valueOf(or.getPrice());

					// 增加插入支付限制时间
					PrdItems item = new PrdItems();
					item.setItemId(or.getItemId());
					item = prdItemsService.selectByPrimaryKey(request, item);
					dto.setPayLimitTime(item.getAttribute91() == null ? 0L : Long.valueOf(item.getAttribute91()));
				}
			}
		} else if (dto.getSourceType().equals("SUPPORT")) {
			for (String id : ids) {
				if (StringUtils.isNotBlank(id)) {
					TrnSupport ts = new TrnSupport();
					ts.setSupportId(Long.valueOf(id));
					ts = trnSupportService.selectByPrimaryKey(request, ts);
					price += ts.getAmount() == null ? 0.0 : ts.getAmount().doubleValue();

					// 增加插入支付限制时间
					String limitTime = profileService.getProfileValue(request, "SUPPORT_PAY_LIMIT_TIME");
					dto.setPayLimitTime(limitTime == null ? 0L : Long.valueOf(limitTime));
				}
			}
		}
		return price;
	}

	/*****
	 * 修改订单源数据
	 *
	 * @param request
	 * @param dto
	 * @return
	 */
	public void updateSourceDate(IRequest request, PaymentOrder dto) {
		String[] ids = dto.getSourceId().split(",");
		if (dto.getSourceType().equals("COURSE")) {
			// 一个用户合并多个订单支付，但只需要发一次
			boolean sendFlag = true;
			for (String id : ids) {
				if (StringUtils.isNotBlank(id)) {
					trnCourseStudentService.changeStatusById(request, "PAID", Long.valueOf(id));
					// 调用通知接口发送通知信息 -KC0001
					TrnCourseStudent trnCourseStudent = new TrnCourseStudent();
					trnCourseStudent.setLineId(Long.valueOf(id));
					trnCourseStudent = trnCourseStudentService.selectByPrimaryKey(request, trnCourseStudent);
					TrnCourse trnCourse = new TrnCourse();
					trnCourse.setCourseId(trnCourseStudent.getCourseId());
					trnCourse = trnCourseService.selectByPrimaryKey(request, trnCourse);
					// 调用通知接口发送通知信息
					Map<String, Object> noticeMap = new HashMap<String, Object>();
					noticeMap.put("topic", trnCourse.getTopic());
					noticeMap.put("password", trnCourse.getPassword());
					if (trnCourseStudent.getCreatedBy() > 0 && sendFlag) {
						ntnNotificationService.sendNotification(request, trnCourseStudent.getCreatedBy(), "KC0001",
								noticeMap);
						sendFlag = false;
					}
				}
			}
		} else if (dto.getSourceType().equals("ORDER")) {
			for (String id : ids) {
				if (StringUtils.isNotBlank(id)) {
					OrdOrder or = new OrdOrder();
					or.setOrderId(Long.valueOf(id));
					or.setStatus("RESERVE_SUCCESS");
					ordOrderService.updateStatus(request, or);
				}
			}
		} else if (dto.getSourceType().equals("SUPPORT")) {
			for (String id : ids) {
				if (StringUtils.isNotBlank(id)) {
					TrnSupport ts = new TrnSupport();
					ts.setSupportId(Long.valueOf(id));
					ts.setStatus("AMOUNT");
					trnSupportService.updateByPrimaryKeySelective(request, ts);
				}
			}
		}
	}

	@Override
	public PaymentOrder queryByOrderNumber(IRequest request, String orderNum) {
		PaymentOrder order = new PaymentOrder();
		order.setOrderNumber(orderNum);
		List<PaymentOrder> list = paymentOrderMapper.select(order);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}


	@Override
	public ResponseData insertPaymentOrder(IRequest request, TrnCourseStudent trnCourseStudent) {
		ResponseData rd =new ResponseData();
		PaymentOrder paymentOrder = new PaymentOrder();
		try {
			if(("COURSE").equals(trnCourseStudent.getAppPayType())){
				trnCourseStudent.setPayStatus("PAID");
				ClbUser clbUser =new ClbUser();
				clbUser.setUserId(trnCourseStudent.getUserId());
				ClbUser clbUser1 = clbUserMapper.selectByPrimaryKey(clbUser);
				trnCourseStudent.setName(clbUser1.getUserName());
				//trnCourseStudent.setJoinMethod(2);

				service.insertSelective(request,trnCourseStudent);

				paymentOrder.setPaymentType(trnCourseStudent.getAppPayType());
				paymentOrder.setSourceId(trnCourseStudent.getCourseId()+"");
				paymentOrder.setOrderSubject("视频支付完成");
				paymentOrder.setOrderContent("视频支付完成");
				paymentOrder.setAmount((double)0);
				paymentOrder.setStatus("PAID");
				paymentOrder.setSourceType("COURSE");
				paymentOrder.setObjectVersionNumber(1L);
				paymentOrder.setOrderNumber(CommonUtil.getTradeNo());
				self().insertSelective(request, paymentOrder);
			}
			else if (("VIDEO").equals(trnCourseStudent.getAppPayType())){
				paymentOrder.setPaymentType(trnCourseStudent.getAppPayType());
				paymentOrder.setSourceId(trnCourseStudent.getCourseId()+"");
				paymentOrder.setOrderSubject("课程报名支付完成");
				paymentOrder.setOrderContent("课程报名支付完成");
				paymentOrder.setAmount((double)0);
				paymentOrder.setStatus("PAID");
				paymentOrder.setSourceType("ViDEO");
				paymentOrder.setObjectVersionNumber(1L);
				paymentOrder.setOrderNumber(CommonUtil.getTradeNo());
				self().insertSelective(request, paymentOrder);
			}
			else {
			}
		}
		catch (Exception e){
			rd.setSuccess(false);
			rd.setMessage(e.getMessage());
			e.printStackTrace();
		}
		return rd;
	}
}