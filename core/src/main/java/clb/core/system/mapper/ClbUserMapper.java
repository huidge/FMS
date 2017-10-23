package clb.core.system.mapper;
import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.system.dto.ClbUser;

public interface ClbUserMapper extends Mapper<ClbUser>{

    public List<ClbUser> selectAllFields(ClbUser clbUser);
    
    public List<ClbUser> selectLecturer(ClbUser clbUser);
	/****
	 * 根据员工类型员工类型、渠道/供应商ID 查找用户
	 * @param user
	 * @return
	 */
	public List<ClbUser> selectFunctionUser(ClbUser user);


	/**
	 * 更新用户状态
	 * @param user
	 */
	public void updateStatus(ClbUser user);

	/**
	 *
	 * @param user
	 */
	public void updatePassword(ClbUser user);

	/**
	 *
	 * @param user
	 */
	public void updateUserName(ClbUser user);

	/**
	 * 渠道导入程序  通过关联方名称和用户类型  查询用户
	 * @param clbUser
	 * @return
	 */
	public List<ClbUser> queryUserIdByUserTypeAndRelatedPartyName(ClbUser clbUser);
	/**
	 * 通过关联方id和关联方类型  查询平台用户的角色为售后行政的员工
	 * @param user
	 * @return
	 */
	public List<ClbUser> queryUserByOwnership(ClbUser user);

	public ClbUser queryUserByUserTypeAndRoleNameAndUserName(ClbUser clbUser);

	public List<ClbUser> queryOneUserByAppId(Long appId);

	public void updatePlanQuota(ClbUser user);

}