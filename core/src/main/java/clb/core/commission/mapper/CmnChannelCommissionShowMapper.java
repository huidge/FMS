package clb.core.commission.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.commission.dto.CmnChannelCommissionShow;

public interface CmnChannelCommissionShowMapper extends Mapper<CmnChannelCommissionShow>{

    public void deleteCmnData();

    public void insertShowCmnData();

    public void transferShowCmnData(Long batchId);
}