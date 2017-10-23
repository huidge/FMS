package clb.core.wecorp.login.service;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface IClientDetailService {
    UserDetails loadUserByUsername(String username, String password,String appId,String isApp) throws AuthenticationException;
}
