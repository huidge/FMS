package clb.core.whtcustom.service.impl;

import clb.core.wecorp.dto.HmsWxResponseDto;
import clb.core.whtcustom.service.IWecorpServerService;
import clb.core.whtcustom.service.IWxService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by enlline on 9/9/16.
 */
@Service
@Transactional
public class WecorpServerServiceImpl implements IWecorpServerService {
    @Autowired
    private IWxService iWxTokenService;

    private String create_menu_url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
    private String conditional_menu_url = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=ACCESS_TOKEN";
    private String delconditional_menu_url = "https://api.weixin.qq.com/cgi-bin/menu/delconditional?access_token=ACCESS_TOKEN";
    private String delete_menu_url = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
    private String get_user_url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
    private String template_send = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
    private String create_group_url = "https://api.weixin.qq.com/cgi-bin/groups/create?access_token=ACCESS_TOKEN";
    private String default_avatar = "https://res.mail.qq.com/bizmp/zh_CN/images/dev/icon_avatar_default.png";
    private String qrcode_create = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";
    private String get_media_url="https://api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
    private String batchget_material_url="https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=ACCESS_TOKEN";
    private String custom_send_url="https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
    private String get_material_count="https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token=ACCESS_TOKEN";
    private String get_temple_id = "https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token=ACCESS_TOKEN";
    private String get_all_temple = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=ACCESS_TOKEN";
    private String del_temple_id = "https://api.weixin.qq.com/cgi-bin/template/del_private_template?access_token=ACCESS_TOKEN";

    @Override
    public HmsWxResponseDto getAccessToken() {
        // TODO Auto-generated method stub
        String appid = "";
        HmsWxResponseDto wxRe = new HmsWxResponseDto();
        try{
            wxRe.setErrcode("0");
            wxRe.setErrmsg(iWxTokenService.connectForToken(appid));
        }catch (Exception ex){
            ex.printStackTrace();
            wxRe.setErrcode("1");
            wxRe.setErrmsg("ERROR");
        }
        return wxRe;
    }

