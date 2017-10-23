package clb.core.whtcustom.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.wecorp.service.IWxService;
import clb.core.whtcustom.dto.WhtCustomReply;
import clb.core.whtcustom.dto.WhtOfficialAccountCfg;
import clb.core.whtcustom.service.IWhtOfficialAccountCfgService;
	

    @Controller
    public class WhtOfficialAccountCfgController extends BaseController{

	public static final String GET_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";// 获取access
	
    @Autowired
    private IWhtOfficialAccountCfgService service;
    
    @Autowired
    private IWxService IWxService;


    @RequestMapping(value = "/fms/wht/official/account/cfg/query")
    @ResponseBody
    public ResponseData query(WhtOfficialAccountCfg dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectAllField(requestContext,dto,page,pageSize));
    }
    
    @RequestMapping(value = "/fms/wht/official/account/cfg/selectAccountName")
    @ResponseBody
    public ResponseData selectAccountName(WhtOfficialAccountCfg dto,HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
    	return new ResponseData(service.selectAccountName(requestContext,dto));
    }
    
    @RequestMapping(value = "/fms/wht/official/account/cfg/selectcfgId")
	@ResponseBody 
	public ResponseData selectcfgId(HttpServletRequest request,@RequestParam("originalId") String originalId) {
    	IRequest requestCtx = createRequestContext(request);
		WhtOfficialAccountCfg WhtOfficialAccountCfg = new WhtOfficialAccountCfg();
		WhtOfficialAccountCfg.setOriginalId(originalId);
		List<WhtOfficialAccountCfg> selectAll = service.selectAllField(requestCtx,WhtOfficialAccountCfg,1,1000);
		return new ResponseData(selectAll);
	}
    
    @RequestMapping(value = "/fms/wht/official/account/cfg/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<WhtOfficialAccountCfg> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @RequestMapping(value = "/fms/wht/official/account/cfg/updateCfg")
    @ResponseBody
    public ResponseData updateCfg(HttpServletRequest request,@RequestBody List<WhtOfficialAccountCfg> dto){
        IRequest requestCtx = createRequestContext(request);
        WhtOfficialAccountCfg whtOfficialAccountCfg = dto.get(0);
        WhtOfficialAccountCfg whtOfficialAccountCfg2 = service.updateByPrimaryKey(requestCtx, whtOfficialAccountCfg);
        List<WhtOfficialAccountCfg> list = new ArrayList<>();
        list.add(whtOfficialAccountCfg2);
        return new ResponseData(list);
        //return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
    
    @RequestMapping(value="/fms/wht/official/account/cfg/add")
    @ResponseBody
    public ResponseData add(HttpServletRequest request, @RequestBody List<WhtOfficialAccountCfg> obj){

        IRequest requestContext = createRequestContext(request);
        WhtOfficialAccountCfg whtOfficialAccountCfg = obj.get(0);
        WhtOfficialAccountCfg insertSelective = service.insertSelective(requestContext,whtOfficialAccountCfg);
		List<WhtOfficialAccountCfg> list = new ArrayList<>();
		list.add(insertSelective);
        return new ResponseData(list);
    }
    
    @RequestMapping(value = "/fms/wht/official/account/cfg/getAccestToken")
	@ResponseBody 
	public ResponseData updataAmout(HttpServletRequest request,@RequestParam("backgroundAppid") String backgroundAppid,@RequestParam("backgroundAppSecret") String backgroundAppSecret) throws Exception {
		//IRequest requestCtx = createRequestContext(request);
    	WhtOfficialAccountCfg whtOfficialAccountCfg = new WhtOfficialAccountCfg();
    	if(!"".equals(backgroundAppid)){
    		String connectForToken = IWxService.connectForToken(backgroundAppid);
    		if(!"".equals(connectForToken)){
				whtOfficialAccountCfg.setAccesstToken(connectForToken);
			}
    	}
		List<WhtOfficialAccountCfg> list = new ArrayList<>();
		list.add(whtOfficialAccountCfg);
		return new ResponseData(list);
	}
    
    
    @RequestMapping(value = "/fms/wht/official/account/cfg/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<WhtOfficialAccountCfg> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }