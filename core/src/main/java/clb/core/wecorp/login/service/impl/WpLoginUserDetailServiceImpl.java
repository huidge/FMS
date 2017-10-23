package clb.core.wecorp.login.service.impl;

import java.util.ArrayList;
import java.util.Collection;


import clb.core.wecorp.login.service.IClientDetailService;
import clb.core.wecorp.login.service.ILoginService;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hand.hap.security.CustomUserDetails;

@Service
public class WpLoginUserDetailServiceImpl  implements IClientDetailService {
	private ILoginService wxLoginServiceImpl;
	
	
	public ILoginService getWxLoginServiceImpl() {
		return wxLoginServiceImpl;
	}


	public void setWxLoginServiceImpl(ILoginService wxLoginServiceImpl) {
		this.wxLoginServiceImpl = wxLoginServiceImpl;
	}


	@Override
	public UserDetails loadUserByUsername(String userName,String password,String appId,String isApp)
			throws AuthenticationException {
		if("Y".equals(isApp)){
			String appUser = wxLoginServiceImpl.checkLoginForApp(userName);
			if (appUser.equals("N")) {
				return null;
			}
			Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

			UserDetails userDetails = new CustomUserDetails(1L, "app@"+appUser,
					password, true, true, true, true, authorities);
			return userDetails;
		}else {
			String openId = wxLoginServiceImpl.checkLogin(userName, appId);
			if (openId.equals("N")) {
				return null;
			}
			Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

			UserDetails userDetails = new CustomUserDetails(1L, openId,
					password, true, true, true, true, authorities);
			return userDetails;
		}
	}
}
