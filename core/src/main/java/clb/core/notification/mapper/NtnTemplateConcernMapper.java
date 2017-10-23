package clb.core.notification.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.notification.dto.NtnTemplateConcern;
import org.apache.ibatis.annotations.Param;

public interface NtnTemplateConcernMapper extends Mapper<NtnTemplateConcern>{
    int existOpenIdAndAppId(@Param("openId")String openId,@Param("appId")String appId);

    int subscribe(@Param("openId")String openId,@Param("appId")String appId);

    int unsubscribe(@Param("openId")String openId,@Param("appId")String appId);

}