package clb.core.wecorp.mapper;


import clb.core.wecorp.dto.WecorpUser;
import com.hand.hap.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/6/14.
 */
public interface WecorpUserMapper extends Mapper<WecorpUser> {

    List<WecorpUser> getUserByPhoneAndType(@Param("phone")String phone,@Param("type")String type);
}
