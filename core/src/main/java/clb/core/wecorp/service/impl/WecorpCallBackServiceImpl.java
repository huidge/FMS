package clb.core.wecorp.service.impl;

import clb.core.wecorp.dto.WecorpCustomReply;
import clb.core.wecorp.dto.WecorpResponseDTO;
import clb.core.wecorp.mapper.WecorpCustomReplyMapper;
import clb.core.wecorp.service.IWecorpAccountMenuService;
import clb.core.wecorp.service.IWecorpCallBackService;
import clb.core.wecorp.service.IWecorpReplyService;
import clb.core.wecorp.service.IWxService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/26.
 */
@Service
@Transactional
public class WecorpCallBackServiceImpl implements IWecorpCallBackService {

    @Autowired
    private IWxService wxService;
    @Autowired
    private IWecorpReplyService wecorpReplyService;
    @Autowired
    private IWecorpAccountMenuService wecorpAccountMenuService;
    @Autowired
    private WecorpCustomReplyMapper wecorpCustomReplyMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public void subscribeEvent(Map map, String appid) throws Exception {
        String openid = (String)map.get("FromUserName");
        String account_num = (String)map.get("ToUserName");
        String event_key = (String)map.get("EventKey");
        WecorpCustomReply wecorpCustomReply = wecorpCustomReplyMapper.findByAccountNumAndReplyType(account_num, "FOLLOW_REPLY");
        WecorpResponseDTO d=new WecorpResponseDTO();
        if(wecorpCustomReply.getMsgType().equals("text")){
            d.setContent(wecorpCustomReply.getContent());
            d.setType(wecorpCustomReply.getMsgType());
        }else{
            d.setMediaId(wecorpCustomReply.getContent());
            d.setType(wecorpCustomReply.getMsgType());
        }
        logger.info("关注返回信息：{}",wecorpCustomReply.getContent());
        this.sendMessageByType(openid,appid,d);
    }

    @Override
    public void menuEvent(Map map, String appid) throws Exception {
        String openid = (String)map.get("FromUserName");
        String account_num = (String)map.get("ToUserName");
        String event_key = (String)map.get("EventKey");
        WecorpResponseDTO dto=new WecorpResponseDTO();
        dto.setContent("测试阶段中。。。。");
        dto.setType("text");
        sendMessageByType(openid,appid,dto);
    }

    @Override
    public String keywordEvent(Map map, String appid) throws Exception {
        String openid = (String)map.get("FromUserName");
        String account_num = (String)map.get("ToUserName");
        String msg = (String) map.get("Content");
        JSONArray jsonArray=wecorpReplyService.getKeywordReplyContents(account_num, "KEYWORD_REPLY", msg);
        for(int i = 0;i < jsonArray.size();i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            WecorpResponseDTO o=new WecorpResponseDTO();
            o.setType(object.getString("type"));
            o.setMediaId(object.getString("mediaId"));
            o.setContent(object.getString("content"));
            this.sendMessageByType(openid,appid,o);
        }
        return null;
    }

