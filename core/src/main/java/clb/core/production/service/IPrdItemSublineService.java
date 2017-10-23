package clb.core.production.service;

import clb.core.production.dto.PrdItemSubline;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface IPrdItemSublineService extends IBaseService<PrdItemSubline>, ProxySelf<IPrdItemSublineService>{

    List<PrdItemSubline> query(IRequest iRequest, PrdItemSubline dto, int page, int pagesize);

    List<PrdItemSubline> batchUpdate(IRequest request, List<PrdItemSubline> dtoList);

    List<PrdItemSubline> selectByChannel(IRequest requestContext, PrdItemSubline dto);
    
    /**
     * 通过产品id和年期名称查询
     * @param sublineItemName
     * @param itemId
     * @return
     */
    Long selectByCondition(@Param(value="sublineItemName")String sublineItemName, @Param(value="itemId")Long itemId);

}