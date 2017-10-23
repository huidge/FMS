package clb.core.wecorp.mapper;

import clb.core.wecorp.dto.WecorpKF;
import com.hand.hap.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WecorpKFMapper  extends Mapper<WecorpKF> {

    List<WecorpKF> findActiveOpenId(@Param("openId")String openId,@Param("appId")String appId);

    int active(@Param("openId")String openId,@Param("appId")String appId,@Param("kf")String kf);

    int end(@Param("openId")String openId,@Param("appId")String appId,@Param("kf")String kf);

    int transfer(@Param("fromkf")String fromkf,@Param("tokf")String  tokf,@Param("openId")String  openId,@Param("appId")String  appId);

}