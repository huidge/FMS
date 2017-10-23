package clb.core.commission.service;
/**
 * Created by wanjun.feng on 2017-09-11.
 */

import clb.core.commission.dto.CmnChannelRatioDetail;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;

public interface ICmnChannelRatioDetailService extends IBaseService<CmnChannelRatioDetail>, ProxySelf<ICmnChannelRatioDetailService> {

    public List<CmnChannelRatioDetail> selectChannelRatioDetails(IRequest requestContext, CmnChannelRatioDetail cmnChannelRatioDetail, int page, int pageSize);
    
    public ResponseData batchSubmit(IRequest requestContext, List<CmnChannelRatioDetail> cmnChannelRatioDetailList);
}