package clb.core.production.mapper;

import clb.core.production.dto.PrdItemSubline;
import com.hand.hap.mybatis.common.Mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface PrdItemSublineMapper extends Mapper<PrdItemSubline>{

    public List<PrdItemSubline> selectByChannel(PrdItemSubline dto);
    
    /**
     * 通过产品id和年期名称查询年期id
     * @param sublineItemName
     * @param itemId
     * @return
     */
    public Long selectByCondition(@Param(value="sublineItemName")String sublineItemName, @Param(value="itemId")Long itemId);
}