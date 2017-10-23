package clb.core.wecorp.service.impl;

import clb.core.wecorp.dto.WecorpArticle;
import clb.core.wecorp.dto.WecorpArticleDTO;
import clb.core.wecorp.dto.WecorpMaterial;
import clb.core.wecorp.mapper.WecorpArticleMapper;
import clb.core.wecorp.service.IWecorpArticleService;
import clb.core.wecorp.service.IWecorpMaterialService;
import clb.core.wecorp.service.IWxService;
import clb.core.wecorp.utils.Constant;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by shanhd on 2016/10/20.
 */
@Service
public class WecorpArticleServiceImpl extends BaseServiceImpl<WecorpArticle> implements IWecorpArticleService {

    private final Logger logger = LoggerFactory.getLogger(WecorpArticleServiceImpl.class);

    @Autowired
    private WecorpArticleMapper wecorpArticleMapper;
    @Autowired
    private IWecorpMaterialService materialService;

    @Autowired
    private IWxService iWxService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createSave(WecorpArticle[] articles,IRequest iRequest) {
        String groupNumber= UUID.randomUUID().toString();
        int groupSortNum=1;
        boolean groupFlag=false;
        int count=articles.length;
        for (WecorpArticle article:articles){
            article.setStatus(Constant.ARTICLE_WAIT_SEND);
            if(count>1){
                article.setGroupNumber(groupNumber);
                article.setGroupSortNum(groupSortNum);
                groupSortNum++;
            }
            self().insertSelective(iRequest,article);
        }
        return 1;
    }

    @Override
    public int editSave(WecorpArticleDTO dto,IRequest iRequest) {
        HashMap<String,Boolean> isExitMap=new HashMap<String,Boolean>();
        HashMap<String,WecorpArticle> originArticleMap=new HashMap<String,WecorpArticle>();
        if(dto.isGroup()){  //被编辑的图文为图文组
            List<WecorpArticle> list=wecorpArticleMapper.getArticleByGroupNumber(dto.getOriginGroupNumber());
            for (WecorpArticle bgArticle:list){
                isExitMap.put(bgArticle.getId()+"",false);
                originArticleMap.put(bgArticle.getId()+"", bgArticle);
            }
        }else{ //被编辑的图文为单图文
            WecorpArticle wecorpArticle=new WecorpArticle();
            wecorpArticle.setId(dto.getOriginGroupNumber());
            wecorpArticle=self().selectByPrimaryKey(iRequest,wecorpArticle);
            isExitMap.put(wecorpArticle.getId()+"",false);
            originArticleMap.put(wecorpArticle.getId()+"", wecorpArticle);
        }
        String groupNumber=UUID.randomUUID().toString();
        int groupSortNum=1;
        WecorpArticle[] articles=dto.getArticles();
        for (WecorpArticle article:articles){
            if(articles.length>1){ //图文组
                article.setGroupNumber(groupNumber);
                article.setGroupSortNum(groupSortNum);
                groupSortNum++;
            }
            if(article.getId()!=null){ //原图文
                self().updateByPrimaryKey(iRequest,article);
                isExitMap.put(article.getId()+"",true);
            }else{ //新增图文
                self().insertSelective(iRequest,article);
            }
        }
        for (String key:isExitMap.keySet()){
            if(!isExitMap.get(key)){ //已被删除
                WecorpArticle wecorpArticle=originArticleMap.get(key);
                self().deleteByPrimaryKey(wecorpArticle);
            }
        }
        return 1;
    }

    @Override
    public List<WecorpArticle> getArticle(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return wecorpArticleMapper.getArticle();
    }

    @Override
    public List<WecorpArticle> getArticleByGroupNumber(String groupNumber) {
        return wecorpArticleMapper.getArticleByGroupNumber(groupNumber);
    }

    @Override
    public boolean sendArticle(String articleId, boolean isGroup) {
        IRequest iRequest= RequestHelper.newEmptyRequest();
        boolean result=false;
        List<WecorpArticle> articleList=new ArrayList<WecorpArticle>();
        try{
            JSONObject getJson;
            List<String> news_id_array =  new ArrayList<String>();
            if(isGroup){  //图文组
                List<WecorpArticle> list = wecorpArticleMapper.getArticleByGroupNumber(articleId);
                for(WecorpArticle article:list){
                    news_id_array.add(article.getId());
                }
                if(news_id_array.size() == 0){
                    throw new Exception("没有选定的素材");
                }
                articleList.addAll(list);
            }else{ //单图文
                WecorpArticle article=new WecorpArticle();
                article.setId(articleId);
                article=self().selectByPrimaryKey(iRequest,article);
                if(article == null){
                    throw new Exception("没有选定的素材");
                }
                news_id_array.add(articleId);
                articleList.add(article);
            }
            JSONObject msg_json = materialService.createNewsMaterialByArticles(news_id_array);
            String account_media_id=null;
            if(!"0".equals(msg_json.getString("errcode"))){
                throw new Exception(msg_json.getString("errmsg"));
            }else{
                account_media_id = msg_json.getString("media_id");
            }
            WecorpMaterial material = new WecorpMaterial();
            material.setAccountNum(msg_json.getString("account_num"));
            material.setPurpose(Constant.MATERIAL_MASS_MESSAGE);      //群发消息
            material.setMaterialType(Constant.MATERIAL_TYPE_NEWS);    //图文
            material.setAccountMediaId(account_media_id);
            material.setAttachmentId(msg_json.get("cover_jsonArray").toString());
            material=materialService.insert(iRequest,material);
            if (material==null) {
                throw new Exception("Can not save material");
            }
            if("formal".equals(msg_json.getString("account_type"))){
                String send_json_string = ("{\"filter\":{ \"is_to_all\":true},\"mpnews\":{ \"media_id\":\""+account_media_id+"\" },\"msgtype\":\"mpnews\"}");
                JSONObject sned_json = JSONObject.fromObject(send_json_string);
                getJson = JSONObject.fromObject(iWxService.sendJson("https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token="+iWxService.connectForToken(msg_json.getString("appId")),sned_json));
                if("0".equals(getJson.getString("errcode"))){
                    logger.info(getJson.toString());
                    result=true;
                }else{
                    logger.info(getJson.toString());
                    result=false;
                }
            }
            if("test".equals(msg_json.getString("account_type"))){
                JSONObject openid_json = iWxService.getUserListAll(msg_json.getString("appId"));
                String send_json_string = ("{\"touser\":"+openid_json.getJSONObject("data").getJSONArray("openid").toString()+",\"mpnews\":{ \"media_id\":\""+account_media_id+"\" },\"msgtype\":\"mpnews\"}");
                JSONObject sned_json = JSONObject.fromObject(send_json_string);
                getJson =JSONObject.fromObject(iWxService.sendJson("https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=" + iWxService.connectForToken(msg_json.getString("appId")),sned_json));
                if("0".equals(getJson.getString("errcode"))){
                    logger.info(getJson.toString());
                    result=true;
                }else{
                    logger.info(getJson.toString());
                    result=false;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            result=false;
        }
        if(result){ //发送成功
            for(WecorpArticle article:articleList){
                article.setStatus(Constant.ARTICLE_SENT);
                self().updateByPrimaryKeySelective(iRequest,article);
            }
        }

        return result;
    }


}
