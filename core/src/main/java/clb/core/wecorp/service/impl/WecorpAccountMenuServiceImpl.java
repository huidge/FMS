package clb.core.wecorp.service.impl;

import clb.core.wecorp.dto.HmsWxResponseDto;
import clb.core.wecorp.dto.WecorpAccountMenu;
import clb.core.wecorp.dto.WecorpArticle;
import clb.core.wecorp.dto.WecorpMaterial;
import clb.core.wecorp.mapper.*;
import clb.core.wecorp.service.*;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/6/19.
 */
@Service
@Transactional
public class WecorpAccountMenuServiceImpl extends BaseServiceImpl<WecorpAccountMenu> implements IWecorpAccountMenuService {
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
    private IWecorpReplyService wecorpReplyService;
    @Autowired
    private IWecorpServerService wecorpServerService;
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
    @Autowired
    private WecorpAccountMenuMapper wecorpAccountMenuMapper;

    @Override
    public WecorpAccountMenu findByAccountNum(String accountNum) {
        return wecorpAccountMenuMapper.findByAccountNum(accountNum);
    }

    @Override
    public HmsWxResponseDto createMenu(WecorpAccountMenu wecorpAccountMenu) throws Exception {
        String accountNum = wecorpAccountMenu.getAccountNum();
        String appid = iWxService.getAppId(accountNum);
        HmsWxResponseDto response = wecorpServerService.createMenu(wecorpAccountMenu.getContent(), appid);
//        if ("0".equals(response.getErrcode())) {
//            WecorpAccountMenu res = wecorpAccountMenuMapper.findByAccountNum(wecorpAccountMenu.getAccountNum());
//            if (res != null) {
//                res.setContent(wecorpAccountMenu.getContent());
//                wecorpAccountMenuMapper.updateByPrimaryKeySelective(res);
//            } else {
//                wecorpAccountMenu.setAccountMenuId(UUID.randomUUID().toString());
//                wecorpAccountMenuMapper.insertSelective(wecorpAccountMenu);
//            }
//        }
        return response;
    }

