package clb.core.wecorp.service;

import com.hand.hap.core.ProxySelf;

public interface IWxTokenService extends ProxySelf<IWxTokenService> {
    String connectForToken(String var1);

    String post(String var1, String var2);

    String get(String var1);
}