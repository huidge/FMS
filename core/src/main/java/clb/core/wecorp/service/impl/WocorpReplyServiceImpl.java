package clb.core.wecorp.service.impl;


import clb.core.wecorp.dto.*;
import clb.core.wecorp.mapper.*;
import clb.core.wecorp.service.*;
import clb.core.wecorp.utils.Constant;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by lp on 2016/11/4.
 */
@Service
@Transactional
public class WocorpReplyServiceImpl extends BaseServiceImpl<WecorpCustomReply> implements IWecorpReplyService {

    @Autowired
    private IWxService iWxService;
    @Autowired
    private IWecorpMaterialService wecorpMaterialService;
    @Autowired
    private IWecorpArticleService wecorpArticleService;
    @Autowired
    private IWecorpKeyRuleService wecorpKeyRuleService;
    @Autowired
    private IWecorpKeywordService wecorpKeywordService;
    @Autowired
    private WecorpCustomReplyMapper wecorpCustomReplyMapper;
    @Autowired
    private WecorpMaterialMapper wecorpMaterialMapper;
    @Autowired
    private WecorpKeyRuleMapper wecorpKeyRuleMapper;
    @Autowired
    private WecorpArticleMapper wecorpArticleMapper;
    @Autowired
    private WecorpAttachmentMapper wecorpAttachmentMapper;

