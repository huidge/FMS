package clb.core.wecorp.login.service.impl;

import clb.core.wecorp.dto.WecorpAccount;
import clb.core.wecorp.login.service.ILoginService;
import clb.core.wecorp.service.IWecorpAccountService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.IProfileService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WpLoginServiceImpl implements ILoginService {

    private Logger logger = LoggerFactory.getLogger(WpLoginServiceImpl.class);

    @Autowired
    private IWecorpAccountService wecorpAccountService;

    @Autowired
    @Qualifier("profileServiceImpl")
    private IProfileService profileService;

    @Override
    public String checkLogin(String code, String appId) throws AuthenticationException {
        // TODO Auto-generated method stubcr
        logger.info("********************code:"+code);
        String openId = getOpenId(code,appId);
        if(openId != null) {
            return openId;
        }
        return "N";
    }

    @Override
    public String checkLoginForApp(String code) throws AuthenticationException {
        // TODO Auto-generated method stubcr
        logger.info("********************code:"+code);
        String appUser = getAppUser(code);
        if(appUser != null) {
            return appUser;
        }
        return "N";
    }

    // 通过token和code获取员工工号
    public String getOpenId(String code,String appId) throws AuthenticationException {
        // TODO Auto-generated method stub
        WecorpAccount wecorpAccount = wecorpAccountService.getWoaAccountByAppId(appId);
        if(wecorpAccount==null){
            logger.error("appId尚未配置**********************");
            throw new AuthenticationServiceException("appId尚未配置**********************");
        }
        String https_url ="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appId+"&secret="+wecorpAccount.getAppSecret()+"&code="+code+"&grant_type=authorization_code";
        logger.info("https_url:"+https_url);
        String retMsg = "";
        JSONObject obj = null;
        try {
            URL url = new URL(https_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // conn.setRequestProperty("Content-type", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            InputStream sendStatus = conn.getInputStream();
            int size = sendStatus.available();
            byte[] jsonBytes = new byte[size];
            sendStatus.read(jsonBytes);
            retMsg = new String(jsonBytes, "UTF-8");
            sendStatus.close();
            conn.disconnect();
            obj = JSONObject.fromObject(retMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (obj.containsKey("openid")) {
            return obj.getString("openid");
        }else{
            logger.error("请求openid失败");
            throw new AuthenticationServiceException(retMsg);
        }
    }

    // 通过token和code获取员工工号
    public String getAppUser(String code) throws AuthenticationException  {
        JSONObject json=new JSONObject();
        json.put("authCode",code);
        IRequest irequest = RequestHelper.newEmptyRequest();
        irequest.setUserId(-1L);
        irequest.setRoleId(-1L);
        //根据APP_VERIFY_URL找到对应的验证url
        String https_url= profileService.getProfileValue(irequest, "APP_VERIFY_URL");
        logger.info("https_url:"+https_url);
        try {
            URL uploadUrl = new URL(https_url);
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
            JSONObject res= JSONObject.fromObject(buffer.toString());
            Object data=res.get("data");
            if(data==null||data.equals("null")){
                throw new AuthenticationServiceException(res.toString());
            }else{
                Object clbId=((JSONObject) data).get("clbId");
                if(clbId==null){
                    throw new AuthenticationServiceException(res.toString());
                }else{
                    return clbId.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthenticationServiceException(e.getMessage());
        }
    }

    @Override
    public String loginOut() {
        // TODO Auto-generated method stub
        return null;
    }

}
