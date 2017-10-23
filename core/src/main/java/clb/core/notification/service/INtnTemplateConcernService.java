package clb.core.notification.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.notification.dto.NtnTemplateConcern;

public interface INtnTemplateConcernService extends IBaseService<NtnTemplateConcern>, ProxySelf<INtnTemplateConcernService>{

    public Boolean existOpenIdAndAppId(String openId,String appId);

    public Boolean subscribe(String openId,String appId);

    public Boolean unsubscribe(String openId,String appId);

    public NtnTemplateConcern bind(IRequest request,String backgroundAppid, String openId,Long userId);
    
    public void unbind(IRequest request, String openId);
    //重绑定
    public NtnTemplateConcern resetbind(IRequest request,String backgroundAppid, String openId,Long userId);
}