    @Override
    public String get(String https_url) {
        // TODO Auto-generated method stub
        String retMsg;
        OutputStream os =null;
        InputStream sendStatus = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(https_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // conn.setRequestProperty("Content-type", "application/json");
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
        } catch (Exception e) {
            e.printStackTrace();
            retMsg = "{\"errcode\":-100,\"errmsg\":\"unknow\"}";
        }finally{
            try {
                sendStatus.close();
                conn.disconnect();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return retMsg;
    }

    @Override
    public String post(String body,String https_url) {
        // TODO Auto-generated method stub
        HmsWxResponseDto wxResponseDto = new HmsWxResponseDto();
        String retMsg;
        OutputStream os =null;
        InputStream sendStatus = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(https_url);
            conn = (HttpURLConnection) url.openConnection();
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
            int size = sendStatus.available();
            byte[] jsonBytes = new byte[size];
            sendStatus.read(jsonBytes);
            retMsg = new String(jsonBytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            retMsg = "{\"errcode\":-100,\"errmsg\":\"unknow\"}";
        }finally{
            try {
                sendStatus.close();
                os.close();
                conn.disconnect();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return retMsg;
    }

    @Override
    public String postFile(InputStream is,String fileName,String https_url) {
        // TODO Auto-generated method stub
        HmsWxResponseDto wxResponseDto = new HmsWxResponseDto();
        String retMsg;
        String boundary = "------------7da2e536604c8";
        OutputStream os =null;
        InputStream sendStatus = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(https_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            // 获取媒体文件上传的输出流（往微信服务器写数据）
            os = conn.getOutputStream();
            // 请求体开始
            os.write(("--" + boundary + "\r\n").getBytes());
            os.write(String.format("Content-Disposition: form-data; name=\"media\"; filename=\"%s\"\r\n",fileName).getBytes());
            os.write(String.format("Content-Type:%s\r\n\r\n","multipart/form-data;boundary=" + boundary).getBytes());
            // 获取媒体文件的输入流（读取文件）
            //outputStream.write(filebyte.getBytes());
            byte[] f = new byte[1024];
            int len  = 0;
            while((len=is.read(f))!=-1){
                os.write(f,0,len);
            }
            // 请求体结束
            os.write(("\r\n--" + boundary + "--\r\n").getBytes());
            os.flush();
            sendStatus = conn.getInputStream();
            int size = sendStatus.available();
            byte[] jsonBytes = new byte[size];
            sendStatus.read(jsonBytes);
            retMsg = new String(jsonBytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            retMsg = "{\"errcode\":-100,\"errmsg\":\"unknow\"}";
        }finally{
            try {
                sendStatus.close();
                os.close();
                conn.disconnect();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return retMsg;
    }
//    //发送消息
//    @Override
//    public HmsWxResponseDto sendMessage(JSONObject sendJson){
//        HmsWxResponseDto wxRe = new HmsWxResponseDto();
//        JSONObject obj = JSONObject.fromObject(this.post(sendJson.toString(), send_message_url.replace("ACCESS_TOKEN", this.getAccessToken().getErrmsg())));
//        if(obj.getInt("errcode")==0){
//            wxRe.setErrcode("0");
//            wxRe.setErrmsg("发送成功");
//        }else{
//            wxRe.setErrcode(obj.getString("errcode"));
//            wxRe.setErrmsg(obj.getString("errmsg"));
//        }
//        return wxRe;
//    }

    //创建分组

    @Override
    public JSONObject createGroup(String body,String appid) {
        String token=iWxTokenService.connectForToken(appid);
        HmsWxResponseDto wxRe = new HmsWxResponseDto();
        JSONObject obj = JSONObject.fromObject(this.post(body.toString(), create_group_url.replace("ACCESS_TOKEN", token)));
        return obj;
    }
    //创建菜单

    @Override
    public HmsWxResponseDto createMenu(String body,String appid) {
        String token=iWxTokenService.connectForToken(appid);
        HmsWxResponseDto wxRe = new HmsWxResponseDto();
        JSONObject obj = JSONObject.fromObject(this.post(body.toString(), create_menu_url.replace("ACCESS_TOKEN", token)));
        if(obj.getInt("errcode")==0){
            wxRe.setErrcode("0");
            wxRe.setErrmsg("创建成功");
        }else{
            wxRe.setErrcode(obj.getString("errcode"));
            wxRe.setErrmsg(obj.getString("errmsg"));
        }
        return wxRe;
    }

    //创建个性化菜单

    @Override
    public JSONObject createConditionalMenu(String body,String appid) {
        String token=iWxTokenService.connectForToken(appid);
        HmsWxResponseDto wxRe = new HmsWxResponseDto();
        JSONObject obj = JSONObject.fromObject(this.post(body.toString(), conditional_menu_url.replace("ACCESS_TOKEN", token)));
        return obj;
    }
    //删除个性化菜单

    @Override
    public JSONObject deleteConditionalMenu(String body,String appid) {
        String token=iWxTokenService.connectForToken(appid);
        HmsWxResponseDto wxRe = new HmsWxResponseDto();
        JSONObject obj = JSONObject.fromObject(this.post(body.toString(), delconditional_menu_url.replace("ACCESS_TOKEN", token)));
        return obj;
    }
    //删除菜单

    @Override
    public HmsWxResponseDto deleteMenu() {
        HmsWxResponseDto wxRe = new HmsWxResponseDto();
        JSONObject obj = JSONObject.fromObject(this.get(delete_menu_url.replace("ACCESS_TOKEN", this.getAccessToken().getErrmsg())));
        if(obj.getInt("errcode")==0){
            wxRe.setErrcode("0");
            wxRe.setErrmsg("删除成功");
        }else{
            wxRe.setErrcode(obj.getString("errcode"));
            wxRe.setErrmsg(obj.getString("errmsg"));
        }
        return wxRe;
    }

    @Override
    public HmsWxResponseDto getUserAvatar(String userId) {
        HmsWxResponseDto wxRe = new HmsWxResponseDto();
        JSONObject obj = JSONObject.fromObject(this.get(get_user_url.replace("ACCESS_TOKEN", this.getAccessToken().getErrmsg()).replace("USERID", userId)));
        if(obj.has("avatar")){
            wxRe.setErrcode("0");
            wxRe.setErrmsg(obj.getString("avatar"));
        }else{
            wxRe.setErrcode(obj.getString("errcode"));
            wxRe.setErrmsg(default_avatar);
        }
        return wxRe;
    }
//    @Override
//    public HmsWxResponseDto sendIndustry(JSONObject obj,String userId,String templateId,String url,boolean state,String str) {
//        HmsWxResponseDto wxRe = new HmsWxResponseDto();
//        JSONObject body=new JSONObject();
//        JSONObject data=new JSONObject();
//        Map<String, Object> map =obj;
//        for (Map.Entry<String, Object> entry : map.entrySet()) {
//            JSONObject keyword1=new JSONObject();
//            keyword1.put("value",entry.getValue());
//            keyword1.put("color","#173177");
//            data.put(entry.getKey(),keyword1.toString());
//        }
//        body.put("touser",userId);
//        body.put("template_id",templateId);
//        String appid =hmsSystemConfigService.selectByConfigKey("yadea.wx.appid").getConfigValue();
//        String lurl="";
//        if(state){
//            lurl="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+url+"&response_type=code&scope=snsapi_base&state="+str+"#wechat_redirect";
//        }else {
//            lurl="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+url+"&response_type=code&scope=snsapi_base&state=yadea#wechat_redirect";
//        }
//        body.put("url",lurl);
//        body.put("data", data.toString());
//        JSONObject jobj = JSONObject.fromObject(this.post(body.toString(), template_send.replace("ACCESS_TOKEN", this.getAccessToken().getErrmsg()).replace("USERID", userId)));
//        if(jobj.has("avatar")){
//            wxRe.setErrcode("0");
//            wxRe.setErrmsg(jobj.getString("avatar"));
//        }else{
//            wxRe.setErrcode(jobj.getString("errcode"));
//            wxRe.setErrmsg(default_avatar);
//        }
//        return wxRe;
//    }
//    @Override
//    public HmsWxResponseDto sendIndustry(JSONObject obj,String userId,String templateId,String url,boolean state,String str,Map<String,String> color) {
//        HmsWxResponseDto wxRe = new HmsWxResponseDto();
//        JSONObject body=new JSONObject();
//        JSONObject data=new JSONObject();
//        Map<String, Object> map =obj;
//        for (Map.Entry<String, Object> entry : map.entrySet()) {
//            JSONObject keyword1=new JSONObject();
//            keyword1.put("value", entry.getValue());
//            String cl=color.get(entry.getKey());
//            if(cl!=null && !"".equals(cl)){
//                keyword1.put("color",cl);
//            }else {
//                keyword1.put("color","#173177");
//            }
//            data.put(entry.getKey(),keyword1.toString());
//        }
//        body.put("touser",userId);
//        body.put("template_id",templateId);
//        String appid =hmsSystemConfigService.selectByConfigKey("yadea.wx.appid").getConfigValue();
//        String lurl="";
//        if(state){
//            lurl="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+url+"&response_type=code&scope=snsapi_base&state="+str+"#wechat_redirect";
//        }else {
//            lurl="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+url+"&response_type=code&scope=snsapi_base&state=yadea#wechat_redirect";
//        }
//        body.put("url",lurl);
//        body.put("data", data.toString());
//        JSONObject jobj = JSONObject.fromObject(this.post(body.toString(), template_send.replace("ACCESS_TOKEN", this.getAccessToken().getErrmsg()).replace("USERID", userId)));
//        if(jobj.has("avatar")){
//            wxRe.setErrcode("0");
//            wxRe.setErrmsg(jobj.getString("avatar"));
//        }else{
//            wxRe.setErrcode(jobj.getString("errcode"));
//            wxRe.setErrmsg(default_avatar);
//        }
//        return wxRe;
//    }
//    @Override
    public JSONObject messageCustomSend(JSONObject body,String appid){
        String token=iWxTokenService.connectForToken(appid);
        JSONObject obj = JSONObject.fromObject(this.post(body.toString(), custom_send_url.replace("ACCESS_TOKEN", token)));
        return obj;
    }

    @Override
    public String getMediaId(String title,String appid){
        String token=iWxTokenService.connectForToken(appid);
        int offset = 0;
        int num = 20;
        JSONObject body = new JSONObject();
        JSONObject info = new JSONObject();
        body.put("type", "news");
        body.put("count", 20);
        JSONArray list;
        JSONArray news;
        while(num >= 20) {
            body.put("offset",offset);
            String data=this.basicPost(body.toString(), batchget_material_url.replace("ACCESS_TOKEN", token));
            info = JSONObject.fromObject(data);
            num = Integer.parseInt(info.getString("item_count"));
            offset = offset + num;
            list = info.getJSONArray("item");
            for(int i=0;i<list.size();i++){
                news = JSONObject.fromObject(JSONObject.fromObject(list.get(i)).get("content")).getJSONArray("news_item");
                for(int j = 0;j < news.size();j++) {
                    if(title.equals(JSONObject.fromObject(news.get(j)).getString("title"))){
                        return JSONObject.fromObject(list.get(i)).getString("media_id");
                    }
                }
            }
        }

        return null;
    }

    @Override
    public JSONObject getMaterialCount(String appid) {
        String token=iWxTokenService.connectForToken(appid);
        JSONObject obj = JSONObject.fromObject(this.get(get_material_count.replace("ACCESS_TOKEN", token)));
        return null;
    }

    @Override
    public JSONObject getTemlateMsgId(String templateCode, String appid) {
        JSONObject body = new JSONObject();
        body.put("template_id_short",templateCode);
        String token=iWxTokenService.connectForToken(appid);
        JSONObject obj = JSONObject.fromObject(this.post(body.toString(), get_temple_id.replace("ACCESS_TOKEN", token)));
        return obj;
    }

    @Override
    public JSONObject getAllTemple(String appid) {
        String token=iWxTokenService.connectForToken(appid);
        JSONObject obj = JSONObject.fromObject(this.get(get_all_temple.replace("ACCESS_TOKEN", token)));
        return obj;
    }

    @Override
    public JSONObject delTempleId(String templateId, String appid) {
        JSONObject body = new JSONObject();
        body.put("template_id", templateId);
        String token=iWxTokenService.connectForToken(appid);
        JSONObject obj = JSONObject.fromObject(this.post(body.toString(), del_temple_id.replace("ACCESS_TOKEN", token)));
        return obj;
    }


    @Override
    public JSONObject getWxQrcode(JSONObject body,String appid){
        String token=iWxTokenService.connectForToken(appid);
        JSONObject obj = JSONObject.fromObject(this.post(body.toString(), qrcode_create.replace("ACCESS_TOKEN", token)));
        return obj;
    }
    /*@Override
    public UploadFileDTO mediaDowm(String name,String media_id) {
        JSONObject obj=new JSONObject();
        obj.put("name",name);
        obj.put("media_id",media_id);
        String https_url=get_media_url.replace("ACCESS_TOKEN", this.getAccessToken().getErrmsg()).replace("MEDIA_ID", media_id);
        InputStream sendStatus = null;
        HttpURLConnection conn = null;
        UploadFileDTO upd=null;
        try {
            URL url = new URL(https_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();

            sendStatus = conn.getInputStream();
        }catch (Exception e){
        }
        return upd;
    }*/
    @Override
    public JSONObject batchgetMaterial(JSONObject body,String appid) {
        String token=iWxTokenService.connectForToken(appid);
        String data=this.basicPost(body.toString(), batchget_material_url.replace("ACCESS_TOKEN", token));
        System.out.println(data);
        JSONObject info = JSONObject.fromObject(data);
        return info;
    }
    private String basicPost(String params, String url) {
        String resultData = "";
        try {
            URL myURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) myURL.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            if (!params.isEmpty()) {
                connection.setDoOutput(true);
            }
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            if (!params.isEmpty()) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(params.getBytes());
                outputStream.flush();
                outputStream.close();
            }
            if (connection.getResponseCode() != 200) {
                throw new RuntimeException(
                        "HTTP GET Request Failed with Error code : " + connection.getResponseCode());
            }

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder results = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                results.append(line);
            }
            reader.close();
            connection.disconnect();
            resultData = results.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultData;
    }


}
