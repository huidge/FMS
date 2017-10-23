package clb.core.wecorp.mapper;

import clb.core.wecorp.dto.WecorpCustomReply;
import com.hand.hap.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2017/6/20.
 */
public interface WecorpCustomReplyMapper extends Mapper<WecorpCustomReply> {
    WecorpCustomReply selectByReplyType(WecorpCustomReply wecorpCustomReply);
    WecorpCustomReply findByAccountNumAndReplyType(@Param("accountNum") String accountNum,@Param("replyType") String replyType);
}
