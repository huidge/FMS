package clb.core.wecorp.mapper;

import clb.core.wecorp.dto.WecorpAccountTicket;
import com.hand.hap.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by shanhd on 2016/10/24.
 */
public interface WecorpAccountTicketMapper extends Mapper<WecorpAccountTicket> {

    WecorpAccountTicket findByAppIdAndType(@Param("appId") String appId, @Param("type") String type);
}
