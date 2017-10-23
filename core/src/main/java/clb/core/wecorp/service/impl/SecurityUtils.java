package clb.core.wecorp.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Administrator on 2017/6/13.
 */
public final class SecurityUtils {
    private SecurityUtils() {
    }

    public static String getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userName = null;
        if(authentication != null) {
            if(authentication.getPrincipal() instanceof UserDetails) {
                UserDetails springSecurityUser = (UserDetails)authentication.getPrincipal();
                userName = springSecurityUser.getUsername();
            } else if(authentication.getPrincipal() instanceof String) {
                userName = (String)authentication.getPrincipal();
            }
        }

        return userName;
    }

    public static String getCurrentClient() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String clientId = null;
        if(authentication instanceof OAuth2Authentication) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication)authentication;
            clientId = oAuth2Authentication.getOAuth2Request().getClientId();
        }

        return clientId;
    }

    public static boolean isAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Collection authorities = securityContext.getAuthentication().getAuthorities();
        if(authorities != null) {
            Iterator var2 = authorities.iterator();

            while(var2.hasNext()) {
                GrantedAuthority authority = (GrantedAuthority)var2.next();
                if(authority.getAuthority().equals("ROLE_ANONYMOUS")) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isCurrentUserInRole(String authority) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails)authentication.getPrincipal();
            return springSecurityUser.getAuthorities().contains(new SimpleGrantedAuthority(authority));
        } else {
            return false;
        }
    }
}

