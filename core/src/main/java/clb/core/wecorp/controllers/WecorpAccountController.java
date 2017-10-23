package clb.core.wecorp.controllers;

import clb.core.sys.controllers.ClbBaseController;
import clb.core.wecorp.dto.WecorpAccount;
import clb.core.wecorp.service.IWecorpAccountService;
import clb.core.wecorp.service.IWecorpTempleService;
import clb.core.wecorp.service.IWxService;
import clb.core.wecorp.service.impl.SecurityUtils;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Administrator on 2017/6/13.
 */
@Controller
public class WecorpAccountController extends ClbBaseController {

    @Autowired
    private IWecorpAccountService wecorpAccountService;
    @Autowired
    private IWecorpTempleService wecorpTempleService;
    @Autowired
    private IWxService wxService;

    private List<WecorpAccount> lists;

    /**
     * 获取open_id的接口.
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/wxAccount/getOpenId")
    @ResponseBody
    public ResponseData getOpenId(HttpServletRequest request) {
        String loginName=SecurityUtils.getCurrentUserLogin();
        Map res=new HashMap<>();
        if(loginName.substring(0, 4).equals("app@")){
            //app
            res.put("type","app");
            res.put("appId",SecurityUtils.getCurrentUserLogin().substring(4));
        }else{
            //openid
            res.put("type","wx");
            res.put("openId",SecurityUtils.getCurrentUserLogin());
        }
        return new ResponseData(Collections.singletonList(res));
    }

    /**
     * 图片上传.
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/public/wx/upload")
    @ResponseBody
    public String upload(HttpServletRequest request,@RequestBody String image) throws Exception {
        return wxService.upload(image);
    }

    /**
     * 图片上传透传接口.
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/wx/exchange",method = RequestMethod.POST,produces="application/json")
    @ResponseBody
    public JSONObject exchange(HttpServletRequest request,@RequestBody JSONObject image) throws Exception {
        String token="";
        if(request.getParameter("access_token")!=null&&!"".equals(request.getParameter("access_token"))){
            token=request.getParameter("access_token");
        }else{
            token=request.getHeader("Authorization").split(" ")[1];
        }
        String url=request.getRequestURL().toString().replace("/api/wx/exchange", "/api/commons/attach?access_token=" + token);
        if(image.get("base64Img")==null||"".equals(image.getString("base64Img"))){
            return JSONObject.fromObject("{\n" +
                    "    \"message\": \"base64Img为空\",\n" +
                    "    \"info\": \"base64Img为空\"\n" +
                    "}");
        }
        return JSONObject.fromObject(wxService.exchange(image.getString("base64Img"),image.get("fileName")==null?null:image.getString("fileName"),url));
    }

    @RequestMapping(value = "/api/public/wx/getImage")
    @ResponseBody
    public void getImage(HttpServletRequest request,HttpServletResponse response,@RequestParam String key) throws Exception {
        InputStream in= wxService.getImage(key);
        byte[] buff = new byte[100];
        int rc = 0;
        response.setContentType("image/jpeg;charset=UTF-8");
        while ((rc = in.read(buff, 0, 100)) > 0) {
            response.getOutputStream().write(buff, 0, rc);
        }
        in.close();
        response.getOutputStream().close();
    }

    /**
     * 获取微信token的接口.
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/public/wxAccount/getToken" ,method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getToken(HttpServletRequest request,@RequestParam String appId) {
        JSONObject res=new JSONObject();
        try {
            JSONObject token=wxService.connectForTokenNew(appId);
            if((Boolean)token.get("success")) {
                String access_token=token.getString("access_token");
                res.put("access_token",access_token);
                res.put("errcode","0");
                res.put("errmsg","ok");
            }else{
                String errcode=token.getString("errcode");
                String errmsg=token.getString("errmsg");
                res.put("errcode",errcode);
                res.put("errmsg",errmsg);
            }
        } catch (Exception e) {
            res.put("errcode","-100");
            res.put("errmsg","中央服务器出现异常");
        }
        return res;
    }

    /**
     * 获取并刷新微信token的接口.
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/public/wxAccount/validateAppIdAndSecret" ,method = RequestMethod.POST)
    @ResponseBody
    public JSONObject validateAppIdAndSecret(HttpServletRequest request,@RequestParam String appId,@RequestParam String secret) {
        JSONObject res=new JSONObject();
        try {
            JSONObject token=wxService.firstConnectForTokenNew(appId, secret);
            if((Boolean)token.get("success")) {
                String access_token=token.getString("access_token");
                res.put("access_token",access_token);
                res.put("errcode","0");
                res.put("errmsg","ok");
            }else{
                String errcode=token.getString("errcode");
                String errmsg=token.getString("errmsg");
                res.put("errcode",errcode);
                res.put("errmsg",errmsg);
            }
        } catch (Exception e) {
            res.put("errcode","-100");
            res.put("errmsg","中央服务器出现异常");
        }
        return res;
    }

    /**
     * 获取ticket.
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/public/wxAccount/getTicket" ,method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getTicket(HttpServletRequest request,@RequestParam String appId) throws Exception {
        return wxService.getTicket(appId);
    }

    /**
     * 签名算法.
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/public/wxAccount/getJsSdkInfo" ,method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getJsSdkInfo(HttpServletRequest request,@RequestBody JSONObject object) throws Exception {
        return wxService.getJsSdkInfo(object.getString("url"),object.getString("appId"));
    }

    @RequestMapping(value = "/wecorp/account/query")
    @ResponseBody
    public ResponseData query(WecorpAccount dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        lists = wecorpAccountService.select(requestContext, dto, page, pageSize);
        return new ResponseData(lists);
    }

    @RequestMapping(value = "/api/public/wecorp/account/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody WecorpAccount wecorpAccount) {
        IRequest requestContext = createRequestContext(request);
//        WecorpAccount wecorpAccount = dtos.get(0);
        WecorpAccount account = wecorpAccountService.getWoaAccountByAccountNum(wecorpAccount.getAccountNum());
        if(account != null) {
            wecorpAccountService.updateByPrimaryKey(requestContext,wecorpAccount);
        }else {
            wecorpAccountService.insertSelective(requestContext,wecorpAccount);
        }
        return new ResponseData();
    }

    @RequestMapping(value = "/wecorp/account/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<WecorpAccount> dtos) {
        wecorpAccountService.batchDelete(dtos);
        return new ResponseData();
    }

    @RequestMapping(value = "/api/public/wecorp/addTempleId")
    @ResponseBody
    public ResponseData addTempleId(HttpServletRequest request,@RequestBody JSONObject jsonObject) throws Exception {
        IRequest requestContext = createRequestContext(request);
        String accountNum = jsonObject.getString("accountNum");
        String templeCode = jsonObject.getString("templeCode");
        String appid = wxService.getAppId(accountNum);

        String msg = wecorpTempleService.addTempleId(templeCode, appid);

        ResponseData responseData = new ResponseData();
        responseData.setMessage(msg);
        return responseData;
    }


    /**
     * 获取模板消息列表
     * @return
     */
    @RequestMapping(value = "/api/public/wecorp/getTempleData")
    @ResponseBody
    public ResponseData getTempleData(HttpServletRequest request,@RequestBody JSONObject jsonObject) throws Exception {
        String accountNum = jsonObject.getString("accountNum");
        String appid = wxService.getAppId(accountNum);
        return new ResponseData(wecorpTempleService.getTempleData(appid));
    }

    /**
     * 删除模板消息
     * @return
     */
    @RequestMapping(value = "/api/public/wecorp/delTempleId")
    @ResponseBody
    public ResponseData delTempleId(HttpServletRequest request,@RequestBody JSONObject jsonObject) throws Exception {
        String accountNum = jsonObject.getString("accountNum");
        String appid = wxService.getAppId(accountNum);
        String templeId = jsonObject.getString("templeId");


        String msg = wecorpTempleService.delTempleId(templeId, appid);

        ResponseData responseData = new ResponseData();
        responseData.setMessage(msg);
        return responseData;

    }

}
