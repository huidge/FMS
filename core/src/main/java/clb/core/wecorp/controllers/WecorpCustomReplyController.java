package clb.core.wecorp.controllers;

import clb.core.sys.controllers.ClbBaseController;
import clb.core.wecorp.dto.WecorpAccount;
import clb.core.wecorp.dto.WecorpArticle;
import clb.core.wecorp.dto.WecorpCustomReply;
import clb.core.wecorp.dto.WecorpMaterial;
import clb.core.wecorp.mapper.WecorpArticleMapper;
import clb.core.wecorp.service.*;
import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/20.
 */
@Controller
public class WecorpCustomReplyController extends ClbBaseController {


    @Autowired
    private WecorpArticleMapper wecorpArticleMapper;
    @Autowired
    private IWecorpAccountService wecorpAccountService;
    @Autowired
    private IWecorpReplyService wecorpReplyService;
    @Autowired
    private IWecorpMaterialService wecorpMaterialService;
    @Autowired
    private IWecorpArticleService wecorpArticleService;
    @Autowired
    private IWxService wxService;

    @RequestMapping(value = "/api/customReply/add")
    @ResponseBody
    public ResponseData addCustomReply(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws Exception {
        IRequest requestContext = createRequestContext(request);
        ResponseData responseData=new ResponseData();
        String account_num = jsonObject.getString("account_num");
        String reply_type = jsonObject.getString("reply_type");
        String msg_type = jsonObject.getString("msg_type");



        JSONObject contentJson = new JSONObject();
            if(("FOLLOW_REPLY".equals(reply_type)||"MSG_REPLY".equals(reply_type))&&"text".equals(msg_type)){
                String content = jsonObject.getString("content");

                contentJson.put("content",content);
                contentJson.put("init_content",jsonObject.getString("init_content"));
                wecorpReplyService.saveReplyMsg(reply_type,contentJson,account_num,msg_type);
//                wecorpCustomReply.setContent(jsonObject.getString("content"));
//                wecorpReplyService.updateByPrimaryKeySelective(requestContext,wecorpCustomReply);
            }
            if(("FOLLOW_REPLY".equals(reply_type)||"MSG_REPLY".equals(reply_type))&&"image".equals(msg_type)){
                String attachmentId  =  jsonObject.getString("attachment_id");
                if(attachmentId == null || attachmentId != "") {

                }else {
                    contentJson.put("attachmentId",attachmentId);
                    contentJson.put("store_path",jsonObject.getString("store_path"));
                    wecorpReplyService.saveReplyMsg(reply_type,contentJson,account_num,msg_type);
                }

            }
            if(("FOLLOW_REPLY".equals(reply_type)||"MSG_REPLY".equals(reply_type))&&"news".equals(msg_type)){
                String articleId=jsonObject.getString("articleId");
                String isGroup=jsonObject.getString("isGroup");
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
                    article=wecorpArticleService.selectByPrimaryKey(requestContext,article);
                    if(article == null){
//                        throw new Exception("没有选定的素材");
                    }
                    news_id_array.add(articleId);
                }
                contentJson.put("news_id_array",news_id_array);
                wecorpReplyService.saveReplyMsg(reply_type, contentJson, account_num, msg_type);
            }
            if(("FOLLOW_REPLY".equals(reply_type)||"MSG_REPLY".equals(reply_type))&&"custom_news".equals(msg_type)){
                contentJson.put("attachment_id",jsonObject.getString("attachment_id"));
                contentJson.put("store_path",jsonObject.getString("store_path"));
                contentJson.put("title",jsonObject.getString("title"));
                contentJson.put("description",jsonObject.getString("description"));
                contentJson.put("url",jsonObject.getString("url"));
                wecorpReplyService.saveReplyMsg(reply_type, contentJson, account_num, msg_type);
            }



        return responseData;
    }

    @RequestMapping(value = "/api/customReply/getContent")
    @ResponseBody
    @Timed
    public ResponseData getContent(HttpServletRequest request,@RequestBody JSONObject jsonObject){
        String account_num = jsonObject.getString("account_num");
        String reply_type = jsonObject.getString("reply_type");
        return new ResponseData(wecorpReplyService.getContent(account_num, reply_type));
    }

    @RequestMapping(value = "/api/customReply/addRule")
    @ResponseBody
    @Timed
    public ResponseData saveRule(HttpServletRequest request,@RequestBody JSONObject jsonObject) throws Exception {

        wecorpReplyService.saveKeywordRuleProcess(jsonObject,jsonObject.getString("reply_type"));
        return null;
    }

    @RequestMapping(value = "/api/customReply/getAllRule")
    @ResponseBody
    @Timed
    public ResponseData getRule(HttpServletRequest request,@RequestBody JSONObject jsonObject){
        String account_num = jsonObject.getString("account_num");
        List jsonArray = wecorpReplyService.getKeywordRule(account_num, "KEYWORD_REPLY");
        return new ResponseData(jsonArray);
    }
}
