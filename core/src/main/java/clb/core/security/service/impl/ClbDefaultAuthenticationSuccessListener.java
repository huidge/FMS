package clb.core.security.service.impl;

import com.hand.hap.account.dto.User;
import com.hand.hap.account.exception.RoleException;
import com.hand.hap.account.service.IRole;
import com.hand.hap.account.service.IRoleService;
import com.hand.hap.account.service.IUserService;
import com.hand.hap.core.components.CaptchaConfig;
import com.hand.hap.core.components.SysConfigManager;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.core.util.TimeZoneUtil;
import com.hand.hap.security.IAuthenticationSuccessListener;
import com.hand.hap.security.PasswordManager;
import com.hand.hap.security.TokenUtils;
import com.hand.hap.security.service.impl.UserSecurityStrategyManager;
import com.hand.hap.system.dto.SysPreferences;
import com.hand.hap.system.service.ISysPreferencesService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
/**
 * Created by wanjun.feng on 17/4/12.
 */
@Component
public class ClbDefaultAuthenticationSuccessListener implements IAuthenticationSuccessListener {
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

    public ClbDefaultAuthenticationSuccessListener() {
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Locale locale = RequestContextUtils.getLocale(request);
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = this.userService.selectByUserName(userDetails.getUsername());
        HttpSession session = request.getSession(true);
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("userName", user.getUserName());
        this.setTimeZoneFromPreference(session, user.getUserId());
        this.setLocalePreference(request, user.getUserId());
        this.setRoleInfo(request, session, user);
        this.generateSecurityKey(session);
        this.captchaConfig.resetLoginFailureInfo(request, response);
    }

    private void setTimeZoneFromPreference(HttpSession session, Long accountId) {
        SysPreferences pref = this.preferencesService.selectUserPreference("timeZone", accountId);
        String tz = pref == null?null:pref.getPreferencesValue();
        if(StringUtils.isBlank(tz)) {
            tz = TimeZoneUtil.toGMTFormat(TimeZone.getDefault());
        }
        session.setAttribute("timeZone", tz);
    }

    private void setLocalePreference(HttpServletRequest request, Long accountId) {
        SysPreferences pref = this.preferencesService.selectUserPreference("locale", accountId);
        if(pref != null) {
            WebUtils.setSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, org.springframework.util.StringUtils.parseLocaleString(pref.getPreferencesValue()));
        }
        pref = this.preferencesService.selectUserPreference("locale2", accountId);
        if(pref != null) {
            WebUtils.setSessionAttribute(request, "locale2", pref.getPreferencesValue());
        }else{
        	 WebUtils.setSessionAttribute(request, "locale2","Simple");
        }
    }

    private void setRoleInfo(HttpServletRequest request, HttpSession session, User user) {
        List roles = this.roleService.selectRolesByUser(RequestHelper.createServiceRequest(request), user);
        if(roles.isEmpty()) {
            request.setAttribute("code", "NO_ROLE");
            throw new RuntimeException(new RoleException((String)null, "error.account_no_role", (Object[])null));
        } else {
            if(this.sysConfigManager.getRoleMergeFlag()) {
                ArrayList roleIds = new ArrayList();
                Iterator ids = roles.iterator();

                while(ids.hasNext()) {
                    IRole role = (IRole)ids.next();
                    roleIds.add(role.getRoleId());
                }

                Long[] ids1 = (Long[])roleIds.toArray(new Long[roleIds.size()]);
                session.setAttribute("roleIds", ids1);
                session.setAttribute("roleId", ((IRole)roles.get(0)).getRoleId());
            }

        }
    }

    private String generateSecurityKey(HttpSession session) {
        return TokenUtils.setSecurityKey(session);
    }

    public int getOrder() {
        return 0;
    }
}