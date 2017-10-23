package clb.core.wecorp.controllers;

import clb.core.sys.controllers.ClbBaseController;
import clb.core.wecorp.service.IWxService;
import clb.core.wecorp.service.impl.SecurityUtils;
import clb.core.wecorp.utils.WxPay;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.intergration.exception.HapApiException;
import com.hand.hap.intergration.util.JSONAndMap;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IProfileService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Created by zyc on 2017/8/15.
 */
@Controller
public class WxpayController extends ClbBaseController {

    @Autowired
    private IWxService wxService;

    @Autowired
    @Qualifier("profileServiceImpl")
    private IProfileService profileService;

    private static Logger logger= LoggerFactory.getLogger(WxpayController.class);


    @RequestMapping(value = "/api/wxpay/order")
    @ResponseBody
    public JSONObject order(HttpServletRequest request,@RequestParam String appId,@RequestParam  String mchId,@RequestParam  String secret,@RequestParam  String body,
                            @RequestParam  String orderId) {
        String openId=SecurityUtils.getCurrentUserLogin();
        IRequest iRequest=createRequestContext(request);
        //调用生成订单接口(提供入参：orderId(订单编号)；返回：金额，订单编号（必须为UUID自动生成）)

        return wxService.order(iRequest,appId,  mchId,  secret,  body, UUID.randomUUID().toString(), 1);
    }

    /**
     * 支付成功的回调函数.
     * @param request
     * @param response
     * @param reqXml
     * @throws IOException
     * @throws HapApiException
     */
    @RequestMapping(value = "/api/public/wxpay/SUCCESS", method = RequestMethod.POST)
    @ResponseBody
    public void SUCCESS(HttpServletRequest request,HttpServletResponse response,@RequestBody String reqXml) throws IOException, HapApiException {
        /*<xml>
        <appid><![CDATA[wx2421b1c4370ec43b]]></appid>
        <attach><![CDATA[支付测试]]></attach>
        <bank_type><![CDATA[CFT]]></bank_type>
        <fee_type><![CDATA[CNY]]></fee_type>
        <is_subscribe><![CDATA[Y]]></is_subscribe>
        <mch_id><![CDATA[10000100]]></mch_id>
        <nonce_str><![CDATA[5d2b6c2a8db53831f7eda20af46e531c]]></nonce_str>
        <openid><![CDATA[oUpF8uMEb4qRXf22hE3X68TekukE]]></openid>
        <out_trade_no><![CDATA[1409811653]]></out_trade_no>
        <result_code><![CDATA[SUCCESS]]></result_code>
        <return_code><![CDATA[SUCCESS]]></return_code>
        <sign><![CDATA[B552ED6B279343CB493C5DD0D78AB241]]></sign>
        <sub_mch_id><![CDATA[10000100]]></sub_mch_id>
        <time_end><![CDATA[20140903131540]]></time_end>
        <total_fee>1</total_fee>
        <trade_type><![CDATA[JSAPI]]></trade_type>
        <transaction_id><![CDATA[1004400740201409030005092168]]></transaction_id>
        </xml>*/
        //先回包
        logger.info("**********checkSign:{}" , reqXml);
        response.setContentType("text/txt");
        response.getOutputStream().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>".getBytes());

        //回调逻辑
        Map map = JSONAndMap.xml2map(reqXml);
        Map resMap = (Map) map.get("xml");
        String mch_id = resMap.get("mch_id").toString();
        IRequest irequest = RequestHelper.newEmptyRequest();
        irequest.setUserId(-1L);
        irequest.setRoleId(-1L);
        //根据mch_id找到对应的商户密钥
        String secret= profileService.getProfileValue(irequest, "MCH_ID_"+mch_id);
        logger.info("++++++++++++secret:{}",secret);
        if(secret==null||"".equals(secret)){
            logger.error("error:{}", "MCH_ID_"+mch_id+"为空");
        }else {
            if (WxPay.checkSign(resMap, secret)) {
                //的确是微信端推过来的消息
                String appid = resMap.get("appid").toString();
                String openid = resMap.get("openid").toString();
                String out_trade_no = resMap.get("out_trade_no").toString();
                String time_end = resMap.get("time_end").toString();
                String transaction_id = resMap.get("transaction_id").toString();//支付订单号
                String trade_type = resMap.get("trade_type").toString();
                String total_fee = resMap.get("total_fee").toString();

                String bank_type = resMap.get("bank_type").toString();
                String nonce_str = resMap.get("nonce_str").toString();
                String sign = resMap.get("sign").toString();
                //触发支付成功的业务逻辑（入参：out_trade_no，mch_id,openid）可能面临多次重复的提交  所以流程需自行处理
                logger.info("********************触发支付成功的业务逻辑********************");

            } else {
                logger.info("********************验证签名失败********************");
            }
        }
    }

    /**
     * 退款流程.
     * @param request
     * @param appId
     * @param mchId
     * @param secret
     * @param transactionId
     * @param refundFee
     * @param totalFee
     * @return
     */
    @RequestMapping(value = "/api/wxpay/refund")
    @ResponseBody
    public ResponseData refund(HttpServletRequest request,@RequestParam String appId,@RequestParam  String mchId,@RequestParam String secret,
                            @RequestParam  String transactionId,@RequestParam  String refundFee,@RequestParam String totalFee) {
        IRequest iRequest=createRequestContext(request);
        JSONObject resMap=wxService.refund(iRequest, appId, mchId, secret, transactionId, refundFee, totalFee);
        if(resMap.containsKey("error")){
            ResponseData r= new ResponseData(false);
            r.setMessage(resMap.getString("error"));
            return r;
        }else{
            String out_refund_no = resMap.getString("out_refund_no");//退款单id  用作主键
            String transaction_id = resMap.getString("transaction_id");
            String appid = resMap.getString("appid");
            String mch_id = resMap.getString("mch_id");
            String out_trade_no = resMap.getString("out_trade_no");
            String refund_id = resMap.getString("refund_id");//微信退款单号
            String refund_fee = resMap.getString("refund_fee");//微信退款金额
            //走退款本系统流程

            return new ResponseData(true);
        }
    }
}
