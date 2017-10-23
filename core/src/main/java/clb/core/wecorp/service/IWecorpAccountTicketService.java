package clb.core.wecorp.service;

import clb.core.wecorp.dto.WecorpAccountTicket;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

/**
 * Created by shanhd on 2016/11/15.
 */
public interface IWecorpAccountTicketService extends IBaseService<WecorpAccountTicket>,ProxySelf<IWecorpAccountTicketService> {

    WecorpAccountTicket findByAppIdAndType(String appId, String type);

}
