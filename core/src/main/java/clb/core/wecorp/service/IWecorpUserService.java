package clb.core.wecorp.service;

import clb.core.wecorp.dto.WecorpUser;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

/**
 * Created by Administrator on 2017/6/14.
 */
public interface IWecorpUserService extends IBaseService<WecorpUser>, ProxySelf<IWecorpUserService> {

    List<WecorpUser> getUserByPhoneAndType(String phone,String type);
}
