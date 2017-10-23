package clb.core.wecorp.service.impl;


import clb.core.wecorp.dto.HmsWxResponseDto;
import clb.core.wecorp.dto.WecorpAccount;
import clb.core.wecorp.mapper.WecorpAccountMapper;
import clb.core.wecorp.service.IWxTokenService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class WxTokenServiceImpl implements IWxTokenService {
    @Autowired
    private WecorpAccountMapper woaAccountMapper;

    public WxTokenServiceImpl() {
    }

    public String connectForToken(String appid) {
        String token = null;

        JSONObject jo = this.postForToken(appid);
        if(jo.containsKey("access_token")) {
            token = jo.getString("access_token");


            return token;
        } else {
            return null;
        }
    }


    private JSONObject postForToken(String appid) {
        WecorpAccount account = woaAccountMapper.getWoaAccountByAppId(appid);
        String id = account.getAppid();
        String secret = account.getAppSecret();
        Object token = null;
        String postUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET".replace("APPID", id).replace("APPSECRET", secret);
        String retMsg = "";
        JSONObject obj = null;
        System.out.println("https_url=" + postUrl);
        HttpURLConnection conn = null;
        InputStream sendStatus = null;

        try {
            URL e = new URL(postUrl);
            conn = (HttpURLConnection)e.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            sendStatus = conn.getInputStream();
            int size = sendStatus.available();
            byte[] jsonBytes = new byte[size];
            sendStatus.read(jsonBytes);
            retMsg = new String(jsonBytes, "UTF-8");
            sendStatus.close();
            obj = JSONObject.fromObject(retMsg);
        } catch (Exception var21) {
            var21.printStackTrace();
        } finally {
            try {
                sendStatus.close();
            } catch (IOException var20) {
                var20.printStackTrace();
            }

            conn.disconnect();
        }

        return obj;
    }

    public String post(String body, String https_url) {
        HmsWxResponseDto wxResponseDto = new HmsWxResponseDto();
        OutputStream os = null;
        InputStream sendStatus = null;
        HttpURLConnection conn = null;

        Object size;
        try {
            URL e = new URL(https_url);
            conn = (HttpURLConnection)e.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            os = conn.getOutputStream();
            os.write(body.getBytes("UTF-8"));
            os.flush();
            sendStatus = conn.getInputStream();
            int size1 = sendStatus.available();
            byte[] e1 = new byte[size1];
            sendStatus.read(e1);
            String retMsg = new String(e1, "UTF-8");
            JSONObject obj = JSONObject.fromObject(retMsg);
            wxResponseDto.setErrcode(obj.getString("errcode"));
            wxResponseDto.setErrmsg(obj.getString("errmsg"));
            return retMsg;
        } catch (Exception var20) {
            var20.printStackTrace();
            size = null;
        } finally {
            try {
                sendStatus.close();
                os.close();
                conn.disconnect();
            } catch (IOException var19) {
                var19.printStackTrace();
            }

        }

        return (String)size;
    }

    public String get(String https_url) {
        String inMsg = null;

        try {
            URL url = new URL(https_url);
            HttpsURLConnection e = (HttpsURLConnection)url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(e.getInputStream(), "utf-8"));
            StringBuffer sb = new StringBuffer();
            new String("");

            String oneLine;
            while((oneLine = br.readLine()) != null) {
                sb.append(oneLine);
            }

            inMsg = new String(sb.toString());
            e.disconnect();
            br.close();
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return inMsg;
    }
}
