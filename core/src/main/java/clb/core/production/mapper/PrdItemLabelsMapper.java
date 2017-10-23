package clb.core.production.mapper;

import clb.core.production.dto.PrdItemLabels;
import com.hand.hap.mybatis.common.Mapper;

import java.util.List;

/**
 * Created by Rex.Hua on 17/4/12.
 */
public abstract interface PrdItemLabelsMapper extends Mapper<PrdItemLabels> {

    // 根据条件查询
    public String selectLabelId(PrdItemLabels dto);

}