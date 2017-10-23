package clb.core.app.service;

import clb.core.app.dto.AppUser;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

/**
 * @author 谈晟 cheng.tan@hand-china.com
 * @description
 * @time 2017/10/16
 */
public interface IAppUserService extends IBaseService<AppUser>, ProxySelf<IAppUserService> {

    /**
     * 将app用户保存
     *
     * @param iRequest
     * @param appUserId
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    AppUser saveAppUser(IRequest iRequest, Long appUserId,Long clbUserId, String username, String password) throws Exception;

    /**
     * 通过app用户id删除用户
     *
     * @param iRequest
     * @param appUserId
     * @return
     */
    Integer delAppUser(IRequest iRequest, Long appUserId) throws Exception;

    /**
     * 查询app用户
     *
     * @param iRequest
     * @param appUserId
     * @return
     */
    AppUser queryAppUser(IRequest iRequest, Long appUserId, String username, String password) throws Exception;

    /**
     * 修改app用户
     *
     * @param iRequest
     * @param appUserId
     * @param username
     * @param password
     * @return
     */
    int editAppUser(IRequest iRequest, Long appUserId, String username, String password) throws Exception;


    AppUser selectAppUser(IRequest iRequest, AppUser appUser) throws Exception;

}
