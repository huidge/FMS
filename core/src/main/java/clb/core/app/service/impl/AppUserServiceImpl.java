package clb.core.app.service.impl;

import clb.core.app.dto.AppUser;
import clb.core.app.mapper.AppUserMapper;
import clb.core.app.service.IAppUserService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 谈晟 cheng.tan@hand-china.com
 * @description
 * @time 2017/10/16
 */
@Service
@Transactional
public class AppUserServiceImpl extends BaseServiceImpl<AppUser> implements IAppUserService {

    @Autowired
    private AppUserMapper appUserMapper;

    @Override
    public AppUser saveAppUser(IRequest iRequest, Long appUserId, Long clbUserId, String username, String password) throws Exception {
        AppUser appUser = new AppUser();
        if (appUserId != null) {
            appUser.setAppUserId(appUserId);
        } else {
            throw new Exception("app用户id为空!");
        }
        if (clbUserId != null) {
            appUser.setClbUserId(clbUserId);
        } else {
            throw new Exception("财联邦用户id为空!");
        }
        if (username != null) {
            appUser.setAppUsername(username);
        } else {
            throw new Exception("app用户名为空!");
        }
        if (password != null) {
            appUser.setAppPassword(password);
        } else {
            throw new Exception("app用户密码为空!");
        }
        return self().insertSelective(iRequest, appUser);
    }

    @Override
    public Integer delAppUser(IRequest iRequest, Long appUserId) throws Exception {
        /* 根据app用户id 查询用户 */
        AppUser appUser = getAppUserById(iRequest, appUserId);
        return self().deleteByPrimaryKey(appUser);
    }

    @Override
    public AppUser queryAppUser(IRequest iRequest, Long appUserId, String username, String password) throws Exception {
        if (appUserId == null || username == null || password == null) {
            throw new Exception("必要参数为空!");
        }
        AppUser appUser = new AppUser();
        appUser.setAppUserId(appUserId);
        appUser.setAppUsername(username);
        appUser.setAppPassword(password);
        List<AppUser> list = appUserMapper.select(appUser);
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public int editAppUser(IRequest iRequest, Long appUserId, String username, String password) throws Exception {
        /* 通过app用户id获取对象 */
        AppUser dto = getAppUserById(iRequest, appUserId);
        /* 构造dto */
        AppUser appUser = new AppUser();
        appUser.setId(dto.getId());
        appUser.setAppUserId(appUserId);
        appUser.setAppUsername(username);
        appUser.setAppPassword(password);
        return appUserMapper.updateByPrimaryKeySelective(appUser);
    }

    @Override
    public AppUser selectAppUser(IRequest iRequest, AppUser appUser) throws Exception {
        return appUserMapper.selectOne(appUser);
    }

    private AppUser getAppUserById(IRequest iRequest, Long appUserId) throws Exception {
        AppUser appUser = new AppUser();
        appUser.setAppUserId(appUserId);
        List<AppUser> list = appUserMapper.select(appUser);
        if (list.size() == 0) {
            throw new Exception("找不到对应的app用户!");
        }
        return list.get(0);
    }
}
