package clb.core.wecorp.mapper;

import clb.core.wecorp.dto.WecorpAttachment;
import com.hand.hap.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by shanhd on 2016/10/20.
 */
public interface WecorpAttachmentMapper extends Mapper<WecorpAttachment> {

     int countAttachment(@Param("groupId") String groupId, @Param("type") String type);

     List<WecorpAttachment> queryByGroupIdAndType(@Param("groupId") String groupId, @Param("type") String type);
}
