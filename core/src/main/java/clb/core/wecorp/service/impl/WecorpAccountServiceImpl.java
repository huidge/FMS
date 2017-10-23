package clb.core.wecorp.service.impl;

import clb.core.wecorp.dto.WecorpAccount;
import clb.core.wecorp.mapper.WecorpAccountMapper;
import clb.core.wecorp.service.IWecorpAccountService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2017/6/19.
 */
@Service
@Transactional
public class WecorpAccountServiceImpl extends BaseServiceImpl<WecorpAccount> implements IWecorpAccountService {

    @Autowired
    private WecorpAccountMapper wecorpAccountMapper;

    @Override
    public WecorpAccount getWoaAccountByAppId(String appid) {
        return wecorpAccountMapper.getWoaAccountByAppId(appid);
    }

    @Override
    public WecorpAccount getWoaAccountByAccountNum(String accountNum) {
        return wecorpAccountMapper.getWoaAccountByAppId(accountNum);
    }
}
