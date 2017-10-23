package clb.core.whtcustom.service;

import clb.core.wecorp.dto.HmsWxResponseDto;
import net.sf.json.JSONObject;

import java.io.InputStream;

/**
 * Created by enlline on 9/9/16.
 */
public interface IWecorpServerService {
    //public HmsWxResponseDto connectForToken(); //获取新的token
    public HmsWxResponseDto getAccessToken();//获取可用的token
//    public HmsWxResponseDto sendMessage(JSONObject sendJson);
    public JSONObject createGroup(String body, String appid);
    public HmsWxResponseDto createMenu(String body, String appid);//自定义菜单
    public JSONObject createConditionalMenu(String body, String appid);//个性化菜单
    public JSONObject deleteConditionalMenu(String body, String appid);
    public HmsWxResponseDto deleteMenu();//自定义菜单
    public HmsWxResponseDto getUserAvatar(String userId);//获取员工工号
    public String post(String body, String https_url);
    public String postFile(InputStream is, String fileName, String https_url);
    public String get(String https_url);

//    public HmsWxResponseDto sendIndustry(JSONObject obj, String userId, String templateId, String url, boolean state, String str);
//    public HmsWxResponseDto sendIndustry(JSONObject obj, String userId, String templateId, String url, boolean state, String str, Map<String, String> color);
    public JSONObject getWxQrcode(JSONObject obj, String appid);
//    public UploadFileDTO mediaDowm(String name, String media_id);

    /**
     *
     * @param body
     * @return
     */
    public JSONObject batchgetMaterial(JSONObject body, String appids);

    /**
     * 调用微信接口 ，返回json数据
     * @param body
     * @return
     */
    public JSONObject messageCustomSend(JSONObject body, String appid);

    /**
     * 根据title 返回素材id
     * @param title
     * @return
     */
    public String getMediaId(String title, String appid);

    public JSONObject  getMaterialCount(String appid);

    public JSONObject getTemlateMsgId(String templateCode, String appid);

    public JSONObject getAllTemple(String appid);

    public JSONObject delTempleId(String templateId, String appid);


}
