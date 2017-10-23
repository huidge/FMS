package clb.core.notification.service.impl;

import clb.core.notification.mapper.NtnTemplateConcernMapper;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.notification.dto.NtnTemplateConcern;
import clb.core.notification.service.INtnTemplateConcernService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@Transactional
public class NtnTemplateConcernServiceImpl extends BaseServiceImpl<NtnTemplateConcern> implements INtnTemplateConcernService{

    @Autowired
    NtnTemplateConcernMapper ntnTemplateConcernMapper;

    public Boolean existOpenIdAndAppId(String openId,String appId){
        int count=ntnTemplateConcernMapper.existOpenIdAndAppId(openId,appId);
        if(count>0){
            return true;
        }else{
            return false;
        }
    }

    public Boolean subscribe(String openId,String appId){
        int i=ntnTemplateConcernMapper.subscribe(openId, appId);
        return true;
    }

    public Boolean unsubscribe(String openId,String appId){
        int i=ntnTemplateConcernMapper.unsubscribe(openId,appId);
        return true;
    }

	@Override
	public NtnTemplateConcern bind(IRequest request,String backgroundAppid, String openId,Long userId) {
		NtnTemplateConcern ntnTemplateConcern=new NtnTemplateConcern();
		ntnTemplateConcern.setWechatOpenid(openId);
		ntnTemplateConcern.setBackgroundAppid(backgroundAppid);
		List<NtnTemplateConcern>list= ntnTemplateConcernMapper.select(ntnTemplateConcern);
		if(CollectionUtils.isEmpty(list)){
			//用户是否已绑定其他的，有则重绑定
			resetbind(request, backgroundAppid, openId, userId);
		}else {
			//更新
			for(int i=0;i<list.size();i++){
				ntnTemplateConcern=list.get(i);
				ntnTemplateConcern.setUserId(userId);
				ntnTemplateConcern.setWechatConcernType("Y");
				ntnTemplateConcern.setWechatBindType("Y");
				ntnTemplateConcernMapper.updateByPrimaryKeySelective(ntnTemplateConcern);
			}
		}
		return ntnTemplateConcern;
	}

	@Override
	public void unbind(IRequest request, String openId) {
		NtnTemplateConcern ntnTemplateConcern=new NtnTemplateConcern();
		ntnTemplateConcern.setWechatOpenid(openId);
		List<NtnTemplateConcern>list=ntnTemplateConcernMapper.select(ntnTemplateConcern);
		for(NtnTemplateConcern dto:list){
			dto.setWechatBindType("N");
			ntnTemplateConcernMapper.updateByPrimaryKeySelective(dto);
		}
	}

	@Override
	public NtnTemplateConcern resetbind(IRequest request,String backgroundAppid, String openId,Long userId){
		NtnTemplateConcern ntnTemplateConcern=new NtnTemplateConcern();
		ntnTemplateConcern.setUserId(userId);
		List<NtnTemplateConcern>list= ntnTemplateConcernMapper.select(ntnTemplateConcern);
		if(CollectionUtils.isEmpty(list)){
			//插入
			ntnTemplateConcern.setWechatBindDate(new Date());
			ntnTemplateConcern.setWechatOpenid(openId);
			ntnTemplateConcern.setWechatConcernType("Y");
			ntnTemplateConcern.setWechatBindType("Y");
			ntnTemplateConcern.setBackgroundAppid(backgroundAppid);
			ntnTemplateConcernMapper.insertSelective(ntnTemplateConcern);
		}else {
			//更新
			for(int i=0;i<list.size();i++){
				ntnTemplateConcern=list.get(i);
				ntnTemplateConcern.setWechatOpenid(openId);
				ntnTemplateConcern.setWechatConcernType("Y");
				ntnTemplateConcern.setWechatBindType("Y");
				ntnTemplateConcernMapper.updateByPrimaryKeySelective(ntnTemplateConcern);
			}
		}
		return ntnTemplateConcern;
	}

}