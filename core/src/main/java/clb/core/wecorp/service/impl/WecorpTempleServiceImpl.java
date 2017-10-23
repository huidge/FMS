package clb.core.wecorp.service.impl;

import clb.core.wecorp.dto.WecorpTemple;
import clb.core.wecorp.mapper.WecorpTempleMapper;
import clb.core.wecorp.service.IWecorpServerService;
import clb.core.wecorp.service.IWecorpTempleService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/7/12.
 */
@Service
public class WecorpTempleServiceImpl extends BaseServiceImpl<WecorpTemple> implements IWecorpTempleService {

    @Autowired
    private IWecorpServerService wecorpServerService;
    @Autowired
    private WecorpTempleMapper wecorpTempleMapper;

    @Override
    public String addTempleId(String template_code,String appid){
        IRequest iRequest= RequestHelper.newEmptyRequest();
        WecorpTemple woaTemple = new  WecorpTemple();
        JSONObject json = new JSONObject();
        String data = "";
        try {
            WecorpTemple temple = this.findByTempleCodeAndAppId(template_code,appid);
            if(temple!=null){
                data = "该公众号下已经有该模板";
                return data;
            }
            json = wecorpServerService.getTemlateMsgId(template_code,appid);
            if("45027".equals(json.getString("errcode"))) {
                return "请确定该公众号所属行业是否能添加该模板";
            }

            if("0".equals(json.getString("errcode"))) {
                String id= UUID.randomUUID().toString();
                woaTemple.setId(id);
                woaTemple.setTempleCode(template_code);
                woaTemple.setTempleId(json.getString("template_id"));
                woaTemple.setAppid(appid);

                woaTemple = self().insertSelective(iRequest,woaTemple);
            }


            data = json.getString("errmsg");
        }catch (Exception e){
            data = "something error happened";
            e.printStackTrace();
            return data;
        }
        return data;
    }

    @Override
    public WecorpTemple findByTempleCodeAndAppId(String templeCode, String appid) {
        return wecorpTempleMapper.findByTempleCodeAndAppId(templeCode,appid);
    }

    @Override
    public List getTempleData(String appid) throws Exception {
        JSONObject json = wecorpServerService.getAllTemple(appid);
        return json.getJSONArray("template_list");
    }

    @Override
    public String delTempleId(String templateId, String appid) throws Exception {
        String data = "";
        JSONObject json = new JSONObject();
        JSONObject body = new JSONObject();
        wecorpTempleMapper.delByTempleIdAndAppId(templateId,appid);
        body.put("template_id", templateId);
        json = wecorpServerService.delTempleId(body, appid);
        data = json.getString("errmsg");
        return data;
    }
}
