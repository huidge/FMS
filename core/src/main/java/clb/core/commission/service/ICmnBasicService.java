package clb.core.commission.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.commission.dto.CmnBasic;

import java.util.List;

public interface ICmnBasicService extends IBaseService<CmnBasic>, ProxySelf<ICmnBasicService>{

    List<CmnBasic> queryBasic(IRequest requestContext, CmnBasic dto, int page,
                              int pageSize);

    List<CmnBasic> queryBasic(CmnBasic dto);

    Long queryMaxVersion(CmnBasic cmnBasic);

    List<CmnBasic> addVersion(IRequest request,CmnBasic dto,Long oldVersion);

    /****
     * 校验数据
     * @param request
     * @param dto
     */
    public boolean checkData(IRequest request,CmnBasic dto);
}