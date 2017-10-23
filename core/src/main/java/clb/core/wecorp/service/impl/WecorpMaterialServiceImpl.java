package clb.core.wecorp.service.impl;

import clb.core.wecorp.dto.WecorpArticle;
import clb.core.wecorp.dto.WecorpAttachment;
import clb.core.wecorp.dto.WecorpMaterial;
import clb.core.wecorp.mapper.WecorpAccountMapper;
import clb.core.wecorp.mapper.WecorpArticleMapper;
import clb.core.wecorp.mapper.WecorpMaterialMapper;
import clb.core.wecorp.service.*;
import clb.core.wecorp.utils.Constant;
import com.github.pagehelper.PageHelper;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSStyleDeclaration;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shanhd on 2016/10/24.
 */
@Service
@Transactional
public class WecorpMaterialServiceImpl extends BaseServiceImpl<WecorpMaterial> implements IWecorpMaterialService {

    @Autowired
    private WecorpMaterialMapper wecorpMaterialMapper;
    @Autowired
    private WecorpArticleMapper wecorpArticleMapper;
    @Autowired
    private WecorpAccountMapper wecorpAccountMapper;

    @Autowired
    private IWxService wxService;
    @Autowired
    private IWecorpArticleService wecorpArticleService;
    @Autowired
    private IWecorpAttachmentService wecorpAttachmentService;
    @Autowired
    private IWecorpServerService wecorpServerService;

    @Override
    public WecorpMaterial findMaterial(String attachmentId, String purpose, String accountNum, String enableFlag) {
        return wecorpMaterialMapper.findMaterial(attachmentId,purpose,accountNum,enableFlag);
    }

    @Override
    public WecorpMaterial findMaterialByattachmentId(String attachmentId) {
        return wecorpMaterialMapper.findMaterialByattachmentId(attachmentId);
    }

    @Override
    public WecorpMaterial findByAccountMediaIdAndAccountNum(String accountMediaId,String accountNum) {
        return wecorpMaterialMapper.findByAccountMediaIdAndAccountNum(accountMediaId, accountNum);
    }

    @Override
    public List<WecorpMaterial> queryMaterial(int page, int pagesize,WecorpMaterial dto) {
        PageHelper.startPage(page, pagesize);
        List<WecorpMaterial> list=wecorpMaterialMapper.queryMaterial(dto);
        for(WecorpMaterial woaMaterial:list){
            switch (woaMaterial.getPurpose()){
                case Constant.MATERIAL_NEWS_COVER:
                    woaMaterial.setPurpose("图文封面");break;
                case Constant.MATERIAL_NEWS_CONTENT:
                    woaMaterial.setPurpose("图文正文");break;
                case Constant.MATERIAL_MASS_MESSAGE:
                    woaMaterial.setPurpose("群发消息");break;
                default:
                    break;
            }
            switch (woaMaterial.getMaterialType()){
                case Constant.MATERIAL_TYPE_IMAGE:
                    woaMaterial.setMaterialType("图片");break;
                case Constant.MATERIAL_TYPE_NEWS:
                    woaMaterial.setMaterialType("图文");break;
                default:
                    break;
            }
            if("-1".equals(woaMaterial.getExpiresIn())){
                woaMaterial.setExpiresIn("永久");
            }
        }

        return list;
    }

    @Override
    public Boolean deleteMaterial(String materialId,IRequest iRequest) {
//        WecorpMaterial woaMaterial=new WecorpMaterial();
//        woaMaterial.setId(materialId);
//        woaMaterial=self().selectByPrimaryKey(iRequest, woaMaterial);
//        if(woaMaterial!=null){
//            String appId=wxService.getAppId(woaMaterial.getAccountNum());
//            JSONObject reJson=JSONObject.fromObject(wxService.deleteMaterial(woaMaterial.getAccountMediaId(), appId));
//            if(reJson.getInt("errcode")==0 || reJson.getInt("errcode")==40007){
//                self().deleteByPrimaryKey(woaMaterial);
//            }else{   //删除失败
//                return false;
//            }
//        }
        return true;
    }

