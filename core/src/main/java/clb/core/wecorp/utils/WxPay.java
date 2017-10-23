package clb.core.wecorp.utils;

import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by shanhd on 2017/3/5.
 */
public class WxPay {

    /**
     * 微信下单（预支付）
     * @param appId         微信AppId
     * @param mchId         微信商户号
     * @param secret        商户密钥
     * @param body          商品描述
     * @param outTradeNo    商户订单号
     * @param totalFee      支付金额 （单位：分）
     * @param notifyUrl     通知地址（回调地址）
     * @param tradeType     交易类型: APP--app支付、NATIVE--原生扫码支付
     * @return:             参考微信API接口地址：https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_1
     */
    public static String wxOrder(String appId, String mchId, String secret, String body,
                                       String outTradeNo, int totalFee, String notifyUrl,String tradeType) {
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
        packageParams.add(new BasicNameValuePair("appid", appId));
        packageParams.add(new BasicNameValuePair("body", body));
        packageParams.add(new BasicNameValuePair("input_charset", "UTF-8"));
        packageParams.add(new BasicNameValuePair("mch_id", mchId));
        packageParams.add(new BasicNameValuePair("nonce_str", RandomUtil.getNonceStr()));
        packageParams.add(new BasicNameValuePair("notify_url", notifyUrl));//通知地址
        packageParams.add(new BasicNameValuePair("out_trade_no", outTradeNo)); //商户订单号
        packageParams.add(new BasicNameValuePair("total_fee", String.valueOf(totalFee))); //总金额
        packageParams.add(new BasicNameValuePair("trade_type", tradeType));//JSAPI

        String sign = getSign(packageParams, secret);  //获取签名
        packageParams.add(new BasicNameValuePair("sign", sign));

        String reqXml = toXml(packageParams);
        String result=null;
        result = HttpUtil.httpPost(url, reqXml);  //下单
        return result;
    }

    /**
     * 获取用于微信支付的信息（返回给客户端）
     * @param appId     微信AppId
     * @param mchId     微信商户号
     * @param secret    商户密钥
     * @param prepayId  预支付id (从微信下单接口获取)
     * @return
     */
    public static JSONObject getPayInfo(String appId, String mchId,String secret, String prepayId){
        JSONObject dataMap = new JSONObject();
        String noncestr = RandomUtil.getNonceStr();
        String packageName = "Sign=WXPay";
        String timestamp = StringUtil.getTimeStamp() + "";
        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", appId));
        signParams.add(new BasicNameValuePair("noncestr", noncestr));
        signParams.add(new BasicNameValuePair("package", packageName));
        signParams.add(new BasicNameValuePair("partnerid", mchId));
        signParams.add(new BasicNameValuePair("prepayid", prepayId));
        signParams.add(new BasicNameValuePair("timestamp", timestamp));
        String resSign = getSign(signParams, secret);  //获取签名

        dataMap.put("appId", appId);
        dataMap.put("noncestr", noncestr);
        dataMap.put("package", packageName);
        dataMap.put("partnerId", mchId);
        dataMap.put("prepayId", prepayId);
        dataMap.put("timestamp", timestamp);
        dataMap.put("sign", resSign);

        return dataMap;
    }

    /**
     * 微信申请退款
     * @param appId             微信AppId
     * @param mchId             微信商户号
     * @param secret            商户密钥
     * @param outRefundNo       商户退款单号
     * @param transactionId     微信支付单号
     * @param refundFee         退款金额（单位：分）
     * @param totalFee          订单总金额
     * @param refundCertPath    微信退款证书路径
     * @return:                 参考微信API接口地址：https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_4&index=6
     *
     */
    public static String refund(String appId, String mchId,String secret, String outRefundNo,
                           String transactionId, String refundFee, String totalFee, String refundCertPath) throws Exception {

        String url = "https://api.mch.weixin.qq.com/secapi/pay/refund";
        List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
        packageParams.add(new BasicNameValuePair("appid", appId));
        packageParams.add(new BasicNameValuePair("mch_id", mchId));
        packageParams.add(new BasicNameValuePair("nonce_str", RandomUtil.getNonceStr()));
        packageParams.add(new BasicNameValuePair("op_user_id", mchId));
        packageParams.add(new BasicNameValuePair("out_refund_no", outRefundNo)); //商户退款单号
        packageParams.add(new BasicNameValuePair("refund_fee", refundFee)); //退款金额
        packageParams.add(new BasicNameValuePair("total_fee", totalFee)); //订单总金额
        packageParams.add(new BasicNameValuePair("transaction_id", transactionId)); //微信支付单号

        String sign = getSign(packageParams, secret);  //获取签名
        packageParams.add(new BasicNameValuePair("sign", sign));

        String reqXml = toXml(packageParams);
        String result=null;
        return HttpUtil.payHttps(url, reqXml, mchId, refundCertPath);
    }


