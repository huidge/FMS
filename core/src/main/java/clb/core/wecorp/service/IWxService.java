package clb.core.wecorp.service;

import clb.core.wecorp.dto.MessageTemplateDTO;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.intergration.exception.HapApiException;
import com.hand.hap.system.dto.ResponseData;
import net.sf.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public interface IWxService extends ProxySelf<IWxService> {
    String connectForToken(String var1) throws Exception;

    JSONObject connectForTokenNew(String appid) throws Exception;

    JSONObject firstConnectForTokenNew(String appid,String secret) throws Exception;

    String encode(String str) throws UnsupportedEncodingException;

    JSONObject uploadAlwaysFile(String filename, String contentType, InputStream file, String appId) throws Exception;

    String getAppId(String accountNum) throws Exception;

    JSONObject getJson(String url);

    String sendJson(String url, JSONObject json);

    JSONObject getUserList(String appId, String nextOpenId) throws Exception;

    JSONObject getUserListAll(String appId) throws Exception;

    String uploadNewsImg(String appId, String filename, String contentType, Object file) throws Exception;

    String addNews(String appId, JSONObject body) throws Exception;

    InputStream getAlwaysImg(String mediaId, String appId) throws Exception;

    String getAlwaysMaterial(String frMediaId, String appId) throws Exception;

    ResponseData addCustom(String appId, String kfAccount, String nickName, String password) throws Exception;

    ResponseData invitekf(String appId, String kfAccount, String wxName) throws Exception;

    void createCommunite(String appId, String openid) throws Exception;

    void closeCommunite(String appId, String openid) throws Exception;

    ResponseData checkCommunite(String appId, String openid) throws Exception;

    ResponseData updateCustom(String appId, String kfAccount, String nickName, String password) throws Exception;

    ResponseData delCustom(String appId, String kfAccount) throws Exception;

    ResponseData uploadheadimg(String appId, String kfAccount,String filename,Object file) throws Exception;

    ResponseData getkflist(String appId ) throws Exception;

    ResponseData getkfCommunicateList(String appId, String kfAccount) throws Exception;

    ResponseData getOnlinekflist(String appId ) throws Exception;

    ResponseData waitingList(String appId) throws Exception;

    ResponseData getMsg(String appId, Long starttime, Long endtime, Long msgid, Long number) throws Exception;

    void createSuccess(String kf, String openid,String appId) throws Exception;

    void endSession(String kf, String openid,String appId) throws Exception;

    Boolean ifCloseKF(String appId,String openid,String content) throws Exception;

    void transfer(String fromkf,String tokf,String openid,String appId) throws Exception;

    JSONObject sendTemplate(MessageTemplateDTO messageTemplateDTO, String appId) throws Exception;

    JSONObject getTicket(String appId) throws Exception;

    JSONObject getJsSdkInfo(String url,String appId) throws Exception;

    String upload(String image) throws Exception;

    public InputStream getImage(String fileName) throws Exception;

    String exchange(String image, String fileName, String url) throws Exception;

    public JSONObject order(IRequest iRequest,String appId, String mchId, String secret, String body,String outTradeNo, int totalFee);


    JSONObject refund(IRequest iRequest,String appId, String mchId, String secret, String transactionId, String refundFee, String totalFee);
}