    public JSONObject createNewsMaterialByArticles(List news_id_array) throws Exception {
        IRequest iRequest= RequestHelper.newEmptyRequest();
        JSONObject msg_json = new JSONObject();

        String account_num=wecorpArticleMapper.selectByPrimaryKey((Object)news_id_array.get(0)).getAccountNum();
        String account_type=wecorpAccountMapper.getWoaAccountByAccountNum(account_num).getAccountType();
        String appId=wxService.getAppId(account_num);

        HtmlCompressor htmlCompressor = new HtmlCompressor();
        htmlCompressor.setRemoveMultiSpaces(true);
        htmlCompressor.setRemoveIntertagSpaces(true);
        JSONArray cover_jsonArray = new JSONArray();
        try{
            JSONObject send_json = new JSONObject();
            JSONArray articles_jsonarray = new JSONArray();
            for(int news_id_array_index = 0 ; news_id_array_index < news_id_array.size();news_id_array_index++){
                JSONObject article_json = new JSONObject();
                WecorpArticle article = new WecorpArticle(); //WoaArticle.findById(news_id_array[news_id_array_index])
                article.setId(news_id_array.get(news_id_array_index).toString());
                article=wecorpArticleService.selectByPrimaryKey(iRequest,article);
                if(article!=null){
                    article_json.put("title",article.getTitle());
                    article_json.put("author",article.getAuthor());
                    article_json.put("digest",article.getDigest());
                    article_json.put("show_cover_pic",0);//此处默认不显示封面
                    article_json.put("content_source_url", article.getContentSourceUrl());

                    //封面素材上传，临时素材
                    WecorpAttachment attachment=new WecorpAttachment();
                    attachment.setId(article.getCover());
                    attachment = wecorpAttachmentService.selectByPrimaryKey(iRequest,attachment);    //WoaAttachment.findById(woaArticle.cover)
                    if(attachment!=null){
                        WecorpMaterial woaMaterial =self().findMaterial(article.getCover(), Constant.MATERIAL_NEWS_COVER, account_num, "Y");    //WoaMaterial.findByAttachment_idAndPurposeAndAccount_numAndEnable_flag(woaArticle.cover,'NEWS_COVER',account_num,'N')
                        if(woaMaterial!=null){
                            woaMaterial.setLastUpdateDate(new Date());
                            cover_jsonArray.add(woaMaterial.getId());
                            self().updateByPrimaryKeySelective(iRequest, woaMaterial);
                            article_json.put("thumb_media_id",woaMaterial.getAccountMediaId());
                        }else{
                            String[] store_path_array=attachment.getStorePath().split("/");
                            // Post请求的url，与get不同的是不需要带参数
                            URL postUrl = new URL(wxService.encode(attachment.getStorePath()));
                            // 打开连接
                            HttpURLConnection connection=null;
                            if("http:".equals(store_path_array[0])){
                                connection = (HttpURLConnection) postUrl
                                        .openConnection();
                            }else{
                                connection = (HttpsURLConnection) postUrl
                                        .openConnection();
                            }
                            connection.setRequestProperty("Accept-Charset", "utf-8");
                            connection.setRequestProperty("contentType", "utf-8");
                            // Output to the connection. Default is
                            // false, set to true because post
                            // method must write something to the
                            // connection
                            // 设置是否向connection输出，因为这个是post请求，参数要放在
                            // http正文内，因此需要设为true
                            connection.setDoOutput(true);
                            // Read from the connection. Default is true.
                            connection.setDoInput(true);
                            // Set the post method. Default is GET
                            connection.setRequestMethod("GET");
                            // Post cannot use caches
                            // Post 请求不能使用缓存
                            connection.setUseCaches(false);
                            // This method takes effects to
                            connection.setInstanceFollowRedirects(true);
                            // connection.setRequestProperty("Authorization", jsonObj.token_type+" "+jsonObj.access_token);
                            InputStream is =connection.getInputStream();
                            JSONObject thumb_media_id_json = JSONObject.fromObject(wxService.uploadAlwaysFile(store_path_array[store_path_array.length-1],"image",is,appId));
                            connection.disconnect();
                            Date time =new Date();
                            if(thumb_media_id_json.get("media_id")!=null){
                                WecorpMaterial material = new WecorpMaterial();
                                String id= UUID.randomUUID().toString();
                                material.setId(id);
                                material.setAttachmentId(article.getCover());
                                material.setAccountNum(account_num);
                                material.setPurpose(Constant.MATERIAL_NEWS_COVER); //图文封面
                                material.setMaterialType(Constant.MATERIAL_TYPE_IMAGE);  //图片
                                material.setAccountMediaId(thumb_media_id_json.getString("media_id"));
                                material.setAccountMediaUrl(thumb_media_id_json.getString("url"));
                                material=self().insertSelective(iRequest, material);

                                if (material==null) {
                                    throw new Exception("Can not save woaMaterial: 封面上传失败");
                                }
                                cover_jsonArray.add(id);
                                article_json.put("thumb_media_id",thumb_media_id_json.getString("media_id"));
                            }else{
                                throw new Exception(thumb_media_id_json.getString("errcode")+" "+thumb_media_id_json.getString("errmsg")+" 封面上传失败");
                            }
                        }
                    }else{
                        throw new Exception("未找到封面");
                    }

                    //正文图片
                    try {
                        String content_old = article.getContent();
                        Document doc = Jsoup.parse(content_old);

                        //img标签
                        Elements imgs = doc.select("img");
                        for (Element img : imgs) {
                            String link_href = img.attr("src");

                            String file_type=this.getFileType(link_href);
                            if("jpeg".equals(file_type) || "jpg".equals(file_type) || "png".equals(file_type)){
                                JSONObject reJson = JSONObject.fromObject(wxService.uploadNewsImg(appId, new Date().getTime() + "." + file_type, "image", link_href));
                                if(reJson.get("url")==null){
                                    throw new Exception(reJson.getString("errcode")+" "+reJson.getString("errmsg")+" 正文图片上传失败");
                                }
                                img.attr("src",reJson.getString("url"));
                            }else{
                                JSONObject tempjson = this.uploadContentImage(link_href,appId,account_num);
                                img.attr("src", tempjson.getString("url"));
                                if(!"".equals(tempjson.getString("material_id")))
                                    cover_jsonArray.add(tempjson.getString("material_id"));
                            }
                        }

                        //style*=background
                        Elements backgrounds = doc.select("[style*=background]");
                        for (Element background : backgrounds) {
                            String style = background.attr("style");
                            InputSource source = new InputSource(new StringReader(style));
                            CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
                            CSSStyleDeclaration decl = parser.parseStyleDeclaration(source);
                            String backgroundValue="";
                            String cssName = "";
                            if(!"".equals(decl.getPropertyValue("background-image"))){
                                backgroundValue = decl.getPropertyValue("background-image");
                                cssName = "background-image";
                            }else if(!"".equals(decl.getPropertyValue("background"))){
                                backgroundValue = decl.getPropertyValue("background-image");
                                cssName = "background";
                            }else{
                                continue;
                            }
                            String pattern = "(http:.*\\))";
                            // 创建 Pattern 对象
                            Pattern r = Pattern.compile(pattern);
                            // 现在创建 matcher 对象
                            Matcher m = r.matcher(backgroundValue);
                            if(m.find()){
                                String url=backgroundValue.substring(m.start(),m.end()-1);
                                String newCssValue = "";
                                String newUrl;
                                if(url.charAt(url.length()-1)=='"'){
                                    url = url.substring(0,url.length()-1);
                                }

                                String file_type=this.getFileType(url);
                                if("jpeg".equals(file_type) || "jpg".equals(file_type) || "png".equals(file_type)){
                                    JSONObject reJson = JSONObject.fromObject(wxService.uploadNewsImg(appId, new Date().getTime() + "." + file_type, "image", url));
                                    if(reJson.get("url")==null){
                                        throw new Exception(reJson.getString("errcode")+" "+reJson.getString("errmsg")+" 正文图片上传失败");
                                    }
                                    newUrl = reJson.getString("url");
                                }else{
                                    JSONObject tempjson = this.uploadContentImage(url,appId,account_num);
                                    newUrl =tempjson.getString("url");
                                    if(!"".equals(tempjson.getString("material_id")))
                                        cover_jsonArray.add(tempjson.getString("material_id"));
                                }

                                if(url.charAt(url.length()-1)=='"'){
                                    newCssValue = m.replaceAll(newUrl+"\")");
                                }else{
                                    newCssValue = m.replaceAll(newUrl+")");
                                }

                                decl.setProperty(cssName,newCssValue, null);
                            }
                            background.attr("style",decl.getCssText());
                        }
                        article_json.put("content", htmlCompressor.compress(doc.html()));
                    }catch (Exception e) {
                        e.printStackTrace();
                        msg_json.put("errcode", "100");
                        msg_json.put("errmsg", e.getMessage());
//                        logger.error("上传素材失败");
                        return msg_json;
                    }
                    articles_jsonarray.add(article_json);
                }
            }

            send_json.put("articles", articles_jsonarray);
            JSONObject news_json =JSONObject.fromObject(wxService.addNews(appId, send_json));
            if(news_json.get("media_id")!=null){
                msg_json.put("cover_jsonArray",cover_jsonArray);
                msg_json.put("errcode","0");
                msg_json.put("media_id",news_json.getString("media_id"));
                msg_json.put("account_num",account_num);
                msg_json.put("account_type",account_type);
                msg_json.put("appId",appId);
            }else{
                for(int i=0;i<cover_jsonArray.size(); i++){
                    String value=cover_jsonArray.getString(i);
                    WecorpMaterial woaMaterial=new WecorpMaterial();
                    woaMaterial.setId(value);
                    woaMaterial = self().selectByPrimaryKey(iRequest, woaMaterial);
                    if(woaMaterial!=null){
                        woaMaterial.setEnableFlag("N");
                        self().updateByPrimaryKey(iRequest, woaMaterial);
                    }
                }
                msg_json.put("errcode","100");
                msg_json.put("errmsg",news_json.toString());
                msg_json.put("account_num",account_num);
                msg_json.put("account_type",account_type);
                msg_json.put("appId",appId);
            }

        }catch (Exception e) {  //发送图文异常
            for(int i=0;i<cover_jsonArray.size(); i++){
                String value=cover_jsonArray.getString(i);
                WecorpMaterial woaMaterial=new WecorpMaterial();
                woaMaterial.setId(value);
                woaMaterial = self().selectByPrimaryKey(iRequest, woaMaterial);
                if(woaMaterial!=null){
                    woaMaterial.setEnableFlag("N");
                    self().updateByPrimaryKey(iRequest,woaMaterial);
                }
            }
            msg_json.put("errcode","100");
            msg_json.put("errmsg",e.getMessage());
            msg_json.put("account_num",account_num);
            msg_json.put("account_type",account_type);
            msg_json.put("appId",appId);
        }
        return msg_json;
    }

