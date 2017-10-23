package clb.core.wecorp.service;

import clb.core.wecorp.dto.WecorpTemple;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

/**
 * Created by Administrator on 2017/7/12.
 */
public interface IWecorpTempleService extends IBaseService<WecorpTemple>, ProxySelf<IWecorpTempleService> {

    String addTempleId(String template_code,String appid);

    WecorpTemple findByTempleCodeAndAppId(String templeCode,String appid);

    List getTempleData(String appid) throws Exception;

    String delTempleId(String templateId,String appid) throws Exception;
}
