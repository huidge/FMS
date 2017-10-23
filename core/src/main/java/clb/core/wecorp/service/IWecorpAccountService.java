package clb.core.wecorp.service;

import clb.core.wecorp.dto.WecorpAccount;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

/**
 * Created by Administrator on 2017/6/19.
 */
public interface IWecorpAccountService extends IBaseService<WecorpAccount>, ProxySelf<IWecorpAccountService> {

    public WecorpAccount getWoaAccountByAppId(String appid);

    public WecorpAccount getWoaAccountByAccountNum(String accountNum);
}