    @Override
    public JSONObject sendMessageByType(String openid,String appid,WecorpResponseDTO o,String... kf) throws Exception {
        logger.info("发送消息sendMessageByType*****************");
        JSONObject sendText = new JSONObject();
        JSONObject json = new JSONObject();
        JSONObject rus = new JSONObject();
        if(kf.length!=0){
            JSONObject jsonKF=new JSONObject();
            jsonKF.put("kf_account",kf[0]);
            sendText.put("customservice",jsonKF);
        }
        if(o.getType().equals("text")) {
            sendText.put("touser",openid);
            sendText.put("msgtype","text");
            json.put("content", o.getContent());
            sendText.put("text",json);
            rus = JSONObject.fromObject(wxService.sendJson("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + wxService.connectForToken(appid), sendText));
        }else if(o.getType().equals("image")) {
            sendText.put("touser",openid);
            sendText.put("msgtype","image");
            json.put("media_id", o.getMediaId());
            sendText.put("image",json);
            rus = JSONObject.fromObject(wxService.sendJson("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + wxService.connectForToken(appid), sendText));
        }else if(o.getType().equals("mpnews")||o.getType().equals("news")) {
            sendText.put("touser",openid);
            sendText.put("msgtype","mpnews");
            json.put("media_id", o.getMediaId());
            sendText.put("mpnews",json);
            rus = JSONObject.fromObject(wxService.sendJson("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + wxService.connectForToken(appid), sendText));
        }else if(o.getType().equals("voice")) {
            sendText.put("touser",openid);
            sendText.put("msgtype","voice");
            json.put("media_id",  o.getMediaId());
            sendText.put("voice",json);
            rus = JSONObject.fromObject(wxService.sendJson("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + wxService.connectForToken(appid), sendText));
        }else if(o.getType().equals("video")) {
            sendText.put("touser",openid);
            sendText.put("msgtype","video");
            json.put("media_id",  o.getMediaId());
            sendText.put("video",json);
            rus = JSONObject.fromObject(wxService.sendJson("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + wxService.connectForToken(appid), sendText));
        }else{
            logger.error("****************未定义类型:{}",o.getType());
        }
        logger.info("返回：{}",rus.toString());
        return rus;
    }

//    public JSONObject sendMenuMessage(String openid,String appid,String msgType,JSONObject jsonObject) throws Exception {
//        JSONObject sendText = new JSONObject();
//        JSONObject json = new JSONObject();
//        JSONObject rus = new JSONObject();
//        if(msgType.equals("text")) {
//            sendText.put("touser",openid);
//            sendText.put("msgtype","text");
//            json.put("content", jsonObject.getString("content"));
//            sendText.put("text",json);
//            rus = JSONObject.fromObject(wxService.sendJson("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + wxService.connectForToken(appid), sendText));
//        }
//        else if(msgType.equals("image")) {
//            sendText.put("touser",openid);
//            sendText.put("msgtype","image");
//            json.put("media_id", jsonObject.getString("account_media_id"));
//            sendText.put("image",json);
//            rus = JSONObject.fromObject(wxService.sendJson("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + wxService.connectForToken(appid), sendText));
//
//        }else if(msgType.equals("news")) {
//            sendText.put("touser",openid);
//            sendText.put("msgtype","mpnews");
//            json.put("media_id", jsonObject.getString("account_media_id"));
//            sendText.put("mpnews",json);
//            rus = JSONObject.fromObject(wxService.sendJson("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + wxService.connectForToken(appid), sendText));
//        }
//        return rus;
//    }

//    public JSONObject sendMessage(String openid,String appid,String msgType,JSONObject jsonObject) throws Exception {
//        JSONObject sendText = new JSONObject();
//        JSONObject json = new JSONObject();
//        JSONObject rus = new JSONObject();
//        if(msgType.equals("text")) {
//            sendText.put("touser",openid);
//            sendText.put("msgtype","text");
//            json.put("content", jsonObject.getString("content"));
//            sendText.put("text",json);
//            rus = JSONObject.fromObject(wxService.sendJson("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + wxService.connectForToken(appid), sendText));
//        }
//        else if(msgType.equals("image")) {
//            sendText.put("touser",openid);
//            sendText.put("msgtype","image");
//            json.put("media_id", jsonObject.getString("account_media_id"));
//            sendText.put("image",json);
//            rus = JSONObject.fromObject(wxService.sendJson("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + wxService.connectForToken(appid), sendText));
//
//        }else if(msgType.equals("news")) {
//            sendText.put("touser",openid);
//            sendText.put("msgtype","mpnews");
//            json.put("media_id", jsonObject.getString("account_media_id"));
//            sendText.put("mpnews",json);
//            rus = JSONObject.fromObject(wxService.sendJson("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + wxService.connectForToken(appid), sendText));
//
//        }else if(msgType.equals("custom_news")) {
//            sendText.put("touser",openid);
//            sendText.put("msgtype","news");
//            JSONObject article = new JSONObject();
//            JSONObject news = new JSONObject();
//            article.put("title",jsonObject.getString("title"));
//            article.put("description",jsonObject.getString("description"));
//            article.put("url",jsonObject.getString("url"));
//            article.put("picurl", jsonObject.getString("cover_url"));
//            JSONArray articles = new JSONArray();
//            articles.add(article);
//            news.put("articles", articles);
//            sendText.put("news", news);
//            rus = JSONObject.fromObject(wxService.sendJson("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + wxService.connectForToken(appid), sendText));
//
//        }
//        return rus;
//    }
}
