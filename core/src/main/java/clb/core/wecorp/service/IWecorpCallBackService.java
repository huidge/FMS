package clb.core.wecorp.service;

import clb.core.wecorp.dto.WecorpResponseDTO;
import com.hand.hap.core.ProxySelf;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * Created by Administrator on 2017/6/23.
 */
public interface IWecorpCallBackService extends ProxySelf<IWecorpCallBackService> {
    void subscribeEvent(Map map, String appid) throws Exception;
    void menuEvent(Map map, String appid) throws Exception;
    String keywordEvent(Map map,String appid) throws Exception;
    public JSONObject sendMessageByType(String openid,String appid,WecorpResponseDTO o,String... kf) throws Exception;
}