    @Override
    public JSONArray synMaterial(int count, String appid, String type) throws Exception {
        JSONObject json = new JSONObject();
        json.put("type",type);
        JSONArray array=new JSONArray();
        for(int offset = 0 ;; offset+=20){
            if(20>=count-offset){
                json.put("offset",offset);
                json.put("count",count-offset);
                JSONObject material_json = wecorpServerService.batchgetMaterial(json,appid);
                if(material_json ==null){
                    throw  new Exception(material_json.toString());
                }
                array.addAll(material_json.getJSONArray("item"));
                break;
            }else {
                json.put("offset", offset);
                json.put("count", 20);
                JSONObject material_json = wecorpServerService.batchgetMaterial(json, appid);
                if(material_json ==null){
                    throw  new Exception(material_json.toString());
                }
                array.addAll(material_json.getJSONArray("item"));
            }
        }
        return array;
    }

    //内部方法
    JSONObject uploadContentImage(String url,String appId,String account_num)throws Exception{
        JSONObject rejson = new JSONObject();
        String[] link_href_array=url.toString().split("/");

        // Post请求的url，与get不同的是不需要带参数
        URL postUrl = new URL(wxService.encode(url));
        // 打开连接
        HttpURLConnection connection;
        if("http:".equals(link_href_array[0])){
            connection = (HttpURLConnection) postUrl
                    .openConnection();
        }else{
            connection = (HttpsURLConnection) postUrl
                    .openConnection();
        }
        connection.setRequestProperty("Accept-Charset", "utf-8");
        connection.setRequestProperty("contentType", "utf-8");
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        // Output to the connection. Default is
        // false, set to true because post
        // method must write something to the
        // connection
        // 设置是否向connection输出，因为这个是post请求，参数要放在
        // http正文内，因此需要设为true
        connection.setDoOutput(true);
        // Read from the connection. Default is true.
        connection.setDoInput(true);
        // Set the post method. Default is GET
        connection.setRequestMethod("GET");
        // Post cannot use caches
        // Post 请求不能使用缓存
        connection.setUseCaches(false);
        // This method takes effects to
        connection.setInstanceFollowRedirects(true);
        String content_type = connection.getContentType();
        String[] content_typeArray = content_type.split("/");
        String file_type;
        if(content_typeArray.length==2){
            file_type= content_typeArray[1].toLowerCase();
        }else{
            throw  new Exception("解析文件错误");
        }

        // connection.setRequestProperty("Authorization", jsonObj.token_type+" "+jsonObj.access_token);
        JSONObject thumb_media_id_json = new JSONObject();
        InputStream is =connection.getInputStream();
        if(url.indexOf("http://mmbiz.qpic.cn/")!=-1){
            rejson.put("url",url);
            rejson.put("material_id","");
            return rejson;
        }else{
            thumb_media_id_json =JSONObject.fromObject(wxService.uploadAlwaysFile(new Date().getTime() + "." + file_type, "image", is, appId));
        }

        connection.disconnect();
        Date time =new Date();
        if(thumb_media_id_json.get("media_id")!=null){
            WecorpMaterial material = new WecorpMaterial();
            material.setAccountNum("");
            material.setPurpose(Constant.MATERIAL_NEWS_CONTENT);  //图文正文
            material.setMaterialType(Constant.MATERIAL_TYPE_IMAGE); //图片
            material.setAccountMediaId(thumb_media_id_json.getString("media_id"));
            material.setAccountMediaUrl(thumb_media_id_json.getString("url"));
            IRequest iRequest= RequestHelper.newEmptyRequest();
            material=this.insertSelective(iRequest, material);

            if (material==null) {
                throw new Exception("Can not save woaMaterial: '正文内容上传失败'");
            }
            rejson.put("url", thumb_media_id_json.getString("url"));
            rejson.put("material_id",material.getId());
            return rejson;
        }else{
            throw new Exception(thumb_media_id_json.getString("errcode")+" "+thumb_media_id_json.getString("errmsg")+" 正文图片上传失败,地址: "+url);
        }
    }

