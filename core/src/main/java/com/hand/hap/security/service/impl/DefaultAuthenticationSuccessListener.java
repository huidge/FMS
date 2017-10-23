package com.hand.hap.security.service.impl;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.mapper.CnlChannelMapper;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.mapper.SpeSupplierMapper;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.exception.RoleException;
import com.hand.hap.account.service.IRole;
import com.hand.hap.account.service.IRoleService;
import com.hand.hap.account.service.IUserService;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.components.CaptchaConfig;
import com.hand.hap.core.components.SysConfigManager;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.core.util.TimeZoneUtil;
import com.hand.hap.security.IAuthenticationSuccessListener;
import com.hand.hap.security.PasswordManager;
import com.hand.hap.security.TokenUtils;
import com.hand.hap.system.dto.SysPreferences;
import com.hand.hap.system.service.ISysPreferencesService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author shengyang.zhou@hand-china.com
 * @author njq.niu@hand-china.com
 */
@Component
public class DefaultAuthenticationSuccessListener implements IAuthenticationSuccessListener {
    @Autowired
    IUserService userService;

    @Autowired
    @Qualifier("roleServiceImpl")
    private IRoleService roleService;

    @Autowired
    ISysPreferencesService preferencesService;

    @Autowired
    CaptchaConfig captchaConfig;

    @Autowired
    PasswordManager passwordManager;

    @Autowired
    SysConfigManager sysConfigManager;

    @Autowired
    UserSecurityStrategyManager userSecurityStrategyManager;
    
    /* ----------- 用于获取用户类型等相关信息  add by bo.wu@hand-china.com -----------*/
    @Autowired
    ClbUserMapper clbUserMapper;
    
    @Autowired
    SpeSupplierMapper supplierMapper;
    
    @Autowired
    CnlChannelMapper channelMapper;
    
    private static final String USER_TYPE = "userType";
    
    private static final String COMPANY_ID = "companyId";
    
    private static final String COMPANY_NAME = "companyName";
    /* ----------- 用于获取用户类型等相关信息  add by bo.wu@hand-china.com -----------*/

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        Locale locale = RequestContextUtils.getLocale(request);
        /*
         * CustomUserDetails userDetails = (CustomUserDetails)
         * SecurityContextHolder.getContext().getAuthentication()
         * .getPrincipal();
         */
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.selectByUserName(userDetails.getUsername());
        HttpSession session = request.getSession(true);
        session.setAttribute(User.FIELD_USER_ID, user.getUserId());
        session.setAttribute(User.FIELD_USER_NAME, user.getUserName());
        session.setAttribute(IRequest.FIELD_LOCALE, locale.toString());
        setTimeZoneFromPreference(session, user.getUserId());
        setLocalePreference(request, user.getUserId());
        setRoleInfo(request, session, user);
        setProcessUser(user,session);
        generateSecurityKey(session);
        captchaConfig.resetLoginFailureInfo(request, response);
        
        /* ----------- 用于获取用户类型等相关信息  add by bo.wu@hand-china.com -----------*/
        ClbUser clbUser = new ClbUser();
        clbUser.setUserId(user.getUserId());
        clbUser = clbUserMapper.selectByPrimaryKey(clbUser);
        
        //设置用户类型
        session.setAttribute(USER_TYPE,clbUser.getUserType());
        //设置用户Id
        if(clbUser.getRelatedPartyId() == null){
        	session.setAttribute(COMPANY_ID,"");
        }else{
        	session.setAttribute(COMPANY_ID,clbUser.getRelatedPartyId());
        }
        
        
        //如果用户类型是供应商
        if("SUPPLIER".equals(clbUser.getUserType())){
        	SpeSupplier speSupplier = new SpeSupplier();
        	speSupplier.setSupplierId(clbUser.getRelatedPartyId());
        	speSupplier = supplierMapper.selectByPrimaryKey(speSupplier);
        	//设置用户名
        	if(speSupplier != null){
                session.setAttribute(COMPANY_NAME,speSupplier.getName());
        	}else{
        		//设置用户名为空
                session.setAttribute(COMPANY_NAME,"");
        	}
        }
        //如果用户类型是渠道
        else if("CHANNEL".equals(clbUser.getUserType())){
        	CnlChannel cnlChannel = new CnlChannel();
        	cnlChannel.setChannelId(clbUser.getRelatedPartyId());
        	cnlChannel = channelMapper.selectByPrimaryKey(cnlChannel);
        	//设置用户名
        	if(cnlChannel != null){
        		session.setAttribute(COMPANY_NAME,cnlChannel.getChannelName());
        	}else{
        		//设置用户名为空
                session.setAttribute(COMPANY_NAME,"");
        	}
        }
        else{
        	//设置用户名为空
            session.setAttribute(COMPANY_NAME,"");
        }
        /* ----------- 用于获取用户类型等相关信息  add by bo.wu@hand-china.com -----------*/
    }

    private void setProcessUser(User user,HttpSession session){
        if(user.getEmployeeId()!=null){
            session.setAttribute(User.FIELD_EMPLOYEE_ID,user.getEmployeeId());
            session.setAttribute(User.FIELD_EMPLOYEE_CODE,user.getEmployeeCode());
        }
    }

    private void setTimeZoneFromPreference(HttpSession session, Long accountId) {
        SysPreferences pref = preferencesService.selectUserPreference(BaseConstants.PREFERENCE_TIME_ZONE, accountId);
        String tz = pref == null ? null : pref.getPreferencesValue();
        // String tz = "GMT+0800";
        if (StringUtils.isBlank(tz)) {
            tz = TimeZoneUtil.toGMTFormat(TimeZone.getDefault());
        }
        session.setAttribute(BaseConstants.PREFERENCE_TIME_ZONE, tz);
    }

    private void setLocalePreference(HttpServletRequest request, Long accountId) {
        SysPreferences pref = preferencesService.selectUserPreference(BaseConstants.PREFERENCE_LOCALE, accountId);
        if (pref != null) {
            WebUtils.setSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME,
                    org.springframework.util.StringUtils.parseLocaleString(pref.getPreferencesValue()));
        }
    }

    private void setRoleInfo(HttpServletRequest request, HttpSession session, User user) {
        List<IRole> roles = roleService.selectRolesByUser(RequestHelper.createServiceRequest(request), user);
        if (roles.isEmpty()) {
            request.setAttribute("code", "NO_ROLE");
            throw new RuntimeException(new RoleException(null, RoleException.MSG_NO_USER_ROLE, null));
        }
        if (sysConfigManager.getRoleMergeFlag()) {
            List<Long> roleIds = new ArrayList<Long>();
            for (IRole role : roles) {
                roleIds.add(role.getRoleId());
            }
            Long[] ids = roleIds.toArray(new Long[roleIds.size()]);

            session.setAttribute(IRequest.FIELD_ALL_ROLE_ID, ids);
            session.setAttribute(IRequest.FIELD_ROLE_ID, roles.get(0).getRoleId());
        }
    }

    private String generateSecurityKey(HttpSession session) {
        return TokenUtils.setSecurityKey(session);
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
