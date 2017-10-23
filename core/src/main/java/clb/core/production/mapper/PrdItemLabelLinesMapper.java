package clb.core.production.mapper;

import clb.core.production.dto.PrdItemLabelLines;
import com.hand.hap.mybatis.common.Mapper;

import java.util.List;

/**
 * Created by Rex.Hua on 17/4/12.
 */
public abstract interface PrdItemLabelLinesMapper extends Mapper<PrdItemLabelLines> {

    // 根据条件查询
    public List<PrdItemLabelLines> selectByParam(PrdItemLabelLines dto);
    // 根据条件查询
    public List<PrdItemLabelLines> choiceTags(PrdItemLabelLines dto);

    public PrdItemLabelLines updateByLabelNameSelective(PrdItemLabelLines dto);

    public long getCountByItemLabel(PrdItemLabelLines dto);

    public PrdItemLabelLines deleteByItemLabel(PrdItemLabelLines dto);


}