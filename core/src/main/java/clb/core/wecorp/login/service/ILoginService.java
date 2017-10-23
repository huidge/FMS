package clb.core.wecorp.login.service;

import org.springframework.security.core.AuthenticationException;

public interface ILoginService {
	public String checkLogin(String code,String appId) throws AuthenticationException;
	public String checkLoginForApp(String code) throws AuthenticationException;
	public String loginOut();
}
