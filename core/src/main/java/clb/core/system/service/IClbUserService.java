package clb.core.system.service;
import java.util.List;
import java.util.Locale;

import org.springframework.web.bind.annotation.RequestBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;

import clb.core.channel.dto.CnlChannel;
import clb.core.customer.dto.CtmCustomer;
import clb.core.system.dto.ClbUser;
import net.sf.json.JSONObject;

public interface IClbUserService extends IBaseService<ClbUser>, ProxySelf<IClbUserService>{

    public List<ClbUser> selectAllFields(IRequest requestContext, ClbUser clbUser, int page, int pageSize);
    
    public List<ClbUser> selectAll(IRequest requestContext, ClbUser clbUser);
    
    public List<ClbUser> selectLecturer(IRequest requestContext, ClbUser clbUser);

	public ResponseData addChannelContract(IRequest requestContext, CnlChannel cnlChannel);

    public ResponseData channelUserRegest(IRequest requestContext, ClbUser clbUser,CnlChannel channel,String sendNotifyCode);
    /**
     * 客户注册
     * @param requestContext
     * @param clbUser
     * @param customer
     * @return
     */
    public ResponseData customerUserRegest(IRequest requestContext, ClbUser clbUser,CtmCustomer customer,Locale locale);
    /**
     * 渠道账户管理
     * @param requestCtx
     * @param dto
     * @return
     */
    public ResponseData channelUserSubmit(IRequest requestCtx ,@RequestBody List<ClbUser> dto);
    
    public boolean isAdmin(IRequest request);
    
    public boolean hasRoleByRoleCode(IRequest request,String roleCode);

	public boolean isImporter(IRequest request);
    
    public boolean isSupplier(IRequest request);

	public boolean isDaiBan(IRequest request);
    
    public Long getSupplierId(IRequest request);
	
	/****
	 * 根据手机号查询用户
	 * @param request
	 * @param phone
	 * @param phoneCode
	 * @return
	 */
	public List<ClbUser> queryUserByPhone(IRequest request,String phone,String phoneCode);
	
	/******
	 * 发通知用，返回用户的 手机、email
	 * 如果userId 不为空，则查询userId，否则根据userType查询
	 * @param request
	 * @param userType
	 * @param userId
	 * @return
	 */
	public List<ClbUser> queryUserForNotice(IRequest request,String userType,Long userId);
	
	/****
	 * 更新用户数据
	 * @param irequest
	 * @param user
	 */
	public void updateUserDetail(IRequest irequest,ClbUser user);
	/***
	 * 忘记密码
	 * @param request
	 * @param user
	 * @return
	 */
	public ResponseData forgetPassword(IRequest request,ClbUser user);
	/****
	 * 修改手机号
	 * @param request
	 * @param user
	 * @return
	 */
	public ResponseData changePhone(IRequest request, ClbUser user);
	/**
	 * 渠道导入程序  通过关联方名称和用户类型  查询用户
	 * @param request
	 * @param clbUser
	 * @return
	 */
	public List<ClbUser> queryUserIdByUserTypeAndRelatedPartyName(IRequest request, ClbUser clbUser);
	/**
	 * 通过关联方id和关联方类型  查询平台用户的角色为售后行政的员工
	 * @param requestCtx
	 * @param user
	 * @return
	 */
	public List<ClbUser> queryUserByOwnership(IRequest requestCtx, ClbUser user);

	public ClbUser selectBusinessStaff(ClbUser clbUser);
	
	/**
	 * 微信取消关注 ，清空该用户openId接口
	 * @param request
	 * @param user
	 * @return
	 */
	/*public JSONObject wxUnfollow(IRequest request,ClbUser user);*/
	public List<ClbUser> queryOneUserByAppId(Long appId);
}