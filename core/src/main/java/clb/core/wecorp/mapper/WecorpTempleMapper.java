package clb.core.wecorp.mapper;

import clb.core.wecorp.dto.WecorpTemple;
import com.hand.hap.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2017/7/12.
 */
public interface WecorpTempleMapper extends Mapper<WecorpTemple> {

    WecorpTemple findByTempleCodeAndAppId(@Param("templeCode") String templeCode,@Param("appid") String appid);

    int delByTempleIdAndAppId(@Param("templeId") String templeId,@Param("appid") String appid);
}
