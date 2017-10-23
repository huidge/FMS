package clb.core.wecorp.mapper;


import clb.core.wecorp.dto.WecorpAccount;
import com.hand.hap.mybatis.common.Mapper;

/**
 * Created by Administrator on 2017/6/14.
 */
public interface WecorpAccountMapper extends Mapper<WecorpAccount> {

    WecorpAccount getWoaAccountByAppId(String appid);

    WecorpAccount getWoaAccountByAccountNum(String accountNUm);
}
