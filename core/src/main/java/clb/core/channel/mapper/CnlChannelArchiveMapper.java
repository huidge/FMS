package clb.core.channel.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.channel.dto.CnlChannelArchive;
import java.util.List;

public interface CnlChannelArchiveMapper extends Mapper<CnlChannelArchive>{
    /**
     * 渠道档案信息查询
     * @param cnlChannelArchive
     * @return
     */
    public List<CnlChannelArchive> queryArchive(CnlChannelArchive cnlChannelArchive);
}