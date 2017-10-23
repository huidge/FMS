package clb.core.wecorp.service.impl;


import clb.core.wecorp.dto.*;
import clb.core.wecorp.mapper.WecorpAccessTokenMapper;
import clb.core.wecorp.mapper.WecorpAccountMapper;
import clb.core.wecorp.mapper.WecorpAccountTicketMapper;
import clb.core.wecorp.mapper.WecorpKFMapper;
import clb.core.wecorp.service.IWecorpCallBackService;
import clb.core.wecorp.service.IWxService;
import clb.core.wecorp.utils.HttpUploadFile;
import clb.core.wecorp.utils.Sign;
import clb.core.wecorp.utils.WxPay;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import com.hand.hap.attachment.service.IAttachCategoryService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.intergration.util.JSONAndMap;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IProfileService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WoaServiceImpl implements IWxService {
    @Autowired
    private WecorpAccountMapper woaAccountMapper;
    @Autowired
    private WecorpAccessTokenMapper wecorpAccessTokenMapper;

    @Autowired
    private WecorpKFMapper wecorpKFMapper;

    @Autowired
    private IWecorpCallBackService wecorpCallBackService;

    @Autowired
    private WecorpAccountTicketMapper wecorpAccountTicketMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("profileServiceImpl")
    private IProfileService profileService;


    public WoaServiceImpl() {
    }

    private synchronized void firstTokenInsert(WecorpAccessToken wecorpAccessToken) throws Exception {
        WecorpAccessToken accessToken = wecorpAccessTokenMapper.findByAppId(wecorpAccessToken.getAppId());
        if(accessToken==null) {
            wecorpAccessTokenMapper.insertSelective(wecorpAccessToken);
        }else{
            accessToken.setTokenValue(wecorpAccessToken.getTokenValue());
            wecorpAccessTokenMapper.updateByPrimaryKeySelective(accessToken);
        }

    }

    @Override
    public String connectForToken(String appid) throws Exception {
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
            firstTokenInsert(newToken);
        }
        return token;
    }

    @Override
    public JSONObject connectForTokenNew(String appid) throws Exception {
        JSONObject token = new JSONObject();
        token.put("success", true);
        WecorpAccessToken accessToken = wecorpAccessTokenMapper.findByAppId(appid);
        if (accessToken!=null) {
            Long nowTime = new Date().getTime();
            if (nowTime-accessToken.getLastUpdateDate().getTime() < 7000000) {
                token.put("access_token",accessToken.getTokenValue());
                return token;
            }
        }
        token = this.postForTokenNew(appid);
        token.put("success", true);
        logger.info("token:{}",token.toString());
        if(token.containsKey("access_token")) {
            if (accessToken != null) {
                accessToken.setTokenValue(token.getString("access_token"));
                wecorpAccessTokenMapper.updateByPrimaryKeySelective(accessToken);
            } else {
                WecorpAccessToken newToken = new WecorpAccessToken();
                newToken.setAppId(appid);
                newToken.setTokenValue(token.getString("access_token"));
                firstTokenInsert(newToken);
            }
        }else{
            token.put("success", false);
        }
        return token;

    }

    @Override
    public JSONObject firstConnectForTokenNew(String appid,String secret) throws Exception {
        JSONObject token = this.postForTokenNew2(appid, secret);
        logger.info("token:{}", token.toString());
        WecorpAccessToken accessToken = wecorpAccessTokenMapper.findByAppId(appid);
        if(token.containsKey("access_token")) {
            token.put("success", true);
            if (accessToken != null) {
                accessToken.setTokenValue(token.getString("access_token"));
                wecorpAccessTokenMapper.updateByPrimaryKeySelective(accessToken);
            } else {
                WecorpAccessToken newToken = new WecorpAccessToken();
                newToken.setAppId(appid);
                newToken.setTokenValue(token.getString("access_token"));
                firstTokenInsert(newToken);
            }
        }else{
            token.put("success", false);
        }
        return token;

    }

    private JSONObject postForTokenNew2(String appid,String secret) throws Exception {
        String postUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET".replace("APPID", appid).replace("APPSECRET", secret);
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
            return JSONObject.fromObject("{\"errcode\":-100,\"errmsg\":\""+var21.getMessage()+"\"}");
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

    private JSONObject postForTokenNew(String appid) throws Exception {
        WecorpAccount account = woaAccountMapper.getWoaAccountByAppId(appid);
        String id = account.getAppid();
        String secret = account.getAppSecret();
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
            return JSONObject.fromObject("{\"errcode\":-100,\"errmsg\":\""+var21.getMessage()+"\"}");
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

    private String postForToken(String appid) throws Exception {
        WecorpAccount account = woaAccountMapper.getWoaAccountByAppId(appid);
        String id = account.getAppid();
        String secret = account.getAppSecret();
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
            throw new Exception(retMsg);
        }
    }

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
    public JSONObject uploadAlwaysFile(String filename, String contentType, InputStream file, String appId) throws Exception {
        String uploadMediaUrl = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token="+this.connectForToken(appId);
        return JSONObject.fromObject(this.sendFile(uploadMediaUrl, filename, contentType, file));
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
            return "{\"errcode\":-100,\"errmsg\":\"unknow\"}";
        }
    }

    @Override
    public String getAppId(String accountNum) throws Exception {
        WecorpAccount account = woaAccountMapper.getWoaAccountByAccountNum(accountNum);
        if(account==null){
            throw new Exception("该appid对应的记录不存在，请先进行配置");
        }
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
            return JSONObject.fromObject("{\"errcode\":-100,\"errmsg\":\"unknow\"}");
        }
    }

    //发送消息部分y由于都是json数据的发送，可以直接调用sendJson()方法即可。
    //向URL发送json数据，POST方式
    public String sendJson(String url,JSONObject json){
        try {
            logger.info("*******body:{}",json.toString());
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
            return "{\"errcode\":-100,\"errmsg\":\"unknow\"}";
        }

    }

    //获得用户列表，next_openid不填表示从头获取，填表示从该openid往后获取
    public JSONObject getUserList(String appId,String nextOpenId) throws Exception {
        return this.getJson("https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + this.connectForToken(appId) + "&next_openid=" + nextOpenId);
    }
    public JSONObject getUserListAll(String appId) throws Exception {
        return this.getJson("https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + this.connectForToken(appId));
    }

    //可以在图文消息上传图片、微信卡券中上传图片使用
    public String uploadNewsImg(String appId,String filename,String contentType,Object file) throws Exception {
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
    public String addNews(String appId,JSONObject body) throws Exception {
        return this.sendJson("https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=" + this.connectForToken(appId), body);
    }

    //获取永久素材：除视频素材和图文素材外。
    public InputStream getAlwaysImg(String mediaId,String appId) throws Exception {
        return this.sendJsonDownFile("https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=" + this.connectForToken(appId), JSONObject.fromObject("{\"media_id\":\"" + mediaId + "\"}"));
    }
    //获取永久素材:视频素材和图文素材。
    public String getAlwaysMaterial(String frMediaId,String appId) throws Exception {
        return this.sendJson("https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=" + this.connectForToken(appId), JSONObject.fromObject("{\"media_id\":\"" + frMediaId + "\"}"));
    }

    /**
     * 添加客服.
     * @param appId
     * @param kfAccount
     * @param nickName
     * @param password
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData addCustom(String appId, String kfAccount, String nickName, String password) throws Exception {
        return doCustom(appId, kfAccount, nickName, password, "https://api.weixin.qq.com/customservice/kfaccount/add?access_token=");
    }

    /**
     * 客服邀请.
     * @param appId
     * @param kfAccount
     * @param wxName
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData invitekf(String appId, String kfAccount, String wxName) throws Exception {
        JSONObject result=JSONObject.fromObject(this.sendJson("https://api.weixin.qq.com/customservice/kfaccount/add?access_token=" + this.connectForToken(appId), JSONObject.fromObject("{\n" +
                "     \"kf_account\" : \"" + kfAccount + "\",\n" +
                "     \"invite_wx\" : \"" + wxName + "\",\n" +
                "}")));
        ResponseData res=new ResponseData();
        if(result.getString("errcode").equals("0")){
            res.setSuccess(true);
            res.setMessage("操作成功");
            return res;
        }else{
            res.setSuccess(false);
            res.setMessage("错误代码："+result.getString("errcode")+" 原因："+result.getString("errmsg"));
            return res;
        }
    }

    /**
     * 创建会话.
     * @param appId
     * @param openid
     * @return
     * @throws Exception
     */
    @Override
    public void createCommunite(String appId, String openid) throws Exception {
        //查数据库是否已经建立连接
        List<WecorpKF> wecorpKFs=wecorpKFMapper.findActiveOpenId(openid,appId);
        if(wecorpKFs.size()>0){
            logger.info("已经建立了连接");
            WecorpResponseDTO dto=new WecorpResponseDTO();
            dto.setContent("已经建立了连接,请耐心等待");
            dto.setType("text");
            wecorpCallBackService.sendMessageByType(openid, appId, dto);
        }else{
            ResponseData res=getOnlinekflist(appId);
            if(res.isSuccess()) {
                List<JSONObject> list = (List<JSONObject>) res.getRows();
                if (list.size() == 0) {
                    //给个提示
                    WecorpResponseDTO dto = new WecorpResponseDTO();
                    dto.setContent("抱歉，当前客服都在休息中............");
                    dto.setType("text");
                    wecorpCallBackService.sendMessageByType(openid, appId, dto);
                } else {
                    JSONObject temp = list.get(0);
                    for (int i = 1; i < list.size(); i++) {
                        if (Integer.parseInt(list.get(i).getString("accepted_case")) < Integer.parseInt(temp.getString("accepted_case"))) {
                            temp = list.get(i);
                        }
                    }
                    String count=temp.getString("accepted_case");
                    String kfAccount = temp.getString("kf_account");
                    JSONObject result = JSONObject.fromObject(this.sendJson("https://api.weixin.qq.com/customservice/kfsession/create?access_token=" + this.connectForToken(appId), JSONObject.fromObject("{\n" +
                            "     \"kf_account\" : \"" + kfAccount + "\",\n" +
                            "     \"openid\" : \"" + openid + "\",\n" +
                            "}")));
                    logger.info(result.toString());
                    if (result.getString("errcode").equals("0")) {
                        WecorpKF wecorpKF = new WecorpKF();
                        wecorpKF.setId(UUID.randomUUID().toString());
                        wecorpKF.setKfAccount(kfAccount);
                        wecorpKF.setOpenId(openid);
                        wecorpKF.setStatus("pending");
                        wecorpKF.setAppId(appId);
                        wecorpKFMapper.insertSelective(wecorpKF);
                        WecorpResponseDTO dto=new WecorpResponseDTO();
                        dto.setContent("欢迎使用在线客服功能,您正在排队中（第"+count+"位）,请稍候。输入“退出”或“00”，可退出在线客服");
                        dto.setType("text");
                        wecorpCallBackService.sendMessageByType(openid, appId, dto);
                    }else{
                        WecorpResponseDTO dto=new WecorpResponseDTO();
                        dto.setContent(result.toString());
                        dto.setType("text");
                        wecorpCallBackService.sendMessageByType(openid, appId, dto);                    }
                }
            }else{
                WecorpResponseDTO dto=new WecorpResponseDTO();
                dto.setContent(res.getMessage());
                dto.setType("text");
                wecorpCallBackService.sendMessageByType(openid, appId, dto);
            }
        }
    }

    /**
     * 关闭会话.
     * @param appId
     * @param openid
     * @return
     * @throws Exception
     */
    @Override
    public void closeCommunite(String appId, String openid) throws Exception {
        JSONObject status= (JSONObject) checkCommunite(appId,openid).getRows().get(0);
        if(status.containsKey("kf_account")&&!"".equals(status.getString("kf_account"))){
            JSONObject result=JSONObject.fromObject(this.sendJson("https://api.weixin.qq.com/customservice/kfsession/close?access_token=" + this.connectForToken(appId), JSONObject.fromObject("{\n" +
                    "     \"kf_account\" : \"" + status.getString("kf_account") + "\",\n" +
                    "     \"openid\" : \"" + openid + "\",\n" +
                    "}")));
            if(result.getString("errcode").equals("0")){
                this.endSession(status.getString("kf_account"), openid, appId);
            }else{
                WecorpResponseDTO dto=new WecorpResponseDTO();
                dto.setContent("错误代码："+result.getString("errcode")+" 原因："+result.getString("errmsg"));
                dto.setType("text");
                wecorpCallBackService.sendMessageByType(openid, appId, dto);
            }
        }else{
            wecorpKFMapper.end(openid,appId,status.getString("kf_account"));
            WecorpResponseDTO dto=new WecorpResponseDTO();
            dto.setContent("您当前不在人工服务中");
            dto.setType("text");
            wecorpCallBackService.sendMessageByType(openid, appId, dto);
        }

    }

    /**
     * 查看当前用户会话.
     * @param appId
     * @param openid
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData checkCommunite(String appId , String openid) throws Exception {
        JSONObject result=this.getJson("https://api.weixin.qq.com/customservice/kfsession/getsession?access_token=" + this.connectForToken(appId) + "&openid=" + openid);
        ResponseData res=new ResponseData();
        res.setSuccess(true);
        res.setMessage("操作成功");
        res.setRows(Collections.singletonList(result));
        return res;

    }

    /**
     * 修改客服
     * @param appId
     * @param kfAccount
     * @param nickName
     * @param password
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData updateCustom(String appId, String kfAccount, String nickName, String password) throws Exception {
        return doCustom(appId,kfAccount,nickName,password,"https://api.weixin.qq.com/customservice/kfaccount/update?access_token=");
    }

    /**
     * 删除客服
     * @param appId
     * @param kfAccount
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData delCustom(String appId, String kfAccount) throws Exception {
        String url="https://api.weixin.qq.com/customservice/kfaccount/del?access_token=" + this.connectForToken(appId)+"&kf_account="+kfAccount;
        JSONObject result=getJson(url);
        ResponseData res=new ResponseData();
        if(result.getString("errcode").equals("0")){
            res.setSuccess(true);
            res.setMessage("操作成功");
            return res;
        }else{
            res.setSuccess(false);
            res.setMessage("错误代码："+result.getString("errcode")+" 原因："+result.getString("errmsg"));
            return res;
        }
    }

    /**
     * 客服通用方法.
     * @param appId
     * @param kfAccount
     * @param nickName
     * @param password
     * @return
     * @throws Exception
     */
    public ResponseData doCustom(String appId, String kfAccount, String nickName, String password,String url) throws Exception {
        JSONObject result=JSONObject.fromObject(this.sendJson(url + this.connectForToken(appId), JSONObject.fromObject("{\n" +
                "     \"kf_account\" : \"" + kfAccount + "\",\n" +
                "     \"nickname\" : \"" + nickName + "\",\n" +
                "     \"password\" : \"" + password + "\",\n" +
                "}")));
        ResponseData res=new ResponseData();
        if(result.getString("errcode").equals("0")){
            res.setSuccess(true);
            res.setMessage("操作成功");
            return res;
        }else{
            res.setSuccess(false);
            res.setMessage("错误代码："+result.getString("errcode")+" 原因："+result.getString("errmsg"));
            return res;
        }
    }

    /**
     * 设置客服帐号的头像.
     * @param appId
     * @param kfAccount
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData uploadheadimg(String appId, String kfAccount,String filename,Object file) throws Exception {
        String uploadMediaUrl = "https://api.weixin.qq.com/customservice/kfaccount/uploadheadimg?kf_account="+kfAccount+"&access_token=" + this.connectForToken(appId);
        JSONObject result=null;
        if(file instanceof MultipartFile){
            result=JSONObject.fromObject(this.sendFile(uploadMediaUrl, filename, "image", ((MultipartFile) file).getInputStream()));
        }else if(file instanceof String){
            try {
                URL url = new URL(this.encode((String) file));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Accept-Charset", "utf-8");
                conn.setRequestProperty("contentType", "utf-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("GET");
                conn.setUseCaches(false);
                conn.setInstanceFollowRedirects(true);
                InputStream inStream = conn.getInputStream();
                result= JSONObject.fromObject(this.sendFile(uploadMediaUrl, filename, "image", inStream));
            } catch (Exception e1) {
                return new ResponseData(false);
            }
        }
        if(result==null){
            return new ResponseData(false);
        }else {
            ResponseData res = new ResponseData();
            if (result.getString("errcode").equals("0")) {
                res.setSuccess(true);
                res.setMessage("操作成功");
                return res;
            } else {
                res.setSuccess(false);
                res.setMessage("错误代码：" + result.getString("errcode") + " 原因：" + result.getString("errmsg"));
                return res;
            }
        }
    }

    /**
     * 获取客服列表.
     * @param appId
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData getkflist(String appId) throws Exception {
        JSONObject result=this.getJson("https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=" + this.connectForToken(appId));
        ResponseData res=new ResponseData();
        if(!result.containsKey("errcode")){
            res.setSuccess(true);
            res.setRows(result.getJSONArray("kf_list"));
            res.setMessage("操作成功");
            return res;
        }else{
            res.setSuccess(false);
            res.setMessage("错误代码："+result.getString("errcode")+" 原因："+result.getString("errmsg"));
            return res;
        }
    }

    /**
     * 获取客服会话列表.
     * @param appId
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData getkfCommunicateList(String appId,String kfAccount) throws Exception {
        JSONObject result=this.getJson("https://api.weixin.qq.com/customservice/kfsession/getsessionlist?access_token=" + this.connectForToken(appId)+"&kf_account="+kfAccount);
        ResponseData res=new ResponseData();
        if(!result.containsKey("errcode")){
            res.setSuccess(true);
            res.setRows(result.getJSONArray("sessionlist"));
            res.setMessage("操作成功");
            return res;
        }else{
            res.setSuccess(false);
            res.setMessage("错误代码："+result.getString("errcode")+" 原因："+result.getString("errmsg"));
            return res;
        }
    }

    /**
     * 获取在线客服列表.
     * @param appId
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData getOnlinekflist(String appId) throws Exception {
        JSONObject result=this.getJson("https://api.weixin.qq.com/cgi-bin/customservice/getonlinekflist?access_token=" + this.connectForToken(appId));
        ResponseData res=new ResponseData();
        if(!result.containsKey("errcode")){
            res.setSuccess(true);
            res.setRows(result.getJSONArray("kf_online_list"));
            res.setMessage("操作成功");
            return res;
        }else{
            res.setSuccess(false);
            res.setMessage("错误代码："+result.getString("errcode")+" 原因："+result.getString("errmsg"));
            return res;
        }
    }

    /**
     * 获取未接入会话列表.
     * @param appId
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData waitingList(String appId) throws Exception {
        JSONObject result=this.getJson("https://api.weixin.qq.com/customservice/kfsession/getwaitcase?access_token=" + this.connectForToken(appId));
        ResponseData res=new ResponseData();
        if(!result.containsKey("errcode")){
            res.setSuccess(true);
            res.setRows(result.getJSONArray("waitcaselist"));
            res.setTotal(Long.parseLong(result.get("count") + ""));
            res.setMessage("操作成功");
            return res;
        }else{
            res.setSuccess(false);
            res.setMessage("错误代码："+result.getString("errcode")+" 原因："+result.getString("errmsg"));
            return res;
        }
    }

    /**
     * 获取聊天记录.
     * @param appId
     * @param starttime
     * @param endtime
     * @param msgid
     * @param number
     * @return
     * @throws Exception
     */
    @Override
    public ResponseData getMsg(String appId, Long starttime, Long endtime, Long msgid, Long number) throws Exception {
        JSONObject result=JSONObject.fromObject(this.sendJson("https://api.weixin.qq.com/customservice/msgrecord/getmsglist?access_token=" + this.connectForToken(appId), JSONObject.fromObject("{\n" +
                "     \"starttime\" : " + starttime + ",\n" +
                "     \"endtime\" : " + endtime + ",\n" +
                "     \"msgid\" : " + msgid + ",\n" +
                "     \"number\" : " + number + ",\n" +
                "}")));
        ResponseData res=new ResponseData();
        res.setSuccess(true);
        res.setMessage("操作成功");
        res.setRows(Collections.singletonList(result));
        return res;

    }

    @Override
    public void createSuccess(String kf, String openid,String appId) throws Exception {
        int i=wecorpKFMapper.active(openid, appId, kf);
        WecorpResponseDTO dto = new WecorpResponseDTO();
        dto.setContent("财联邦" + "客服为您服务，请输入您的问题");
        dto.setType("text");
        wecorpCallBackService.sendMessageByType(openid, appId, dto);
    }

    @Override
    public void endSession(String kf, String openid,String appId) throws Exception {
        int i=wecorpKFMapper.end(openid, appId, kf);
        if(i>0) {
            WecorpResponseDTO dto = new WecorpResponseDTO();
            dto.setContent("财联邦" + "客服为您服务完毕");
            dto.setType("text");
            wecorpCallBackService.sendMessageByType(openid, appId, dto);
        }
    }

    @Override
    public Boolean ifCloseKF(String appId, String openid,String content) throws Exception {
        if("退出".equals(content)||"00".equals(content)) {
            JSONObject status = (JSONObject) checkCommunite(appId, openid).getRows().get(0);
            if (status.containsKey("kf_account") && !"".equals(status.getString("kf_account"))) {
                JSONObject result = JSONObject.fromObject(this.sendJson("https://api.weixin.qq.com/customservice/kfsession/close?access_token=" + this.connectForToken(appId), JSONObject.fromObject("{\n" +
                        "     \"kf_account\" : \"" + status.getString("kf_account") + "\",\n" +
                        "     \"openid\" : \"" + openid + "\",\n" +
                        "}")));
                if (result.getString("errcode").equals("0")) {
                    this.endSession(status.getString("kf_account"), openid, appId);
                    WecorpResponseDTO dto = new WecorpResponseDTO();
                    dto.setContent("您已退出在线服务，请点击底部菜单选择您所需要咨询或办理的业务");
                    dto.setType("text");
                    wecorpCallBackService.sendMessageByType(openid, appId, dto);
                } else {
                    WecorpResponseDTO dto = new WecorpResponseDTO();
                    dto.setContent("错误代码：" + result.getString("errcode") + " 原因：" + result.getString("errmsg"));
                    dto.setType("text");
                    wecorpCallBackService.sendMessageByType(openid, appId, dto);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void transfer(String fromkf, String tokf, String openid, String appId)throws Exception {
        int i=wecorpKFMapper.transfer(fromkf, tokf, openid, appId);
        logger.info("转发状态：{}",i);
        WecorpResponseDTO dto=new WecorpResponseDTO();
        dto.setContent("转接到"+"财联邦客服"+"为您服务");
        dto.setType("text");
        wecorpCallBackService.sendMessageByType(openid, appId, dto);
    }

    @Override
    public JSONObject sendTemplate(MessageTemplateDTO messageTemplateDTO, String appId) throws Exception {
        String url="https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+this.connectForToken(appId);
        JSONObject result=JSONObject.fromObject(this.sendJson(url, JSONObject.fromObject(messageTemplateDTO)));
        return result;
    }

    private synchronized void firstTicketInsert(WecorpAccountTicket wecorpAccountTicket) throws Exception {
        WecorpAccountTicket ticket=wecorpAccountTicketMapper.findByAppIdAndType(wecorpAccountTicket.getAppId(), wecorpAccountTicket.getType());
        if(ticket==null){
            wecorpAccountTicketMapper.insertSelective(wecorpAccountTicket);
        }else{
            ticket.setTicketValue(wecorpAccountTicket.getTicketValue());
            wecorpAccountTicketMapper.updateByPrimaryKeySelective(ticket);
        }
    }

    @Override
    public JSONObject getTicket(String appId) throws Exception {
        try {
            JSONObject res=new JSONObject();
            res.put("errcode","0");
            WecorpAccountTicket wecorpAccountTicket=wecorpAccountTicketMapper.findByAppIdAndType(appId, "jsapi");
            if(wecorpAccountTicket==null){
                //无此记录
                JSONObject json = this.getJson("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + this.connectForToken(appId) + "&type=jsapi");
                if(json.containsKey("ticket")){
                    res.put("ticket", json.getString("ticket"));
                    WecorpAccountTicket newData = new WecorpAccountTicket();
                    newData.setAppId(appId);
                    newData.setTicketValue(json.getString("ticket"));
                    newData.setType("jsapi");
                    firstTicketInsert(newData);
                }else{
                    res=json;
                }
            }else{
                Long nowTime = new Date().getTime();
                if (nowTime-wecorpAccountTicket.getLastUpdateDate().getTime() < 7000000) {
                    res.put("ticket",wecorpAccountTicket.getTicketValue());
                }else{
                    //更新此记录
                    JSONObject json = this.getJson("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + this.connectForToken(appId) + "&type=jsapi");
                    if(json.containsKey("ticket")){
                        res.put("ticket", json.getString("ticket"));
                        wecorpAccountTicket.setTicketValue(json.getString("ticket"));
                        wecorpAccountTicketMapper.updateByPrimaryKeySelective(wecorpAccountTicket);
                    }else{
                        res=json;
                    }
                }
            }
            return res;
        } catch (Exception e) {
            JSONObject res=new JSONObject();
            res.put("errcode","-100");
            res.put("errmsg","中央服务器出现异常");
            return res;
        }
    }

    @Override
    public JSONObject getJsSdkInfo(String url, String appId) throws Exception {
        JSONObject ticket=getTicket(appId);
        if(ticket.containsKey("ticket")){
            return Sign.sign(ticket.getString("ticket"),url);
        }else{
            return ticket;
        }
    }

    @Autowired
    private IAttachCategoryService categoryService;

    @Override
    public String upload(String image) throws Exception {
        String[] str=image.split(",");
        String type=str[0];
        if(!(type.contains("image")&&type.contains("base64"))){
            throw new Exception("请传base64加密后的图片");
        }else{
            String strBase64=str[1];
            InputStream in=null;
            OSSClient client=null;
            try {
                in = GenerateImage(strBase64);
                String fileName = UUID.randomUUID().toString();//定义一个名字
                IRequest irequest = RequestHelper.newEmptyRequest();
                irequest.setUserId(-1L);
                irequest.setRoleId(-1L);
                String endpoint = profileService.getProfileValue(irequest, "ENDPOINT");
                String accesskeyid = profileService.getProfileValue(irequest, "ACCESSKEYID");
                String accesskeysecret = profileService.getProfileValue(irequest, "ACCESSKEYSECRET");
                String bucketnamePic = profileService.getProfileValue(irequest, "BUCKETNAME_PIC");
                String bucketnameUnPic = profileService.getProfileValue(irequest, "BUCKETNAME_UNPIC");
                ClientConfiguration config = new ClientConfiguration();
                config.setCrcCheckEnabled(false);
                client = new OSSClient(endpoint, accesskeyid, accesskeysecret, config);
                PutObjectResult result = client.putObject(bucketnamePic, fileName, in);
                return bucketnamePic + " " + fileName;
            }catch (Exception e){
                throw e;
            }finally {
                if(in!=null){
                    in.close();
                }
                if(client!=null){
                    client.shutdown();
                }
            }
        }
    }

    @Override
    public InputStream getImage(String fileName) throws Exception {
        OSSClient client=null;
        OSSObject ossObject=null;
        try {
            IRequest irequest = RequestHelper.newEmptyRequest();
            irequest.setUserId(-1L);
            irequest.setRoleId(-1L);
            String endpoint = profileService.getProfileValue(irequest, "ENDPOINT");
            String accesskeyid = profileService.getProfileValue(irequest, "ACCESSKEYID");
            String accesskeysecret = profileService.getProfileValue(irequest, "ACCESSKEYSECRET");
            String bucketnamePic = profileService.getProfileValue(irequest, "BUCKETNAME_PIC");
            String bucketnameUnPic = profileService.getProfileValue(irequest, "BUCKETNAME_UNPIC");
            ClientConfiguration config = new ClientConfiguration();
            config.setCrcCheckEnabled(false);
            client = new OSSClient(endpoint, accesskeyid, accesskeysecret, config);
            ossObject = client.getObject(bucketnamePic, fileName);
            return ossObject.getObjectContent();
        }catch(Exception e){
            throw e;
        }finally {
            if(client!=null){
                client.shutdown();
            }
        }
    }

    @Override
    public String exchange(String image, String fileName, String url) throws Exception {
        String[] str=image.split(",");
        String type=str[0];
        if(!type.contains("base64")){
            return "{\n" +
                    "    \"message\": \"图片base64格式不对\",\n" +
                    "    \"info\": \"图片base64格式不对\"\n" +
                    "}";
        }else{
            String content_type=type.substring(5,type.indexOf(";"));
            if(fileName==null||"".equals(fileName)){
                fileName = UUID.randomUUID().toString()+"."+content_type.split("/")[1];//定义一个名字
            }
            String strBase64=str[1];
            InputStream in=null;
            try {
                in = GenerateImage(strBase64);
                Map textMap=new HashMap();
                textMap.put("maxSize","5000000");
                textMap.put("modularName", "CNL");
                textMap.put("allowType", "jpg;png;pdf;doc;xls;xlsx;docx");
                return HttpUploadFile.formUpload(url, textMap, in, fileName, content_type);
            }catch (Exception e){
                return "{\n" +
                        "    \"message\": \""+e.toString()+"\",\n" +
                        "    \"info\": \""+e.toString()+"\"\n" +
                        "}";
            }finally {
                if(in!=null){
                    in.close();
                    in=null;
                }

            }
        }
    }

    /**
     *
     * @param iRequest
     * @param appId
     * @param mchId
     * @param secret
     * @param body
     * @param outTradeNo 订单编号
     * @param totalFee  价格单位为分
     * @return
     */
    @Override
    public JSONObject order(IRequest iRequest,String appId, String mchId, String secret, String body,String outTradeNo, int totalFee) {
        try {
            String notifyUrl= profileService.getProfileValue(iRequest, "WXPAY_CALLBACK_URL");
            logger.info("++++++++++++notifyUrl:{}",notifyUrl);
            if(notifyUrl==null||"".equals(notifyUrl)){
                JSONObject error = new JSONObject();
                error.put("error", "WXPAY_CALLBACK_URL为空，请在系统中配置微信支付回调url");
                return error;
            }
            String res_xml = WxPay.wxOrder(appId, mchId, secret, body, outTradeNo, totalFee, notifyUrl, "JSAPI");
            Map map = JSONAndMap.xml2map(res_xml);
            Map resMap = (Map) map.get("xml");
            String return_code = resMap.get("return_code").toString();
            String result_code = resMap.get("result_code").toString();
            if ("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)) {
                String prepay_id = resMap.get("prepay_id").toString();
                JSONObject res=WxPay.getPayInfo(appId, mchId, secret, prepay_id);
                return res;
            } else {
                JSONObject error = new JSONObject();
                error.put("error", resMap.get("return_msg").toString());
                return error;
            }
        }catch (Exception e){
            JSONObject error = new JSONObject();
            error.put("error", e.getMessage());
            return error;
        }
    }

    @Override
    public JSONObject refund(IRequest iRequest,String appId, String mchId, String secret, String transactionId, String refundFee, String totalFee) {
        try {
            String outRefundNo = UUID.randomUUID().toString();
            String refundCertPath= profileService.getProfileValue(iRequest, "WXPAY_CERT_PATH");
            logger.info("++++++++++++refundCertPath:{}",refundCertPath);
            if(refundCertPath==null||"".equals(refundCertPath)){
                JSONObject error = new JSONObject();
                error.put("error", "WXPAY_CERT_PATH为空，请在系统中配置证书路径");
                return error;
            }
            String res_xml = WxPay.refund(appId, mchId, secret, outRefundNo, transactionId, refundFee, totalFee, refundCertPath);
            Map map = JSONAndMap.xml2map(res_xml);
            JSONObject resMap = (JSONObject) map.get("xml");
            String return_code = resMap.get("return_code").toString();
            String result_code = resMap.get("result_code")!=null?resMap.get("result_code").toString():"FAIL";
            if ("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)) {
                String out_refund_no = resMap.get("out_refund_no").toString();//退款单id  用作主键
                String transaction_id = resMap.get("transaction_id").toString();
                String appid = resMap.get("appid").toString();
                String mch_id = resMap.get("mch_id").toString();
                String out_trade_no = resMap.get("out_trade_no").toString();
                String refund_id = resMap.get("refund_id").toString();//微信退款单号
                String refund_fee = resMap.get("refund_fee").toString();//微信退款金额
                return resMap;
            }else{
                JSONObject error = new JSONObject();
                error.put("error", resMap.get("return_msg").toString());
                return error;
            }
        }catch (Exception e){
            JSONObject error = new JSONObject();
            error.put("error", e.getMessage());
            return error;
        }
    }

    //base64字符串转化成图片
    private InputStream GenerateImage(String imgStr) throws IOException {
        //对字节数组字符串进行Base64解码并生成图片
        BASE64Decoder decoder = new BASE64Decoder();
        //Base64解码
        byte[] b = decoder.decodeBuffer(imgStr);
        for(int i=0;i<b.length;++i) {
            if(b[i]<0) {
                //调整异常数据
                b[i]+=256;
            }
        }
        //生成jpeg图片
        return  new ByteArrayInputStream(b);
    }

    public InputStream sendJsonDownFile(String url,JSONObject json) throws Exception{
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
    }
}
