package com.hand.hap.security;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.DigestUtils;

public class ClbPasswordManager extends PasswordManager implements BeanPostProcessor {
	@Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
		String encode=DigestUtils.md5DigestAsHex(rawPassword.toString().getBytes()).toUpperCase();
		return encode.equals(encodedPassword.toUpperCase());
    }
	@Override
    public String encode(CharSequence rawPassword) {
        return DigestUtils.md5DigestAsHex(rawPassword.toString().getBytes()).toUpperCase();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(beanName.equals("passwordManager"))
            return this;
        return bean;
    }
}
