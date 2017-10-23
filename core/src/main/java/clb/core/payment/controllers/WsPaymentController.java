package clb.core.payment.controllers;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.Dom4jUtil;
import clb.core.payment.dto.PaymentOrder;
import clb.core.payment.service.IPaymentOrderService;
import clb.core.sys.controllers.ClbBaseController;
import net.sf.json.JSONObject;

@Controller
public class WsPaymentController extends ClbBaseController {

	private Logger logger = LoggerFactory.getLogger(WsPaymentController.class);

	@Autowired
	private IPaymentOrderService paymentOrderService;

	@Timed
	@HapInbound(apiName = "创建订单")
	@RequestMapping(value = { "/api/payment/createOrder" })
	@ResponseBody
	public JSONObject createOrder(HttpServletRequest request, @RequestBody PaymentOrder dto) {
		IRequest irequest = createRequestContext(request);
		return paymentOrderService.createOrder(irequest, dto, getIpAddr(request));
	}

	@Timed
	@HapInbound(apiName = "查询订单")
	@RequestMapping(value = { "/api/payment/queryBySource" })
	@ResponseBody
	public ResponseData queryBySource(HttpServletRequest request, @RequestBody PaymentOrder dto) {
		IRequest irequest = createRequestContext(request);
		if (StringUtils.isBlank(dto.getSourceType()) || StringUtils.isBlank(dto.getSourceId())) {
			ResponseData response = new ResponseData(false);
			response.setSuccess(false);
			response.setMessage("来源类型/来源ID不能为空！");
			return response;
		}
		List<PaymentOrder> list = new ArrayList<PaymentOrder>();
		list.add(paymentOrderService.queryBySource(irequest, dto.getSourceType(), dto.getSourceId()));
		return new ResponseData(list);
	}

	/****
	 * 阿里支付
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "阿里支付")
	@RequestMapping(value = { "/api/payment/alipayStart" })
	@ResponseBody
	public ResponseData alipayStart(HttpServletRequest request, @RequestBody PaymentOrder dto) {
		IRequest irequest = createRequestContext(request);
		return paymentOrderService.alipayStart(irequest, dto);
	}

	/**
	 * 微信支付 创建二维码
	 */
	@Timed
	@HapInbound(apiName = "创建微信支付二维码")
	@RequestMapping("/api/payment/wxpayStart")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void wxpayStart(PaymentOrder dto, HttpServletRequest request, HttpServletResponse response) {
		IRequest irequest = createRequestContext(request);
		String orderNumber = dto.getOrderNumber();
		if (StringUtils.isNotBlank(orderNumber)) {
			dto = paymentOrderService.queryByOrderNumber(irequest, orderNumber);
			if (dto == null) {
				logger.info("查询不到该订单" + orderNumber);
			} else {
				if (dto.getStatus() != null && dto.getStatus().equals("PAID")) {
					logger.info("该订单已支付!");
				} else {
					if (StringUtils.isBlank(dto.getPaymentUrl())) {
						// 订单为空/微信支付url为空，则生成
						// 生成订单
						paymentOrderService.createWXOrderInfo(irequest, dto, getIpAddr(request));
					}
					try {
						int width = 260;
						int height = 260;
						String format = "png";
						Hashtable hints = new Hashtable();
						hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
						BitMatrix bitMatrix = new MultiFormatWriter().encode(dto.getPaymentUrl(), BarcodeFormat.QR_CODE,
								width, height, hints);
						OutputStream out = null;
						out = response.getOutputStream();
						MatrixToImageWriter.writeToStream(bitMatrix, format, out);
						out.flush();
						out.close();
					} catch (Exception e) {
						CommonUtil.printStackTraceToStr(e);
					}
				}
			}
		} else {
			logger.info("获取微信订单地址异常！");
		}
	}

	/******
	 * 支付成功后，修改数据
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */
	@Timed
	@HapInbound(apiName = "订单查询及修改")
	@RequestMapping(value = { "/api/payment/payOff" })
	@ResponseBody
	public ResponseData payOff(HttpServletRequest request, @RequestBody PaymentOrder dto) {
		IRequest irequest = createRequestContext(request);
		return paymentOrderService.payOff(irequest, dto, getIpAddr(request));
	}

	/******
	 * 支付成功后，修改数据 订单通知接口，用于支付宝支付成功后，阿里支付服务器请求
	 * 
	 * @param request
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	@Timed
	@HapInbound(apiName = "通知接口")
	@RequestMapping(value = "/api/public/noticeUrl", method = {RequestMethod.GET,RequestMethod.POST})
	public void noticeUrl(HttpServletRequest request, HttpServletResponse httpResponse) throws Exception {
		IRequest irequest = createRequestContext(request);
		// 商户订单号
		String out_trade_no =request.getParameter("out_trade_no")==null?null: new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
		logger.info("out_trade_no:" + out_trade_no);
		PaymentOrder dto = new PaymentOrder();
		if (StringUtils.isNotBlank(out_trade_no)) {
			dto.setOrderNumber(out_trade_no);
			dto.setPaymentType("ALIPAY");
		} else {
			dto.setPaymentType("WXPAY");
			BufferedReader reader = request.getReader();
			String line = "";
			StringBuffer inputString = new StringBuffer();
			try {
				while ((line = reader.readLine()) != null) {
					inputString.append(line);
				}
				request.getReader().close();
				logger.info("接收到的报文:" + inputString.toString());
				TreeMap<String, String> rtnMap=Dom4jUtil.xml2Map(inputString.toString());
				dto.setOrderNumber(rtnMap.get("out_trade_no"));
			} catch (Exception e) {
				logger.debug("微信通知异常：", e);
			}
		}
		ResponseData response = paymentOrderService.payOff(irequest, dto, getIpAddr(request));
		logger.info("调用通知接口信息: orderNum={}, success={}, message={}", dto.getOrderNumber(), response.isSuccess(),
				response.getMessage());
		httpResponse.setContentType("text/html;charset=utf-8");
		PrintWriter out = httpResponse.getWriter();
		if (StringUtils.isNotBlank(out_trade_no)) {
			if (response.isSuccess()) {
				out.println("success");
			} else {
				out.println("fail");
			}
		} else {
			if (response.isSuccess()) {
				out.print(
						"<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
			} else {
				out.print("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA["
						+ response.getMessage() + "]]></return_msg></xml>");
			}
		}
		out.close();
	}

	@Timed
	@HapInbound(apiName = "查询订单")
	@RequestMapping(value = { "/api/payment/queryByOrderNumber" })
	@ResponseBody
	public ResponseData queryByOrderNumber(HttpServletRequest request, @RequestBody PaymentOrder dto) {
		IRequest irequest = createRequestContext(request);
		if (StringUtils.isBlank(dto.getOrderNumber())) {
			ResponseData response = new ResponseData(false);
			response.setSuccess(false);
			response.setMessage("订单编号不能为空！");
			return response;
		}
		return new ResponseData(paymentOrderService.select(irequest, dto, 1, 10));
	}
	
}
