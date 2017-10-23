package clb.core.channel.mapper;

import clb.core.channel.dto.CnlChannelContract;
import com.hand.hap.mybatis.common.Mapper;
import clb.core.channel.dto.CnlChannelContact;
import java.util.List;

public interface CnlChannelContactMapper extends Mapper<CnlChannelContact>{
    /**
     * 渠道联系人
     * @param cnlChannelContact
     * @return
     */
    public List<CnlChannelContact> queryContact(CnlChannelContact cnlChannelContact);
}