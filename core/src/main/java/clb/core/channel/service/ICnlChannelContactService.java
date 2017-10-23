package clb.core.channel.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.channel.dto.CnlChannelContact;

import java.util.List;

public interface ICnlChannelContactService extends IBaseService<CnlChannelContact>, ProxySelf<ICnlChannelContactService>{
    List<CnlChannelContact> queryContact(IRequest request, CnlChannelContact cnlChannelContact, int page, int pagesize);
}