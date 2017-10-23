package clb.core.commission.service;
/**
 * Created by wanjun.feng on 2017-09-11.
 */

import clb.core.commission.dto.CmnChannelRatio;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

public interface ICmnChannelRatioService extends IBaseService<CmnChannelRatio>, ProxySelf<ICmnChannelRatioService> {

    public List<CmnChannelRatio> selectChannelRatios(IRequest requestContext, CmnChannelRatio cmnChannelRatio, int page, int pageSize);

    public List<CmnChannelRatio> selectByChannelIdAndRatioName(CmnChannelRatio cmnChannelRatio);
    
    public ResponseData batchSubmit(IRequest requestContext, List<CmnChannelRatio> cmnChannelRatioList);

    public List<CmnChannelRatio> wsSelectChannelRatios(IRequest requestContext, CmnChannelRatio cmnChannelRatio, int page, int pageSize);
    
}