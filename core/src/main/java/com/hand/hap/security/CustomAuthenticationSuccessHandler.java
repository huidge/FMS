//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hap.security;

import com.hand.hap.message.profile.SystemConfigListener;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hap.security.IAuthenticationSuccessListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.util.WebUtils;

public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements SystemConfigListener {
    @Autowired
    private ApplicationContext applicationContext;
    private RequestCache requestCache = new HttpSessionRequestCache();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public CustomAuthenticationSuccessHandler() {
        this.setDefaultTargetUrl("/index");
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        this.clearAuthenticationAttributes(request);
        Map listeners = this.applicationContext.getBeansOfType(IAuthenticationSuccessListener.class);
        ArrayList list = new ArrayList();
        list.addAll(listeners.values());
        Collections.sort(list);
        Object successListener = null;

        try {
            Iterator requestURI = list.iterator();

            while(true) {
                if(!requestURI.hasNext()) {
                    HttpSession requestURI1 = request.getSession(false);
                    requestURI1.setAttribute("login_change_index", "CHANGE");
                    break;
                }

                IAuthenticationSuccessListener isCas1 = (IAuthenticationSuccessListener)requestURI.next();
                isCas1.onAuthenticationSuccess(request, response, authentication);
            }
        } catch (Exception var12) {
            this.logger.error("authentication success, but error occurred in " + successListener, var12);
            HttpSession isCas = request.getSession(false);
            if(isCas != null) {
                isCas.invalidate();
            }

            request.setAttribute("error", Boolean.valueOf(true));
            request.setAttribute("exception", var12);
            request.getRequestDispatcher("/login").forward(request, response);
            return;
        }

        String requestURI2 = request.getRequestURI();
        boolean isCas2 = requestURI2.endsWith("/login/cas");
        if(isCas2) {
            SavedRequest serverRequest = this.requestCache.getRequest(request, response);
            if(serverRequest != null) {
                String httpSessionCsrfTokenRepository1 = serverRequest.getRedirectUrl();
                String defaultCsrfToken1 = this.getDefaultTargetUrl();
                if(!"/index".equalsIgnoreCase(defaultCsrfToken1)) {
                    httpSessionCsrfTokenRepository1 = this.getDefaultTargetUrl() + "?targetUrl=" + httpSessionCsrfTokenRepository1;
                }

                this.getRedirectStrategy().sendRedirect(request, response, httpSessionCsrfTokenRepository1);
                return;
            }
        }

//        ServletServerHttpRequest serverRequest1 = new ServletServerHttpRequest(request);
//        if(!WebUtils.isSameOrigin(serverRequest1)) {
//            HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
//            CsrfToken defaultCsrfToken = httpSessionCsrfTokenRepository.generateToken(request);
//            httpSessionCsrfTokenRepository.saveToken(defaultCsrfToken, request, response);
//            response.getWriter().write("{sessionId:\'" + request.getSession().getId() + "\',csrf_token:\'" + defaultCsrfToken.getToken() + "\'}");
//        } else {
            this.handle(request, response, authentication);
//        }

    }

    public List<String> getAcceptedProfiles() {
        return Arrays.asList(new String[]{"DEFAULT_TARGET_URL"});
    }

    public void updateProfile(String profileName, String profileValue) {
        if(StringUtil.isNotEmpty(profileValue)) {
            this.setDefaultTargetUrl(profileValue);
        }

    }
}
