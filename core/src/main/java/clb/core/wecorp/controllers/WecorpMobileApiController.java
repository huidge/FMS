package clb.core.wecorp.controllers;

import clb.core.sys.controllers.ClbBaseController;
import clb.core.wecorp.dto.WecorpAccount;
import clb.core.wecorp.dto.WecorpUser;
import clb.core.wecorp.dto.WecorpUserBunding;
import clb.core.wecorp.mapper.WecorpUserBundingMapper;
import clb.core.wecorp.service.IWecorpAccountService;
import clb.core.wecorp.service.IWecorpUserService;
import clb.core.wecorp.service.IWecorpVerificationService;
import clb.core.wecorp.service.IWxService;
import clb.core.wecorp.service.impl.SecurityUtils;
import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/6/14.
 */
@RestController
@RequestMapping("/api/wecorpmobileapi")
public class WecorpMobileApiController extends ClbBaseController {

    @Autowired
    private IWxService wxTokenService;
    @Autowired
    private IWecorpUserService woaUserService;
    @Autowired
    private IWecorpVerificationService wecorpVerificationService;
    @Autowired
    private IWecorpAccountService wecorpAccountService;
    @Autowired
    private WecorpUserBundingMapper woaUserBundingMapper;



    /***
     * 发送验证码
     * @param request
     * @param json
     * @return
     */
    @RequestMapping(value = "/getCode", method = RequestMethod.POST, produces = {"application/json" })
    public ResponseData getCode(HttpServletRequest request, @RequestBody JSONObject json) {
        ResponseData responseData = new ResponseData();
        String phone = json.getString("phone");
        wecorpVerificationService.getCode(phone);

        responseData.setMessage("发送成功");
        responseData.setCode("0");
        responseData.setSuccess(true);
        return responseData;
    }


    /***
     * 获取用户id
     * @param request
     * @return
     */
    @RequestMapping(value = "/getUserId", method = RequestMethod.GET, produces = {"application/json" })
    @ResponseBody
    @Timed
    public ResponseData getWxUserId(HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        String userName = SecurityUtils.getCurrentUserLogin();
        /*try {
            String userId = SecurityUtils.getCurrentUserLogin();
            responseData.setMessage(userId);
            responseData.setCode("0");
            responseData.setSuccess(true);
        }catch (Exception e){
            responseData.setMessage("获取工号失败");
            responseData.setCode("1");
            responseData.setSuccess(false);
        }*/
        responseData.setMessage(userName);
        responseData.setCode("0");
        responseData.setSuccess(true);
        return responseData;
    }

    /***
     * 微信绑定用户
     * @param request
     * @param json
     * @return
     */
    @RequestMapping(value = "/userBinding", method = RequestMethod.POST, produces = {"application/json" })
    @ResponseBody
    @Timed
    public ResponseData userBunding(HttpServletRequest request,@RequestBody(required = false) JSONObject json) {
        IRequest requestContext = createRequestContext(request);
        ResponseData responseData = new ResponseData();
        String openId = SecurityUtils.getCurrentUserLogin();
        String appid = json.getString("appid");
        String phone = json.getString("phone");
        String code = json.getString("code");
        JSONObject jsonObject = wecorpVerificationService.verification(phone, code);
        if("false".equals(jsonObject.getString("status"))) {
            responseData.setCode("1");
            responseData.setMessage(jsonObject.getString("msg"));
            responseData.setSuccess(false);
            return responseData;
        }


        WecorpUser user = new WecorpUser();
        WecorpUserBunding woaUserBunding = new WecorpUserBunding();
        WecorpAccount account = wecorpAccountService.getWoaAccountByAppId(appid);
        if("2BFortune".equals(account.getAccountName())) {
            List<WecorpUser> users = woaUserService.getUserByPhoneAndType(phone,"SALESPERSOM");
            if(users.size() <= 0) {
                String pwd = json.getString("password");
                String userName = json.getString("userName");
                Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
                Matcher m = p.matcher(userName);
                if (m.find()) {
                    responseData.setMessage("用户名中包含有中文，保存失败");
                    responseData.setSuccess(false);
                    responseData.setCode("1");
                    return responseData;
                }
                user.setUserName(userName);
                List<WecorpUser> userList=woaUserService.select(requestContext, user, 1, 10);
                if(!CollectionUtils.isEmpty(userList)){
                    responseData.setMessage("用户名已存在!");
                    responseData.setSuccess(false);
                    responseData.setCode("1");
                    return responseData;
                }

                user.setUserName(userName);
                user.setPassword(pwd);
                user.setPhone(phone);
                user.setUserType("SALESPERSOM");
                user.setPlanQuota(10L);
                user = woaUserService.insertSelective(requestContext,user);
            }else {
                user = users.get(0);
            }
            woaUserBunding.setAccountId(account.getAccountId());
            woaUserBunding.setUserId(user.getUserId());
            woaUserBunding.setOpenId(openId);
            woaUserBundingMapper.insertSelective(woaUserBunding);
            responseData.setCode("0");
            responseData.setMessage("绑定成功");
            responseData.setSuccess(true);
        }
        else if("".equals(account.getAccountName())) {
            List<WecorpUser> users = woaUserService.getUserByPhoneAndType(phone,"CUSTOMER");
            if(users.size() <= 0) {
                responseData.setCode("1");
                responseData.setMessage("未找到相关用户");
                responseData.setSuccess(false);
            }else {
                user = users.get(0);
                woaUserBunding.setAccountId(account.getAccountId());
                woaUserBunding.setUserId(user.getUserId());
                woaUserBunding.setOpenId(openId);
                woaUserBundingMapper.insertSelective(woaUserBunding);
                responseData.setCode("0");
                responseData.setMessage("绑定成功");
                responseData.setSuccess(true);
            }
        }
        return responseData;
    }
}