    /**
     * 下载对账单
     * @param appId     微信AppId
     * @param mchId     微信商户号
     * @param secret    商户密钥
     * @param billDate  对账单日期   格式：20140603
     * @param billType  账单类型    ALL，返回当日所有订单信息，默认值; SUCCESS，返回当日成功支付的订单;REFUND，
     *                  返回当日退款订单;RECHARGE_REFUND，返回当日充值退款订单（相比其他对账单多一栏“返还手续费”）;
     * @return:         参考微信API接口地址：https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_6&index=8
     *
     */
    public static String downloadBill(String appId, String mchId,String secret, String billDate,
                               String billType){
        String url = "https://api.mch.weixin.qq.com/pay/downloadbill";
        StringBuffer xml = new StringBuffer();
        xml.append("</xml>");
        List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
        packageParams.add(new BasicNameValuePair("appid", appId));
        packageParams.add(new BasicNameValuePair("bill_date", billDate)); //订单总金额
        packageParams.add(new BasicNameValuePair("bill_type", billType)); //微信支付单号
        packageParams.add(new BasicNameValuePair("mch_id", mchId));
        packageParams.add(new BasicNameValuePair("nonce_str", RandomUtil.getNonceStr()));

        String sign = getSign(packageParams, secret);  //获取签名
        packageParams.add(new BasicNameValuePair("sign", sign));

        String reqXml = toXml(packageParams);
        String result = HttpUtil.httpPost(url, reqXml);

        return result;
    }

    /*转成xml*/
    private static String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<"+params.get(i).getName()+">");
            sb.append(params.get(i).getValue());
            sb.append("</"+params.get(i).getName()+">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 获取微信支付签名
     * @param params 微信预支付下单请求参数
     * @param secret  商户密钥
     * @return
     */
    public static String getSign(List<NameValuePair> params,String secret) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(secret);
        String sign = MD5Util.string2MD5(sb.toString()).toUpperCase();

        return sign;
    }

    /**
     * 异步回调通知结果验签
     * @param dataMap  异步回调通知结果取<xml></xml>节点下的数据
     * @param secret  密钥
     * @return
     */
    public static boolean checkSign(Map<String,String> dataMap,String secret){

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", dataMap.get("appid")));
        signParams.add(new BasicNameValuePair("bank_type", dataMap.get("bank_type")));
        signParams.add(new BasicNameValuePair("cash_fee", dataMap.get("cash_fee")));
        signParams.add(new BasicNameValuePair("fee_type", dataMap.get("fee_type")));
        signParams.add(new BasicNameValuePair("is_subscribe", dataMap.get("is_subscribe")));
        signParams.add(new BasicNameValuePair("mch_id", dataMap.get("mch_id")));
        signParams.add(new BasicNameValuePair("nonce_str", dataMap.get("nonce_str")));
        signParams.add(new BasicNameValuePair("openid", dataMap.get("openid")));
        signParams.add(new BasicNameValuePair("out_trade_no", dataMap.get("out_trade_no")));
        signParams.add(new BasicNameValuePair("result_code", dataMap.get("result_code")));
        signParams.add(new BasicNameValuePair("return_code", dataMap.get("return_code")));
        signParams.add(new BasicNameValuePair("time_end", dataMap.get("time_end")));
        signParams.add(new BasicNameValuePair("total_fee", dataMap.get("total_fee")));
        signParams.add(new BasicNameValuePair("trade_type", dataMap.get("trade_type")));
        signParams.add(new BasicNameValuePair("transaction_id", dataMap.get("transaction_id")));

        String sign=dataMap.get("sign"); //签名
        String resSign = getSign(signParams, secret);

        return resSign.equals(sign);
    }
}
