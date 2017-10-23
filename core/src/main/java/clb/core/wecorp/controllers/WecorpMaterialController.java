package clb.core.wecorp.controllers;

import clb.core.sys.controllers.ClbBaseController;
import clb.core.wecorp.dto.WecorpMaterial;
import clb.core.wecorp.service.IWecorpMaterialService;
import clb.core.wecorp.service.IWecorpServerService;
import clb.core.wecorp.service.IWxService;
import clb.core.wecorp.utils.Constant;
import clb.core.whtcustom.dto.WhtOfficialAccountCfg;
import clb.core.whtcustom.service.IWhtOfficialAccountCfgService;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/7/12.
 */
@Controller
public class WecorpMaterialController extends ClbBaseController {

    @Autowired
    private IWecorpServerService wecorpServerService;
    @Autowired
    private IWxService wxService;
    @Autowired
    private IWecorpMaterialService wecorpMaterialService;
    @Autowired
    private IWhtOfficialAccountCfgService iWhtOfficialAccountCfgService;

    @RequestMapping(value = "/api/public/wecorp/addLocalImage")
    @ResponseBody
    public JSONObject addLocalImage(HttpServletRequest request,@RequestBody MultipartFile file,@RequestParam String appId) throws Exception {
        return wxService.uploadAlwaysFile(file.getOriginalFilename(),"image",file.getInputStream(),appId);
    }

    @RequestMapping(value = {"/api/public/synMaterial/picStream/{appId}/{mediaId}"} ,method= RequestMethod.GET)
    @ResponseBody
    public void getMaterialInfo(HttpServletResponse response,@PathVariable("appId") String appId,@PathVariable("mediaId") String mediaId) {
        InputStream is = null;
        OutputStream output=null;
        try {
            is = wxService.getAlwaysImg(mediaId, appId);
            int i = -1;
            byte[] content = new byte[1024];
            output=response.getOutputStream();
            while ((i = is.read(content)) != -1) {
                output.write(content, 0, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(output!=null) {
                    output.close();
                }
                if(is!=null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @RequestMapping(value = "/api/public/wecorp/menu/synMaterial")
    @ResponseBody
    public ResponseData synMaterial(HttpServletRequest request,@RequestParam String originalId,@RequestParam String type) throws Exception {
    	String appId ="";
    	if(!"".equals(originalId)){
    		IRequest requestContext = createRequestContext(request);
    		WhtOfficialAccountCfg whtOfficialAccountCfg = new WhtOfficialAccountCfg();
    		whtOfficialAccountCfg.setOriginalId(originalId);
    		List<WhtOfficialAccountCfg> selectAll = iWhtOfficialAccountCfgService.selectAll(requestContext, whtOfficialAccountCfg);
    		if(selectAll.size()>0){
    			appId = selectAll.get(0).getBackgroundAppid();
    		}
    	}
    	if(!"".equals(appId)){
    		JSONObject material_count_json = wecorpServerService.getMaterialCount(appId);
    		ResponseData res=new ResponseData();
    		if((Boolean)material_count_json.get("success")){
    			if(type.equals("image")){
    				if(Integer.parseInt(material_count_json.getString("image_count"))!=0){
    					JSONArray array=wecorpMaterialService.synMaterial(Integer.parseInt(material_count_json.getString("image_count")), appId, "image");
    					for(Object json:array){
    						((JSONObject)json).put("url",request.getRequestURL().toString().substring(0,request.getRequestURL().toString().indexOf("/api/public/wecorp/menu/synMaterial"))+ "/api/public/synMaterial/picStream/"+appId+"/"+((JSONObject)json).getString("media_id"));
    					}
    					res.setRows(array);
    				}
    			}else if(type.equals("news")){
    				if(Integer.parseInt(material_count_json.getString("news_count"))!=0){
    					JSONArray array=wecorpMaterialService.synMaterial(Integer.parseInt(material_count_json.getString("news_count")), appId, "news");
    					for(Object json:array){
    						List<JSONObject> list= (List<JSONObject>) ((JSONObject)((JSONObject)json).get("content")).get("news_item");
    						for(JSONObject j:list){
    							j.put("thumb_url",request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("/api/public/wecorp/menu/synMaterial")) + "/api/public/synMaterial/picStream/" + appId + "/" + j.getString("thumb_media_id"));
    						}
    					}
    					res.setRows(array);
    				}
    			}else if(type.equals("video")){
    				if(Integer.parseInt(material_count_json.getString("video_count"))!=0){
    					JSONArray array=wecorpMaterialService.synMaterial(Integer.parseInt(material_count_json.getString("video_count")), appId, "video");
    					res.setRows(array);
    				}
    			}else if(type.equals("voice")){
    				if(Integer.parseInt(material_count_json.getString("voice_count"))!=0){
    					JSONArray array=wecorpMaterialService.synMaterial(Integer.parseInt(material_count_json.getString("voice_count")), appId, "voice");
    					res.setRows(array);
    				}
    			}else{
    				res.setSuccess(false);
    			}
    		}else{
    			res.setSuccess(false);
    			res.setMessage(material_count_json.toString());
    		}
    		return res;
    	}
    	return new ResponseData();
    }

    @RequestMapping(value = {"/api/material/showMaterial/{materialId}"} ,method= RequestMethod.GET)
    @ResponseBody
    public ResponseData showMaterial(HttpServletRequest request,@PathVariable("materialId") String materialId) throws Exception {
        IRequest iRequest=createRequestContext(request);
        JSONObject rejson = new JSONObject();

        WecorpMaterial woaMaterial=new WecorpMaterial();
        woaMaterial.setId(materialId);
        woaMaterial = wecorpMaterialService.selectByPrimaryKey(iRequest,woaMaterial);
        if(Constant.MATERIAL_TYPE_IMAGE.equals(woaMaterial.getMaterialType())){ //图片素材
            rejson.put("url","/c/api/material/getMaterialInfo/"+woaMaterial.getAccountMediaId());
            rejson.put("type","image");

        } else if(Constant.MATERIAL_TYPE_NEWS.equals(woaMaterial.getMaterialType())){ //图文素材
            String appId = wxService.getAppId(woaMaterial.getAccountNum());
            rejson =JSONObject.fromObject(wxService.getAlwaysMaterial(woaMaterial.getAccountMediaId(),appId));
            rejson.put("url","/c/api/material/getMaterialInfo/");
            rejson.put("type","news");
        }
        List list= new ArrayList<JSONObject>();
        list.add(rejson);
        return new ResponseData(list);
    }

}
