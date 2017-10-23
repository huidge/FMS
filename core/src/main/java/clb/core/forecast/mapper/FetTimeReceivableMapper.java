package clb.core.forecast.mapper;

import clb.core.channel.dto.CnlPerformanceParas;
import clb.core.forecast.dto.FetPeriod;
import com.hand.hap.mybatis.common.Mapper;
import clb.core.forecast.dto.FetTimeReceivable;

import java.util.List;

public interface FetTimeReceivableMapper extends Mapper<FetTimeReceivable> {

    public void deleteCurData(FetPeriod fetPeriod);

    public Long queryCurChannelPeriod(Long channelId);

    List<FetTimeReceivable> queryUnconfirmedAmount(CnlPerformanceParas cnlPerformanceParas);
}