    @Override
    public HmsWxResponseDto createMenuNew(WecorpAccountMenu wecorpAccountMenu) throws Exception {
        String accountNum = wecorpAccountMenu.getAccountNum();
        String appid = iWxService.getAppId(accountNum);
        HmsWxResponseDto response=new HmsWxResponseDto();
        if(appid==null){
            response.setErrmsg("appid为空，请在对应微信号添加appid");
            response.setErrcode("-100");
        }else {
            response = wecorpServerService.createMenuNew(wecorpAccountMenu.getContent(), appid);
        }
        return response;
    }

//        IRequest iRequest= RequestHelper.newEmptyRequest();
//        JSONArray buttons_jsonarray = JSONArray.fromObject(content.get("button"));
//        JSONObject rebutton_json = new JSONObject();
//        JSONArray rebuttons_jsonarray = new JSONArray();
//        try {
//            for(int i = 0;i < buttons_jsonarray.size();i++) {
//                JSONObject button_json = JSONObject.fromObject(buttons_jsonarray.get(i));
//                if(button_json.get("sub_button")!=null) {
//                    JSONArray sub_button_jsonarray = JSONArray.fromObject(button_json.get("sub_button"));
//                    JSONArray resub_button_jsonarray = new JSONArray();
//                    for(int j = 0;j < sub_button_jsonarray.size();j++) {
//                        JSONObject sub_button_json = JSONObject.fromObject(sub_button_jsonarray.get(j));
//                        JSONObject resub_button_json = new JSONObject();
//                        String type = sub_button_json.getString("type");
//                        if("image".equals(type)) {
//                            String attachment_id = sub_button_json.getString("content");
//                            WecorpMaterial wecorpMaterial = wecorpMaterialMapper.findMaterial(attachment_id, "CUSTOM_MENU", accountNum, "Y");
//                            if(wecorpMaterial!=null){
//                                sub_button_json.put("account_media_id",wecorpMaterial.getAccountMediaId());
//                                sub_button_json.put("material_id",wecorpMaterial.getId());
//                                sub_button_jsonarray.remove(j);
//                                sub_button_jsonarray.add(j,sub_button_json);
//                            }else {
//                                String url = sub_button_json.getString("store_path");
//                                JSONObject json = wecorpReplyService.uploadReply(url, appid);
//
//                                WecorpMaterial material = new WecorpMaterial();
//                                material.setAccountNum(accountNum);
//                                material.setAccountMediaId(json.getString("media_id"));
//                                material.setAccountMediaUrl(json.getString("url"));
//                                material.setPurpose("CUSTOM_MENU");
//                                material.setMaterialType("image");
//                                material.setAttachmentId(attachment_id);
//
//                                material = wecorpMaterialService.insertSelective(iRequest, material);
//                                if (material==null) {
////                throw new Exception("Can not save material");
//                                }
//                                sub_button_json.put("account_media_id", json.getString("media_id"));
//                                sub_button_json.put("material_id", material.getId());
//                                sub_button_jsonarray.remove(j);
//                                sub_button_jsonarray.add(j,sub_button_json);
//
//                            }
//                            resub_button_json.put("type","click");
//                            resub_button_json.put("name",sub_button_json.getString("name"));
//                            resub_button_json.put("key", i + "," + j);
//                        }
//                        if("news".equals(type)) {
//                            String articleId=sub_button_json.getString("content");
//                            String isGroup=sub_button_json.getString("isGroup");
//                            List<String> news_id_array =  new ArrayList<String>();
//                            if("true".equals(isGroup)){  //图文组
//                                List<WecorpArticle> list = wecorpArticleMapper.getArticleByGroupNumber(articleId);
//                                for(WecorpArticle article:list){
//                                    news_id_array.add(article.getId());
//                                }
//                                if(news_id_array.size() == 0){
////                        throw new Exception("没有选定的素材");
//                                }
//                            }else{ //单图文
//                                WecorpArticle article=new WecorpArticle();
//                                article.setId(articleId);
//                                article=wecorpArticleService.selectByPrimaryKey(iRequest,article);
//                                if(article == null){
////                        throw new Exception("没有选定的素材");
//                                }
//                                news_id_array.add(articleId);
//                            }
//                            if(news_id_array.size() == 0){
//                                return null;
//                            }
//                            sub_button_json.put("news_id_array",news_id_array);
//                            JSONObject msg_json = wecorpMaterialService.createNewsMaterialByArticles(news_id_array);
//                            String account_media_id=null;
//                            if(!"0".equals(msg_json.getString("errcode"))){
////                throw new Exception(msg_json.getString("errmsg"));
//                            }else{
//                                account_media_id = msg_json.getString("media_id");
//                            }
//                            WecorpMaterial material = new WecorpMaterial();
//                            material.setAccountNum(msg_json.getString("account_num"));
//                            material.setPurpose("CUSTOM_MENU");      //群发消息
//                            material.setMaterialType("news");    //图文
//                            material.setAccountMediaId(account_media_id);
//                            material.setAttachmentId(msg_json.get("cover_jsonArray").toString());
//                            material=wecorpMaterialService.insert(iRequest,material);
//                            if (material==null) {
////                throw new Exception("Can not save material");
//                            }
//                            sub_button_json.put("account_media_id", account_media_id);
//                            sub_button_json.put("material_id", material.getId());
//                            sub_button_jsonarray.remove(j);
//                            sub_button_jsonarray.add(j,sub_button_json);
//
//
//                            resub_button_json.put("type","click");
//                            resub_button_json.put("name",sub_button_json.getString("name"));
//                            resub_button_json.put("key", i + "," + j);
//                        }
//                        if("text".equals(type)){
//                            resub_button_json.put("type","click");
//                            resub_button_json.put("name",sub_button_json.getString("name"));
//                            resub_button_json.put("key", i + "," + j);
//                        }
//                        if("view".equals(type)){
//                            resub_button_json = sub_button_json;
//                        }
//                        if("scancode_push".equals(type)){
//                            sub_button_json.put("key","scancode_push");
//                            resub_button_json = sub_button_json;
//                        }
//                        resub_button_jsonarray.add(resub_button_json);
//                    }
//                    button_json.put("sub_button",sub_button_jsonarray);
//                    buttons_jsonarray.remove(i);
//                    buttons_jsonarray.add(i, button_json);
//
//                    rebutton_json.put("sub_button",resub_button_jsonarray);
//                    rebutton_json.put("name",button_json.getString("name"));
//                }else {
//                    JSONObject sub_button_json = button_json;
//                    JSONObject resub_button_json = new JSONObject();
//                    String type = sub_button_json.getString("type");
//                    if("image".equals(type)) {
//                        String attachment_id = sub_button_json.getString("content");
//                        WecorpMaterial wecorpMaterial = wecorpMaterialMapper.findMaterial(attachment_id, "CUSTOM_MENU", accountNum, "Y");
//                        if(wecorpMaterial!=null){
//                            sub_button_json.put("account_media_id",wecorpMaterial.getAccountMediaId());
//                            sub_button_json.put("material_id",wecorpMaterial.getId());
//                            buttons_jsonarray.remove(i);
//                            buttons_jsonarray.add(i,sub_button_json);
//                        }else {
//                            String url = content.getString("store_path");
//                            JSONObject json = wecorpReplyService.uploadReply(url, appid);
//
//                            WecorpMaterial material = new WecorpMaterial();
//                            material.setAccountNum(accountNum);
//                            material.setAccountMediaId(json.getString("media_id"));
//                            material.setAccountMediaUrl(json.getString("url"));
//                            material.setPurpose("CUSTOM_MENU");
//                            material.setMaterialType("image");
//                            material.setAttachmentId(attachment_id);
//
//                            material = wecorpMaterialService.insert(iRequest, material);
//                            if (material==null) {
////                throw new Exception("Can not save material");
//                            }
//                            sub_button_json.put("account_media_id", json.getString("media_id"));
//                            sub_button_json.put("material_id", material.getId());
//                            buttons_jsonarray.remove(i);
//                            buttons_jsonarray.add(i,sub_button_json);
//
//                        }
//                        resub_button_json.put("type","click");
//                        resub_button_json.put("name",sub_button_json.getString("name"));
//                        resub_button_json.put("key", i);
//                    }
//                    if("news".equals(type)) {
//                        String articleId=sub_button_json.getString("content");
//                        String isGroup=sub_button_json.getString("isGroup");
//                        List<String> news_id_array =  new ArrayList<String>();
//                        if("true".equals(isGroup)){  //图文组
//                            List<WecorpArticle> list = wecorpArticleMapper.getArticleByGroupNumber(articleId);
//                            for(WecorpArticle article:list){
//                                news_id_array.add(article.getId());
//                            }
//                            if(news_id_array.size() == 0){
////                        throw new Exception("没有选定的素材");
//                            }
//                        }else{ //单图文
//                            WecorpArticle article=new WecorpArticle();
//                            article.setId(articleId);
//                            article=wecorpArticleService.selectByPrimaryKey(iRequest,article);
//                            if(article == null){
////                        throw new Exception("没有选定的素材");
//                            }
//                            news_id_array.add(articleId);
//                        }
//                        if(news_id_array.size() == 0){
//                            return null;
//                        }
//                        sub_button_json.put("news_id_array",news_id_array);
//                        JSONObject msg_json = wecorpMaterialService.createNewsMaterialByArticles(news_id_array);
//                        String account_media_id=null;
//                        if(!"0".equals(msg_json.getString("errcode"))){
////                throw new Exception(msg_json.getString("errmsg"));
//                        }else{
//                            account_media_id = msg_json.getString("media_id");
//                        }
//                        WecorpMaterial material = new WecorpMaterial();
//                        material.setAccountNum(msg_json.getString("account_num"));
//                        material.setPurpose("CUSTOM_MENU");      //群发消息
//                        material.setMaterialType("news");    //图文
//                        material.setAccountMediaId(account_media_id);
//                        material.setAttachmentId(msg_json.get("cover_jsonArray").toString());
//                        material=wecorpMaterialService.insert(iRequest,material);
//                        if (material==null) {
////                throw new Exception("Can not save material");
//                        }
//                        sub_button_json.put("account_media_id", account_media_id);
//                        sub_button_json.put("material_id", material.getId());
//                        buttons_jsonarray.remove(i);
//                        buttons_jsonarray.add(i,sub_button_json);
//
//
//                        resub_button_json.put("type","click");
//                        resub_button_json.put("name",sub_button_json.getString("name"));
//                        resub_button_json.put("key", i);
//                    }
//                    if("text".equals(type)){
//                        resub_button_json.put("type","click");
//                        resub_button_json.put("name",sub_button_json.getString("name"));
//                        resub_button_json.put("key", i);
//                    }
//                    if("view".equals(type)){
//                        resub_button_json = sub_button_json;
//                    }
//                    if("scancode_push".equals(type)){
//                        sub_button_json.put("key","scancode_push");
//                        resub_button_json = sub_button_json;
//                    }
//                    rebutton_json = resub_button_json;
//                }
//                rebuttons_jsonarray.add(rebutton_json);
//            }
//            JSONObject rejson = new JSONObject();
//            rejson.put("button", rebuttons_jsonarray);
//            System.out.println(rejson.toString());
//            HmsWxResponseDto response = wecorpServerService.createMenu(rejson.toString(), appid);
//            if(Integer.parseInt(response.getErrcode()) == 0) {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("button", buttons_jsonarray);
//                WecorpAccountMenu accountMenu = self().selectByPrimaryKey(iRequest, wecorpAccountMenu);
//                wecorpAccountMenu.setContent(jsonObject.toString().replace("\\\\","\\"));
//                if(accountMenu != null) {
//                    self().updateByPrimaryKeySelective(iRequest, wecorpAccountMenu);
//                }else {
//                    self().insertSelective(iRequest, wecorpAccountMenu);
//                }
//            }
//        }catch (Exception e) {
//
//        }
//        return null;

}
