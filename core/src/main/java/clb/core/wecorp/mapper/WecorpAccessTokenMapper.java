package clb.core.wecorp.mapper;

import clb.core.wecorp.dto.WecorpAccessToken;
import com.hand.hap.mybatis.common.Mapper;

/**
 * Created by shanhd on 2016/10/24.
 */
public interface WecorpAccessTokenMapper extends Mapper<WecorpAccessToken> {

    WecorpAccessToken findByAppId(String appId);
}
