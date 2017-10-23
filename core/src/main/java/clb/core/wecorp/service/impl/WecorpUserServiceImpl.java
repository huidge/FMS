package clb.core.wecorp.service.impl;

import clb.core.wecorp.dto.WecorpUser;
import clb.core.wecorp.mapper.WecorpUserMapper;
import clb.core.wecorp.service.IWecorpUserService;
import com.hand.hap.core.IRequest;
import com.hand.hap.security.PasswordManager;
import com.hand.hap.security.service.impl.UserSecurityStrategyManager;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/6/14.
 */
@Service
@Transactional
public class WecorpUserServiceImpl extends BaseServiceImpl<WecorpUser> implements IWecorpUserService {
    @Autowired
    private PasswordManager passwordManager;
    @Autowired
    UserSecurityStrategyManager userSecurityStrategyManager;
    @Autowired
    private WecorpUserMapper woaUserMapper;

    public WecorpUser insertSelective(IRequest request, WecorpUser record) {
        if(StringUtils.isEmpty(record.getPassword())) {
            record.setPassword(this.passwordManager.getDefaultPassword());
        }

        record.setPasswordEncrypted(this.passwordManager.encode(record.getPassword()));


        record = (WecorpUser)super.insertSelective(request, record);
        return record;
    }

    @Override
    public List<WecorpUser> getUserByPhoneAndType(String phone, String accountName) {
        return woaUserMapper.getUserByPhoneAndType(phone,accountName);
    }
}
