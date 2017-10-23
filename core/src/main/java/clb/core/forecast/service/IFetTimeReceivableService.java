package clb.core.forecast.service;

import clb.core.channel.dto.CnlPerformanceParas;
import clb.core.common.exceptions.CommonException;
import clb.core.forecast.dto.FetPeriod;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.forecast.dto.FetTimeReceivable;

import java.util.List;

public interface IFetTimeReceivableService extends IBaseService<FetTimeReceivable>, ProxySelf<IFetTimeReceivableService>{

    public void insertTimeData(IRequest iRequest, Long channelId);

    List<FetTimeReceivable> queryUnconfirmedAmount(CnlPerformanceParas cnlPerformanceParas);
}