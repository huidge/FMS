package clb.core.wecorp.service;

import clb.core.wecorp.dto.HmsWxResponseDto;
import clb.core.wecorp.dto.WecorpAccountMenu;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import net.sf.json.JSONObject;

/**
 * Created by Administrator on 2017/6/19.
 */
public interface IWecorpAccountMenuService extends IBaseService<WecorpAccountMenu>, ProxySelf<IWecorpAccountMenuService> {
    WecorpAccountMenu findByAccountNum(String accountNum);
    HmsWxResponseDto createMenu(WecorpAccountMenu wecorpAccountMenu) throws Exception;

    HmsWxResponseDto createMenuNew(WecorpAccountMenu wecorpAccountMenu) throws Exception;
}
