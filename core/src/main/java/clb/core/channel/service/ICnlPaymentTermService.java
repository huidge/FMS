package clb.core.channel.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.channel.dto.CnlPaymentTerm;
/**
 * Created by wanjun.feng on 2017/4/19.
 */
public interface ICnlPaymentTermService extends IBaseService<CnlPaymentTerm>, ProxySelf<ICnlPaymentTermService>{
    List<CnlPaymentTerm> query(IRequest iRequest, CnlPaymentTerm cnlPaymentTerm, int page, int pagesize);
    
    List<CnlPaymentTerm> paymentTermBatchUpdate(IRequest iRequest, List<CnlPaymentTerm> cnlPaymentTermList);
}