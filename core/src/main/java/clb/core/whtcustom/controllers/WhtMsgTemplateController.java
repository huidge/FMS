package clb.core.whtcustom.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import clb.core.notification.dto.NtnNotificationTemplate;
import clb.core.notification.service.INtnNotificationTemplateService;
import clb.core.wecorp.service.IWecorpServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.whtcustom.dto.WhtCustomReply;
import clb.core.whtcustom.dto.WhtMsgTemplate;
import clb.core.whtcustom.service.IWhtMsgTemplateService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

    @Controller
    public class WhtMsgTemplateController extends BaseController{

    @Autowired
    private IWhtMsgTemplateService service;
    @Autowired
    private INtnNotificationTemplateService INtnNotificationTemplateService;
    
    @Autowired
    private IWecorpServerService IWecorpServer;

    @RequestMapping(value = "/api/public/fms/wht/msg/template/query")
    @ResponseBody
    public ResponseData query(WhtMsgTemplate dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) throws Exception {
        IRequest requestContext = createRequestContext(request);
        String backgroundAppid = request.getParameter("backgroundAppid");
        List<WhtMsgTemplate> list = new ArrayList<>();
        if(!"".equals(backgroundAppid)){
            JSONObject allTemple;
            try {
                allTemple = IWecorpServer.getAllTemple(backgroundAppid);
            }catch (Exception e){
                ResponseData err=new ResponseData(false);
                err.setMessage(e.getMessage());
                return err;
            }
            if(!allTemple.containsKey("template_list")){
                ResponseData err=new ResponseData(false);
                err.setMessage("allTemple:"+allTemple.toString());
                return err;
            }
        	JSONArray jsonArray = allTemple.getJSONArray("template_list");
        	Iterator<JSONArray> itr = jsonArray.iterator();
        	while(itr.hasNext()) {
        		JSONObject temp = JSONObject.fromObject(itr.next()); 
        		WhtMsgTemplate whtMsgTemplate = new WhtMsgTemplate();
        		whtMsgTemplate.setWhtTemplateId(temp.get("template_id").toString());
        		whtMsgTemplate.setTemplateName(temp.get("title").toString());
        		whtMsgTemplate.setPrimaryIndustry(temp.get("primary_industry").toString());
        		whtMsgTemplate.setSecondaryIndustry(temp.get("deputy_industry").toString());
        		String content = temp.get("content").toString();
        		content = content.replace("\n", "");
        		content = content.replace("\r", "");
        		whtMsgTemplate.setDetailContent(content);
        		String example = temp.get("example").toString();
        		example = example.replace("\n", "");
        		example = example.replace("\r", "");
        		whtMsgTemplate.setContentSample(example);
        		list.add(whtMsgTemplate);
    		}
        }
    	List<WhtMsgTemplate> selectAllField = service.selectAllField(requestContext,dto,page,pageSize);
    	if(list.size()>0){
	        if(selectAllField.size()>0){
	        	for (WhtMsgTemplate whtMsgTemplate : selectAllField) {
	        		String whtTemplateId = whtMsgTemplate.getWhtTemplateId();
	        		for (WhtMsgTemplate whtMsgTemplate2 : list) {
						if(whtTemplateId.equals(whtMsgTemplate2.getWhtTemplateId())){
							whtMsgTemplate.setTemplateName(whtMsgTemplate2.getTemplateName());
							whtMsgTemplate.setPrimaryIndustry(whtMsgTemplate2.getPrimaryIndustry());
							whtMsgTemplate.setSecondaryIndustry(whtMsgTemplate2.getSecondaryIndustry());
							whtMsgTemplate.setDetailContent(whtMsgTemplate2.getDetailContent());
							whtMsgTemplate.setContentSample(whtMsgTemplate2.getContentSample());
							break;
						}
					}
				}
	        }
        }
        return new ResponseData(selectAllField);
    }
    
    @RequestMapping(value = "/fms/wht/msg/template/editTemplateContent")
   	@ResponseBody 
   	public ResponseData editTemplateContent(HttpServletRequest request,@RequestParam("whtTemplateId") String whtTemplateId,@RequestParam("backgroundAppid") String backgroundAppid,@RequestParam("templateId") String templateId) {
       	IRequest requestCtx = createRequestContext(request);
       	List<WhtMsgTemplate> list = new ArrayList<>();
       	if(!"".equals(whtTemplateId) && !"".equals(backgroundAppid)){
       		if(!"".equals(templateId)){
       			NtnNotificationTemplate ntnNotificationTemplate = new NtnNotificationTemplate();
       			ntnNotificationTemplate.setTemplateId(Long.parseLong(templateId));
       			List<NtnNotificationTemplate> selectAll = INtnNotificationTemplateService.selectAll(requestCtx,ntnNotificationTemplate);
       			for (NtnNotificationTemplate ntnNotificationTemplate2 : selectAll) {
					if(whtTemplateId.equals(ntnNotificationTemplate2.getWebchatApi())){
						return new ResponseData(selectAll);
					}else{
						JSONObject allTemple;
						try {
							allTemple = IWecorpServer.getAllTemple(backgroundAppid);
						}catch (Exception e){
							ResponseData err=new ResponseData(false);
							err.setMessage(e.getMessage());
							return err;
						}
						if(!allTemple.containsKey("template_list")){
							ResponseData err=new ResponseData(false);
							err.setMessage("allTemple:"+allTemple.toString());
							return err;
						}
						JSONArray jsonArray = allTemple.getJSONArray("template_list");
						Iterator<JSONArray> itr = jsonArray.iterator();
						while(itr.hasNext()) {
							JSONObject temp = JSONObject.fromObject(itr.next());
							String string = temp.get("template_id").toString();
							if(whtTemplateId.equals(string)){
								WhtMsgTemplate whtMsgTemplate = new WhtMsgTemplate();
								whtMsgTemplate.setWhtTemplateId(temp.get("template_id").toString());
								whtMsgTemplate.setTemplateName(temp.get("title").toString());
								whtMsgTemplate.setPrimaryIndustry(temp.get("primary_industry").toString());
								whtMsgTemplate.setSecondaryIndustry(temp.get("deputy_industry").toString());
								whtMsgTemplate.setDetailContent(temp.get("content").toString());
								whtMsgTemplate.setContentSample(temp.get("example").toString());
								list.add(whtMsgTemplate);
								return new ResponseData(list);
							}
						}
					}
				}
       		}else{
       			JSONObject allTemple;
       			try {
       				allTemple = IWecorpServer.getAllTemple(backgroundAppid);
       			}catch (Exception e){
       				ResponseData err=new ResponseData(false);
       				err.setMessage(e.getMessage());
       				return err;
       			}
       			if(!allTemple.containsKey("template_list")){
       				ResponseData err=new ResponseData(false);
       				err.setMessage("allTemple:"+allTemple.toString());
       				return err;
       			}
       			JSONArray jsonArray = allTemple.getJSONArray("template_list");
       			Iterator<JSONArray> itr = jsonArray.iterator();
       			while(itr.hasNext()) {
       				JSONObject temp = JSONObject.fromObject(itr.next());
       				String string = temp.get("template_id").toString();
       				if(whtTemplateId.equals(string)){
       					WhtMsgTemplate whtMsgTemplate = new WhtMsgTemplate();
       					whtMsgTemplate.setWhtTemplateId(temp.get("template_id").toString());
       					whtMsgTemplate.setTemplateName(temp.get("title").toString());
       					whtMsgTemplate.setPrimaryIndustry(temp.get("primary_industry").toString());
       					whtMsgTemplate.setSecondaryIndustry(temp.get("deputy_industry").toString());
       					whtMsgTemplate.setDetailContent(temp.get("content").toString());
       					whtMsgTemplate.setContentSample(temp.get("example").toString());
       					list.add(whtMsgTemplate);
       					return new ResponseData(list);
       				}
       			}
       		}
       	}
   		return new ResponseData();
   	}
    
    
    @RequestMapping(value = "/fms/wht/msg/template/selectAll")
    @ResponseBody
    public ResponseData selectAll(WhtMsgTemplate dto, HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(service.selectAll(requestContext,dto));
    }
    
    @RequestMapping(value = "/fms/wht/msg/template/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<WhtMsgTemplate> dto){
        IRequest requestCtx = createRequestContext(request);
        WhtMsgTemplate WhtMsgTemplate = dto.get(0);
        WhtMsgTemplate trnCourseFile2 = service.updateByPrimaryKeySelective(requestCtx, WhtMsgTemplate);
        List<WhtMsgTemplate> list = new ArrayList<>();
        list.add(trnCourseFile2);
        return new ResponseData(list);
        
        //return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @RequestMapping(value="/fms/wht/msg/template/selectWhtTemplateId")
    @ResponseBody
    public ResponseData selectWhtTemplateId(HttpServletRequest request, @RequestBody List<WhtMsgTemplate> obj) throws Exception {
        IRequest requestContext = createRequestContext(request);
        WhtMsgTemplate WhtMsgTemplate = obj.get(0);
        WhtMsgTemplate whtMsgTemplate2 = new WhtMsgTemplate();
        whtMsgTemplate2.setCfgId(WhtMsgTemplate.getCfgId());
        List<WhtMsgTemplate> selectAll = service.selectAll(requestContext,whtMsgTemplate2);
        if(selectAll.size()>0){
        	for (WhtMsgTemplate whtMsgTemplate3 : selectAll) {
        		if((WhtMsgTemplate.getTemplateCode()).equals(whtMsgTemplate3.getTemplateCode())){
        			ResponseData responseData = new ResponseData();
        			responseData.setSuccess(false);
        			responseData.setMessage("模板信息已存在");
        			return responseData;
        		}
        	};
        }
        String templateCode = WhtMsgTemplate.getTemplateCode();
        String backgroundAppid = WhtMsgTemplate.getBackgroundAppid();
        List<WhtMsgTemplate> list = new ArrayList<>();
        if(!"".equals(templateCode) && !"".equals(backgroundAppid)){
        	JSONObject object = IWecorpServer.getTemlateMsgId(templateCode, backgroundAppid);
        	if(((object.get("errmsg")).toString()).equals("ok") && !"".equals(object.get("template_id").toString())){
        		WhtMsgTemplate.setWhtTemplateId(object.get("template_id").toString());
        		WhtMsgTemplate insertSelective = service.insertSelective(requestContext,WhtMsgTemplate);
        		list.add(insertSelective);
        		return new ResponseData(list);
        	}
        }
        //WhtMsgTemplate insertSelective = service.insertSelective(requestContext,WhtMsgTemplate);
		//list.add(insertSelective);
        return new ResponseData();
    }
    
    /*@RequestMapping(value="/fms/wht/msg/template/add")
    @ResponseBody
    public ResponseData add(HttpServletRequest request, @RequestBody List<WhtMsgTemplate> obj){
    	IRequest requestContext = createRequestContext(request);
        WhtMsgTemplate WhtMsgTemplate = obj.get(0);
        WhtMsgTemplate insertSelective = service.insertSelective(requestContext,WhtMsgTemplate);
        List<WhtMsgTemplate> list = new ArrayList<>();
		list.add(WhtMsgTemplate);
		list.add(insertSelective);
        return new ResponseData(list);
    }*/
    
    
    @RequestMapping(value = "/fms/wht/msg/template/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<WhtMsgTemplate> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }