package clb.core.whtcustom.service.impl;


import clb.core.wecorp.dto.WecorpAccessToken;
import clb.core.wecorp.dto.WecorpAccount;
import clb.core.wecorp.mapper.WecorpAccessTokenMapper;
import clb.core.wecorp.mapper.WecorpAccountMapper;
import clb.core.whtcustom.dto.WhtOfficialAccountCfg;
import clb.core.whtcustom.mapper.WhtOfficialAccountCfgMapper;
import clb.core.whtcustom.service.IWxService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WxServiceImpl implements IWxService {
    @Autowired
    private WecorpAccountMapper woaAccountMapper;
    @Autowired
    private WecorpAccessTokenMapper wecorpAccessTokenMapper;


    @Autowired
    private WhtOfficialAccountCfgMapper whtOfficialAccountCfgMapper;

    public WxServiceImpl() {
    }

    public String connectForToken(String appid) {
        String token = null;

        WecorpAccessToken accessToken = wecorpAccessTokenMapper.findByAppId(appid);
        if (accessToken!=null) {
            Long nowTime = new Date().getTime();
            if (nowTime-accessToken.getLastUpdateDate().getTime() < 7000000) {
                token = accessToken.getTokenValue();
                return token;
            }
        }

        token = this.postForToken(appid);
        if(accessToken!=null) {
            accessToken.setTokenValue(token);
            wecorpAccessTokenMapper.updateByPrimaryKeySelective(accessToken);
        }else {
            WecorpAccessToken newToken = new WecorpAccessToken();
            newToken.setAppId(appid);
            newToken.setTokenValue(token);
            wecorpAccessTokenMapper.insertSelective(newToken);
        }
        return token;

    }


    private String postForToken(String appid) {
        WhtOfficialAccountCfg whtOfficialAccountCfg = new WhtOfficialAccountCfg();
        whtOfficialAccountCfg.setBackgroundAppid(appid);
        whtOfficialAccountCfg = whtOfficialAccountCfgMapper.selectAppId(whtOfficialAccountCfg);
        String id = whtOfficialAccountCfg.getBackgroundAppid();
        String secret = whtOfficialAccountCfg.getBackgroundAppSecret();
        String token = null;
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

        if(obj.containsKey("access_token")) {
            token = obj.getString("access_token");

            return token;
        } else {
            return null;
        }
    }

    /*public String post(String body, String https_url) {
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
    }*/

    ////用于转换地址中的中文，否则如果系统编码不为UTF-8，将会无法解析网址
    @Override
    public String encode(String str) throws UnsupportedEncodingException {
        String zhPattern = "[\u4e00-\u9fa5]";
        Pattern p = Pattern.compile(zhPattern);
        Matcher m = p.matcher(str);
        StringBuffer b = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(b, URLEncoder.encode(m.group(0), "UTF-8"));
        }
        m.appendTail(b);
        return b.toString();
    }

    //新增永久素材，文件类型。
    @Override
    public String uploadAlwaysFile(String filename,String contentType,InputStream file,String appId){
        String uploadMediaUrl = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token="+this.connectForToken(appId);
        return this.sendFile(uploadMediaUrl, filename, contentType, file);
    }

    //发送文件
    public String sendFile(String uploadMediaUrl,String fileName,String contentType,InputStream file){
        String boundary = "------------7da2e536604c8";
        try {
            URL uploadUrl = new URL(this.encode(uploadMediaUrl));
            HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl.openConnection();
            uploadConn.setDoOutput(true);
            uploadConn.setDoInput(true);
            uploadConn.setRequestMethod("POST");
            // 设置请求头Content-Type
            uploadConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // 获取媒体文件上传的输出流（往微信服务器写数据）
            OutputStream outputStream = uploadConn.getOutputStream();

            // 请求体开始
            outputStream.write(("--" + boundary + "\r\n").getBytes());
            outputStream.write(String.format("Content-Disposition: form-data; name=\"media\"; filename=\"%s\"\r\n",fileName).getBytes());
            outputStream.write(String.format("Content-Type:%s\r\n\r\n",contentType).getBytes());
            // 获取媒体文件的输入流（读取文件）
            //outputStream.write(filebyte.getBytes());
            byte[] f = new byte[1024];
            int len  = 0;
            while((len=file.read(f))!=-1){
                outputStream.write(f,0,len);
            }
            // 请求体结束
            outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());

            outputStream.close();
            InputStream inputStream = uploadConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer buffer = new StringBuffer();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            uploadConn.disconnect();
            return buffer.toString();

        } catch (Exception e) {
            return e.toString();
        }
    }

    @Override
    public String getAppId(String accountNum) {
        WecorpAccount account = woaAccountMapper.getWoaAccountByAccountNum(accountNum);
        return account.getAppid();
    }

    @Override
    public JSONObject getJson(String url) {
        try {
            URL uploadUrl = new URL(url);
            HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl.openConnection();
            uploadConn.setDoOutput(true);
            uploadConn.setDoInput(true);
            uploadConn.setRequestMethod("GET");
            InputStream inputStream = uploadConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer buffer = new StringBuffer();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            uploadConn.disconnect();
            JSONObject jsonObject =JSONObject.fromObject(buffer.toString());
            return jsonObject;
        } catch (Exception e) {
            return null;
        }
    }

    //发送消息部分y由于都是json数据的发送，可以直接调用sendJson()方法即可。
    //向URL发送json数据，POST方式
    public String sendJson(String url,JSONObject json){

        try {
//            String json_str = json.toString()
//            println json_str
//            println json_str.getBytes()
            URL uploadUrl = new URL(url);
            HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl.openConnection();
            uploadConn.setDoOutput(true);
            uploadConn.setDoInput(true);
            uploadConn.setRequestMethod("POST");
            uploadConn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            OutputStream outputStream = uploadConn.getOutputStream();
            byte[] b =json.toString().getBytes("utf-8");
            outputStream.write(b);
            outputStream.close();
            InputStream inputStream = uploadConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer buffer = new StringBuffer();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            uploadConn.disconnect();
            return buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }

    }

    //获得用户列表，next_openid不填表示从头获取，填表示从该openid往后获取
    public JSONObject getUserList(String appId,String nextOpenId){
        return this.getJson("https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + this.connectForToken(appId) + "&next_openid=" + nextOpenId);
    }
    public JSONObject getUserListAll(String appId){
        return this.getJson("https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + this.connectForToken(appId));
    }

    //可以在图文消息上传图片、微信卡券中上传图片使用
    public String uploadNewsImg(String appId,String filename,String contentType,Object file){
        String uploadMediaUrl = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token="+this.connectForToken(appId);
        if(file instanceof InputStream){
            return this.sendFile(uploadMediaUrl,filename,contentType,(InputStream)file);
        }else if(file instanceof String){//下载链接
            URL url = null;
            try {
                url = new URL(this.encode((String) file));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Accept-Charset", "utf-8");
                conn.setRequestProperty("contentType", "utf-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                conn.setUseCaches(false);
                conn.setInstanceFollowRedirects(true);
                InputStream inStream = conn.getInputStream();
                return this.sendFile(uploadMediaUrl,filename,"image",inStream);
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        }
        return null;
    }

    //新增永久图文素材,thumb_media_id为永久media_id，由uploadAlwaysImg()获得。content中的图片可以由uploadNewsImg()获得url
    public String addNews(String appId,JSONObject body){
        return this.sendJson("https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=" + this.connectForToken(appId), body);
    }

    //获取永久素材：除视频素材和图文素材外。
    public InputStream getAlwaysImg(String frMediaId,String appId){
        return this.sendJsonDownFile("https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=" + this.connectForToken(appId), JSONObject.fromObject("{\"media_id\":\"" + frMediaId + "\"}"));
    }
    //获取永久素材:视频素材和图文素材。
    public String getAlwaysMaterial(String frMediaId,String appId){
        return this.sendJson("https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=" + this.connectForToken(appId), JSONObject.fromObject("{\"media_id\":\"" + frMediaId + "\"}"));
    }

    public InputStream sendJsonDownFile(String url,JSONObject json){
        try {
            URL uploadUrl = new URL(url);
            HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl.openConnection();
            uploadConn.setDoOutput(true);
            uploadConn.setDoInput(true);
            uploadConn.setRequestMethod("POST");
            OutputStream outputStream = uploadConn.getOutputStream();
            byte[] b =json.toString().getBytes();
            outputStream.write(b);
            outputStream.close();
            InputStream inputStream = uploadConn.getInputStream();
            return inputStream;
        } catch (Exception e) {
//            System.out.println(e.toString());
        }
        return null;

    }
}
