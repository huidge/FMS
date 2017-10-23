package clb.core.channel.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.channel.dto.CnlChannelArchive;

import java.util.List;

public interface ICnlChannelArchiveService extends IBaseService<CnlChannelArchive>, ProxySelf<ICnlChannelArchiveService>{

    List<CnlChannelArchive> queryArchive(IRequest request, CnlChannelArchive cnlChannelArchive, int page, int pagesize);
}