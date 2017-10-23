package clb.core.commission.mapper;
/**
 * Created by wanjun.feng on 2017-09-11.
 */

import clb.core.commission.dto.CmnChannelRatio;
import com.hand.hap.mybatis.common.Mapper;

import java.util.List;

public interface CmnChannelRatioMapper extends Mapper<CmnChannelRatio> {

    public List<CmnChannelRatio> selectChannelRatios(CmnChannelRatio cmnChannelRatio);
    
    public List<CmnChannelRatio> wsSelectChannelRatios(CmnChannelRatio cmnChannelRatio);

    public List<CmnChannelRatio> selectByChannelIdAndRatioName(CmnChannelRatio cmnChannelRatio);
}