package clb.core.whtcustom.service;

import com.hand.hap.core.ProxySelf;
import net.sf.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public interface IWxService extends ProxySelf<IWxService> {
    String connectForToken(String var1);

//    String post(String var1, String var2);

//    String get(String var1);

    String encode(String str) throws UnsupportedEncodingException;

    String uploadAlwaysFile(String filename, String contentType, InputStream file, String appId);

    String getAppId(String accountNum);

    JSONObject getJson(String url);

    String sendJson(String url, JSONObject json);

    JSONObject getUserList(String appId, String nextOpenId);

    JSONObject getUserListAll(String appId);

    String uploadNewsImg(String appId, String filename, String contentType, Object file);

    String addNews(String appId, JSONObject body);

    InputStream getAlwaysImg(String frMediaId, String appId);

    String getAlwaysMaterial(String frMediaId, String appId);
}