    @Override
    public JSONObject uploadReply(String url, String appId) {
        String media_id;
        JSONObject thumb_media_id_json;
        try {
            String pathUrl[] = url.split("/");
            URL postUrl = null;
            postUrl = new URL(iWxService.encode(url));
            HttpURLConnection connection=null;
            connection = (HttpURLConnection) postUrl.openConnection();
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("contentType", "utf-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            InputStream is =connection.getInputStream();
            thumb_media_id_json = JSONObject.fromObject(iWxService.uploadAlwaysFile(pathUrl[pathUrl.length - 1], "image", is, appId));
//            System.out.println("上传图片后返回的是"+thumb_media_id_json.toString());
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return thumb_media_id_json;
    }

    @Override
    public String saveReplyMsg(String reply_type, JSONObject content, String account_num, String msg_type) throws Exception {

        String contentStr = content.getString("content");
        String appid = iWxService.getAppId(account_num);
        WecorpCustomReply wecorpCustomReply = wecorpCustomReplyMapper.findByAccountNumAndReplyType(account_num, reply_type);
        if("image".equals(msg_type)) {
            String attachment_id = content.getString("attachment_id");
            WecorpMaterial wecorpMaterial = wecorpMaterialMapper.findMaterial(attachment_id, "CUSTOM_REPLY", account_num, "Y");
            if(wecorpMaterial!=null){
                content.put("account_media_id",wecorpMaterial.getAccountMediaId());
                content.put("material_id",wecorpMaterial.getId());
                contentStr = content.toString();
            }else {
                String url = content.getString("store_path");
                JSONObject json = self().uploadReply(url,appid);

                WecorpMaterial material = new WecorpMaterial();
                material.setAccountMediaId(json.getString("media_id"));
                material.setAccountMediaUrl(json.getString("url"));
                material.setPurpose("CUSTOM_REPLY");
                material.setMaterialType("image");
                material.setAttachmentId(attachment_id);
                IRequest iRequest= RequestHelper.newEmptyRequest();
                material = wecorpMaterialService.insert(iRequest, material);
                if (material==null) {
                    throw new Exception("Can not save material");
                }
                content.put("account_media_id", json.getString("media_id"));
                content.put("material_id", material.getId());
                contentStr = content.toString();
            }
        }
        if("news".equals(msg_type)) {
            List news_id_array = (List)content.get("news_id_array");
            if(news_id_array.size() == 0){
                return null;
            }
            JSONObject msg_json = wecorpMaterialService.createNewsMaterialByArticles(news_id_array);
            String account_media_id=null;
            if(!"0".equals(msg_json.getString("errcode"))){
                throw new Exception(msg_json.getString("errmsg"));
            }else{
                account_media_id = msg_json.getString("media_id");
            }
            WecorpMaterial material = new WecorpMaterial();
            material.setAccountNum(msg_json.getString("account_num"));
            material.setPurpose("CUSTOM_REPLY");      //群发消息
            material.setMaterialType("news");    //图文
            material.setAccountMediaId(account_media_id);
            material.setAttachmentId(msg_json.get("cover_jsonArray").toString());
            IRequest iRequest= RequestHelper.newEmptyRequest();
            material=wecorpMaterialService.insert(iRequest,material);
            if (material==null) {
                throw new Exception("Can not save material");
            }
            content.put("account_media_id", account_media_id);
            content.put("material_id",material.getId());
            contentStr = content.toString();
        }
        if("custom_news".equals(msg_type)) {
            String url = content.getString("store_path");
            String pathUrl[] = url.split("/");
            JSONObject reJson = JSONObject.fromObject(iWxService.uploadNewsImg(appid, pathUrl[pathUrl.length - 1], "image", url));
            if(reJson.get("url")==null){
                throw new Exception(reJson.getString("errcode")+" "+reJson.getString("errmsg")+" 正文图片上传失败");
            }else {
                content.put("cover_url", reJson.get("url"));
            }
            contentStr = content.toString();
        }
        if(wecorpCustomReply!= null) {
            wecorpCustomReply.setContent(contentStr);
            wecorpCustomReply.setMsgType(msg_type);
            int result = wecorpCustomReplyMapper.updateByPrimaryKeySelective(wecorpCustomReply);
//            if(result <= 0) {
//
//            }
        }else {
            WecorpCustomReply reply = new WecorpCustomReply();
            reply.setAccountNum(account_num);
            reply.setMsgType(msg_type);
            reply.setContent(contentStr);
            reply.setReplyType(reply_type);
            int result = wecorpCustomReplyMapper.insertSelective(reply);
            if(result <= 0) {

            }
        }
        return "";
    }

    @Override
    public List getContent(String account_num, String reply_type) {
        JSONArray content_jsonArray = new JSONArray();
        WecorpCustomReply wecorpCustomReply = wecorpCustomReplyMapper.findByAccountNumAndReplyType(account_num, reply_type);
        if(wecorpCustomReply==null){
            return null;
        }
        JSONObject jsonObject = JSONObject.fromObject(wecorpCustomReply.getContent());
        if("news".equals(wecorpCustomReply.getMsgType())) {
            JSONArray news_array = new JSONArray();
            List<String> articleIds = (List)jsonObject.get("news_id_array");
            for(int i = 0;i < articleIds.size();i++) {
                JSONObject news_json = new JSONObject();
                WecorpArticle article = new WecorpArticle();
                article.setId(articleIds.get(i));
                article = wecorpArticleMapper.selectOne(article);
                news_json.put("title",article.getTitle());
                news_json.put("digest",article.getDigest());
                WecorpAttachment attachment = new WecorpAttachment();
                attachment.setId(article.getCover());
                news_json.put("cover", wecorpAttachmentMapper.selectOne(attachment).getStorePath());
                news_array.add(news_json);
            }

            jsonObject.put("news", news_array);
            content_jsonArray.add(jsonObject);
        }
        return content_jsonArray;
    }

    @Override
    public String saveKeywordRuleProcess(JSONObject content, String reply_type) throws Exception {
        String account_num = content.getString("account_num");
        String appid = iWxService.getAppId(account_num);
        IRequest iRequest= RequestHelper.newEmptyRequest();
        try{
            WecorpKeyRule wecorpKeyRule = new WecorpKeyRule();
            String rule_id = content.getString("rule_id");
            if(rule_id == ""||rule_id == null){
                wecorpKeyRule.setRuleName(content.getString("rule_name"));
                wecorpKeyRule.setReplyAllFlag(content.getString("reply_all_flag"));
                wecorpKeyRule.setContent(content.getString("reply_msg"));
            }else {
                wecorpKeyRule.setRuleId(rule_id);
                wecorpKeyRule = wecorpKeyRuleMapper.selectOne(wecorpKeyRule);
                if(wecorpKeyRule != null) {
                    JSONArray jsonArray = JSONArray.fromObject(wecorpKeyRule.getContent());
                    for(int index = 0;index < jsonArray.size();index++) {
                    }
                }
                wecorpKeyRule.setRuleName(content.getString("rule_name"));
                wecorpKeyRule.setReplyAllFlag(content.getString("reply_all_flag"));
                wecorpKeyRule.setContent(content.getString("reply_msg"));
            }
            JSONArray reply_msg_jsonarray = JSONArray.fromObject(content.getString("reply_msg"));
            JSONArray content_jsonarray = new JSONArray();
            for(int index = 0;index < reply_msg_jsonarray.size();index++) {
                JSONObject reply_msg_json = JSONObject.fromObject(reply_msg_jsonarray.get(index));
                String msg_type = reply_msg_json.getString("msg_type");
                if("image".equals(msg_type)) {
                    String attachment_id = reply_msg_json.getString("content");
                    WecorpMaterial wecorpMaterial = wecorpMaterialMapper.findMaterial(attachment_id, "CUSTOM_REPLY", account_num, "Y");
                    if(wecorpMaterial!=null){
                        reply_msg_json.put("account_media_id",wecorpMaterial.getAccountMediaId());
                        reply_msg_json.put("material_id",wecorpMaterial.getId());
                        content_jsonarray.add(reply_msg_json);
                    }else {
                        String url = content.getString("store_path");
                        JSONObject json = self().uploadReply(url,appid);

                        WecorpMaterial material = new WecorpMaterial();
                        material.setAccountMediaId(json.getString("media_id"));
                        material.setAccountMediaUrl(json.getString("url"));
                        material.setPurpose("CUSTOM_REPLY");
                        material.setMaterialType("image");
                        material.setAttachmentId(attachment_id);

                        material = wecorpMaterialService.insert(iRequest, material);
                        if (material==null) {
//                throw new Exception("Can not save material");
                        }
                        reply_msg_json.put("account_media_id", json.getString("media_id"));
                        reply_msg_json.put("material_id", material.getId());
                        content_jsonarray.add(reply_msg_json);

                    }
                }
                if("news".equals(msg_type)) {
                    String articleId=reply_msg_json.getString("content");
                    String isGroup=reply_msg_json.getString("isGroup");
                    List<String> news_id_array =  new ArrayList<String>();
                    if("true".equals(isGroup)){  //图文组
                        List<WecorpArticle> list = wecorpArticleMapper.getArticleByGroupNumber(articleId);
                        for(WecorpArticle article:list){
                            news_id_array.add(article.getId());
                        }
                        if(news_id_array.size() == 0){
//                        throw new Exception("没有选定的素材");
                        }
                    }else{ //单图文
                        WecorpArticle article=new WecorpArticle();
                        article.setId(articleId);
                        article=wecorpArticleService.selectByPrimaryKey(iRequest,article);
                        if(article == null){
//                        throw new Exception("没有选定的素材");
                        }
                        news_id_array.add(articleId);
                    }
                    if(news_id_array.size() == 0){
                        return null;
                    }
                    reply_msg_json.put("news_id_array",news_id_array);
                    JSONObject msg_json = wecorpMaterialService.createNewsMaterialByArticles(news_id_array);
                    String account_media_id=null;
                    if(!"0".equals(msg_json.getString("errcode"))){
//                throw new Exception(msg_json.getString("errmsg"));
                    }else{
                        account_media_id = msg_json.getString("media_id");
                    }
                    WecorpMaterial material = new WecorpMaterial();
                    material.setAccountNum(msg_json.getString("account_num"));
                    material.setPurpose("CUSTOM_REPLY");      //群发消息
                    material.setMaterialType("news");    //图文
                    material.setAccountMediaId(account_media_id);
                    material.setAttachmentId(msg_json.get("cover_jsonArray").toString());
                    material=wecorpMaterialService.insert(iRequest,material);
                    if (material==null) {
//                throw new Exception("Can not save material");
                    }
                    reply_msg_json.put("account_media_id", account_media_id);
                    reply_msg_json.put("material_id", material.getId());
                    content_jsonarray.add(reply_msg_json);
                }
                if("text".equals(msg_type)){
                    JSONObject json = new JSONObject();
                    json.put("content",reply_msg_json.getString("content"));
                    json.put("msg_type",reply_msg_json.getString("msg_type"));
                    json.put("value",reply_msg_json.getString("value"));
                    content_jsonarray.add(json);
                }
                if("'custom_news'".equals(msg_type)){
                    String url = reply_msg_json.getString("store_path");
                    String pathUrl[] = url.split("/");
                    JSONObject reJson = JSONObject.fromObject(iWxService.uploadNewsImg(appid, pathUrl[pathUrl.length - 1], "image", url));
                    if(reJson.get("url")==null){
//                throw new Exception(reJson.getString("errcode")+" "+reJson.getString("errmsg")+" 正文图片上传失败");
                    }else {
                        reply_msg_json.put("cover_url", reJson.get("url"));
                    }
                    content_jsonarray.add(reply_msg_json);
                }
            }
            wecorpKeyRule.setContent(content_jsonarray.toString());
            if(rule_id == ""||rule_id == null) {
                wecorpKeyRule = wecorpKeyRuleService.insertSelective(iRequest,wecorpKeyRule);
            }else {
                wecorpKeyRule = wecorpKeyRuleService.updateByPrimaryKeySelective(iRequest,wecorpKeyRule);
            }

            JSONArray key_word = JSONArray.fromObject(content.get("key_word"));
            for (int m=0;m<key_word.size();m++){
                JSONObject keyJson = key_word.getJSONObject(m);
                WecorpKeyword bgKeyword = new WecorpKeyword();
                bgKeyword.setKeywordValue(keyJson.getString("keyword_value"));
                bgKeyword.setMatchingFlag(keyJson.getString("matching_flag"));
                bgKeyword.setRuleId(wecorpKeyRule.getRuleId());
                wecorpKeywordService.insertSelective(iRequest,bgKeyword);
            }
            JSONArray contentJsonArray = new JSONArray();
            WecorpCustomReply wecorpCustomReply = wecorpCustomReplyMapper.findByAccountNumAndReplyType(account_num,reply_type);
            if(wecorpCustomReply != null) {
                contentJsonArray = JSONArray.fromObject(wecorpCustomReply.getContent());
                if(!contentJsonArray.contains(wecorpKeyRule.getRuleId())) {
                    contentJsonArray.add(wecorpKeyRule.getRuleId());
                    wecorpCustomReply.setContent(contentJsonArray.toString());
                    wecorpCustomReplyMapper.updateByPrimaryKey(wecorpCustomReply);
                }
            }else {
                wecorpCustomReply = new WecorpCustomReply();
                wecorpCustomReply.setAccountNum(account_num);
                wecorpCustomReply.setMsgType(reply_type);
                contentJsonArray.add(wecorpKeyRule.getRuleId());
                wecorpCustomReply.setContent(contentJsonArray.toString());
                wecorpCustomReply.setReplyType(reply_type);
                wecorpCustomReplyMapper.insert(wecorpCustomReply);
            }

        }catch (Exception e) {

        }
        return null;
    }

    @Override
    public List getKeywordRule(String account_num, String reply_type) {
        IRequest iRequest= RequestHelper.newEmptyRequest();
        WecorpCustomReply wecorpCustomReply = wecorpCustomReplyMapper.findByAccountNumAndReplyType(account_num, reply_type);
        if(wecorpCustomReply==null){
            return null;
        }
        JSONArray rule_array = JSONArray.fromObject(wecorpCustomReply.getContent());
        JSONArray re_rule_array = new JSONArray();
        for(int index = 0;index < rule_array.size();index++) {
            JSONObject keywordRule_json = new JSONObject();
            WecorpKeyRule wecorpKeyRule = new WecorpKeyRule();
            wecorpKeyRule.setRuleId(rule_array.getString(index));
            wecorpKeyRule = wecorpKeyRuleMapper.selectOne(wecorpKeyRule);
            keywordRule_json.put("rule_id", wecorpKeyRule.getRuleId());
            keywordRule_json.put("rule_name", wecorpKeyRule.getRuleName());
            keywordRule_json.put("reply_all_flag",wecorpKeyRule.getReplyAllFlag());
            JSONArray content_jsonArray = JSONArray.fromObject(wecorpKeyRule.getContent());
            for(int i = 0;i < content_jsonArray.size();i++) {
                JSONObject content_json = content_jsonArray.getJSONObject(i);
                if("news".equals(content_json.getString("msg_type"))) {
                    JSONArray news_array = new JSONArray();
                    String articleId=content_json.getString("content");
                    String isGroup=content_json.getString("isGroup");
                    if("true".equals(isGroup)){  //图文组
                        JSONObject news_json = new JSONObject();
                        List<WecorpArticle> list = wecorpArticleMapper.getArticleByGroupNumber(articleId);
                        for(WecorpArticle article:list){
                            news_json.put("isGroup",isGroup);
                            news_json.put("articleId",articleId);
                            news_json.put("title",article.getTitle());
                            news_json.put("digest",article.getDigest());
                            WecorpAttachment attachment = new WecorpAttachment();
                            attachment.setId(article.getCover());
                            news_json.put("cover", wecorpAttachmentMapper.selectOne(attachment).getStorePath());
                            news_array.add(news_json);
                        }

                    }else{ //单图文
                        JSONObject news_json = new JSONObject();
                        WecorpArticle article=new WecorpArticle();
                        article.setId(articleId);
                        article=wecorpArticleService.selectByPrimaryKey(iRequest,article);
                        news_json.put("isGroup",isGroup);
                        news_json.put("articleId",articleId);
                        news_json.put("title",article.getTitle());
                        news_json.put("digest", article.getDigest());
                        WecorpAttachment attachment = new WecorpAttachment();
                        attachment.setId(article.getCover());
                        news_json.put("cover", wecorpAttachmentMapper.selectOne(attachment).getStorePath());
                        news_array.add(news_json);
                    }
                    content_json.put("news",news_array);
                    content_jsonArray.add(i,content_json);
                }
            }
            keywordRule_json.put("content",content_jsonArray);
            List<WecorpKeyword> keywords = wecorpKeywordService.getKeywordByRuleId(wecorpKeyRule.getRuleId());
            JSONArray keyword_array = new JSONArray();
            for(int keyword_index = 0;keyword_index < keywords.size();keyword_index++) {
                JSONObject woaAccountCrKeyword_json = new JSONObject();
                woaAccountCrKeyword_json.put("keyword_id",keywords.get(keyword_index).getKeywordId());
                woaAccountCrKeyword_json.put("value",keywords.get(keyword_index).getKeywordValue());
                woaAccountCrKeyword_json.put("matching_flag",keywords.get(keyword_index).getMatchingFlag());
                keyword_array.add(woaAccountCrKeyword_json);
            }
            keywordRule_json.put("keyword",keyword_array);
            re_rule_array.add(keywordRule_json);
        }
        return re_rule_array;
    }

    @Override
    public WecorpCustomReply findByAccountNumAndReplyType(String accountNum, String replyType) {
        return wecorpCustomReplyMapper.findByAccountNumAndReplyType(accountNum,replyType);
    }

    @Override
    public JSONArray getKeywordReplyContents(String account_num,String reply_type,String msg) {
        JSONArray reply_jsonarray = new JSONArray();
        WecorpCustomReply wecorpCustomReply = this.findByAccountNumAndReplyType(account_num,reply_type);
        if(wecorpCustomReply==null) {
            return reply_jsonarray;

        }
        JSONArray content_jsonarray = JSONArray.fromObject(wecorpCustomReply.getContent());
        for(int i = 0;i < content_jsonarray.size();i++) {
            List<WecorpKeyword> wecorpKeywordList = wecorpKeywordService.getKeywordByRuleId(content_jsonarray.get(i).toString());
            if(wecorpKeywordList.size() > 0) {
                Boolean need_reply = false;
                for(int j = 0;j < wecorpKeywordList.size();j++) {
                    WecorpKeyword keyword = wecorpKeywordList.get(j);
                    if(keyword.getMatchingFlag().equals("Y") && keyword.getKeywordValue().equals(msg)) {
                        need_reply = true;
                        break;
                    }
                    if(keyword.getMatchingFlag().equals("N") &&msg.indexOf(keyword.getKeywordValue())!=-1) {
                        need_reply = true;
                        break;
                    }
                }
                if(need_reply) {
                    WecorpKeyRule wecorpKeyRule = new WecorpKeyRule();
                    wecorpKeyRule.setRuleId(content_jsonarray.get(i).toString());
                    wecorpKeyRule = wecorpKeyRuleMapper.selectOne(wecorpKeyRule);
                    if(wecorpKeyRule != null) {
                        JSONArray rule_content_jsonarray = JSONArray.fromObject(wecorpKeyRule.getContent());
                        if(wecorpKeyRule.getReplyAllFlag().equals("Y")) {
                            reply_jsonarray.addAll(rule_content_jsonarray);
                        }else {
                            Random rand = new Random();
                            reply_jsonarray.add(rule_content_jsonarray.get(rand.nextInt(rule_content_jsonarray.size())));
                        }
                    }
                }
            }
        }
        return reply_jsonarray;
    }
}
