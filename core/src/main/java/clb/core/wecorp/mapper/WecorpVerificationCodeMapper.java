package clb.core.wecorp.mapper;

import clb.core.wecorp.dto.WecorpVerificationCode;
import com.hand.hap.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2017/6/15.
 */
public interface WecorpVerificationCodeMapper extends Mapper<WecorpVerificationCode> {

    Integer disableOtherCode(@Param("phone")String phone);
    WecorpVerificationCode findByPhoneAndEnableFlag(@Param("phone")String phone);
}