    String getFileType(String file_url) throws Exception {
        URL url = new URL(wxService.encode(file_url));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Accept-Charset", "utf-8");
        conn.setRequestProperty("contentType", "utf-8");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        conn.setUseCaches(false);
        conn.setInstanceFollowRedirects(true);
        String content_type = conn.getContentType();
        conn.disconnect();
        String[] content_typeArray = content_type.split("/");
        if(content_typeArray.length==2){
            return content_typeArray[1].toLowerCase();
        }else{
            throw  new Exception("解析文件错误");
        }
    }

    void addUnknowMaterial(String account_num,JSONObject object,String material_type){
        IRequest iRequest= RequestHelper.newEmptyRequest();
        WecorpMaterial woaMaterial = this.findByAccountMediaIdAndAccountNum(object.getString("media_id"), account_num);
        if(woaMaterial!=null){
            woaMaterial.setAccountNum(account_num);
            woaMaterial.setPurpose(material_type);
            woaMaterial.setMaterialType(material_type);
            if(object.get("media_id")!=null) {
                woaMaterial.setAccountMediaId(object.getString("media_id"));
            }
            if(object.get("url")!=null) {
                woaMaterial.setAccountMediaUrl(object.getString("url"));
            }
            woaMaterial.setContent(object.toString());
            wecorpMaterialMapper.updateByPrimaryKeySelective(woaMaterial);
        }else {
            woaMaterial = new WecorpMaterial();
            String id = UUID.randomUUID().toString();
            woaMaterial.setId(id);
            woaMaterial.setAccountNum(account_num);
            woaMaterial.setPurpose(material_type);
            woaMaterial.setMaterialType(material_type);
            if(object.get("media_id")!=null) {
                woaMaterial.setAccountMediaId(object.getString("media_id"));
            }
            if(object.get("url")!=null) {
                woaMaterial.setAccountMediaUrl(object.getString("url"));
            }
            woaMaterial.setContent(object.toString());
            wecorpMaterialMapper.insertSelective(woaMaterial);
        }
    }
}
