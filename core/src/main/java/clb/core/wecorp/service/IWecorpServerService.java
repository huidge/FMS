package clb.core.wecorp.service;

import clb.core.wecorp.dto.HmsWxResponseDto;
import net.sf.json.JSONObject;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by enlline on 9/9/16.
 */
public interface IWecorpServerService {
    //public HmsWxResponseDto connectForToken(); //获取新的token
    public HmsWxResponseDto getAccessToken();//获取可用的token
//    public HmsWxResponseDto sendMessage(JSONObject sendJson);
    public JSONObject createGroup(String body,String appid) throws Exception;
    public HmsWxResponseDto createMenu(String body,String appid) throws Exception;//自定义菜单

    HmsWxResponseDto createMenuNew(String body, String appid) throws Exception;

    public JSONObject createConditionalMenu(String body,String appid) throws Exception;//个性化菜单
    public JSONObject deleteConditionalMenu(String body,String appid) throws Exception;
    public HmsWxResponseDto deleteMenu();//自定义菜单
    public HmsWxResponseDto getUserAvatar(String userId);//获取员工工号
    public String post(String body, String https_url);
    public String postFile(InputStream is, String fileName, String https_url);
    public String get(String https_url);

//    public HmsWxResponseDto sendIndustry(JSONObject obj, String userId, String templateId, String url, boolean state, String str);
//    public HmsWxResponseDto sendIndustry(JSONObject obj, String userId, String templateId, String url, boolean state, String str, Map<String, String> color);
    public JSONObject getWxQrcode(JSONObject obj,String appid) throws Exception;
//    public UploadFileDTO mediaDowm(String name, String media_id);

    /**
     *
     * @param body
     * @return
     */
    public JSONObject batchgetMaterial(JSONObject body,String appids) throws Exception;

    /**
     * 调用微信接口 ，返回json数据
     * @param body
     * @return
     */
    public JSONObject messageCustomSend(JSONObject body,String appid) throws Exception;

    /**
     * 根据title 返回素材id
     * @param title
     * @return
     */
    public String getMediaId(String title,String appid) throws Exception;

    public JSONObject  getMaterialCount(String appid) throws Exception;

    public JSONObject getTemlateMsgId(String templateCode,String appid) throws Exception;

    public JSONObject getAllTemple(String appid) throws Exception;

    public JSONObject delTempleId(JSONObject body,String appid) throws Exception;


}
