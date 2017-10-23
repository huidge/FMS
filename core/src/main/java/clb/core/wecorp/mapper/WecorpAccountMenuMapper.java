package clb.core.wecorp.mapper;

import clb.core.wecorp.dto.WecorpAccountMenu;
import com.hand.hap.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2017/6/19.
 */
public interface WecorpAccountMenuMapper extends Mapper<WecorpAccountMenu> {
    WecorpAccountMenu findByAccountNum(@Param("accountNum") String accountNum);
}
