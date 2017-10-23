package clb.core.wecorp.service.impl;

import clb.core.wecorp.dto.WecorpAccountTicket;
import clb.core.wecorp.mapper.WecorpAccountTicketMapper;
import clb.core.wecorp.service.IWecorpAccountTicketService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by shanhd on 2016/11/15.
 */
@Service
public class WecorpAccountTicketServiceImpl extends BaseServiceImpl<WecorpAccountTicket> implements IWecorpAccountTicketService {

    @Autowired
    private WecorpAccountTicketMapper wecorpAccountTicketMapper;

    @Override
    public WecorpAccountTicket findByAppIdAndType(String appId, String type) {
        return wecorpAccountTicketMapper.findByAppIdAndType(appId,type);
    }
}
