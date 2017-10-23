package clb.core.commission.mapper;
/**
 * Created by wanjun.feng on 2017-09-11.
 */

import clb.core.commission.dto.CmnChannelRatioDetail;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;


public interface CmnChannelRatioDetailMapper extends Mapper<CmnChannelRatioDetail> {

    public List<CmnChannelRatioDetail> selectChannelRatioDetails(CmnChannelRatioDetail cmnChannelRatioDetail);
    
    public List<CmnChannelRatioDetail> selectChannelRatioDetailsByNull(CmnChannelRatioDetail cmnChannelRatioDetail);
    
}